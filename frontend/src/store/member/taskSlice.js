import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  getMyTasksApi,
  getTasksByYearAndMonthApi,
  updateTaskStatusApi,
} from "../../services/member/taskService";

const initialState = {
  tasks: [],
  loading: false,
  error: null,

  calendarLoading: false,
  calendarTasks: [],
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

      return res;
    } catch (err) {
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

      console.log("Calendar: ", res);

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
      });
  },
});

export const { updateStatus } = taskSlice.actions;
export default taskSlice.reducer;
