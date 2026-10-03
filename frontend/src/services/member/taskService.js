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
