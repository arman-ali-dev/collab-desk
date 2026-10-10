import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  getMyRemindersApi,
  getMyTasksApi,
  getMyTasksByProjectApi,
  getTasksByYearAndMonthApi,
  updateTaskStatusApi,
} from "../../services/member/taskService";

const initialState = {
  tasks: [],
  loading: false,
  error: null,

  calendarLoading: false,
  calendarTasks: [],

  projectTasks: [],
  kanbanLoading: false,

  reminders: [],
  remindersLoading: false,
};

export const fetchMyTasks = createAsyncThunk(
  "memberTask/fetchMyTasks",
  async (_, { rejectWithValue }) => {
    try {
      const res = await getMyTasksApi();

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

export const updateTaskStatus = createAsyncThunk(
  "memberTask/updateStatus",
  async ({ id, status }, { rejectWithValue, dispatch }) => {
    try {
      const res = await updateTaskStatusApi(id, status);
      dispatch(updateStatus(res));
      dispatch(updateStatusInKanban(res));

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

export const fetchMyTasksByYearAndMonth = createAsyncThunk(
  "memberTask/fetchMyTasksByYearAndMonth",
  async ({ year, month }, { rejectWithValue }) => {
    try {
      const res = await getTasksByYearAndMonthApi(year, month);

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

export const fetchTasksByProject = createAsyncThunk(
  "memberTask/fetchTasksByProject",
  async (projectId, { rejectWithValue }) => {
    try {
      const res = await getMyTasksByProjectApi(projectId);

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

export const fetchReminders = createAsyncThunk(
  "memberTask/fetchReminders",
  async (_, { rejectWithValue }) => {
    try {
      const res = await getMyRemindersApi();

      console.log("Reminders: ", res);

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

const taskSlice = createSlice({
  name: "memberTasks",
  initialState,
  reducers: {
    updateStatus: (state, action) => {
      state.tasks = state.tasks.map((t) =>
        t.id == action.payload.id ? action.payload : t,
      );
    },
    clearTasksProject: (state, action) => {
      state.projectTasks = [];
    },
    updateStatusInKanban: (state, action) => {
      state.projectTasks = state.projectTasks.map((t) =>
        t.id == action.payload.id ? action.payload : t,
      );
    },

    addTasksInProjectTasks: (state, action) => {
      state.projectTasks = [action.payload, ...state.projectTasks];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMyTasks.pending, (state, action) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMyTasks.fulfilled, (state, action) => {
        state.loading = false;
        state.tasks = action.payload;
      })
      .addCase(fetchMyTasks.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      })

      .addCase(fetchMyTasksByYearAndMonth.pending, (state, action) => {
        state.calendarLoading = true;
      })
      .addCase(fetchMyTasksByYearAndMonth.fulfilled, (state, action) => {
        state.calendarLoading = false;
        state.calendarTasks = action.payload;
      })
      .addCase(fetchMyTasksByYearAndMonth.rejected, (state, action) => {
        state.calendarLoading = false;
      })

      .addCase(fetchTasksByProject.pending, (state, action) => {
        state.kanbanLoading = true;
      })
      .addCase(fetchTasksByProject.fulfilled, (state, action) => {
        state.kanbanLoading = false;
        state.projectTasks = action.payload;
      })
      .addCase(fetchTasksByProject.rejected, (state, action) => {
        state.kanbanLoading = false;
      })
      .addCase(fetchReminders.pending, (state, action) => {
        state.remindersLoading = true;
      })
      .addCase(fetchReminders.fulfilled, (state, action) => {
        state.remindersLoading = false;
        state.reminders = action.payload;
      })
      .addCase(fetchReminders.rejected, (state, action) => {
        state.remindersLoading = false;
      });
  },
});

export const {
  updateStatus,
  clearTasksProject,
  updateStatusInKanban,
  addTasksInProjectTasks,
} = taskSlice.actions;
export default taskSlice.reducer;
