import api from "../api";

export const createProjectApi = async (data) => {
  const response = await api.post("/api/admin/projects", data);
  return response.data;
};

export const updateProjectApi = async (id, data) => {
  const response = await api.put(`/api/admin/projects/${id}`, data);
  return response.data;
};

export const deleteProjectApi = async (id) => {
  await api.delete(`/api/admin/projects/${id}`);
};
