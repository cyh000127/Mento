package com.mento.domain.notification.service.schedule;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.util.TimeUtils;
import com.mento.common.util.AesUtils;
import com.mento.domain.brand.entity.Brand;
import com.mento.domain.brand.repository.BrandRepository;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.repository.ItemRepository;
import com.mento.domain.item.service.query.ItemQueryService;
import com.mento.domain.item.service.query.ItemQueryServiceImpl;
import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.product.entity.Product;
import com.mento.domain.product.repository.ProductRepository;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.repository.UserRepository;
import com.mento.domain.user.service.query.UserQueryService;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@SpringBootTest(classes = ItemExpiryNotificationScheduleBenchmarkTest.BenchmarkConfig.class, properties = {
	"encryption.secret-key=0000000000000000000000000000000000000000000000000000000000000000",
	"spring.ai.openai.api-key=test-key",
	"spring.ai.openai.speech.api-key=test-key",
	"spring.jpa.properties.hibernate.generate_statistics=true",
	"spring.task.scheduling.enabled=false"
})
@ActiveProfiles("test")
@Transactional
@EnabledIfEnvironmentVariable(named = "BENCHMARK", matches = "true")
class ItemExpiryNotificationScheduleBenchmarkTest {

	private static final int USER_COUNT = 200;
	private static final int ITEM_COUNT_PER_USER = 30;
	private static final int WARMUP_COUNT = 3;
	private static final int MEASURE_COUNT = 5;

	@Autowired
	private BrandRepository brandRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ItemQueryService itemQueryService;

	@Autowired
	private UserQueryService userQueryService;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Test
	@DisplayName("기존_만료_예정_아이템_알림_조회_그룹핑_성능을_측정한다")
	void 기존_만료_예정_아이템_알림_조회_그룹핑_성능을_측정한다() {
		// given
		prepareExpiringItems();

		for (int warmupIndex = 0; warmupIndex < WARMUP_COUNT; warmupIndex++) {
			measureLegacyReadAndGrouping();
		}

		// when
		List<BenchmarkSample> samples = new ArrayList<>();
		for (int measureIndex = 0; measureIndex < MEASURE_COUNT; measureIndex++) {
			samples.add(measureLegacyReadAndGrouping());
		}

		BenchmarkSummary summary = BenchmarkSummary.from(samples);
		System.out.printf(
			"%n[ItemExpiryNotification][legacy] users=%d, items=%d, avgMs=%.2f, p95Ms=%.2f, "
				+ "preparedStatements=%.1f, entityLoads=%.1f%n",
			USER_COUNT,
			USER_COUNT * ITEM_COUNT_PER_USER,
			summary.averageElapsedMillis(),
			summary.p95ElapsedMillis(),
			summary.averagePreparedStatements(),
			summary.averageEntityLoads()
		);

		// then
		assertThat(summary.averageNotificationCount()).isEqualTo(USER_COUNT);
		assertThat(summary.averagePreparedStatements()).isLessThanOrEqualTo(2.0);
	}

	private BenchmarkSample measureLegacyReadAndGrouping() {
		entityManager.flush();
		entityManager.clear();

		Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		statistics.clear();

		LocalDate today = TimeUtils.nowAsLocalDate();
		LocalDate oneWeekLater = today.plusDays(7);
		LocalDateTime nextScheduleTime = LocalDateTime.of(today.plusDays(1), LocalTime.of(12, 0));

		long startNanos = System.nanoTime();
		List<Item> expiringItems = itemQueryService.findItemsExpiringBetween(today, oneWeekLater);
		Map<Long, Long> userItemCountMap = expiringItems.stream()
			.collect(Collectors.groupingBy(
				item -> item.getUser().getId(),
				Collectors.counting()
			));

		List<Notification> notifications = userItemCountMap.entrySet().stream()
			.map(entry -> {
				User user = userQueryService.findById(entry.getKey());
				return NotificationConverter.toEntity(
					user,
					NotificationType.INVENTORY_EXPIRY,
					String.valueOf(entry.getValue()),
					nextScheduleTime
				);
			})
			.toList();
		long elapsedNanos = System.nanoTime() - startNanos;

		return new BenchmarkSample(
			elapsedNanos,
			statistics.getPrepareStatementCount(),
			statistics.getEntityLoadCount(),
			notifications.size()
		);
	}

	private void prepareExpiringItems() {
		Brand brand = brandRepository.save(Brand.builder()
			.brandName("benchmark-brand")
			.build());
		Product product = productRepository.save(Product.builder()
			.brand(brand)
			.oliveyoungGoodsNo("BENCHMARK-001")
			.name("benchmark-product")
			.defaultUsageDays(90)
			.build());

		List<User> users = new ArrayList<>();
		for (int userIndex = 0; userIndex < USER_COUNT; userIndex++) {
			users.add(User.builder()
				.name("benchmark-user-" + userIndex)
				.email("benchmark-user-" + userIndex + "@test.com")
				.password("password")
				.kakaoId("kakao-" + userIndex)
				.build());
		}
		userRepository.saveAll(users);

		LocalDate expiryDate = TimeUtils.nowAsLocalDate().plusDays(3);
		List<Item> items = new ArrayList<>();
		for (User user : users) {
			for (int itemIndex = 0; itemIndex < ITEM_COUNT_PER_USER; itemIndex++) {
				items.add(Item.builder()
					.user(user)
					.product(product)
					.status(ItemStatus.OWNED)
					.isFavorite(false)
					.purchaseCount(1)
					.purchaseDate(TimeUtils.nowAsLocalDate())
					.expectedExpiryDate(expiryDate)
					.build());
			}
		}
		itemRepository.saveAll(items);
		entityManager.flush();
		entityManager.clear();
	}

	private record BenchmarkSample(
		long elapsedNanos,
		long preparedStatements,
		long entityLoads,
		int notificationCount
	) {
		double elapsedMillis() {
			return elapsedNanos / 1_000_000.0;
		}
	}

	private record BenchmarkSummary(
		double averageElapsedMillis,
		double p95ElapsedMillis,
		double averagePreparedStatements,
		double averageEntityLoads,
		int averageNotificationCount
	) {
		private static BenchmarkSummary from(final List<BenchmarkSample> samples) {
			List<BenchmarkSample> sortedSamples = samples.stream()
				.sorted(Comparator.comparingDouble(BenchmarkSample::elapsedMillis))
				.toList();
			int p95Index = Math.min(sortedSamples.size() - 1, (int)Math.ceil(sortedSamples.size() * 0.95) - 1);

			return new BenchmarkSummary(
				samples.stream().mapToDouble(BenchmarkSample::elapsedMillis).average().orElse(0.0),
				sortedSamples.get(p95Index).elapsedMillis(),
				samples.stream().mapToLong(BenchmarkSample::preparedStatements).average().orElse(0.0),
				samples.stream().mapToLong(BenchmarkSample::entityLoads).average().orElse(0.0),
				(int)samples.stream().mapToInt(BenchmarkSample::notificationCount).average().orElse(0.0)
			);
		}
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@EnableJpaAuditing
	@EnableTransactionManagement
	@EntityScan(basePackages = "com.mento.domain")
	@EnableJpaRepositories(basePackageClasses = {
		BrandRepository.class,
		ItemRepository.class,
		ProductRepository.class,
		UserRepository.class
	})
	@Import({
		AesUtils.class,
		ItemQueryServiceImpl.class,
		UserQueryServiceImpl.class
	})
	static class BenchmarkConfig {
	}
}
