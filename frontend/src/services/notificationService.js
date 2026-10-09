import api from "./api";

export const getNotifications = async () => {
  const res = await api.get("/api/notifications");
  return res.data;
};

export const markAllNotificationsReadApi = async () => {
  const res = await api.patch("/api/notifications/read-all");
};
