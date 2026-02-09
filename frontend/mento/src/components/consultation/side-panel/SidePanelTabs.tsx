import { useConsultationStore } from '@/stores/useConsultationStore';

const tabs = [
  { id: 'share' as const, label: '화면공유' },
  { id: 'inventory' as const, label: '인벤토리' },
  { id: 'mask' as const, label: '마스크' },
  { id: 'record' as const, label: '녹화' },
];

type TabType = typeof tabs[number]['id'];

interface SidePanelTabsProps {
  allowedTabs?: ReadonlyArray<TabType>;
}

export function SidePanelTabs({ allowedTabs }: SidePanelTabsProps) {
  const { activeTab, setActiveTab } = useConsultationStore();
  const visibleTabs = allowedTabs ? tabs.filter((tab) => allowedTabs.includes(tab.id)) : tabs;

  return (
    <div className="flex w-full border-b border-gray-700">
      {visibleTabs.map((tab) => (
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
