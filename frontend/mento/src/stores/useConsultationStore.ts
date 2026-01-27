import { create } from 'zustand';

type TabType = 'share' | 'inventory' | 'mask' | 'record';
type MaskAreaType = 'T-zone' | 'U-zone' | 'Nose zone' | 'Apple zone' | null;

interface ConsultationState {
  activeTab: TabType;
  selectedMaskArea: MaskAreaType;
  setActiveTab: (tab: TabType) => void;
  setSelectedMaskArea: (area: MaskAreaType) => void;
}

export const useConsultationStore = create<ConsultationState>((set) => ({
  activeTab: 'share',
  selectedMaskArea: null,
  setActiveTab: (tab) => set({ activeTab: tab }),
  setSelectedMaskArea: (area) => set({ selectedMaskArea: area }),
}));
