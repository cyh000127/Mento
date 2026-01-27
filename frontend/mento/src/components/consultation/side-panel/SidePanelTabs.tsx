import { useConsultationStore } from '@/stores/useConsultationStore';

const tabs = [
  { id: 'share' as const, label: '화면공유' },
  { id: 'inventory' as const, label: '인벤토리' },
  { id: 'mask' as const, label: '마스크' },
  { id: 'record' as const, label: '녹화' },
];

export function SidePanelTabs() {
  const { activeTab, setActiveTab } = useConsultationStore();

  return (
    <div className="flex w-full border-b border-gray-700">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          onClick={() => setActiveTab(tab.id)}
          className={`
            flex-1 py-3 px-4 text-sm font-medium transition-colors
            ${
              activeTab === tab.id
                ? 'bg-blue-600 text-white border-b-2 border-blue-400'
                : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
            }
          `}
        >
          {tab.label}
        </button>
      ))}
    </div>
  );
}
