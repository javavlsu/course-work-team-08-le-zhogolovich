import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";

import avatarDefault from "../images/такса.svg"; // дефолтный аватар
const API_BASE_URL = "http://localhost:8080/movie-project/backend"

function ProfilePage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token")?.trim();

    if (!token) {
      console.warn("Token not found, redirecting to login");
      navigate("/login");
      return;
    }

    const fetchUser = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/movie-project/users/me",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        console.log("User data:", response.data);
        setUser(response.data);
      } catch (error) {
        console.error("Ошибка загрузки профиля", error);
        setErrorMsg("Не удалось загрузить профиль. Попробуйте войти снова.");
        localStorage.removeItem("token"); 
        navigate("/login"); 
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, [navigate]);

  if (loading) {
    return <div className="text-white text-center mt-5">Загрузка...</div>;
  }

  if (errorMsg) {
    return <div className="text-danger text-center mt-5">{errorMsg}</div>;
  }

  return (
    <div className="container-wrapper">
      {/* Меню */}
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
          <Link to="/" className="nav-btn">Главная</Link>
          <Link to="/movies" className="nav-btn">Фильмы</Link>
          <Link to="/collections" className="nav-btn">Подборки</Link>
          <Link to="/reviews" className="nav-btn">Рецензии</Link>
          <Link to="/profile" className="nav-btn">Моя страница</Link>
        </nav>
      </header>

      <main className="container-xl px-4 px-md-5 mt-5">
        <div style={{ maxWidth: "1100px", margin: "0 auto" }}>
         
          <div className="row align-items-start mb-5 g-0">
            <div className="col-auto d-flex flex-column align-items-center">
              <div
                className="rounded-circle overflow-hidden mb-4 shadow"
                style={{ width: "220px", height: "220px" }}
              >
                <img
  src={user.avatarUrl ? `${API_BASE_URL}${user.avatarUrl}` : avatarDefault}
  className="img-fluid"
  alt="Avatar"
  style={{ width: "100%", height: "100%", objectFit: "cover" }}
/>
              </div>

              <Link 
  to="/edit-profile" 
  className="custom-btn user-pill py-3 px-5 text-decoration-none d-inline-block"
>
  Редактировать
</Link>
            </div>

            <div className="col ps-md-5 pt-1">
              <span className="text-white fs-3">@{user.username || "user"}</span>

              <div
                className="ms-3"
                style={{ height: "2px", backgroundColor: "white" }}
              ></div>

              <p className="text-white fs-5 mt-3">Email: {user.email}</p>
            </div>
          </div>

         
          <div
            className="stats-card p-4 text-white"
            style={{
              border: "2px solid white",
              borderRadius: "20px",
              background: "rgba(255,255,255,0.05)",
            }}
          >
            <h2>Моя статистика</h2>
            <p className="text-white-50">Пока в разработке</p>
          </div>
        </div>
      </main>
    </div>
  );
}

export default ProfilePage;