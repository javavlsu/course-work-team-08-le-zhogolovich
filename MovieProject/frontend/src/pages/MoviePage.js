import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";
import "bootstrap/dist/css/bootstrap.min.css";

const API_BASE_URL = "http://localhost:8080/movie-project";
const CommentItem = ({
  comment,
  currentUser,
  onDelete,
  onEdit,
  API_BASE_URL,
  avatarDefault,
}) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editText, setEditText] = useState(comment.content);

  const authorName = comment.authorName || "Аноним";
  const authorAvatar = comment.authorAvatar;

  return (
    <div
      className="p-3 rounded-4 mb-3"
      style={{ background: "rgba(255,255,255,0.05)" }}
    >
      <div className="d-flex justify-content-between align-items-start mb-2">
        <div className="d-flex align-items-center gap-2">
          <Link to={authorName !== "Аноним" ? `/users/${authorName}` : "#"}>
            <img
              src={
                comment.authorAvatar
                  ? `${API_BASE_URL}${comment.authorAvatar}`
                  : avatarDefault
              }
              width="35"
              height="35"
              className="rounded-circle"
              style={{ objectFit: "cover" }}
              alt="avatar"
            />
          </Link>
          <Link
            to={comment.authorName ? `/users/${comment.authorName}` : "#"}
            className="text-decoration-none fw-bold text-info"
          >
            @{comment.authorName || "Аноним"}
          </Link>
        </div>

        {currentUser && currentUser.username === authorName && (
          <div className="d-flex gap-2">
            <button
              className="btn btn-sm btn-outline-light border-0"
              onClick={() => setIsEditing(true)}
            >
              <i className="fa-solid fa-pen"></i>
            </button>
            <button
              className="btn btn-sm btn-outline-danger border-0"
              onClick={() => onDelete(comment.id)}
            >
              <i className="fa-solid fa-trash"></i>
            </button>
          </div>
        )}
      </div>

      {isEditing ? (
        <div className="mt-2">
          <textarea
            className="form-control custom-input text-white mb-2"
            value={editText}
            onChange={(e) => setEditText(e.target.value)}
          />
          <div className="d-flex gap-2 justify-content-end">
            <button
              className="btn btn-sm btn-link text-white text-decoration-none"
              onClick={() => setIsEditing(false)}
            >
              Отмена
            </button>
            <button
              className="custom-btn py-1 px-3"
              onClick={() => {
                if (!editText.trim()) {
                  alert("Комментарий не может быть пустым");
                  return;
                }
                onEdit(comment.id, editText);
                setIsEditing(false);
              }}
            >
              Сохранить
            </button>
          </div>
        </div>
      ) : (
        <p className="m-0 ps-1" style={{ whiteSpace: "pre-wrap" }}>
          {comment.content}
        </p>
      )}
    </div>
  );
};
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

  const handleSendComment = async () => {
    if (!commentText.trim()) return;
    try {
      await apiClient.post(`/comment/movie/${id}`, { content: commentText });
      setCommentText("");
      fetchComments();
    } catch (e) {
      alert("Не удалось отправить комментарий");
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm("Удалить комментарий?")) return;
    try {
      await apiClient.delete(`/comment/${commentId}`);
      setComments((prev) => prev.filter((c) => c.id !== commentId));
    } catch (e) {
      alert("Ошибка при удалении");
    }
  };

  const handleSaveEdit = async (commentId, newContent) => {
    if (!newContent || !newContent.trim()) {
      alert("Комментарий не может быть пустым");
      return;
    }

    try {
      await apiClient.patch(`/comment/${commentId}`, { content: newContent });
      setEditingCommentId(null);
      fetchComments(); 
    } catch (e) {
      console.error("Ошибка при сохранении:", e);
      alert("Ошибка при сохранении");
    }
  };

  const handleAddToCompilation = async (compilationId) => {
    try {
      await apiClient.post(`/movies/${id}/compilations`, {
        compilationIds: [Number(compilationId)],
      });

      alert("Добавлено!");
      setShowModal(false);
    } catch (e) {
      console.error("Ошибка API:", e.response?.data || e.message);
      alert("Ошибка при добавлении");
    }
  };

  const filteredCompilations = userCompilations.filter((c) =>
    c.title.toLowerCase().includes(searchQuery.toLowerCase()),
  );
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

  const handleStarClick = (value) => {
    setUserRating(value);
    handleRateMovie(value);
  };

  const handleRatingChange = (e) => {
    if (voted) {
      alert("Вы уже поставили оценку этому фильму");
      return;
    }
    const val = parseFloat(e.target.value);
    setUserRating(val);

    handleRateMovie(val);
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

          {/* Описание и жанры */}
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
            <div className="d-flex  flex-column align-items-center justify-content-start gap-1 mt-3">
              <p
                className="text-white text-center m-0 fs-5 fw-light"
                style={{ letterSpacing: "1px" }}
              >
                РЕЙТИНГ НА НАШЕМ САЙТЕ{" "}
                <span className="fw-bold text-warning">{movie.avgRating}</span>
              </p>

              <div className="d-flex align-items-center justify-content-start gap-4 mt-3">
                <div className="d-flex align-items-center justify-content-start gap-4 mt-3">
                  <div className="stars-display-interactive d-flex gap-1">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <i
                        key={star}
                        // Если рейтинг больше или равен номеру звезды — она закрашена
                        className={`fa-star fs-3 ${userRating >= star ? "fa-solid" : "fa-regular"}`}
                        style={{
                          color: userRating >= star ? "#FFD700" : "#444",
                          cursor: "pointer",
                          transition: "transform 0.1s",
                        }}
                        onClick={() => handleStarClick(star)}
                        onMouseEnter={(e) =>
                          (e.target.style.transform = "scale(1.2)")
                        }
                        onMouseLeave={(e) =>
                          (e.target.style.transform = "scale(1.0)")
                        }
                      ></i>
                    ))}
                  </div>

                  <div className="text-secondary fs-4">
                    <strong className="text-white">{userRating}</strong>
                    <span className="ms-2" style={{ opacity: 0.6 }}>
                      ({movie.ratingsCount} оценили)
                    </span>
                  </div>
                </div>{" "}
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
          {isAuth ? (
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
                <Link to="/profile" className="text-decoration-none text-white">
                  @{currentUser.username || "loading..."}
                </Link>
              </div>
              <textarea
                className="form-control text-white custom-input mb-3"
                style={{ minHeight: "100px" }}
                placeholder="Введите текст..."
                value={commentText}
                onChange={(e) => setCommentText(e.target.value)}
              ></textarea>
              <div className="d-flex justify-content-end">
                <button
                  onClick={handleSendComment}
                  className="custom-btn py-2 px-4"
                >
                  Отправить
                </button>
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
            className="comment-list d-flex flex-column gap-2 mx-auto text-white"
            style={{ maxWidth: "800px" }}
          >
            {comments.length > 0 ? (
              comments.map((comment) => (
                <CommentItem
                  key={comment.id}
                  comment={comment}
                  currentUser={currentUser}
                  API_BASE_URL={API_BASE_URL}
                  avatarDefault={avatarDefault}
                  onDelete={handleDeleteComment}
                  onEdit={handleSaveEdit}
                />
              ))
            ) : (
              <p className="text-center text-white-50">Комментариев пока нет</p>
            )}
          </div>
        </section>
      </main>
    </div>
  );
};

export default MoviePage;
