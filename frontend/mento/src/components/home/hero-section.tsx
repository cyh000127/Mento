import { Link } from "react-router-dom";
import { ArrowRight, ChevronDown } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { LoadingIntro } from "./loading-intro";
import { Button } from "@/components/ui/button";
import { LoginModal } from "@/components/login-modal";
import { useAuthStore } from "@/stores/useAuthStore";

// Import grooming images
import curology1 from "@/assets/images/curology-iKoH1gNON70-unsplash.jpg";
import curology2 from "@/assets/images/curology-WYU8DzFNaLo-unsplash.jpg";
import andrea from "@/assets/images/andrea-donato-2LEqbbP5OZI-unsplash.jpg";
import kadarius from "@/assets/images/kadarius-seegars-Mxy5gokl8mE-unsplash.jpg";
import luis from "@/assets/images/luis-eduardo-25R-R4NwrLw-unsplash.jpg";
import michela from "@/assets/images/michela-ampolo-7tDGb3HrITg-unsplash.jpg";
import mike1 from "@/assets/images/mike-mgc-4vifU_h3VmY-unsplash.jpg";
import mike2 from "@/assets/images/mike-mgc-dh8_9YhLs4s-unsplash.jpg";
import mostafa from "@/assets/images/mostafa-meraji-z2qyry-n-PA-unsplash.jpg";
import nataliya1 from "@/assets/images/nataliya-melnychuk-dFBhXJHKNeo-unsplash.jpg";
import nataliya2 from "@/assets/images/nataliya-melnychuk-I-6Ap7JXHq8-unsplash.jpg";
import pexels from "@/assets/images/pexels-cup-of-couple-6633786.jpg";
import sunny from "@/assets/images/sunny-ng-KVIlNRoGwxk-unsplash.jpg";
import tarah from "@/assets/images/tarah-dane-4JsvuH-pRzo-unsplash.jpg";
import nix1 from "@/assets/images/the-nix-company-3_KYuMVl1Q8-unsplash.jpg";
import nix2 from "@/assets/images/the-nix-company-QanhCEMlSdk-unsplash.jpg";

import dryingHairVideo from "@/assets/videos/drying_hair.mp4";

// Left film strip images - grooming process shots
const LEFT_IMAGES = [
  curology1, // skincare products
  andrea, // grooming close-up
  mike1, // skincare application
  nataliya1, // product texture
  luis, // male grooming
  sunny, // skincare routine
  michela, // grooming tools
  curology2, // skincare products
];

// Right film strip images - lifestyle and results
const RIGHT_IMAGES = [
  kadarius, // confident portrait
  nix1, // grooming lifestyle
  mostafa, // male skincare
  nataliya2, // product close-up
  mike2, // grooming moment
  pexels, // grooming couple
  tarah, // skincare application
  nix2, // lifestyle grooming
];

interface HeroSectionProps {
  showIntro?: boolean;
  onIntroComplete?: () => void;
}

const INTRO_STORAGE_KEY = "hasVisitedHome";

export function HeroSection(props: HeroSectionProps) {
  const videoRef = useRef<HTMLVideoElement>(null);
  const sectionRefs = useRef<HTMLElement[]>([]);
  const [currentScene, setCurrentScene] = useState(0);
  const [sectionCount, setSectionCount] = useState(0);
  const [imagesLoaded, setImagesLoaded] = useState(false);
  const [isLoginOpen, setIsLoginOpen] = useState(false);
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn);
  const [showIntro, setShowIntro] = useState(() => {
    if (typeof window === "undefined") return false;
    return localStorage.getItem(INTRO_STORAGE_KEY) !== "true";
  });

  // Preload images
  useEffect(() => {
    const allImages = [...LEFT_IMAGES, ...RIGHT_IMAGES];
    let loadedCount = 0;

    const preloadImage = (src: string) => {
      return new Promise((resolve) => {
        const img = new Image();
        img.onload = () => {
          loadedCount++;
          if (loadedCount === allImages.length) {
            setImagesLoaded(true);
          }
          resolve(true);
        };
        img.onerror = () => {
          loadedCount++;
          if (loadedCount === allImages.length) {
            setImagesLoaded(true);
          }
          resolve(false);
        };
        img.src = src;
      });
    };

    Promise.all(allImages.map(preloadImage));
  }, []);

  useEffect(() => {
    if (videoRef.current && currentScene === 2) {
      videoRef.current.play().catch((error) => {
        console.error(error);
      });
    }
  }, [currentScene]);

  useEffect(() => {
    if (showIntro || !imagesLoaded) return;

    const scrollContainer = document.querySelector("[data-home-scroll]") as HTMLElement | null;
    if (!scrollContainer) return;

    const collectSections = () => {
      const sectionElements = Array.from(scrollContainer.querySelectorAll<HTMLElement>("[data-home-section]"));
      sectionRefs.current = sectionElements;
      setSectionCount(sectionElements.length);
      return sectionElements;
    };

    const sectionElements = collectSections();
    if (sectionElements.length === 0) return;
    setCurrentScene(0);

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (!entry.isIntersecting) return;
          const index = sectionRefs.current.indexOf(entry.target as HTMLElement);
          if (index >= 0) setCurrentScene(index);
        });
      },
      { root: scrollContainer, threshold: 0.6 },
    );

    sectionRefs.current.forEach((section) => {
      observer.observe(section);
    });

    const mutationObserver = new MutationObserver(() => {
      observer.disconnect();
      const nextSections = collectSections();
      nextSections.forEach((section) => observer.observe(section));
    });

    mutationObserver.observe(scrollContainer, { childList: true, subtree: true });

    return () => {
      observer.disconnect();
      mutationObserver.disconnect();
    };
  }, [showIntro, imagesLoaded]);

  const handleIntroComplete = () => {
    localStorage.setItem(INTRO_STORAGE_KEY, "true");
    setShowIntro(false);
    props.onIntroComplete?.();
  };

  const scrollToSection = (index: number) => {
    const scrollContainer = document.querySelector("[data-home-scroll]") as HTMLElement | null;
    const targetSection = sectionRefs.current[index];
    if (!scrollContainer || !targetSection) return;
    scrollContainer.scrollTo({ top: targetSection.offsetTop, behavior: "smooth" });
  };

  const handleLearnMore = () => {
    const nextIndex = Math.min(currentScene + 1, sectionRefs.current.length - 1);
    scrollToSection(nextIndex);
  };

  return showIntro ? (
    <LoadingIntro onComplete={handleIntroComplete} />
  ) : (
    <>
      {/* Progress indicator */}
      <div className="fixed right-8 top-1/2 z-50 flex -translate-y-1/2 flex-col gap-3">
        {Array.from({ length: sectionCount }, (_, index) => (
          <button
            key={index}
            onClick={() => {
              scrollToSection(index);
            }}
            className="group relative"
            aria-label={`Scene ${index + 1}`}
          >
            <span
              className={`block h-3 w-3 rounded-full border-2 transition-all duration-300 ${
                currentScene === index ? "border-primary-500 bg-primary-500 scale-125" : "border-primary-500/30 bg-transparent hover:border-primary-500/60"
              }`}
            />
            {currentScene === index && <span className="absolute inset-0 animate-ping rounded-full bg-primary-500 opacity-20" />}
          </button>
        ))}
      </div>

      {/* Scene 1: Film Strip Intro */}
      <section data-home-section className="relative h-screen w-full snap-start snap-always bg-background animate-fade-in">
        <div className="flex h-full w-full">
          {/* Left Film Strip */}
          <div className="w-[20%] overflow-hidden border-r border-border/30 bg-black">
            <div className="animate-scroll-up flex flex-col">
              {[...LEFT_IMAGES, ...LEFT_IMAGES].map((img, index) => (
                <div key={index} className="relative h-[300px] w-full flex-shrink-0 border-b border-border/20">
                  <div className="absolute inset-0 bg-black/40 z-10" />
                  <img src={img} alt="" className="h-full w-full object-cover" />
                </div>
              ))}
            </div>
          </div>

          {/* Center Content */}
          <div className="relative flex w-[60%] flex-col items-center justify-center bg-background px-8">
            <div className="text-center">
              <div className="animate-fade-in-up mb-6 inline-flex items-center gap-2 rounded-full border border-primary-500/20 bg-primary-100/50 px-6 py-3 shadow-lg shadow-primary-500/10">
                <span className="h-2 w-2 animate-pulse rounded-full bg-primary-500" />
                <span className="text-sm font-medium text-primary-500">남성 뷰티의 새로운 기준</span>
              </div>

              <h1
                className="animate-scale-in mb-6 text-balance text-5xl font-bold leading-tight tracking-tight text-text-primary md:text-6xl lg:text-7xl"
                style={{ animationDelay: "0.2s", animationFillMode: "backwards" }}
              >
                당신만을 위한
                <br />
                <span className="bg-gradient-to-r from-primary-500 to-primary-300 bg-clip-text text-transparent">뷰티 파트너</span>
              </h1>

              <p
                className="animate-fade-in-up mx-auto mb-10 max-w-2xl text-pretty text-base leading-relaxed text-text-secondary md:text-lg lg:text-xl"
                style={{ animationDelay: "0.4s", animationFillMode: "backwards" }}
              >
                체계적인 관리부터 전문가 멘토링까지
                <br />
                MENTO와 함께 새로운 루틴을 시작하세요
              </p>

              <div className="animate-fade-in-up mt-6 flex flex-col items-center justify-center gap-3 sm:flex-row" style={{ animationDelay: "0.8s", animationFillMode: "backwards" }}>
                {!isLoggedIn && (
                  <Button size="lg" onClick={() => setIsLoginOpen(true)} className="w-full sm:w-auto">
                    로그인
                    <ArrowRight className="h-5 w-5" />
                  </Button>
                )}
                <Button size="lg" variant="outline" onClick={handleLearnMore} className="w-full sm:w-auto">
                  더 알아보기
                </Button>
              </div>

              <div className="animate-fade-in-up mt-4" style={{ animationDelay: "0.6s", animationFillMode: "backwards" }}>
                <div className="animate-bounce">
                  <ChevronDown className="mx-auto h-8 w-8 text-primary-500" />
                </div>
              </div>
            </div>
          </div>

          {/* Right Film Strip */}
          <div className="w-[20%] overflow-hidden border-l border-border/30 bg-black">
            <div className="animate-scroll-down flex flex-col">
              {[...RIGHT_IMAGES, ...RIGHT_IMAGES].map((img, index) => (
                <div key={index} className="relative h-[300px] w-full flex-shrink-0 border-b border-border/20">
                  <div className="absolute inset-0 bg-black/40 z-10" />
                  <img src={img} alt="" className="h-full w-full object-cover" />
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Scene 2: Film Strip Climax */}
      <section data-home-section className="relative h-screen w-full snap-start snap-always bg-background">
        <div className="flex h-full w-full">
          {/* Left Film Strip */}
          <div className="w-[20%] overflow-hidden border-r border-border/30 bg-black">
            <div className="animate-scroll-up flex flex-col">
              {[...LEFT_IMAGES, ...LEFT_IMAGES].map((img, index) => (
                <div key={index} className="relative h-[300px] w-full flex-shrink-0 border-b border-border/20">
                  <div className="absolute inset-0 bg-black/40 z-10" />
                  <img src={img} alt="" className="h-full w-full object-cover" />
                </div>
              ))}
            </div>
          </div>

          {/* Center Content */}
          <div className="relative flex w-[60%] flex-col items-center justify-center bg-background px-8">
            <div className="text-center">
              <h2 className="animate-scale-in mb-6 text-balance text-4xl font-bold text-text-primary md:text-5xl lg:text-6xl">
                AI 기반
                <br />
                <span className="bg-gradient-to-r from-primary-500 to-primary-300 bg-clip-text text-transparent">스마트 스킨케어</span>
              </h2>

              <p className="animate-fade-in-up mx-auto max-w-2xl text-pretty text-lg leading-relaxed text-text-secondary md:text-xl" style={{ animationDelay: "0.3s", animationFillMode: "backwards" }}>
                개인 맞춤 분석과 데이터 기반 추천으로
                <br />더 나은 당신을 만들어갑니다
              </p>
            </div>

            <div className="animate-fade-in-up mt-4" style={{ animationDelay: "0.6s", animationFillMode: "backwards" }}>
              <div className="animate-bounce">
                <ChevronDown className="mx-auto h-8 w-8 text-primary-500" />
              </div>
            </div>
          </div>

          {/* Right Film Strip */}
          <div className="w-[20%] overflow-hidden border-l border-border/30 bg-black">
            <div className="animate-scroll-down flex flex-col">
              {[...RIGHT_IMAGES, ...RIGHT_IMAGES].map((img, index) => (
                <div key={index} className="relative h-[300px] w-full flex-shrink-0 border-b border-border/20">
                  <div className="absolute inset-0 bg-black/40 z-10" />
                  <img src={img} alt="" className="h-full w-full object-cover" />
                </div>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Scene 3: Video Hero */}
      <section data-home-section className="relative h-screen w-full snap-start snap-always bg-background">
        <div className="relative h-full w-full">
          {/* Video Background */}
          <video ref={videoRef} autoPlay muted loop playsInline className="absolute inset-0 h-full w-full object-cover opacity-30" src={dryingHairVideo} />

          {/* Video overlay */}
          <div className="absolute inset-0 bg-black/40 pointer-events-none" />

          {/* Content Overlay */}
          <div className="absolute inset-0 flex flex-col items-center justify-center bg-gradient-to-b from-background/40 via-background/20 to-background/40 px-8 text-center">
            <div>
              <h2 className="animate-scale-in mb-6 text-balance text-5xl font-bold text-text-primary drop-shadow-lg md:text-6xl lg:text-7xl">
                완성된
                <br />
                <span className="text-primary-500">뷰티 루틴</span>
              </h2>

              <p
                className="animate-fade-in-up mx-auto mb-10 max-w-2xl text-pretty text-lg leading-relaxed text-text-primary/90 drop-shadow md:text-xl"
                style={{ animationDelay: "0.2s", animationFillMode: "backwards" }}
              >
                개인 뷰티 인벤토리 관리부터 AI 스킨케어 분석, 전문가 멘토링까지
                <br />
                MENTO와 함께 체계적인 뷰티 루틴을 시작하세요
              </p>

              {/* CTA Buttons */}
              <div className="animate-fade-in-up flex flex-wrap items-center justify-center gap-4" style={{ animationDelay: "0.4s", animationFillMode: "backwards" }}>
                <Link
                  to="/inventory"
                  className="inline-flex items-center gap-2 rounded-xl bg-primary-500 px-8 py-4 text-base font-semibold text-dark-bg shadow-lg shadow-primary-500/25 transition-all hover:bg-primary-400 hover:shadow-xl hover:shadow-primary-500/40 hover:scale-105"
                >
                  시작하기
                  <ArrowRight className="h-5 w-5" />
                </Link>
              </div>
            </div>

            <div className="animate-fade-in-up mt-4" style={{ animationDelay: "0.6s", animationFillMode: "backwards" }}>
              <div className="animate-bounce">
                <ChevronDown className="mx-auto h-8 w-8 text-primary-500" />
              </div>
            </div>
          </div>
        </div>
      </section>

      <LoginModal isOpen={isLoginOpen} onClose={() => setIsLoginOpen(false)} />
    </>
  );
}
