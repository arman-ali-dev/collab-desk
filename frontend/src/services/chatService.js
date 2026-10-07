import api from "./api";

export const getAllChatRooms = async () => {
  const res = await api.get("/api/chat-rooms");
  return res.data;
};
