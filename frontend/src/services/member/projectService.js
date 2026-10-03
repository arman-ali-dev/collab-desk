import api from "../api";

export const getProjects = async () => {
  const response = await api.get("/api/projects/all");
  return response.data;
};

export const searchProjectApi = async (q) => {
  const res = await api.get("/api/projects/search", { params: { keyword: q } });
  return res.data;
};

export const filterProjectApi = async (status, priority) => {
  const res = await api.get("/api/projects/filter", {
    params: { status, priority },
  });
  return res.data;
};
