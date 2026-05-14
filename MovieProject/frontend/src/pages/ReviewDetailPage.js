import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";
import "bootstrap/dist/css/bootstrap.min.css";
import ConfirmDeleteModal from "../components/ConfirmDeleteModal";
const API_BASE_URL = "http://localhost:8080/movie-project";

function ReviewDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [review, setReview] = useState(null);
  const [movie, setMovie] = useState(null);
  const [loading, setLoading] = useState(true);
  const [currentUser, setCurrentUser] = useState(null);
  const [isLiked, setIsLiked] = useState(false);
  const [likeCount, setLikeCount] = useState(0);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const reviewRes = await apiClient.get(`/reviews/${id}`);
        const reviewData = reviewRes.data;
        setReview(reviewData);
        setLikeCount(reviewData.likesCount || 0);
        setIsLiked(reviewData.likedByCurrentUser || false);

        if (reviewData.movieId) {
          const movieRes = await apiClient.get(`/movies/${reviewData.movieId}`);
          setMovie(movieRes.data);
        }

        const token = localStorage.getItem("token");
        if (token) {
          const userRes = await apiClient.get("/users/me");
          setCurrentUser(userRes.data);
        }
      } catch (err) {
        console.error("Ошибка загрузки данных:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleLike = async () => {
  const method = isLiked ? 'delete' : 'post';
  try {
    await apiClient[method](`/reviews/${id}/like`);
    setLikeCount(prev => isLiked ? prev - 1 : prev + 1);
    setIsLiked(!isLiked);
  } catch (err) {
    console.error("Ошибка:", err);
  }
};

  const canEdit =
    currentUser &&
    review &&
    (currentUser.role === "ADMIN" || currentUser.username === review.authorName &&
    currentUser.role === "REVIEWER");

  const handleDelete = async () => {
  try {
    await apiClient.delete(`/reviews/${id}`);
    navigate("/reviews"); 
  } catch (err) {
    console.error("Ошибка при удалении рецензии:", err);
    alert("Не удалось удалить рецензию");
  } finally {
    setShowDeleteModal(false);
  }
};

  if (loading)
    return <div className="text-white text-center mt-5">Загрузка...</div>;
  if (!review)
    return (
      <div className="text-white text-center mt-5">Рецензия не найдена</div>
    );

  return (
    <div className="container-wrapper text-white">
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
                <Link to="/searchuser" className="nav-btn">
                 Пользователи
                </Link>
                <Link to="/profile" className="nav-btn">
                  Моя страница
                </Link>
                
                
              </nav>
            </header>

      <main className="container-xl px-4 px-md-5 mt-5">
        <div className="row mb-5 align-items-start">
          <div className="col-md-5 col-lg-5 mb-4 mb-md-0">
            <div className="mb-4">
          <button 
            onClick={() => navigate(-1)} 
            className="btn text-white-50 p-0 d-flex align-items-center gap-2 border-0 bg-transparent hover-white transition-all"
            style={{ fontSize: '1.1rem' }}
          >
            <i className="fa-solid fa-arrow-left"></i>
            <span>Назад</span>
          </button>
        </div>
            <div
              className="movie-card-static mb-4 mx-auto"
              style={{ maxWidth: "300px", position: "relative" }}
            >
              <img
                src={`${API_BASE_URL}${review.movieCover}` || avatarDefault}
                alt={movie?.name || movie?.title || "Постер"}
                className="img-fluid rounded-3"
                style={{ display: "block", width: "100%", aspectRatio:'2/3' }}
              />
            </div>
          </div>

          <div className="col-md-7 col-lg-7 ps-md-5">
            <div className="d-flex justify-content-between align-items-center mb-3">
              <Link
                to={`/users/${review.authorName}`}
                className="custom-btn user-pill text-decoration-none"
              >
                @{review.authorName}
              </Link>
              <span className="text-white-50 fs-5">
                {new Date(review.createdAt).toLocaleDateString()}
              </span>
            </div>

            <p className="text-white-50 fs-3 mb-1">
              {movie ? (
                <Link
                  to={`/movies/${review.movieId}`}
                  className="text-white-50 text-decoration-none hover-white"
                >
                  {movie.name || movie.title}
                </Link>
              ) : (
                "Загрузка названия..."
              )}
            </p>

            <h1 className="article-title mb-4">{review.title}</h1>

            <div className="d-flex align-items-center gap-4">
              <div className="d-flex align-items-center gap-3">
                <span className="fs-2 fw-light">{likeCount}</span>
                <button
                  type="button"
                  className={`like-btn ${isLiked ? "active" : ""}`}
                  onClick={handleLike}
                >
                  <i
                    className={`${isLiked ? "fa-solid" : "fa-regular"} fa-heart`}
                  ></i>
                </button>
              </div>

              {canEdit && (
                <div className="d-flex gap-2">
                <button
                  className="custom-btn rounded-pill px-4"
                  onClick={() => navigate(`/reviews/edit/${review.id}`)}
                >
                  <i className="fa-solid fa-pen-to-square me-2"></i> Изменить
                </button>
                <button
      className="btn btn-outline-danger rounded-pill px-3"
      onClick={() => setShowDeleteModal(true)}
      title="Удалить рецензию"
    >
      <i className="fa-solid fa-trash"></i>
    </button>
  </div>
              )}
            </div>
          </div>
        </div>

        <div className="mx-auto col-12 text-white">
          <div
            className="article-text mb-4 fs-4 text-white"
            style={{ color: "white !important" }}
            dangerouslySetInnerHTML={{ __html: review.content }}
          />
        </div>
      </main>

      <ConfirmDeleteModal
  show={showDeleteModal}
  title="Удаление рецензии"
  message="Вы уверены, что хотите безвозвратно удалить эту рецензию? Это действие нельзя отменить."
  onConfirm={handleDelete}
  onCancel={() => setShowDeleteModal(false)}
/>
    </div>
  );
}


export default ReviewDetailPage;
