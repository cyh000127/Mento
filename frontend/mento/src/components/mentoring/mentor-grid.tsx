import { useState } from "react"
import { Star, MessageCircle, Calendar, Check } from "lucide-react"

const specialties = [
  { id: "all", label: "전체" },
  { id: "skincare", label: "스킨케어" },
  { id: "haircare", label: "헤어케어" },
  { id: "bodycare", label: "바디케어" },
  { id: "fragrance", label: "향수" },
  { id: "styling", label: "스타일링" },
]

const mentors = [
  {
    id: 1,
    name: "김민준",
    title: "피부과 전문의",
    specialty: "스킨케어",
    experience: "15년",
    rating: 4.9,
    reviews: 328,
    price: 50000,
    available: true,
    tags: ["여드름", "민감성", "안티에이징"],
    bio: "15년간 피부과 진료 경험을 바탕으로 개인 맞춤 스킨케어 루틴을 제안합니다.",
  },
  {
    id: 2,
    name: "이서준",
    title: "헤어 디자이너",
    specialty: "헤어케어",
    experience: "12년",
    rating: 4.8,
    reviews: 256,
    price: 40000,
    available: true,
    tags: ["탈모예방", "스타일링", "두피케어"],
    bio: "남성 헤어 전문가로서 두피 건강부터 트렌디한 스타일링까지 조언드립니다.",
  },
  {
    id: 3,
    name: "박준혁",
    title: "향수 큐레이터",
    specialty: "향수",
    experience: "8년",
    rating: 4.9,
    reviews: 189,
    price: 35000,
    available: false,
    tags: ["시그니처향", "시즌향수", "향수레이어링"],
    bio: "당신만의 시그니처 향을 찾아드립니다. 상황별 맞춤 향수 추천 전문입니다.",
  },
  {
    id: 4,
    name: "최우진",
    title: "스타일리스트",
    specialty: "스타일링",
    experience: "10년",
    rating: 4.7,
    reviews: 412,
    price: 45000,
    available: true,
    tags: ["비즈니스룩", "캐주얼", "퍼스널컬러"],
    bio: "체형과 퍼스널컬러에 맞는 스타일링으로 자신감 있는 이미지를 만들어드립니다.",
  },
  {
    id: 5,
    name: "정도현",
    title: "피부관리사",
    specialty: "스킨케어",
    experience: "7년",
    rating: 4.8,
    reviews: 167,
    price: 30000,
    available: true,
    tags: ["모공케어", "미백", "수분관리"],
    bio: "실용적인 홈케어 루틴과 제품 사용법을 알려드립니다.",
  },
  {
    id: 6,
    name: "한승우",
    title: "바디케어 전문가",
    specialty: "바디케어",
    experience: "9년",
    rating: 4.6,
    reviews: 134,
    price: 35000,
    available: true,
    tags: ["체취관리", "제모", "보습케어"],
    bio: "남성 바디케어의 기초부터 심화까지 체계적으로 안내해드립니다.",
  },
]

export function MentorGrid() {
  const [selectedSpecialty, setSelectedSpecialty] = useState("all")

  const filteredMentors =
    selectedSpecialty === "all"
      ? mentors
      : mentors.filter((mentor) => mentor.specialty === specialties.find(s => s.id === selectedSpecialty)?.label)

  return (
    <section className="bg-background py-12 md:py-16">
      <div className="mx-auto max-w-[1200px] px-6">
        {/* Filter */}
        <div className="mb-8 flex flex-wrap gap-2">
          {specialties.map((specialty) => (
            <button
              key={specialty.id}
              type="button"
              onClick={() => setSelectedSpecialty(specialty.id)}
              className={`rounded-full px-4 py-2 text-sm font-medium transition-all ${
                selectedSpecialty === specialty.id
                  ? "bg-primary-500 text-dark-bg shadow-md shadow-primary-500/25"
                  : "bg-muted text-text-secondary hover:bg-muted/80 hover:text-text-primary"
              }`}
            >
              {specialty.label}
            </button>
          ))}
        </div>

        {/* Mentor Grid */}
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {filteredMentors.map((mentor) => (
            <div
              key={mentor.id}
              className="group overflow-hidden rounded-2xl border border-border bg-background shadow-sm transition-all hover:-translate-y-1 hover:shadow-lg hover:shadow-primary-500/5"
            >
              {/* Header */}
              <div className="relative bg-gradient-to-br from-primary-100 to-pastel-purple-100 p-6">
                {/* Availability Badge */}
                <div
                  className={`absolute right-4 top-4 inline-flex items-center gap-1 rounded-full px-2.5 py-1 text-xs font-medium ${
                    mentor.available
                      ? "bg-pastel-green-100 text-text-primary"
                      : "bg-muted text-text-secondary"
                  }`}
                >
                  <span
                    className={`h-1.5 w-1.5 rounded-full ${
                      mentor.available ? "bg-green-500" : "bg-text-secondary"
                    }`}
                  />
                  {mentor.available ? "상담 가능" : "예약 마감"}
                </div>

                {/* Avatar */}
                <div className="mb-4 flex h-20 w-20 items-center justify-center rounded-full bg-gradient-to-br from-primary-400 to-primary-500 text-2xl font-bold text-dark-bg">
                  {mentor.name.charAt(0)}
                </div>

                {/* Name & Title */}
                <h3 className="text-lg font-bold text-text-primary">
                  {mentor.name}
                </h3>
                <p className="text-sm text-text-secondary">{mentor.title}</p>
              </div>

              {/* Content */}
              <div className="p-6">
                {/* Bio */}
                <p className="mb-4 text-sm leading-relaxed text-text-secondary">
                  {mentor.bio}
                </p>

                {/* Tags */}
                <div className="mb-4 flex flex-wrap gap-1.5">
                  {mentor.tags.map((tag) => (
                    <span
                      key={tag}
                      className="rounded-full bg-muted px-2.5 py-0.5 text-xs text-text-secondary"
                    >
                      {tag}
                    </span>
                  ))}
                </div>

                {/* Stats */}
                <div className="mb-4 flex items-center gap-4 text-sm">
                  <div className="flex items-center gap-1">
                    <Star className="h-4 w-4 fill-amber-400 text-amber-400" />
                    <span className="font-medium text-text-primary">
                      {mentor.rating}
                    </span>
                    <span className="text-text-secondary">
                      ({mentor.reviews})
                    </span>
                  </div>
                  <span className="text-text-secondary">
                    경력 {mentor.experience}
                  </span>
                </div>

                {/* Price & CTA */}
                <div className="flex items-center justify-between border-t border-border pt-4">
                  <div>
                    <p className="text-xs text-text-secondary">상담료</p>
                    <p className="text-lg font-bold text-text-primary">
                      {mentor.price.toLocaleString()}원
                      <span className="text-sm font-normal text-text-secondary">
                        /30분
                      </span>
                    </p>
                  </div>
                  <button
                    type="button"
                    disabled={!mentor.available}
                    className={`flex items-center gap-1.5 rounded-lg px-4 py-2 text-sm font-medium transition-all ${
                      mentor.available
                        ? "bg-primary-500 text-dark-bg hover:bg-primary-400"
                        : "cursor-not-allowed bg-muted text-text-secondary"
                    }`}
                  >
                    {mentor.available ? (
                      <>
                        <Calendar className="h-4 w-4" />
                        예약하기
                      </>
                    ) : (
                      <>
                        <Check className="h-4 w-4" />
                        예약 마감
                      </>
                    )}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
