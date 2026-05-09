import React, { useEffect, useState, useCallback } from "react";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";
import { Link } from "react-router-dom";

const API_BASE_URL = "http://localhost:8080/movie-project";

function CollectionsPage() {
  const [compilations, setCompilations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 10; // Количество элементов на странице

  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState("title");
  const [sortOrder, setSortOrder] = useState("desc");

  const fetchCompilations = useCallback(async (page, query, sort, order) => {
    try {
      setLoading(true);
      const response = await apiClient.get("/compilations", {
        params: {
          page: page,
          size: pageSize,
          query: query || undefined,
          sortBy: sort,
          sortOrder: order,
        },
      });

      const data = response.data;
      setCompilations(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error("Ошибка при загрузке подборок:", err);
      setError("Не удалось загрузить подборки");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchCompilations(currentPage, searchQuery, sortBy, sortOrder);
  }, [currentPage, searchQuery, sortBy, sortOrder, fetchCompilations]);

  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < totalPages) {
      setCurrentPage(newPage);
      window.scrollTo(0, 0);
    }
  };

  if (error) return <div className="text-danger text-center mt-5">{error}</div>;

  return (
    <div className="container-wrapper min-vh-100 text-white">
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
          <Link to="/" className="nav-btn">
            Главная
          </Link>
          <Link to="/movies" className="nav-btn">
            Фильмы
          </Link>
          <Link to="/collections" className="nav-btn active">
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

      <main className="container-xl px-4 px-md-5">
        {/* Поиск  */}
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
                возрастанию <i className="fa-solid fa-arrow-up"></i>
              </>
            ) : (
              <>
                убыванию <i className="fa-solid fa-arrow-down"></i>
              </>
            )}
          </button>
        </div>
        <h2 className="section-title fw-light mb-5 text-center text-white">
          Подборки пользователей
        </h2>

        {loading ? (
          <div className="text-center py-5">Загрузка...</div>
        ) : (
          <>
            <div className="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-4">
              {compilations.map((comp) => (
                <div className="col" key={comp.id}>
                  <Link
                    to={`/compilations/${comp.id}`}
                    className="coll-card d-block text-decoration-none"
                  >
                    <div
                      className="img-box rounded-4 overflow-hidden mb-3 shadow-sm"
                      style={{
                        aspectRatio: "1/1",
                        background: "rgba(255,255,255,0.05)",
                      }}
                    >
                      <img
                        src={
                          comp.coverUrl
                            ? `${API_BASE_URL}${comp.coverUrl}`
                            : avatarDefault
                        }
                        alt={comp.title}
                        className="w-100 h-100 object-fit-cover"
                      />
                    </div>
                    <p className="text-light m-0 fw-bold">{comp.title}</p>
                    <small className="text-white-50">
                      Автор: @{comp.authorName || "user"}
                    </small>
                  </Link>
                </div>
              ))}
            </div>

            {/* Блок пагинации */}
            {totalPages > 1 && (
              <nav className="d-flex justify-content-center mt-5 mb-5">
                <ul className="pagination custom-pagination">
                  <li
                    className={`page-item ${currentPage === 0 ? "disabled" : ""}`}
                  >
                    <button
                      className="page-link"
                      onClick={() => handlePageChange(currentPage - 1)}
                    >
                      &laquo;
                    </button>
                  </li>

                  {[...Array(totalPages)].map((_, index) => (
                    <li
                      key={index}
                      className={`page-item ${index === currentPage ? "active" : ""}`}
                    >
                      <button
                        className="page-link"
                        onClick={() => handlePageChange(index)}
                      >
                        {index + 1}
                      </button>
                    </li>
                  ))}

                  <li
                    className={`page-item ${currentPage === totalPages - 1 ? "disabled" : ""}`}
                  >
                    <button
                      className="page-link"
                      onClick={() => handlePageChange(currentPage + 1)}
                    >
                      &raquo;
                    </button>
                  </li>
                </ul>
              </nav>
            )}
          </>
        )}
      </main>
    </div>
  );
}

export default CollectionsPage;
