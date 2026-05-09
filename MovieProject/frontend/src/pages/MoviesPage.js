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
  const [genreId, setGenreId] = useState("");
  const [tagId, setTagId] = useState("");
  const [countryId, setCountryId] = useState("");
  const [year, setYear] = useState("");
  
  const [sortBy, setSortBy] = useState("name");
  const [sortOrder, setSortOrder] = useState("desc");

  const [isFilterOpen, setIsFilterOpen] = useState(false);

  const fetchMovies = useCallback(async (p) => {
    try {
      const params = new URLSearchParams({
        page: p,
        size: 20,
        sortBy,
        sortOrder
      });

      if (searchQuery.trim()) params.append("query", searchQuery);
      if (genreId) params.append("genreId", genreId);
      if (tagId) params.append("tagId", tagId);
      if (countryId) params.append("countryId", countryId);
      if (year) params.append("year", year);

      const url = `/movies?${params.toString()}`;
      const res = await apiClient.get(url);
      
      setMovies(res.data.content || []);
      setTotalPages(res.data.page?.totalPages || 0);
    } catch (error) {
      console.error("Ошибка загрузки фильмов:", error);
    }
  }, [searchQuery, genreId, tagId, countryId, year, sortBy, sortOrder]);

  useEffect(() => {
    fetchMovies(page);
  }, [page, fetchMovies]);

  const onSearchSubmit = (e) => {
    if (e) e.preventDefault();
    setPage(0);
    fetchMovies(0);
  };

  return (
    <div className="wrapper movie-page-bg">
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-4">
          <Link to="/" className="nav-btn">Главная</Link>
          <Link to="/movies" className="nav-btn active">Каталог</Link>
          <Link to="/collections" className="nav-btn">Подборки</Link>
          <Link to="/reviews" className="nav-btn">Рецензии</Link>
          <Link to="/profile" className="nav-btn">Моя страница</Link>
        </nav>
      </header>

      <main className="content px-4">
        <section className="d-flex flex-column align-items-center mb-5">
          
          <form onSubmit={onSearchSubmit} className="position-relative w-100 mb-3" style={{ maxWidth: "450px" }}>
            <input
              type="text"
              className="search-input-pill"
              placeholder="Поиск по названию"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <i className="fa fa-search search-icon-pill" onClick={onSearchSubmit}></i>
          </form>

          <div className="d-flex flex-wrap justify-content-center gap-2 mb-4">
            <select className="filter-pill-select" value={genreId} onChange={(e) => setGenreId(e.target.value)}>
              <option value="">Жанр</option>
            </select>

            <select className="filter-pill-select" value={tagId} onChange={(e) => setTagId(e.target.value)}>
              <option value="">Тег</option>
            </select>

            <select className="filter-pill-select" value={countryId} onChange={(e) => setCountryId(e.target.value)}>
              <option value="">Страна</option>
            </select>

            <input 
              type="number" 
              className="filter-pill-select year-input" 
              placeholder="Год" 
              value={year}
              onChange={(e) => setYear(e.target.value)}
            />
          </div>

          <div className="search-separator-line-full mb-4"></div>

          <div className="dropdown">
            <button 
              className="filter-main-btn" 
              type="button" 
              onClick={() => setIsFilterOpen(!isFilterOpen)}
            >
              Фильтр <i className="fa fa-filter ms-2"></i>
            </button>
            
            {isFilterOpen && (
              <div className="filter-dropdown-panel p-3 mt-2 shadow">
                <div className="mb-2">
                  <label className="text-white-50 small d-block mb-1">Сортировать по</label>
                  <select className="filter-select-custom" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
                    <option value="name">Названию</option>
                    <option value="popularity">Популярности</option>
                    <option value="year">Году</option>
                  </select>
                </div>
                <div className="mb-3">
                  <label className="text-white-50 small d-block mb-1">Порядок</label>
                  <select className="filter-select-custom" value={sortOrder} onChange={(e) => setSortOrder(e.target.value)}>
                    <option value="desc">Убывание</option>
                    <option value="asc">Возрастание</option>
                  </select>
                </div>
              </div>
            )}
          </div>
        </section>

        <section className="container">
          <div className="movie-grid">
            {movies.length > 0 ? (
              movies.map((movie) => {
                const displayName = movie.name || movie.originalName || "Без названия";
                const posterPath = movie.posterUrl ? `${API_BASE_URL}${movie.posterUrl}` : defaultMoviePoster;
                return (
                  <Link to={`/movies/${movie.id}`} key={movie.id} className="movie-card text-decoration-none">
                    <div className="movie-card-poster-wrapper">
                      <div className="badge-overlay">{movie.ratingKinopoisk || "—"}</div>
                      <img src={posterPath} alt={displayName} className="poster-img" />
                    </div>
                    <div className="movie-card-info">
                      <p className="text-white text-center mt-2 mb-1 fw-bold">{displayName}</p>
                      <p className="text-white-50 text-center small">{movie.releaseYear || movie.year}</p>
                    </div>
                  </Link>
                );
              })
            ) : (
              <h4 className="text-white text-center w-100 opacity-50">Фильмы не найдены</h4>
            )}
          </div>

          {totalPages > 1 && (
            <div className="d-flex justify-content-center align-items-center gap-2 mt-5 mb-5">
              <button className="pag-circle" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>&lt;</button>
              <button className="pag-circle active">{page + 1}</button>
              <button className="pag-circle" onClick={() => setPage((p) => p + 1)} disabled={page >= totalPages - 1}>&gt;</button>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default MoviesPage;