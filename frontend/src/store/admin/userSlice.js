import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  createUserApi,
  deleteUserApi,
  filterUsersApi,
  getUsers,
  searchUsers,
} from "../../services/admin/userService";

const initialState = {
  users: [],
  loading: false,
  error: null,

  searchResults: [],
  searchLoading: false,
  searchError: null,

  createLoading: false,

  deleteUserId: null,
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

export const deleteUser = createAsyncThunk(
  "adminUser/delete",
  async (id, { rejectWithValue }) => {
    try {
      await deleteUserApi(id);

      return id;
    } catch (err) {
      return rejectWithValue(err.response?.data?.message || "Search failed");
    }
  },
);

export const createUser = createAsyncThunk(
  "adminUser/create",
  async (credentials, { rejectWithValue }) => {
    try {
      const res = await createUserApi(credentials);
      console.log("User creaeted ", res);

      return res;
    } catch (err) {
      console.log(err);

      let message = "Unexpected error occurred";

      if (err.response) {
        const status = err.response.status;
        const serverMsg = err.response.data?.message;

        if (status == 409) {
          message = "User already exists!";
        } else if (status >= 400 && status < 500) {
          message = "Invalid Request";
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

export const filterUsers = createAsyncThunk(
  "adminUsers/filter",
  async (q, { rejectWithValue }) => {
    try {
      const res = await filterUsersApi(q);
      console.log("filter results: ", res);

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
      })

      .addCase(createUser.pending, (state, action) => {
        state.createLoading = true;
      })
      .addCase(createUser.fulfilled, (state, action) => {
        state.createLoading = false;
        state.users = [action.payload, ...state.users];
      })
      .addCase(createUser.rejected, (state, action) => {
        state.createLoading = false;
      })

      .addCase(filterUsers.pending, (state, action) => {
        state.loading = true;
      })
      .addCase(filterUsers.fulfilled, (state, action) => {
        state.loading = false;
        state.users = action.payload;
      })
      .addCase(filterUsers.rejected, (state, action) => {
        state.loading = false;
      })

      .addCase(deleteUser.pending, (state, action) => {
        state.deleteUserId = action.meta.arg;
      })
      .addCase(deleteUser.fulfilled, (state, action) => {
        state.deleteUserId = null;
        state.users = state.users.filter((u) => u.id !== action.payload);
      })
      .addCase(deleteUser.rejected, (state, action) => {
        state.deleteUserId = null;
      });
  },
});

export const { clearSearchResults } = userSlice.actions;
export default userSlice.reducer;
