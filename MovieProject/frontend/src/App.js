import React from "react";
import {
  BrowserRouter as Router,
  Routes,
  Route,
  Navigate,
} from "react-router-dom";
import HomePage from "./pages/HomePage";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import ProfilePage from "./pages/ProfilePage";
import EditProfile from "./pages/EditProfile";
import MoviePage from "./pages/MoviePage";
import CreateCompilationPage from "./pages/CreateCompilationPage";
import CompilationPage from "./pages/CompilationPage";
import EditCompilation from "./pages/EditCompilation";
function PrivateRoute({ children }) {
  const token = localStorage.getItem("token");
  return token ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <Router>
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

        
        <Route path="/movies/:id" element={<MoviePage />} />
        <Route path="/create-compilation" element={<CreateCompilationPage />} />
        <Route path="/compilations/:id" element={<CompilationPage />} />
        <Route path="/compilations/:id/edit" element={<EditCompilation />} />
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </Router>
  );
}

export default App;
