import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link, useNavigate } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";

import avatarDefault from "../images/такса.svg"; 
const API_BASE_URL = "http://localhost:8080/movie-project/backend";

function ProfilePage() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [compilations, setCompilations] = useState([]); 
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");
  

  useEffect(() => {
    const token = localStorage.getItem("token")?.trim();

    if (!token) {
      navigate("/login");
      return;
    }

    const fetchProfileData = async () => {
      try {
        const [userRes, compRes] = await Promise.all([
          axios.get("http://localhost:8080/movie-project/users/me", {
            headers: { Authorization: `Bearer ${token}` },
          }),
          axios.get("http://localhost:8080/movie-project/compilations/my", {
            headers: { Authorization: `Bearer ${token}` },
          })
        ]);

        setUser(userRes.data);
        setCompilations(compRes.data);
      } catch (error) {
        console.error("Ошибка загрузки данных", error);
        if (error.response?.status === 403) {
          localStorage.removeItem("token");
          navigate("/login");
        } else {
          setErrorMsg("Не удалось загрузить данные.");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchProfileData();
  }, [navigate]);
  const handleLogout = () => {
  
    localStorage.removeItem("token");

    navigate("/login");
};

  if (loading) return <div className="text-white text-center mt-5">Загрузка...</div>;
  if (errorMsg) return <div className="text-danger text-center mt-5">{errorMsg}</div>;

  return (
    <div className="container-wrapper">
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
          <Link to="/" className="nav-btn">Главная</Link>
          <Link to="/movies" className="nav-btn">Фильмы</Link>
          <Link to="/collections" className="nav-btn">Подборки</Link>
          <Link to="/reviews" className="nav-btn">Рецензии</Link>
          <Link to="/profile" className="nav-btn">Моя страница</Link>

          <button 
    onClick={handleLogout} 
    className="nav-btn border-0 bg-transparent text-danger fw-bold"
  >
    Выйти
  </button>
        </nav>
      </header>

      <main className="container-xl px-4 px-md-5 mt-5">
        <div style={{ maxWidth: "1100px", margin: "0 auto" }}>
          {/* Инфо профиля */}
          <div className="row align-items-start mb-5 g-0">
            <div className="col-auto d-flex flex-column align-items-center">
              <div className="rounded-circle overflow-hidden mb-4 shadow" style={{ width: "220px", height: "220px" }}>
                <img
                  src={user.avatarUrl ? `${API_BASE_URL}${user.avatarUrl}` : avatarDefault}
                  className="img-fluid"
                  alt="Avatar"
                  style={{ width: "100%", height: "100%", objectFit: "cover" }}
                />
              </div>
              <Link to="/edit-profile" className="custom-btn user-pill py-3 px-5 text-decoration-none">
                Редактировать
              </Link>
            </div>

            <div className="col ps-md-5 pt-1">
              <span className="text-white fs-3">@{user.username || "user"}</span>
              <div className="ms-1 mt-1" style={{ height: "2px", backgroundColor: "white", width: "100%" }}></div>
              <p className="text-white fs-5 mt-3"> {user.aboutMe}</p>
            </div>
          </div>

          <section className="mb-5 text-center section-divider">
            <h2 className="section-title fw-light mb-2 pt-4 d-inline-block w-75 text-white">Мои подборки</h2>

            <div className="text-center w-100 mt-4 mb-5"> 
 
        <Link 
            to="/create-compilation"
            className="custom-btn user-pill py-3 px-5 text-decoration-none text-center d-inline-block"
            style={{ fontSize: "1.1rem", minWidth: "180px" }}
        >
            + Новая
        </Link>
    </div>

            {/* Список подборок */}
            <div className="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-4">
              {compilations.length > 0 ? (
                compilations.map((comp) => (
                  <div className="col" key={comp.id}>
                    <Link to={`/compilations/${comp.id}`} className="coll-card d-block text-decoration-none">
                      <div className="img-box rounded-4 overflow-hidden mb-3 shadow-sm" style={{ aspectRatio: '1/1' }}>
                        {!comp.isPublic && (
                          <div className="card-badge" style={{ position: 'absolute', top: '10px', left: '10px', color: 'white' }}>
                            <i className="fa-solid fa-lock"></i>
                          </div>
                        )}
                        <img 
                          src={comp.coverUrl ? `${API_BASE_URL}${comp.coverUrl}` : "images/default_coll.jpg"} 
                          alt={comp.title} 
                          className="w-100 h-100 object-fit-cover" 
                        />
                      </div>
                      <p className="text-light m-0 fw-bold">{comp.title}</p>
                      
                    </Link>
                  </div>
                ))
              ) : (
                <div className="w-100 text-center text-white-50 py-4">
                  У вас пока нет созданных подборок.
                </div>
              )}
            </div>
          </section>

          {/* Статистика */}
          <div className="stats-card p-4 text-white mb-5" style={{ border: "2px solid white", borderRadius: "20px", background: "rgba(255,255,255,0.05)" }}>
            <h2>Моя статистика</h2>
            <p className="text-white-50">Всего просмотрено: {user.watchedCount || 0}</p>
          </div>
        </div>
      </main>
    </div>
  );
}

export default ProfilePage;