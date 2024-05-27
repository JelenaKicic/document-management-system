import { configureStore } from "@reduxjs/toolkit";
import DocumentsSlice from "../features/documents/DocumentsSlice";

export const store = configureStore({
  reducer: {
    documents: DocumentsSlice,
  },
});
