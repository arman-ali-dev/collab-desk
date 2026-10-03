import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { editProfileApi, getProfile } from "../services/profileService";

const initialState = {
  profile: null,
  loading: false,
  error: null,

  updateLoading: false,
};

export const fetchProfile = createAsyncThunk(
  "profile/fetch",
  async (_, { rejectWithValue }) => {
    try {
      const res = await getProfile();

      console.log("profile res", res);

      return res;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || "failed");
    }
  },
);

export const editProfile = createAsyncThunk(
  "profile/edit",
  async (credentials, { rejectWithValue }) => {
    try {
      const res = await editProfileApi(credentials);

      console.log("edit profile res", res);

      return res;
    } catch (err) {
      let message = "Unexpected error occurred";

      if (err.response) {
        const status = err.response.status;
        const serverMsg = err.response.data?.message;

        if (status === 400 || status === 401) {
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

const profileSlice = createSlice({
  name: "profile",
  initialState,
  reducers: {
    clearProfile: (state) => {
      state.profile = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(fetchProfile.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchProfile.fulfilled, (state, action) => {
        state.loading = false;
        state.profile = action.payload;
      })
      .addCase(fetchProfile.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      })
      .addCase(editProfile.pending, (state) => {
        state.updateLoading = true;
      })
      .addCase(editProfile.fulfilled, (state, action) => {
        state.updateLoading = false;
        state.profile = action.payload;
      })
      .addCase(editProfile.rejected, (state, action) => {
        state.updateLoading = false;
      });
  },
});

export const { clearProfile } = profileSlice.actions;
export default profileSlice.reducer;
