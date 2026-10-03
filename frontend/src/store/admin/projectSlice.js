import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import {
  createProjectApi,
  deleteProjectApi,
  updateProjectApi,
} from "../../services/admin/projectService";
import { addProject, editProject, removeProject } from "../member/projectSlice";

const initialState = {
  createLoading: false,
  createError: null,

  updateLoading: false,
  updateError: null,

  deleteProjectId: null,
};

export const createProject = createAsyncThunk(
  "adminProject/create",
  async (credentials, { rejectWithValue, dispatch }) => {
    try {
      const res = await createProjectApi(credentials);
      dispatch(addProject(res));
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

export const updateProject = createAsyncThunk(
  "adminProject/update",
  async ({ id, data }, { rejectWithValue, dispatch }) => {
    try {
      const res = await updateProjectApi(id, data);

      console.log(res);

      dispatch(editProject(res));

      return res;
    } catch (err) {
      console.log(err);

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

export const deleteProject = createAsyncThunk(
  "adminProject/delete",
  async (id, { rejectWithValue, dispatch }) => {
    try {
      await deleteProjectApi(id);
      dispatch(removeProject(id));

      return id;
    } catch (err) {
      console.log(err);

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
  name: "adminProjects",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(createProject.pending, (state, action) => {
        state.createLoading = true;
        state.createError = null;
      })
      .addCase(createProject.fulfilled, (state, action) => {
        state.createLoading = false;
      })
      .addCase(createProject.rejected, (state, action) => {
        state.createLoading = false;
        state.createError = action.payload || "Something went wrong";
      })
      .addCase(updateProject.pending, (state, action) => {
        state.updateLoading = true;
        state.updateError = null;
      })
      .addCase(updateProject.fulfilled, (state, action) => {
        state.updateLoading = false;
      })
      .addCase(updateProject.rejected, (state, action) => {
        state.updateLoading = false;
        state.updateError = action.payload || "Something went wrong";
      })
      .addCase(deleteProject.pending, (state, action) => {
        state.deleteProjectId = action.meta.arg;
      })
      .addCase(deleteProject.fulfilled, (state, action) => {
        state.deleteProjectId = null;
      })
      .addCase(deleteProject.rejected, (state, action) => {
        state.updateLoading = null;
      });
  },
});

export default projectSlice.reducer;
