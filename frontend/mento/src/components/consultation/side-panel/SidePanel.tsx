import { useEffect } from 'react';
import { useConsultationStore } from '@/stores/useConsultationStore';
import { SidePanelTabs } from './SidePanelTabs';
import { SharePanel, type SharePanelProps } from './SharePanel';
import { InventoryPanel } from './InventoryPanel';
import { MaskPanel } from './MaskPanel';
import { RecordPanel, type RecordPanelProps } from './RecordPanel';

type TabType = 'share' | 'inventory' | 'mask' | 'record';

interface SidePanelProps {
  allowedTabs?: ReadonlyArray<TabType>;
  recordProps?: RecordPanelProps;
  shareProps?: SharePanelProps;
}

const defaultTabs: TabType[] = ['share', 'inventory', 'mask', 'record'];

export function SidePanel({ allowedTabs, recordProps, shareProps }: SidePanelProps) {
  const { activeTab, setActiveTab } = useConsultationStore();
  const visibleTabs = allowedTabs ?? defaultTabs;

  useEffect(() => {
    if (visibleTabs.length === 0) return;
    if (!visibleTabs.includes(activeTab)) {
      setActiveTab(visibleTabs[0]);
    }
  }, [activeTab, setActiveTab, visibleTabs]);

  const renderContent = () => {
    if (!visibleTabs.includes(activeTab)) return null;
    switch (activeTab) {
      case 'share':
        return shareProps ? <SharePanel {...shareProps} /> : null;
      case 'inventory':
        return <InventoryPanel />;
      case 'mask':
        return <MaskPanel />;
      case 'record':
        return <RecordPanel {...recordProps} />;
      default:
        return null;
    }
  };

  return (
    <div className="fixed right-0 top-0 h-screen w-[26rem] bg-gray-900 shadow-2xl flex flex-col">
      <SidePanelTabs allowedTabs={visibleTabs} />
      <div className="flex-1 overflow-y-auto scrollbar-slim">
        {renderContent()}
      </div>
    </div>
  );
}
