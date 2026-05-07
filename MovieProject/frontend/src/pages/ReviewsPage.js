import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/apiClient";
import "bootstrap/dist/css/bootstrap.min.css";
import avatarDefault from "../images/такса.svg";

const API_BASE_URL = "http://localhost:8080/movie-project";

function ReviewsPage() {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        setLoading(true);
        const response = await apiClient.get("/reviews");
        if (response.data && response.data.content) {
          setReviews(response.data.content);
        }
      } catch (error) {
        console.error("Ошибка загрузки рецензий:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchReviews();
  }, []);

  if (loading) return <div className="text-white text-center mt-5">Загрузка...</div>;

  return (
    <div className="container-wrapper min-vh-100">
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center">
          <Link to="/" className="nav-btn">Главная</Link>
          <Link to="/movies" className="nav-btn">Фильмы</Link>
          <Link to="/collections" className="nav-btn">Подборки</Link>
          <Link to="/reviews" className="nav-btn active">Рецензии</Link>
          <Link to="/profile" className="nav-btn">Моя страница</Link>
        </nav>
      </header>

      <main className="container-xl px-4 px-md-5">
        <h2 className="section-title fw-light mb-5 text-center text-white">
          Популярные рецензии
        </h2>

        <div className="d-flex flex-column gap-1">
          {reviews.map((rev) => {
            const strippedContent =
              rev.content
                ?.replace(/<\/p>|<\/div>|<\/h3>|<br\s*\/?>/gi, "\n")
                ?.replace(/<[^>]+>/g, "") || "";

            return (
              <div key={rev.id} className="w-100">
                <div
                  className="article-container p-4 mx-auto text-start mb-5"
                  style={{ maxWidth: "900px" }}
                >
                  <div className="d-flex flex-column flex-md-row gap-4 mb-4 align-items-stretch">
                    <img
                      src={
                        rev.movieCover
                          ? `${API_BASE_URL}${rev.movieCover}`
                          : avatarDefault
                      }
                      className="article-img rounded-1 object-fit-cover"
                      alt="Review cover"
                      style={{ width: "200px", height: "300px" }}
                    />
                    <div className="d-flex flex-column gap-3 flex-grow-1">
                      <Link
                        to={`/users/${rev.authorName}`}
                        className="user-pill custom-btn align-self-start text-decoration-none"
                      >
                        @{rev.authorName || "user"}
                      </Link>
                      <Link
                        to={`/reviews/${rev.id}`}
                        className="text-decoration-none text-white hover-opacity"
                      >
                        <h3 className="fw-bold m-0">
                          {rev.title || "Название рецензии"}
                        </h3>
                      </Link>
                      <div className="d-flex align-items-center gap-3">
                        <span className="fs-2 text-white fw-light">
                          {rev.likesCount || 0}
                        </span>
                        <i className={`${rev.likedByCurrentUser ? 'fa-solid' : 'fa-regular'} text-white fa-heart fs-3`}></i>
                      </div>
                    </div>
                  </div>
                  <div className="article-content">
                    <p className="article-text text-secondary mt-2 review-preview-text">
                      {strippedContent.substring(0, 350)}...
                    </p>
                    <div className="d-flex justify-content-end">
                      <Link
                        to={`/reviews/${rev.id}`}
                        className="arrow-btn text-decoration-none"
                      >
                        ⟶
                      </Link>
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </main>
    </div>
  );
}

export default ReviewsPage;