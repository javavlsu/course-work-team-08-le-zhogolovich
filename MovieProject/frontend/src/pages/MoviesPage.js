import React, { useEffect, useState, useCallback } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/apiClient";
import "bootstrap/dist/css/bootstrap.min.css";
import defaultMoviePoster from "../images/BasePoster.png";

const API_BASE_URL = "http://localhost:8080/movie-project";

function MoviesPage() {
  const [movies, setMovies] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const [searchQuery, setSearchQuery] = useState("");
  const [sortBy, setSortBy] = useState("name");
  const [sortOrder, setSortOrder] = useState("desc");

  const fetchMovies = useCallback(async (p, q, s, o) => {
    try {
      const url = `/movies?page=${p}&size=20&sortBy=${s}&sortOrder=${o}${
        q.trim() ? `&query=${encodeURIComponent(q)}` : ""
      }`;

      console.log("Запрос уходит на:", url);

      const res = await apiClient.get(url);
      setMovies(res.data.content || []);
      setTotalPages(res.data.page?.totalPages || 0);
    } catch (error) {
      console.error("Ошибка загрузки фильмов:", error);
    }
  }, []);

  useEffect(() => {
    fetchMovies(page, searchQuery, sortBy, sortOrder);
  }, [page]); 

  const onSearchButtonClick = () => {
    setPage(0);
    fetchMovies(0, searchQuery, sortBy, sortOrder);
  };

  return (
    <div className="wrapper">
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
          <Link to="/" className="nav-btn">Главная</Link>
          <Link to="/movies" className="nav-btn">Фильмы</Link>
          <Link to="/collections" className="nav-btn">Подборки</Link>
          <Link to="/reviews" className="nav-btn">Рецензии</Link>
          <Link to="/profile" className="nav-btn">Моя страница</Link>
        </nav>
      </header>

      <main className="content px-4">
        <section className="search-panel mb-5 p-4 mx-auto" style={{ 
            maxWidth: "1100px", background: "rgba(255, 255, 255, 0.03)", 
            backdropFilter: "blur(12px)", borderRadius: "20px", border: "1px solid rgba(255, 255, 255, 0.1)" 
        }}>
          <div className="row g-3 align-items-end">
            <div className="col-lg-4">
              <label className="text-white-50 small mb-2 ms-2">Поиск</label>
              <input
                type="text"
                className="w-100 px-3 py-2 text-white bg-dark border-secondary rounded"
                placeholder="Введите название..."
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
            </div>
            <div className="col-lg-2">
              <label className="text-white-50 small mb-2 ms-2">Сортировка</label>
              <select className="w-100 px-3 py-2 text-white bg-dark border-secondary rounded" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                <option value="name">По названию</option>
                <option value="releaseYear">По году</option>
                <option value="ratingKinopoisk">По рейтингу</option>
              </select>
            </div>
            <div className="col-lg-2">
              <label className="text-white-50 small mb-2 ms-2">Порядок</label>
              <select className="w-100 px-3 py-2 text-white bg-dark border-secondary rounded" value={sortOrder} onChange={(e) => setSortOrder(e.target.value)}>
                <option value="desc">Убывание</option>
                <option value="asc">Возрастание</option>
              </select>
            </div>
            <div className="col-lg-4">
              <button 
                onClick={onSearchButtonClick}
                className="custom-btn w-100 py-2"
                style={{ borderRadius: "10px", fontWeight: "bold" }}
              >
                Найти
              </button>
            </div>
          </div>
        </section>

        <section className="section">
          <div className="movie-grid">
            {movies.length > 0 ? (
              movies.map((movie) => {
                const displayName = movie.name || movie.originalName || "Без названия";
                const posterPath = movie.posterUrl ? `${API_BASE_URL}${movie.posterUrl}` : defaultMoviePoster;

                return (
                  <Link to={`/movies/${movie.id}`} key={movie.id} className="movie-card text-decoration-none">
                    <div className="movie-card-poster-wrapper">
                      <div className="badge-overlay">{movie.ratingKinopoisk || "—"}</div>
                      <img src={posterPath} alt={displayName} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                    </div>
                    <div className="movie-card-info">
                      <p className="text-white text-center mt-2">{displayName}</p>
                      <p className="text-white-50 text-center small">{movie.releaseYear}</p>
                    </div>
                  </Link>
                );
              })
            ) : (
              <h4 className="text-white text-center w-100 opacity-50">Фильмы не найдены</h4>
            )}
          </div>

          {totalPages > 1 && (
            <div className="d-flex justify-content-center align-items-center gap-3 mt-5 mb-5">
              <button className="custom-btn px-4" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>Назад</button>
              <span className="text-white opacity-75">{page + 1} / {totalPages}</span>
              <button className="custom-btn px-4" onClick={() => setPage((p) => p + 1)} disabled={page >= totalPages - 1}>Вперед</button>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default MoviesPage;