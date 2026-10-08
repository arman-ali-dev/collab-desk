import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import { getAllMessages } from "../services/chatService";

const initialState = {
  connected: false,
  messages: [],
};

export const fetchMessages = createAsyncThunk(
  "messages/fetch",
  async (id, { rejectWithValue }) => {
    try {
      const res = await getAllMessages(id);

      console.log("messages ", res);
      return res;
    } catch (err) {
      console.log(err);

      return rejectWithValue(err.response?.data?.message || "failed");
    }
  },
);

const chatSlice = createSlice({
  name: "chat",
  initialState,
  reducers: {
    setConnected: (state, action) => {
      state.connected = action.payload;
    },
    clearMessages: (state) => {
      state.messages = [];
    },
    upsertMessage: (state, action) => {
      const m = action.payload;
      const i = state.messages.findIndex((x) => x.id === m.id);
      if (i >= 0) state.messages[i] = m;
      else state.messages.push(m);
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchMessages.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchMessages.fulfilled, (state, action) => {
        state.loading = false;
        state.messages = action.payload;
      })
      .addCase(fetchMessages.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      });
  },
});

export const { clearMessages, setConnected, upsertMessage } = chatSlice.actions;
export default chatSlice.reducer;
