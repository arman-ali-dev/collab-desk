import api from "../api";

export const getUsers = async () => {
  const response = await api.get("/api/admin/users/all");
  return response.data;
};

export const searchUsers = async (query) => {
  const response = await api.get("/api/admin/users/search", {
    params: { fullName: query, email: query },
  });
  return response.data;
};
