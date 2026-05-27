import axios from "axios";

const apiBaseUrl =
  process.env.NODE_ENV === "development"
    ? process.env.REACT_APP_API_URL
    : "/api";

const apiClient = axios.create({
  baseURL: apiBaseUrl,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem("token");

      window.location.href = "/login";
    }

    return Promise.reject(error);
  },
);

export default apiClient;
