interface ProductTypeCardProps {
  id: string
  label: string
  image?: string
  isSelected: boolean
  onClick: () => void
}

export function ProductTypeCard({
  label,
  image,
  isSelected,
  onClick,
}: ProductTypeCardProps) {
  return (
    <button
      onClick={onClick}
      className={`
        relative p-5 rounded-xl border-2 transition-all duration-300 w-full
        ${
          isSelected
            ? "border-primary-500 bg-primary-100/50 shadow-md scale-[1.02]"
            : "border-border bg-background hover:border-primary-300 hover:bg-pastel-blue-100/30 hover:shadow-sm"
        }
      `}
    >
      <div className="flex flex-col items-center gap-3">
        <div
          className={`
          h-14 w-14 rounded-full flex items-center justify-center overflow-hidden
          transition-all duration-300
          ${
            isSelected
              ? "bg-primary-500 text-white shadow-sm"
              : "bg-muted text-text-secondary"
          }
        `}
        >
          {image ? (
            <img 
              src={image} 
              alt={label} 
              className="w-full h-full object-contain p-2"
            />
          ) : (
            <div className="text-xl font-bold">
              {label.charAt(0)}
            </div>
          )}
        </div>
        <span
          className={`
          text-sm font-semibold transition-colors text-center
          ${isSelected ? "text-primary-500" : "text-text-primary"}
        `}
        >
          {label}
        </span>
      </div>
    </button>
  )
}
