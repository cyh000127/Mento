import { create } from "zustand";

interface RecordingState {
  isRecording: boolean;
  egressId: string | null;
  startRecording: (egressId: string) => void;
  stopRecording: () => void;
}

export const useRecordingStore = create<RecordingState>((set) => ({
  isRecording: false,
  egressId: null,
  startRecording: (egressId) => set({ isRecording: true, egressId }),
  stopRecording: () => set({ isRecording: false, egressId: null }),
}));
