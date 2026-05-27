const backendBaseUrl =
  process.env.NODE_ENV === "development" ? process.env.REACT_APP_API_URL : "";

export const getImageUrl = (path) => {
  if (!path) return null;

  return `${backendBaseUrl}${path}`;
};
