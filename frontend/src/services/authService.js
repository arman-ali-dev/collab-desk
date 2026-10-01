import api from "./api";

export const loginUser = async (data) => {
  const response = await api.post("/auth/signin", data);
  return response.data;
};
