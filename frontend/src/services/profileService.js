import api from "./api";

export const getProfile = async () => {
  const res = await api.get("/api/users/me");
  return res.data;
};

export const editProfileApi = async (data) => {
  const res = await api.put("/api/users/me/edit", data);
  return res.data;
};
