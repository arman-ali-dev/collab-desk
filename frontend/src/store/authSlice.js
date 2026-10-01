import {
  createSlice,
  createAsyncThunk,
  isRejectedWithValue,
} from "@reduxjs/toolkit";
import { loginUser } from "../services/authService";

const initialState = {
  user: null,
  token: null,
  isAuthenticated: false,
};

export const login = createAsyncThunk(
  "auth/login",
  async (credentials, { rejectWithValue }) => {
    try {
      const res = await loginUser(credentials);
      console.log(res);

      return res;
    } catch (err) {
      let message = "Unexpected error occurred";

      if (err.response) {
        const status = err.response.status;
        const serverMsg = err.response.data?.message;

        if (status === 400 || status === 401) {
          message = serverMsg || "Invalid email or password";
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

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    logout: (state) => {
      state.user = null;
      state.token = null;
      state.isAuthenticated = false;
    },
  },
  extraReducers: (builder) => {
    builder.addCase(login.fulfilled, (state, action) => {
      state.user = action.payload.user;
      state.token = action.payload.token;
      state.isAuthenticated = true;
    });
  },
});

export const { logout } = authSlice.actions;
export default authSlice.reducer;
