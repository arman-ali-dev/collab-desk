import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { getAllChatRooms } from "../services/chatService";

const initialState = {
  chatRooms: [],
  loading: false,
  error: null,
};

export const fetchChatRooms = createAsyncThunk(
  "chat/fetch",
  async (_, { rejectWithValue }) => {
    try {
      const res = await getAllChatRooms();

      console.log("chat rooms", res);
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
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchChatRooms.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchChatRooms.fulfilled, (state, action) => {
        state.loading = false;
        state.chatRooms = action.payload;
      })
      .addCase(fetchChatRooms.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      });
  },
});

export default chatSlice.reducer;
