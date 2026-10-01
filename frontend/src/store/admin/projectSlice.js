import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { createProjectApi } from "../../services/projectService";
import { addProject } from "../member/projectSlice";

const initialState = {
  createLoading: false,
  createError: null,
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

        if (status >= 400 || status < 500) {
          message = data?.message || "Invalid request";
        }
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
      });
  },
});

export default projectSlice.reducer;
