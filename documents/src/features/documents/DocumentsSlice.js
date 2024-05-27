import { createSlice, createAsyncThunk } from "@reduxjs/toolkit";
import { configure } from "@testing-library/react";
import axios from "axios";
import confiig from "../../config/config.json";
import authHeader from "./AuthHeader";

const initialState = {
  files: {},
  logs: [],
  error: "",
};

export const uploadFile = createAsyncThunk(
  "documents/uploadFile",
  async (data) => {
    const response = await axios.post(
      `${confiig.api}/files/upload`,
      data.formData,
      {
        headers: {
          ...authHeader(),
          path: data.path,
        },
      }
    );
    return response.data;
  }
);

export const updateFile = createAsyncThunk(
  "documents/updateFile",
  async (data) => {
    const response = await axios.put(
      `${confiig.api}/files/update`,
      data.formData,
      {
        headers: {
          ...authHeader(),
          path: data.path,
        },
      }
    );
    return response.data;
  }
);

export const createDirectory = createAsyncThunk(
  "documents/createDirectory",
  async (data) => {
    const response = await axios.post(
      `${confiig.api}/directories/create`,
      {
        path: data.path,
        name: data.name,
      },
      {
        headers: {
          ...authHeader(),
        },
      }
    );
    return response.data;
  }
);

export const moveFile = createAsyncThunk("documents/moveFile", async (data) => {
  const response = await axios.post(
    `${confiig.api}/files/move`,
    {
      path: data.path,
      newPath: data.newPath,
    },
    {
      headers: {
        ...authHeader(),
      },
    }
  );
  return response.data;
});

export const deleteDirectory = createAsyncThunk(
  "documents/deleteDirectory",
  async (data) => {
    const response = await axios.delete(`${confiig.api}/directories/delete`, {
      headers: {
        ...authHeader(),
        path: data.path,
      },
    });
    return response.data;
  }
);

export const deleteFile = createAsyncThunk(
  "documents/deleteFile",
  async (path) => {
    const response = await axios.delete(
      `${confiig.api}/files/delete?filePath=${path}`,
      {
        headers: authHeader(),
      }
    );
    return response.data;
  }
);

export const downloadFile = createAsyncThunk(
  "documents/downloadFile",
  async (data) => {
    const response = await axios.get(
      `${confiig.api}/files/download?filePath=${data.path}`,
      {
        headers: authHeader(),
      }
    );
    return response.data;
  }
);

export const fetchFiles = createAsyncThunk("documents/fetchFiles", async () => {
  const response = await axios.get(`${confiig.api}/directories`, {
    headers: authHeader(),
  });
  return response.data;
});

export const fetchLogs = createAsyncThunk("documents/fetchLogs", async () => {
  const response = await axios.get(`${confiig.api}/actions`, {
    headers: authHeader(),
  });
  return response.data;
});

export const usersSlice = createSlice({
  name: "documents",
  initialState,
  extraReducers(builder) {
    builder
      // .addCase(fetchUsers.pending, (state, action) => {})
      .addCase(deleteFile.rejected, (state, action) => {
        state.error = action.error.message;
      })
      .addCase(uploadFile.rejected, (state, action) => {
        state.error = action.error.message;
      })
      .addCase(createDirectory.rejected, (state, action) => {
        state.error = action.error.message;
      })
      .addCase(deleteDirectory.rejected, (state, action) => {
        state.error = action.error.message;
      })
      .addCase(updateFile.rejected, (state, action) => {
        state.error = action.error.message;
      })
      .addCase(downloadFile.rejected, (state, action) => {
        state.error = action.error.message;
      })
      .addCase(moveFile.rejected, (state, action) => {
        state.error = action.error.message;
      })
      .addCase(fetchFiles.fulfilled, (state, action) => {
        state.files = action.payload;
      })
      .addCase(fetchLogs.fulfilled, (state, action) => {
        state.logs = action.payload;
      });
  },
});

export default usersSlice.reducer;

export const selectFiles = (state) => state.documents.files;
export const selectLogs = (state) => state.documents.logs;

export const selectError = (state) => state.documents.error;
