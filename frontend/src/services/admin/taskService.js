import api from "../api";

export const createTaskApi = async (data) => {
  const response = await api.post("/api/admin/tasks", data);
  return response.data;
};

export const getTasksApi = async () => {
  const response = await api.get("/api/admin/tasks/all");
  return response.data;
};

export const updateMembersInTaskApi = async (taskId, assignedTo) => {
  const response = await api.patch(
    `/api/admin/tasks/members/update/${taskId}`,
    assignedTo,
  );
  return response.data;
};
