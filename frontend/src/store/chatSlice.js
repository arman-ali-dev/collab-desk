import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  connected: false,
  messages: [],
};

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
});

export const { clearMessages, setConnected, upsertMessage } = chatSlice.actions;
export default chatSlice.reducer;
