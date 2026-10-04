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

export const createUserApi = async (data) => {
  const response = await api.post("/api/admin/users/create", data);
  return response.data;
};

export const filterUsersApi = async (status) => {
  console.log("status: ", status);

  const res = await api.get("/api/admin/users/filter", {
    params: { status },
  });
  return res.data;
};
