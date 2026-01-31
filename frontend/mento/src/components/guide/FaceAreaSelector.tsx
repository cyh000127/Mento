interface FaceAreaSelectorProps {
  selectedArea: string
  onAreaSelect: (area: string) => void
}

const faceAreas = [
  { id: "forehead", label: "이마", top: "15%", left: "50%" },
  { id: "nose", label: "코", top: "45%", left: "50%" },
  { id: "cheeks", label: "볼", top: "50%", left: "25%" },
  { id: "jaw", label: "턱", top: "75%", left: "50%" },
]

export function FaceAreaSelector({
  selectedArea,
  onAreaSelect,
}: FaceAreaSelectorProps) {
  return (
    <div className="flex flex-col items-center gap-6">
      <h3 className="text-lg font-semibold text-text-primary">
        부위를 선택하세요
      </h3>

      {/* Face Silhouette - Hidden on mobile, use buttons instead */}
      <div className="hidden md:block relative w-full max-w-[280px] aspect-[3/4] bg-gradient-to-b from-muted/30 to-muted/50 rounded-full border-2 border-border">
        {/* Abstract Face Guide */}
        <svg
          viewBox="0 0 200 260"
          className="absolute inset-0 w-full h-full opacity-20"
        >
          {/* Simple face outline */}
          <ellipse
            cx="100"
            cy="130"
            rx="80"
            ry="110"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            className="text-text-secondary"
          />
          {/* Eyes */}
          <circle cx="70" cy="100" r="4" className="fill-text-secondary" />
          <circle cx="130" cy="100" r="4" className="fill-text-secondary" />
          {/* Nose */}
          <line
            x1="100"
            y1="115"
            x2="100"
            y2="135"
            stroke="currentColor"
            strokeWidth="2"
            className="text-text-secondary"
          />
        </svg>

        {/* Interactive Area Buttons */}
        {faceAreas.map((area) => (
          <button
            key={area.id}
            onClick={() => onAreaSelect(area.id)}
            style={{
              position: "absolute",
              top: area.top,
              left: area.left,
              transform: "translate(-50%, -50%)",
            }}
            className={`
              px-4 py-2 rounded-full text-sm font-medium
              transition-all duration-300 whitespace-nowrap
              ${
                selectedArea === area.id
                  ? "bg-primary-500 text-white shadow-lg scale-110"
                  : "bg-background border-2 border-border text-text-primary hover:border-primary-300 hover:bg-primary-100/50"
              }
            `}
            aria-label={`${area.label} 선택`}
          >
            {area.label}
          </button>
        ))}
      </div>

      {/* Area Button List (Mobile & Tablet) */}
      <div className="flex flex-wrap gap-3 justify-center md:hidden">
        {faceAreas.map((area) => (
          <button
            key={area.id}
            onClick={() => onAreaSelect(area.id)}
            className={`
              px-6 py-3 rounded-full text-sm font-medium
              transition-all duration-300
              ${
                selectedArea === area.id
                  ? "bg-primary-500 text-white shadow-md"
                  : "bg-background border-2 border-border text-text-primary hover:border-primary-300 active:scale-95"
              }
            `}
            aria-label={`${area.label} 선택`}
          >
            {area.label}
          </button>
        ))}
      </div>
    </div>
  )
}
