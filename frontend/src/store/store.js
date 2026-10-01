import { configureStore, combineReducers } from "@reduxjs/toolkit";
import {
  persistStore,
  persistReducer,
  FLUSH,
  REHYDRATE,
  PAUSE,
  PERSIST,
  PURGE,
  REGISTER,
} from "redux-persist";
import storageModule from "redux-persist/lib/storage";
import authReducer from "./authSlice";
import projectReducer from "./member/projectSlice";
import adminUserReducer from "./admin/userSlice";
import adminProjectReducer from "./admin/projectSlice";

const storage = storageModule.default;

const rootReducer = combineReducers({
  auth: authReducer,
  memberProjects: projectReducer,
  adminUsers: adminUserReducer,
  adminProjects: adminProjectReducer,
});

const persistConfig = {
  key: "root",
  storage,
  whitelist: ["auth"],
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
  reducer: persistedReducer,
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [FLUSH, REHYDRATE, PAUSE, PERSIST, PURGE, REGISTER],
      },
    }),
});

export const persistor = persistStore(store);
