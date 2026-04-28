import React from "react";
import { Routes, Route, Navigate, BrowserRouter } from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ProfilePage from "./pages/ProfilePage";
import EditProfile from "./pages/EditProfile";
import MoviePage from "./pages/MoviePage";
import CreateCompilationPage from "./pages/CreateCompilationPage";
import CompilationPage from "./pages/CompilationPage";
import EditCompilation from "./pages/EditCompilation";
import FollowsPage from "./pages/FollowsPage";
import WriteReviewPage from "./pages/WriteReviewPage";


function PrivateRoute({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomePage />} />

        <Route path="/login" element={<LoginPage />} />

        <Route path="/register" element={<RegisterPage />} />
        <Route path="/edit-profile" element={<EditProfile />} />

        <Route
          path="/profile"
          element={
            <PrivateRoute>
              <ProfilePage />
            </PrivateRoute>
          }
        />
        <Route path="/users/:username" element={<ProfilePage />} />

        <Route path="/movies/:id" element={<MoviePage />} />
        <Route path="/create-compilation" element={<CreateCompilationPage />} />
        <Route path="/compilations/:id" element={<CompilationPage />} />
        <Route path="/compilations/:id/edit" element={<EditCompilation />} />
        <Route path="*" element={<Navigate to="/" />} />

        <Route path="/users/:username/followers" element={<FollowsPage />} />
<Route path="/users/:username/followings" element={<FollowsPage />} />
{/* Создание рецензии для конкретного фильма */}
<Route path="/movies/:movieId/write-review" element={<WriteReviewPage />} />
{/* Редактирование существующей рецензии */}
<Route path="/reviews/edit/:reviewId" element={<WriteReviewPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
