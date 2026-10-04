import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  createTaskApi,
  getTasksApi,
  updateMembersInTaskApi,
} from "../../services/admin/taskService";

const initialState = {
  tasks: [],
  loading: false,
  error: null,

  createLoading: false,

  updateMemberLoading: false,
};

export const fetchTasks = createAsyncThunk(
  "adminTask/fetchAll",
  async (_, { rejectWithValue }) => {
    try {
      const res = await getTasksApi();

      console.log("Task Response: ", res);

      return res;
    } catch (err) {
      let message = "Unexpected error occurred";

      if (err.response) {
        const status = err.response.status;
        const serverMsg = err.response.data?.message;

        if (status >= 500) {
          message = "Server error, please try later";
        } else {
          message = serverMsg || "Something went wrong";
        }
      } else if (err.request) {
        message = "Cannot reach server. Check your internet or try later";
      }

      return rejectWithValue(message);
    }
  },
);

export const createTask = createAsyncThunk(
  "adminTasks/create",
  async (credentials, { rejectWithValue, dispatch }) => {
    try {
      const res = await createTaskApi(credentials);
      return res;
    } catch (err) {
      console.log(err);

      let message = "Unexpected error occurred";

      if (err.response) {
        const status = err.response.status;
        const serverMsg = err.response.data?.message;

        if (status >= 400 && status < 500) {
          message = serverMsg || "Invalid request";
        } else if (status >= 500) {
          message = "Server error, please try later";
        } else {
          message = serverMsg || "Something went wrong";
        }
      } else if (err.request) {
        message = "Cannot reach server. Check your internet or try later";
      }

      return rejectWithValue(message);
    }
  },
);

export const updateMembersInTask = createAsyncThunk(
  "adminTasks/updateMembers",
  async ({ id, assignedTo }, { rejectWithValue, dispatch }) => {
    try {
      const res = await updateMembersInTaskApi(id, { assignedTo });
      return res;
    } catch (err) {
      console.log(err);

      let message = "Unexpected error occurred";

      if (err.response) {
        const status = err.response.status;
        const serverMsg = err.response.data?.message;

        if (status >= 400 && status < 500) {
          message = serverMsg || "Invalid request";
        } else if (status >= 500) {
          message = "Server error, please try later";
        } else {
          message = serverMsg || "Something went wrong";
        }
      } else if (err.request) {
        message = "Cannot reach server. Check your internet or try later";
      }

      return rejectWithValue(message);
    }
  },
);

const taskSlice = createSlice({
  name: "adminTasks",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchTasks.pending, (state, action) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchTasks.fulfilled, (state, action) => {
        state.loading = false;
        state.tasks = action.payload;
      })
      .addCase(fetchTasks.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      })

      .addCase(createTask.pending, (state, action) => {
        state.createLoading = true;
      })
      .addCase(createTask.fulfilled, (state, action) => {
        state.createLoading = false;
        state.tasks = [action.payload, ...state.tasks];
      })
      .addCase(createTask.rejected, (state, action) => {
        state.createLoading = false;
      })

      .addCase(updateMembersInTask.pending, (state, action) => {
        state.updateMemberLoading = true;
      })
      .addCase(updateMembersInTask.fulfilled, (state, action) => {
        state.updateMemberLoading = false;
        state.tasks = state.tasks.map((t) =>
          t.id == action.payload.id ? action.payload : t,
        );
      })
      .addCase(updateMembersInTask.rejected, (state, action) => {
        state.updateMemberLoading = false;
      });
  },
});

export default taskSlice.reducer;
