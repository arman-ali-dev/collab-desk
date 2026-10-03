import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { getUsers, searchUsers } from "../../services/admin/userService";

const initialState = {
  users: [],
  loading: false,
  error: null,

  searchResults: [],
  searchLoading: false,
  searchError: null,
};

export const getAllUsers = createAsyncThunk(
  "adminUser/fetchAll",
  async (_, { rejectWithValue }) => {
    try {
      const res = await getUsers();
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

export const searchUser = createAsyncThunk(
  "adminUser/search",
  async (query, { rejectWithValue }) => {
    try {
      const res = await searchUsers(query);

      return res;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || "Search failed");
    }
  },
);

const userSlice = createSlice({
  name: "adminUsers",
  initialState,
  reducers: {
    clearSearchResults: (state) => {
      state.searchResults = [];
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(getAllUsers.pending, (state, action) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getAllUsers.fulfilled, (state, action) => {
        state.loading = false;
        state.users = action.payload;
      })
      .addCase(getAllUsers.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      })

      // Search

      .addCase(searchUser.pending, (state, action) => {
        state.searchLoading = true;
        state.searchError = null;
      })
      .addCase(searchUser.fulfilled, (state, action) => {
        state.searchLoading = false;
        state.searchResults = action.payload;
      })
      .addCase(searchUser.rejected, (state, action) => {
        state.searchLoading = false;
        state.searchLoading = action.payload || "Something went wrong";
      });
  },
});

export const { clearSearchResults } = userSlice.actions;
export default userSlice.reducer;
