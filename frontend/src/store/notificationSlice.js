import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import {
  getNotifications,
  markAllNotificationsReadApi,
} from "../services/notificationService";

const initialState = {
  notifications: [],
  loading: false,
  error: null,
};

export const fetchNotifications = createAsyncThunk(
  "notification/fetch",
  async (_, { rejectWithValue }) => {
    try {
      const res = await getNotifications();

      console.log("Notifications: ", res);

      return res;
    } catch (err) {
      console.log("Notifications Error: ", err);

      return rejectWithValue(err.response?.data?.message || "failed");
    }
  },
);

export const markAllNotificationsRead = createAsyncThunk(
  "notification/markAllRead",
  async (_, { rejectWithValue }) => {
    try {
      markAllNotificationsReadApi();
    } catch (err) {
      console.log("Notifications Mark Read All Error: ", err);

      return rejectWithValue(err.response?.data?.message || "failed");
    }
  },
);

const notificationSlice = createSlice({
  name: "notification",
  initialState,
  reducers: {
    addNotification: (state, action) => {
      const n = action.payload;
      const i = state.notifications.findIndex((x) => x.id === n.id);

      if (i >= 0) {
        state.notifications[i] = n;
      } else {
        state.notifications.unshift(n);
      }
    },
    clearNotifications(state) {
      state.notifications = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchNotifications.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchNotifications.fulfilled, (state, action) => {
        state.loading = false;
        state.notifications = action.payload;
      })
      .addCase(fetchNotifications.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      })
      .addCase(markAllNotificationsRead.fulfilled, (state, action) => {
        state.notifications = state.notifications.map((n) => (n.readAt = true));
      });
  },
});

export const { addNotification, clearNotifications } =
  notificationSlice.actions;
export default notificationSlice.reducer;
