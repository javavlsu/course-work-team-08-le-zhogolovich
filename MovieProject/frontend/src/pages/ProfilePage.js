import React, { useEffect, useState } from "react";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";
import "bootstrap/dist/css/bootstrap.min.css";
import { Link, useNavigate, useParams } from "react-router-dom"; 
// Добавь импорт jwtDecode:
import { jwtDecode } from "jwt-decode";

const API_BASE_URL = "http://localhost:8080/movie-project";

function ProfilePage() {
  const { username: urlUsername } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [compilations, setCompilations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");
  const [isMyProfile, setIsMyProfile] = useState(false);

 useEffect(() => {
  const fetchProfileData = async () => {
    try {
      setLoading(true);

      const userEndpoint = urlUsername ? `/users/${urlUsername}` : "/users/me";
      const userRes = await apiClient.get(userEndpoint);
      const userData = userRes.data;
      setUser(userData);

      let isMy = false;
      if (!urlUsername) {
        isMy = true;
      } else {
        const token = localStorage.getItem("token");
        if (token) {
          const decoded = jwtDecode(token);
          isMy = (decoded.sub === urlUsername);
        }
      }
      setIsMyProfile(isMy);


      const compEndpoint = isMy 
        ? "/compilations/my" 
        : `/compilations/user/${userData.id}`; 

      const compRes = await apiClient.get(compEndpoint);
      setCompilations(compRes.data);

    } catch (error) {
      console.error("Ошибка загрузки", error);
      if (error.response?.status === 404) {
        setErrorMsg("Пользователь не найден.");
      } else if (error.response?.status === 401 || error.response?.status === 403) {
        if (!urlUsername) navigate("/login");
      } else {
        setErrorMsg("Не удалось загрузить данные.");
      }
    } finally {
      setLoading(false);
    }
  };

  fetchProfileData();
}, [urlUsername, navigate]);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  if (loading)
    return <div className="text-white text-center mt-5">Загрузка...</div>;

  if (errorMsg)
    return <div className="text-danger text-center mt-5">{errorMsg}</div>;

 return (
    <div className="container-wrapper">
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
          <Link to="/" className="nav-btn">Главная</Link>
          <Link to="/movies" className="nav-btn">Фильмы</Link>
          <Link to="/collections" className="nav-btn">Подборки</Link>
          <Link to="/reviews" className="nav-btn">Рецензии</Link>
          <Link to="/profile" className="nav-btn">Моя страница</Link>

          {isMyProfile && (
            <button onClick={handleLogout} className="nav-btn border-0 bg-transparent text-danger fw-bold">
              Выйти
            </button>
          )}
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
                  className="img-fluid w-100 h-100 object-fit-cover"
                  alt="Avatar"
                />
              </div>
              {isMyProfile && (
                <Link to="/edit-profile" className="custom-btn user-pill py-3 px-5 text-decoration-none">
                  Редактировать
                </Link>
              )}
            </div>

            <div className="col ps-md-5 pt-1">
              <span className="text-white fs-3">@{user.username}</span>
              <div className="ms-1 mt-1" style={{ height: "2px", backgroundColor: "white", width: "100%" }}></div>
              <p className="text-white fs-5 mt-3">{user.aboutMe || "Информация отсутствует"}</p>
            </div>
          </div>

          {/* Секция подборок */}
          <section className="mb-5 text-center section-divider">
            <h2 className="section-title fw-light mb-2 pt-4 d-inline-block w-75 text-white">
              {isMyProfile ? "Мои подборки" : `Подборки @${user.username}`}
            </h2>

            {isMyProfile && (
              <div className="text-center w-100 mt-4 mb-5">
                <Link to="/create-compilation" className="custom-btn user-pill py-3 px-5 text-decoration-none text-center d-inline-block">
                  + Новая
                </Link>
              </div>
            )}

            <div className="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-4 mt-2">
              {compilations.length > 0 ? (
                compilations.map((comp) => (
                  <div className="col" key={comp.id}>
                    <Link to={`/compilations/${comp.id}`} className="coll-card d-block text-decoration-none">
                      <div className="img-box rounded-4 overflow-hidden mb-3 shadow-sm position-relative" style={{ aspectRatio: "1/1" }}>
                        {!comp.isPublic && isMyProfile && (
                          <div className="card-badge" style={{ position: "absolute", top: "10px", left: "10px", color: "white", zIndex: 2 }}>
                            <i className="fa-solid fa-lock"></i>
                          </div>
                        )}
                        <img
                          src={comp.coverUrl ? `${API_BASE_URL}${comp.coverUrl}` : "/images/default_coll.jpg"}
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
                  {isMyProfile ? "У вас пока нет созданных подборок." : "Пользователь еще не опубликовал ни одной подборки."}
                </div>
              )}
            </div>
          </section>

          {/* Статистика */}
          <div className="stats-card p-4 text-white mb-5" style={{ border: "2px solid white", borderRadius: "20px", background: "rgba(255,255,255,0.05)" }}>
            <h2>Статистика</h2>
            <p className="text-white-50 m-0">
              Всего просмотрено: {user.watchedCount || 0}
            </p>
          </div>
        </div>
      </main>
    </div>
  );
}


export default ProfilePage;
