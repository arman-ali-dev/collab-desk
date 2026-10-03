import api from "../api";

export const getMyTasksApi = async () => {
  const response = await api.get("/api/tasks/my");
  return response.data;
};

export const updateTaskStatusApi = async (id, status) => {
  const response = await api.patch(`/api/tasks/${id}/status`, null, {
    params: { status },
  });
  return response.data;
};

export const getTasksByYearAndMonthApi = async (year, month) => {
  const response = await api.get("/api/tasks/calender/my", {
    params: { year, month },
  });

  return response.data;
};

export const getMyTasksByProjectApi = async (projectId) => {
  const response = await api.get(`/api/tasks/project/${projectId}`);
  return response.data;
};
