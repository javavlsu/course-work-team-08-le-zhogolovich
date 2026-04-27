import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";
import "bootstrap/dist/css/bootstrap.min.css";
import AddToCompilationModal from "../components/AddToCompilationModal";
import MovieTagsModal from "../components/MovieTagsModal";
import MovieComments from "../components/MovieComments";

const API_BASE_URL = "http://localhost:8080/movie-project";

const MoviePage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);

  const [currentUser, setCurrentUser] = useState({
    username: "",
    avatarUrl: "",
  });
  const [userCompilations, setUserCompilations] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [modalLoading, setModalLoading] = useState(false);

  const [label, setLabel] = useState("Добавить метку");
  const [voted, setVoted] = useState(false);
  const [userRating, setUserRating] = useState(0);
  const [isAdmin, setIsAdmin] = useState(false);

  const [isAuth, setIsAuth] = useState(false);

  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState("");
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editText, setEditText] = useState("");

  const [hoverRating, setHoverRating] = useState(0);
  const [isHovered, setIsHovered] = useState(false);

  const [selectedCompIds, setSelectedCompIds] = useState([]);
  const [showTagsModal, setShowTagsModal] = useState(false);

  const fetchMovieData = async () => {
    try {
      const response = await apiClient.get(`/movies/${id}`);
      setMovie(response.data);
    } catch (error) {
      console.error("Ошибка при обновлении данных фильма:", error);
    }
  };
  const fetchData = async () => {
    try {
      const movieRes = await apiClient.get(`/movies/${id}`);
      setMovie(movieRes.data);
      setUserRating(movieRes.data.myRating || 0);

      try {
        const userRes = await apiClient.get("/users/me");
        setCurrentUser(userRes.data);
      } catch (e) {}
    } catch (error) {
      console.error("Ошибка загрузки", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsAuth(!!token);

    if (token) {
      try {
        const decoded = jwtDecode(token);
        if (decoded.role === "ADMIN" || decoded.roles?.includes("ROLE_ADMIN")) {
          setIsAdmin(true);
        }
      } catch (e) {
        console.error("Ошибка декодирования токена", e);
      }
    }

    fetchData();
  }, [id]);

  useEffect(() => {
    if (showModal) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
  }, [showModal]);

  const handleOpenModal = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      if (window.confirm("Нужно войти. Перейти?")) {
        navigate("/login");
      }

      return;
    }
    setSelectedCompIds([]);
    setShowModal(true);
    setModalLoading(true);

    try {
      const res = await apiClient.get("/compilations/my");
      setUserCompilations(res.data);
    } catch (e) {
      console.error("Ошибка загрузки подборок", e);
    } finally {
      setModalLoading(false);
    }
  };

  const fetchComments = async () => {
    try {
      const res = await apiClient.get(`/comment/movie/${id}`);

      const commentsData = res.data.content || res.data || [];

      setComments(commentsData);
    } catch (e) {
      console.error("Ошибка загрузки комментариев", e);
      setComments([]);
    }
  };
  useEffect(() => {
    fetchData();
    fetchComments();
  }, [id]);


  const handleRateMovie = async (ratingValue) => {
    if (!isAuth) {
      if (
        window.confirm(
          "Чтобы поставить оценку, нужно войти. Перейти на страницу входа?",
        )
      ) {
        navigate("/login");
      }
      return;
    }

    try {
      await apiClient.post(`/movies/${id}/rating`, {
        rating: ratingValue,
      });

      fetchData();
    } catch (e) {
      console.error("Ошибка при выставлении рейтинга:", e);
      const errorMsg =
        e.response?.data?.message || "Не удалось сохранить оценку";
      alert(errorMsg);
    }
  };

  const checkAuth = () => {
    const token = localStorage.getItem("token");

    if (!token) {
      if (window.confirm("Нужно войти. Перейти?")) {
        navigate("/login");
      }
      return false;
    }

    return true;
  };

  if (loading)
    return <div className="text-white text-center mt-5">Загрузка...</div>;
  if (!movie)
    return <div className="text-white text-center mt-5">Фильм не найден</div>;

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
              style={{ maxWidth: "300px", position: "relative" }}
            >
              <img
                src={movie.posterUrl}
                alt={movie.name}
                className="img-fluid rounded-3"
                style={{ display: "block", width: "100%" }}
              />

              <div
                style={{
                  position: "absolute",
                  top: "10px",
                  right: "10px",
                  backgroundColor: "black",
                  color: "gold",
                  padding: "5px 10px",
                  borderRadius: "20px",
                }}
              >
                {movie.ratingKinopoisk}
              </div>
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

          {/* Описание, жанры и метки */}
          <div className="col-lg-6 d-flex flex-column gap-4 position-relative">
            <h1 className="text-center mb-5 text-white">{movie.name}</h1>
            <div className="article-container p-3 mt-auto">
              <p className="text-white fs-5 m-0">
                {movie.overview ? (
                  movie.overview
                ) : (
                  <span className="text-white-50 italic">
                    упс... кажется описание отсутствует
                  </span>
                )}
              </p>
            </div>

            <div className="d-flex flex-wrap justify-content-center gap-2 mt-0 mb-auto">
              {movie.genres && movie.genres.length > 0 ? (
                movie.genres.map((genre, index) => (
                  <span key={index} className="tag-pill">
                    {genre}
                  </span>
                ))
              ) : (
                <span className="text-white-50 small italic">
                  метки отсутствуют
                </span>
              )}
              <div className="d-flex flex-wrap justify-content-center gap-2 mt-0">
                {movie.tags && movie.tags.length > 0
                  ? movie.tags.map((tag, index) => (
                      <span
                        key={`tag-${index}`}
                        className="tag-pill"
                        style={{ borderStyle: "dashed", opacity: 0.9 }}
                      >
                        #{tag}
                      </span>
                    ))
                  : !isAdmin && (
                      <span className="text-white-50 small italic">
                        метки отсутствуют
                      </span>
                    )}
              </div>
              {isAdmin && (
                <button
                  onClick={() => setShowTagsModal(true)}
                  className="btn btn-sm btn-outline-warning rounded-pill px-3 mb-3"
                >
                  <i className="fa-solid fa-tags me-2"></i>
                  Редактировать теги
                </button>
              )}

              {/* Компонент модального окна */}
              <MovieTagsModal
                show={showTagsModal}
                movieId={movie.id}
                currentTags={movie.tags} // Список тегов из MovieFullDto
                onClose={() => setShowTagsModal(false)}
                onTagsUpdated={fetchMovieData} // Перезагрузка данных фильма
              />
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
              <AddToCompilationModal
                movieId={id}
                onClose={() => setShowModal(false)}
                onSuccess={(count) => alert(`Добавлено в ${count} подборки!`)}
              />
            )}

            {/* Рейтинг */}
            <div className="d-flex flex-column align-items-center justify-content-start gap-1 mt-3">
              <p
                className="text-white text-center m-0 fs-5 fw-light"
                style={{ letterSpacing: "1px" }}
              >
                РЕЙТИНГ НА НАШЕМ САЙТЕ{" "}
                <span className="fw-bold text-warning">
                  {movie.avgRating?.toFixed(1) || "—"}
                </span>
                <span className="ms-2" style={{ opacity: 0.6 }}>
                  ({movie.ratingsCount || 0} оценили)
                </span>
              </p>

              <div className="d-flex align-items-center justify-content-start gap-4 mt-3">
                <div className="stars-display-interactive d-flex gap-1">
                  {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10].map((starValue) => {
                    const isHoveredFull = hoverRating >= starValue;
                    const isHoveredHalf =
                      hoverRating >= starValue - 0.5 && hoverRating < starValue;

                    const isSelectedFull = userRating >= starValue;
                    const isSelectedHalf =
                      userRating >= starValue - 0.5 && userRating < starValue;

                    const showFull = isHovered ? isHoveredFull : isSelectedFull;
                    const showHalf = isHovered ? isHoveredHalf : isSelectedHalf;

                    return (
                      <div
                        key={starValue}
                        className="star-container"
                        style={{
                          position: "relative",
                          display: "inline-block",
                        }}
                        onMouseMove={(e) => {
                          if (!isAuth) return;
                          const rect = e.currentTarget.getBoundingClientRect();
                          const mouseX = e.clientX - rect.left;
                          const halfWidth = rect.width / 2;

                          let newHoverRating;
                          if (mouseX < halfWidth) {
                            newHoverRating = starValue - 0.5;
                          } else {
                            newHoverRating = starValue;
                          }
                          setHoverRating(newHoverRating);
                          setIsHovered(true);
                        }}
                        onMouseLeave={() => {
                          setIsHovered(false);
                          setHoverRating(0);
                        }}
                        onClick={(e) => {
                          if (!isAuth) {
                            if (
                              window.confirm(
                                "Чтобы поставить оценку, нужно войти. Перейти?",
                              )
                            ) {
                              navigate("/login");
                            }
                            return;
                          }
                          const rect = e.currentTarget.getBoundingClientRect();
                          const mouseX = e.clientX - rect.left;
                          const halfWidth = rect.width / 2;

                          let newRating;
                          if (mouseX < halfWidth) {
                            newRating = starValue - 0.5;
                          } else {
                            newRating = starValue;
                          }

                          if (newRating !== userRating) {
                            setUserRating(newRating);
                            handleRateMovie(newRating);
                          }
                        }}
                      >
                        <i
                          className={`fa-star fs-3 ${showFull ? "fa-solid" : showHalf ? "fa-solid fa-star-half-stroke" : "fa-regular"}`}
                          style={{
                            color: showFull || showHalf ? "#FFD700" : "#444",
                            cursor: isAuth ? "pointer" : "default",
                            transition: "all 0.2s ease-in-out",
                            transform:
                              isHovered && isAuth ? "scale(1.15)" : "scale(1)",
                          }}
                        ></i>
                      </div>
                    );
                  })}
                </div>

                <div className="text-secondary fs-4">
                  <strong className="text-white">
                    {userRating > 0 ? userRating.toFixed(1) : "—"}
                  </strong>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Секция комментариев */}
        <section className="mb-5">
        

          <MovieComments 
          movieId={id} 
          currentUser={currentUser} 
          isAuth={isAuth} 
          avatarDefault={avatarDefault} 
        />
        </section>
      </main>
    </div>
  );
};

export default MoviePage;
