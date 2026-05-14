import React from "react";
import { Routes, Route, Navigate, BrowserRouter } from "react-router-dom";
import HomePage from "./pages/MoviesPage";
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
import ReviewDetailPage from "./pages/ReviewDetailPage";
import CollectionsPage from "./pages/CollectionsPage";
import MainPage from "./pages/MainPage";
import ReviewsPage from "./pages/ReviewsPage";
import UsersPage from "./pages/UsersPage";

function PrivateRoute({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/movies" element={<HomePage />} />

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
        <Route
          path="/movies/:movieId/write-review"
          element={<WriteReviewPage />}
        />
        <Route path="/reviews/edit/:reviewId" element={<WriteReviewPage />} />
        <Route path="/reviews/:id" element={<ReviewDetailPage />} />
        <Route path="/collections" element={<CollectionsPage />} />
        <Route path="/" element={<MainPage />} />
        <Route path="/reviews" element={<ReviewsPage />} />
        <Route path="/searchuser" element={<UsersPage />} />
        <Route path="/edit-profile/:id" element={<EditProfile />} />

      </Routes>
    </BrowserRouter>
  );
}

export default App;
