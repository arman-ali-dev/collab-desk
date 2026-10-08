import api from "./api";

export const getAllChatRooms = async () => {
  const res = await api.get("/api/chat/rooms");
  return res.data;
};

export const getAllMessages = async (id) => {
  const res = await api.get(`/api/messages/chat/rooms/${id}`);
  return res.data;
};
