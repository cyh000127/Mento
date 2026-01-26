import { useConsultationStore } from '@/stores/useConsultationStore';

const maskAreas = [
  { id: 'T-zone' as const, label: 'T-zone', description: '이마, 코' },
  { id: 'U-zone' as const, label: 'U-zone', description: '볼, 턱' },
  { id: 'Nose zone' as const, label: 'Nose zone', description: '코 주변' },
  { id: 'Apple zone' as const, label: 'Apple zone', description: '사과 존' },
];

export function MaskPanel() {
  const { selectedMaskArea, setSelectedMaskArea } = useConsultationStore();

  return (
    <div className="p-6">
      <div className="grid grid-cols-2 gap-4">
        {maskAreas.map((area) => (
          <button
            key={area.id}
            onClick={() => setSelectedMaskArea(area.id)}
            className={`
              aspect-square rounded-lg p-6 transition-all duration-200
              flex flex-col items-center justify-center gap-2
              border-2
              ${
                selectedMaskArea === area.id
                  ? 'bg-blue-600 border-blue-400 shadow-lg shadow-blue-500/50 scale-105'
                  : 'bg-gray-800 border-gray-700 hover:bg-gray-700 hover:border-gray-600'
              }
            `}
          >
            <div className="text-center">
              <div
                className={`
                text-lg font-bold mb-1
                ${selectedMaskArea === area.id ? 'text-white' : 'text-gray-200'}
              `}
              >
                {area.label}
              </div>
              <div
                className={`
                text-xs
                ${selectedMaskArea === area.id ? 'text-blue-100' : 'text-gray-400'}
              `}
              >
                {area.description}
              </div>
            </div>
          </button>
        ))}
      </div>

      {selectedMaskArea && (
        <div className="mt-6 p-4 bg-gray-800 rounded-lg border border-gray-700">
          <p className="text-sm text-gray-300">
            선택된 영역: <span className="font-semibold text-blue-400">{selectedMaskArea}</span>
          </p>
        </div>
      )}
    </div>
  );
}
