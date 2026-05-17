import { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/apiClient";
import "bootstrap/dist/css/bootstrap.min.css";
import avatarDefault from "../images/такса.svg";
import { getImageUrl } from "../utils/getImageUrl";

function ReviewsPage() {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState("title");
  const [sortOrder, setSortOrder] = useState("desc");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10;

  const fetchReviews = useCallback(async (page, query, sort, order) => {
    try {
      setLoading(true);
      const response = await apiClient.get("/reviews", {
        params: {
          page: page,
          size: pageSize,
          query: query || undefined,
          sortBy: sort,
          sortOrder: order,
        },
      });

      if (response.data) {
        setReviews(response.data.content || []);
        setTotalPages(
          response.data.page?.totalPages || response.data.totalPages || 0,
        );
      }
    } catch (error) {
      console.error("Ошибка загрузки рецензий:", error);
      setError("Не удалось загрузить рецензии");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchReviews(currentPage, searchQuery, sortBy, sortOrder);
  }, [currentPage, searchQuery, sortBy, sortOrder, fetchReviews]);
  
  const handlePageChange = (newPage) => {
    setCurrentPage(newPage);
    window.scrollTo(0, 0);
  };

  if (error) return <div className="text-danger text-center mt-5">{error}</div>;

  return (
    <div className="container-wrapper min-vh-100">
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

      <main className="container-xl px-4 px-md-5">
        {/* Секция поиска */}
        <div className="search-section d-flex flex-column align-items-center mb-5">
          <div
            className="search-input-container position-relative mb-4"
            style={{ maxWidth: "500px", width: "100%" }}
          >
            <input
              type="text"
              className="search-input w-100 py-2 px-4 rounded-pill border border-white text-white bg-transparent"
              placeholder="Поиск по названию"
              value={searchQuery}
              onChange={(e) => {
                setSearchQuery(e.target.value);
                setCurrentPage(0);
              }}
            />
            <i className="fa fa-search search-icon-pill"></i>
          </div>

          <hr className="w-75 border-white opacity-25 mb-4" />

          <button
            className="btn border-white rounded-3 px-4 py-2 text-white d-flex align-items-center gap-2 bg-transparent"
            onClick={() => setSortOrder(sortOrder === "asc" ? "desc" : "asc")}
          >
            Сортировать по{" "}
            {sortOrder === "asc" ? (
              <>
                возрастанию <i className="fa-solid fa-sort-up"></i>
              </>
            ) : (
              <>
                убыванию <i className="fa-solid fa-sort-down"></i>
              </>
            )}
          </button>
        </div>

        <h2 className="section-title fw-light mb-5 text-center text-white">
          {searchQuery
            ? `Рецензии на сайте: ${searchQuery}`
            : "Рецензии на сайте"}
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
                          ? `${getImageUrl(rev.movieCover)}`
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
                        <i
                          className={`${rev.likedByCurrentUser ? "fa-solid" : "fa-regular"} text-white fa-heart fs-3`}
                        ></i>
                      </div>
                    </div>
                  </div>
                  <div className="article-content">
                    <p className="article-text text-secondary text-white mt-2 review-preview-text">
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
        {totalPages > 1 && (
              <div className="d-flex justify-content-center align-items-center gap-2 mt-5 mb-5">
                <button
                  className="pag-circle"
                  onClick={() => handlePageChange(currentPage - 1)}
                  disabled={currentPage === 0}
                >
                  &lt;
                </button>

                <button className="pag-circle active">
                  {currentPage + 1}
                </button>

                <button
                  className="pag-circle"
                  onClick={() => handlePageChange(currentPage + 1)}
                  disabled={currentPage >= totalPages - 1}
                >
                  &gt;
                </button>
              </div>
            )}
      </main>
    </div>
  );
}

export default ReviewsPage;
