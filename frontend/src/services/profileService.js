import api from "./api";

export const getProfile = async () => {
  const res = await api.get("/api/users/me");
  return res.data;
};
