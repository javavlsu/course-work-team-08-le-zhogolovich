import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import avatarDefault from "../images/такса.svg"; // дефолтный аватар
const API_BASE_URL = "http://localhost:8080/movie-project";

const MoviePage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    const [movie, setMovie] = useState(null);
    const [loading, setLoading] = useState(true);
    
    const [currentUser, setCurrentUser] = useState({
        username: '',
        avatarUrl: ''
    });
    const [userCompilations, setUserCompilations] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");
    const [modalLoading, setModalLoading] = useState(false);

    const [label, setLabel] = useState("Добавить метку");
    const [voted, setVoted] = useState(false);
    const [userRating, setUserRating] = useState(0);
    const [isAdmin, setIsAdmin] = useState(false);

    const avatarDefault = '../images/такса.svg';
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (token) {
        try {
            const decoded = jwtDecode(token);
            if (decoded.role === 'ADMIN' || decoded.roles?.includes('ROLE_ADMIN')) {
                setIsAdmin(true);
            }
        } catch (e) {
            console.error("Ошибка декодирования токена", e);
        }
    }

        const fetchData = async () => {
            try {
              // Запрос фильма (без токена или с ним)
              const movieRes = await axios.get(`${API_BASE_URL}/movies/${id}`, {
                headers: token ? { Authorization: `Bearer ${token}` } : {},
              });
              setMovie(movieRes.data);
              setUserRating(movieRes.data.rating || 0);

              // Запрос профиля (ТОЛЬКО если есть токен)
              if (token) {
                const userRes = await axios.get(`${API_BASE_URL}/users/me`, {
                  headers: { Authorization: `Bearer ${token}` },
                });
                setCurrentUser(userRes.data);
              }
            } catch (error) {
              console.error("Ошибка при загрузке данных", error);
              if (error.response?.status === 401) navigate("/login");
              setLoading(false);
            } finally {
              setLoading(false);
            }
        };

        fetchData();
    }, [id, navigate, token]);

    useEffect(() => {
  if (showModal) {
    document.body.style.overflow = 'hidden';
  } else {
    document.body.style.overflow = 'unset';
  }
}, [showModal]);

    const handleOpenModal = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        if (
          window.confirm(
            "Чтобы добавить фильм в подборку, нужно войти в аккаунт. Перейти к входу?",
          )
        ) {
          navigate("/login");
        }
        return;
      }

      setShowModal(true);
      setModalLoading(true);
      try {
        const res = await axios.get(`${API_BASE_URL}/compilations/my`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUserCompilations(res.data);
      } catch (e) {
        console.error("Ошибка загрузки подборок", e);
      } finally {
        setModalLoading(false);
      }
    };

    const handleAddToCompilation = async (compilationId) => {
    try {
        
        const data = {
            compilationIds: [Number(compilationId)] 
        };

        console.log("Отправляем данные:", data);

        await axios.post(
            `${API_BASE_URL}/movies/${id}/compilations`, 
            data, 
            {
                headers: { 
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json' 
                }
            }
        );

        alert("Добавлено!");
        setShowModal(false);
    } catch (e) {
        console.error("Ошибка API:", e.response?.data || e.message);
        alert("Ошибка на стороне сервера. Проверьте логи.");
    }
};
    const filteredCompilations = userCompilations.filter(c => 
        c.title.toLowerCase().includes(searchQuery.toLowerCase())
    );

    const handleRatingChange = (e) => {
        const val = parseFloat(e.target.value);
        setUserRating(val);
        if (!voted) {
            setVoted(true);
        }
    };

    const checkAuth = () => {
      if (!token) {
        if (
          window.confirm(
            "Чтобы выполнить это действие, нужно войти. Перейти на страницу входа?",
          )
        ) {
          navigate("/login");
        }
        return false;
      }
      return true;
    };

    if (loading) return <div className="text-white text-center mt-5">Загрузка...</div>;
    if (!movie) return <div className="text-white text-center mt-5">Фильм не найден</div>;

    return (
      <div className="container-wrapper">
        <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
          <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
            <Link to="/" className="nav-btn">
              Главная
            </Link>
            <Link to="/movies" className="nav-btn">
              Фильмы
            </Link>
            <Link to="/collections" className="nav-btn">
              Подборки
            </Link>
            <Link to="/reviews" className="nav-btn">
              Рецензии
            </Link>
            <Link to="/profile" className="nav-btn">
              Моя страница
            </Link>
          </nav>
        </header>

        <main className="container-xl px-4">
          <div className="justify-content-center row g-5 mb-5">
            {/* Постер */}
            <div className="col-lg-3 col-md-5 text-center">
              <div
                className="movie-card-static mb-4 mx-auto"
                style={{ maxWidth: "300px" }}
              >
                <img
                  src={movie.posterUrl}
                  alt={movie.name}
                  className="img-fluid rounded-3"
                />
              </div>

              <div className="dropdown">
                <button
                  className="custom-btn w-70 dropdown-toggle"
                  type="button"
                  data-bs-toggle="dropdown"
                >
                  {label}
                </button>
                <ul className="dropdown-menu dropdown-menu-dark custom-dropdown text-center">
                  <li>
                    <button
                      className="dropdown-item"
                      onClick={() => setLabel("Буду смотреть")}
                    >
                      Буду смотреть
                    </button>
                  </li>
                  <li>
                    <button
                      className="dropdown-item"
                      onClick={() => setLabel("Просмотрено")}
                    >
                      Просмотрено
                    </button>
                  </li>
                  <li>
                    <button
                      className="dropdown-item"
                      onClick={() => setLabel("Любимое")}
                    >
                      Любимое
                    </button>
                  </li>
                  <li>
                    <hr className="dropdown-divider border-secondary" />
                  </li>
                  <li>
                    <button
                      className="dropdown-item text-danger"
                      onClick={() => setLabel("Добавить метку")}
                    >
                      Убрать метку
                    </button>
                  </li>
                </ul>
              </div>
            </div>

            {/* Описание и жанры */}
            <div className="col-lg-6 d-flex flex-column gap-4 position-relative">
              <h1 className="text-center mb-5 text-white">{movie.name}</h1>
              <div className="article-container p-3 mt-auto">
                <p className="text-white fs-5 m-0">
                  {movie.overview || movie.description}
                </p>
              </div>

              <div className="d-flex flex-wrap justify-content-center gap-2 mt-0 mb-auto">
                {movie.genres?.map((genre, index) => (
                  <span key={index} className="tag-pill">
                    {genre}
                  </span>
                ))}
                {isAdmin && (
                  <button className="tag-pill tag-add-btn">
                    <i className="fa-solid fa-plus"></i>
                  </button>
                )}
              </div>
              <div>
                <button
                  className="custom-btn w-100"
                  style={{
                    background: "transparent",
                    border: "1px solid white",
                  }}
                  onClick={() => checkAuth() && handleOpenModal()}
                >
                  <i className="fa-solid fa-folder-plus me-2"></i> В подборку
                </button>
              </div>
              {showModal && (
                <div
                  className="modal-overlay"
                  onClick={() => setShowModal(false)}
                >
                  <div
                    className="custom-modal-content"
                    onClick={(e) => e.stopPropagation()}
                  >
                    <div className="modal-header-custom d-flex justify-content-between align-items-center mb-4">
                      <h4 className="text-white m-0">Выберите подборку</h4>
                      <button
                        className="btn-close btn-close-white"
                        onClick={() => setShowModal(false)}
                      ></button>
                    </div>

                    <input
                      type="text"
                      className=" form-control custom-input2 mb-4 "
                      placeholder="Поиск подборки..."
                      value={searchQuery}
                      onChange={(e) => setSearchQuery(e.target.value)}
                    />

                    <div
                      className="compilation-list-scrollable"
                      style={{ maxHeight: "400px", overflowY: "auto" }}
                    >
                      {modalLoading ? (
                        <p className="text-center text-white-50">
                          Загрузка ваших подборок...
                        </p>
                      ) : filteredCompilations.length > 0 ? (
                        filteredCompilations.map((comp) => (
                          <div
                            key={comp.id}
                            className="compilation-item-card d-flex align-items-center justify-content-between p-3 mb-2"
                          >
                            <div className="d-flex align-items-center gap-3">
                              <img
                                src={
                                  comp.coverUrl
                                    ? `http://localhost:8080/movie-project/backend${comp.coverUrl}`
                                    : "/images/default_coll.jpg"
                                }
                                alt="cover"
                                style={{
                                  width: "50px",
                                  height: "50px",
                                  objectFit: "cover",
                                  borderRadius: "8px",
                                }}
                              />
                              <div>
                                <div className="text-white fw-bold">
                                  {comp.title}
                                </div>
                              </div>
                            </div>
                            <button
                              className="custom-btn py-1 px-3"
                              style={{ fontSize: "0.9rem" }}
                              onClick={() => handleAddToCompilation(comp.id)}
                            >
                              Выбрать
                            </button>
                          </div>
                        ))
                      ) : (
                        <p className="text-center text-white-50">
                          Подборки не найдены
                        </p>
                      )}
                    </div>
                  </div>
                </div>
              )}

              {/* Рейтинг  */}
              <div className="d-flex align-items-center justify-content-start gap-4 mt-3">
                <div className="star-rating-interactive">
                  <input
                    type="range"
                    className="star-range-input"
                    min="0"
                    max="5"
                    step="0.5"
                    value={userRating}
                    onChange={(e) => checkAuth() && handleRatingChange(e)}
                  />
                  <div className="stars-display">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <i
                        key={star}
                        className={`fa-star ${userRating >= star ? "fa-solid" : userRating >= star - 0.5 ? "fa-solid fa-star-half-stroke" : "fa-regular"}`}
                        style={{
                          color: userRating >= star - 0.5 ? "#FFD700" : "#444",
                        }}
                      ></i>
                    ))}
                  </div>
                </div>

                <div className="text-secondary fs-4">
                  <strong className="text-white">{userRating}</strong>
                  <span className="ms-2" style={{ opacity: 0.6 }}>
                    (
                    {voted
                      ? (movie.votesCount || 0) + 1
                      : movie.votesCount || 0}{" "}
                    оценили)
                  </span>
                </div>
              </div>
            </div>
          </div>

          {/* Секция комментариев */}
          <section className="mb-5">
            <h2 className="section-title fw-light text-center mb-5">
              Комментарии
            </h2>

            {/* Форма нового комментария  */}
            {token ? (
              <div className="p-4 mb-5 mx-auto" style={{ maxWidth: "800px" }}>
                <div className="d-flex align-items-center gap-3 mb-3">
                  <img
                    src={
                      currentUser.avatarUrl
                        ? `${API_BASE_URL}${currentUser.avatarUrl}`
                        : avatarDefault
                    }
                    className="rounded-circle"
                    width="40"
                    height="40"
                    alt="My Avatar"
                    style={{ objectFit: "cover" }}
                  />
                  <Link
                    to="/profile"
                    className="text-decoration-none text-white"
                  >
                    @{currentUser.username || "loading..."}
                  </Link>
                </div>
                <textarea
                  className="form-control text-white custom-input mb-3"
                  style={{ minHeight: "100px" }}
                  placeholder="Введите текст..."
                ></textarea>
                <div className="d-flex justify-content-end">
                  <button className="custom-btn py-2 px-4">Отправить</button>
                </div>
              </div>
            ) : (
              <div
                className="p-5 mb-5 mx-auto text-center border border-secondary rounded-4"
                style={{
                  maxWidth: "800px",
                  background: "rgba(255,255,255,0.05)",
                }}
              >
                <p className="text-white-50 fs-5 mb-4">
                  Вы пока не можете оставлять комментарии
                </p>
                <div className="d-flex justify-content-center gap-3">
                  <Link
                    to="/register"
                    className="custom-btn py-2 px-4 text-decoration-none"
                  >
                    Зарегистрироваться
                  </Link>
                  <Link
                    to="/login"
                    className="btn btn-outline-light rounded-pill py-2 px-4"
                  >
                    Войти
                  </Link>
                </div>
              </div>
            )}

            {/* Список всех комментариев */}
            <div
              className="comment-list d-flex flex-column gap-4 mx-auto text-white"
              style={{ maxWidth: "800px" }}
            >
              <p>Комментариев пока нет</p>
            </div>
          </section>
        </main>
      </div>
    );
};

export default MoviePage;