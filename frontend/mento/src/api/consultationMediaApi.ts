import { api } from "./axios";

export async function uploadConsultationMedia(reservationId: number, files: File[]) {
  const formData = new FormData();
  files.forEach((file) => {
    formData.append("files", file);
  });

  const response = await api.post(`/reservations/${reservationId}/media`, formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });

  return response.data;
}
