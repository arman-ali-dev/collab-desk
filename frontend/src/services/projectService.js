import api from "./api";

export const getProjects = async () => {
  const response = await api.get("/api/projects/all");
  return response.data;
};

export const createProjectApi = async (data) => {
  const response = await api.post("/api/admin/projects", data);
  return response.data;
};
