import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import apiClient from "../api/apiClient";
import "bootstrap/dist/css/bootstrap.min.css";

const API_BASE_URL = "http://localhost:8080/movie-project";

const CompilationPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [compilation, setCompilation] = useState(null);
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isLiked, setIsLiked] = useState(false);
  const [isSubscribed, setIsSubscribed] = useState(false);
  const [authorAvatar, setAuthorAvatar] = useState(null);
  useEffect(() => {
    const fetchData = async () => {
      try {
        const compRes = await apiClient.get(`/compilations/${id}`);
        setCompilation(compRes.data);

        if (compRes.data.isSubscribed !== undefined) {
          setIsSubscribed(compRes.data.isSubscribed);
        }

        if (compRes.data.likedByCurrentUser !== undefined) {
          setIsLiked(compRes.data.likedByCurrentUser);
        }

        if (compRes.data.authorId) {
          try {
            const authorRes = await apiClient.get(
              `/users/id/${compRes.data.authorId}`,
            );
            setAuthorAvatar(authorRes.data.avatarUrl);
          } catch (e) {
            console.error("Ошибка загрузки профиля автора", e);
          }
        }

        try {
          const userRes = await apiClient.get(`/users/me`);
          setCurrentUser(userRes.data);
        } catch (e) {}
      } catch (error) {
        console.error("Ошибка загрузки подборки:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [id]);

  const checkAuth = () => {
    const token = localStorage.getItem("token");
    if (!token) {
      if (window.confirm("Войдите в аккаунт. Перейти к входу?")) {
        navigate("/login");
      }
      return false;
    }
    return true;
  };

  const handleSubscribe = async () => {
    if (!checkAuth()) return;

    const prevStatus = isSubscribed;
    const prevCount = compilation.subscribersCount || 0;

    setIsSubscribed(!prevStatus);
    setCompilation({
      ...compilation,
      subscribersCount: prevStatus ? prevCount - 1 : prevCount + 1,
    });

    try {
      if (prevStatus) {
        await apiClient.delete(`/compilations/${id}/subscribe`);
      } else {
        await apiClient.post(`/compilations/${id}/subscribe`);
      }
    } catch (e) {
      setIsSubscribed(prevStatus);
      setCompilation({
        ...compilation,
        subscribersCount: prevCount,
      });
      alert("Не удалось обновить подписку");
    }
  };
  const handleLike = async () => {
    if (!checkAuth()) return;

    const prevLiked = isLiked;
    const prevCount = compilation.likesCount;

    setIsLiked(!prevLiked);
    setCompilation({
      ...compilation,
      likesCount: prevLiked ? prevCount - 1 : prevCount + 1,
    });

    try {
      if (prevLiked) {
        await apiClient.delete(`/compilations/${id}/like`);
      } else {
        await apiClient.post(`/compilations/${id}/like`);
      }
    } catch (e) {
      setIsLiked(prevLiked);
      setCompilation({ ...compilation, likesCount: prevCount });
      alert("Ошибка лайка");
    }
  };

  const handleDelete = async () => {
    if (!checkAuth()) return;

    if (!window.confirm("Удалить подборку?")) return;

    try {
      await apiClient.delete(`/compilations/${id}`);
      navigate("/profile");
    } catch (error) {
      alert("Ошибка удаления");
    }
  };

  if (loading)
    return <div className="text-white text-center mt-5">Загрузка...</div>;
  if (!compilation)
    return <div className="text-white text-center mt-5">Не найдено</div>;

  const isOwner = currentUser && compilation.authorId === currentUser.id;

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
          <Link to="/profile" className="nav-btn">
            Моя страница
          </Link>
        </nav>
      </header>

      <main className="container-xl px-4 px-md-5">
        <h1 className="text-center mb-5 text-white">{compilation.title}</h1>
        <div className="d-flex justify-content-center mb-3">
          <Link
            to={`/users/${compilation.authorName}`}
            className="text-decoration-none d-flex align-items-center gap-2 p-2 px-3"
            style={{
              background: "rgba(255, 255, 255, 0.05)",
              borderRadius: "50px",
              border: "1px solid rgba(255, 255, 255, 0.1)",
              transition: "background 0.2s",
            }}
            onMouseEnter={(e) =>
              (e.currentTarget.style.background = "rgba(255, 255, 255, 0.1)")
            }
            onMouseLeave={(e) =>
              (e.currentTarget.style.background = "rgba(255, 255, 255, 0.05)")
            }
          >
            <img
              src={
                authorAvatar
                  ? `${API_BASE_URL}${authorAvatar}`
                  : "/images/default-avatar.png"
              }
              alt="Author"
              style={{
                width: "30px",
                height: "30px",
                borderRadius: "50%",
                objectFit: "cover",
              }}
            />
            <span className="text-white-50" style={{ fontSize: "0.9rem" }}>
              Автор:
            </span>
            <span className="text-white fw-bold" style={{ fontSize: "0.9rem" }}>
              {compilation.authorName}
            </span>
          </Link>
        </div>
        <div className="row justify-content-center g-5 mb-5">
          <div className="col-lg-2 col-md-2 text-center">
            <div
              className="movie-card-static mb-4 mx-auto"
              style={{ maxWidth: "300px" }}
            >
              <img
                src={
                  compilation.coverUrl
                    ? `${API_BASE_URL}/backend${compilation.coverUrl}`
                    : "/images/default_coll.jpg"
                }
                alt="Обложка"
                className="img-fluid rounded-3 shadow"
              />
            </div>
          </div>

          <div className="col-lg-6 d-flex flex-column gap-4">
            <div
              className="article-container p-4"
              style={{
                background: "rgba(255,255,255,0.05)",
                borderRadius: "15px",
              }}
            >
              <p className="text-white fs-5 m-0">
                {compilation.description || "Описание отсутствует"}
              </p>
            </div>

            <div className="form-actions d-flex justify-content-between align-items-center mt-auto">
              <div className="d-flex align-items-center gap-3 text-white">
                <span className="fs-3 fw-light">
                  {compilation.likesCount || 0}
                </span>
                <button
                  onClick={handleLike}
                  className={`like-btn ${isLiked ? "active" : ""}`}
                  style={{
                    background: "none",
                    border: "none",
                    color: isLiked ? "#ff4d4d" : "white",
                    fontSize: "1.5rem",
                  }}
                >
                  <i
                    className={
                      isLiked ? "fa-solid fa-heart" : "fa-regular fa-heart"
                    }
                  ></i>
                </button>
              </div>

              {isOwner ? (
                <div className="d-flex gap-2">
                  <Link
                    to={`/compilations/${id}/edit`}
                    className="custom-btn text-decoration-none d-inline-block"
                  >
                    Редактировать
                  </Link>
                  <button
                    onClick={handleDelete}
                    className="btn btn-outline-danger rounded-pill px-4"
                    style={{ border: "2px solid" }}
                  >
                    Удалить
                  </button>
                </div>
              ) : (
                <div className="d-flex align-items-center gap-3">
                  <div className="text-white d-flex align-items-center gap-2">
                    <i className="fa-solid fa-users text-white-50"></i>
                    <span className="fs-4 fw-light">
                      {compilation.subscribersCount || 0}
                    </span>

                    {/* Текст отдельно без лишних классов */}
                    <span className="text-white-50">подписаны</span>
                  </div>

                  <button
                    onClick={handleSubscribe}
                    className="custom-btn py-2 px-4"
                    style={{
                      backgroundColor: isSubscribed
                        ? "rgba(255, 255, 255, 0.15)"
                        : "transparent",
                      color: "white",
                      border: isSubscribed
                        ? "1px solid rgba(255, 255, 255, 0.8)"
                        : "1px solid white",
                      borderRadius: "20px",
                      transition: "all 0.3s ease",
                      minWidth: "160px",
                    }}
                  >
                    {isSubscribed ? (
                      <span>
                        <i className="fa-solid fa-check me-2"></i>
                        Вы подписаны
                      </span>
                    ) : (
                      "Подписаться"
                    )}
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-4 px-4">
          {compilation.movies?.map((movie) => (
            <div className="col" key={movie.id}>
              <Link
                to={`/movies/${movie.id}`}
                className="movie-card d-block text-decoration-none"
              >
                <div className="badge-overlay">{movie.ratingKinopoisk}</div>
                <img
                  src={movie.posterUrl}
                  alt={movie.name}
                  className="img-fluid rounded-3 mb-3"
                />
                <p className="card-title text-white">{movie.name}</p>
                <p className="card-year text-white-50">{movie.releaseYear}</p>
              </Link>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default CompilationPage;
