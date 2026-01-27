import { useConsultationStore } from '@/stores/useConsultationStore';
import { SidePanelTabs } from './SidePanelTabs';
import { SharePanel } from './SharePanel';
import { InventoryPanel } from './InventoryPanel';
import { MaskPanel } from './MaskPanel';
import { RecordPanel } from './RecordPanel';

export function SidePanel() {
  const { activeTab } = useConsultationStore();

  const renderContent = () => {
    switch (activeTab) {
      case 'share':
        return <SharePanel />;
      case 'inventory':
        return <InventoryPanel />;
      case 'mask':
        return <MaskPanel />;
      case 'record':
        return <RecordPanel />;
      default:
        return null;
    }
  };

  return (
    <div className="fixed right-0 top-0 h-screen w-96 bg-gray-900 shadow-2xl flex flex-col">
      <SidePanelTabs />
      <div className="flex-1 overflow-y-auto">
        {renderContent()}
      </div>
    </div>
  );
}
