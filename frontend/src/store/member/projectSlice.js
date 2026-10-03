import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  filterProjectApi,
  getProjectApi,
  getProjects,
  searchProjectApi,
} from "../../services/member/projectService";

const initialState = {
  projects: [],
  loading: false,
  error: null,

  searchResults: [],
  searchLoading: false,
  searchError: null,

  project: null,
  getLoading: false,
};

export const getAllProjects = createAsyncThunk(
  "project/fetchAll",
  async (_, { rejectWithValue }) => {
    try {
      const res = await getProjects();

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

export const searchProjects = createAsyncThunk(
  "project/search",
  async (q, { rejectWithValue }) => {
    try {
      const res = await searchProjectApi(q);

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

export const filterProjects = createAsyncThunk(
  "project/filter",
  async (q, { rejectWithValue }) => {
    try {
      const res = await filterProjectApi(q.status, q.priority);
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

export const getProject = createAsyncThunk(
  "project/get",
  async (id, { rejectWithValue }) => {
    try {
      const res = await getProjectApi(id);
      console.log("Get: ", res);

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

const projectSlice = createSlice({
  name: "project",
  initialState,
  reducers: {
    addProject: (state, action) => {
      state.projects = [action.payload, ...state.projects];
    },
    editProject: (state, action) => {
      state.projects = state.projects.map((p) =>
        p.id == action.payload.id ? action.payload : p,
      );
    },
    removeProject: (state, action) => {
      state.projects = state.projects.filter((p) => p.id != action.payload);
    },

    clearSearchResults: (state, _) => {
      state.searchResults = [];
    },

    clearProject: (state, _) => {
      state.project = null;
    },
  },
  extraReducers: (builder) => {
    builder
      .addCase(getAllProjects.pending, (state, action) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getAllProjects.fulfilled, (state, action) => {
        state.loading = false;
        state.projects = action.payload;
      })
      .addCase(getAllProjects.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      })
      .addCase(searchProjects.pending, (state, action) => {
        state.searchLoading = true;
        state.searchError = null;
      })
      .addCase(searchProjects.fulfilled, (state, action) => {
        state.searchLoading = false;
        state.searchResults = action.payload;
      })
      .addCase(searchProjects.rejected, (state, action) => {
        state.searchLoading = false;
        state.searchError = action.payload || "Something went wrong";
      })
      .addCase(filterProjects.pending, (state, action) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(filterProjects.fulfilled, (state, action) => {
        state.loading = false;
        state.projects = action.payload;
      })
      .addCase(filterProjects.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload || "Something went wrong";
      })
      .addCase(getProject.pending, (state, action) => {
        state.getLoading = true;
      })
      .addCase(getProject.fulfilled, (state, action) => {
        state.getLoading = false;
        state.project = action.payload;
      })
      .addCase(getProject.rejected, (state, action) => {
        state.getLoading = false;
      });
  },
});

export const {
  addProject,
  editProject,
  removeProject,
  clearSearchResults,
  clearProject,
} = projectSlice.actions;
export default projectSlice.reducer;
