import axios from "axios";

let store;
export const injectStore = (_store) => {
  store = _store;
};

const api = axios.create({
  baseURL: import.meta.env.VITE_BASE_URL,
  headers: { "Content-Type": "application/json" },
  timeout: 10000,
});

const PUBLIC_ROUTES = ["/auth/login"];

api.interceptors.request.use((config) => {
  const isPublic = PUBLIC_ROUTES.some((route) => config.url?.includes(route));
  const token = localStorage.getItem("token");

  if (token && !isPublic) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const isPublic = PUBLIC_ROUTES.some((route) =>
      error.config?.url?.includes(route),
    );

    if (
      error.response?.status === 401 &&
      !isPublic &&
      localStorage.getItem("token")
    ) {
      localStorage.removeItem("token");
      window.location.href = "/signin";
    }

    return Promise.reject(error);
  },
);

export default api;
