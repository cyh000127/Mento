import { MentoringHero } from "@/components/mentoring/mentoring-hero"
import { HowMentoringWorks } from "@/components/mentoring/how-mentoring-works"
import { MentoringFaq } from "@/components/mentoring/mentoring-faq"

export default function MentoringPage() {
  return (
    <>
      <MentoringHero />
      <HowMentoringWorks />
      <MentoringFaq />
    </>
  )
}
