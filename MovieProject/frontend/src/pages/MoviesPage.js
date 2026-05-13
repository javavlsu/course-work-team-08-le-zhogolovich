import React, { useEffect, useState, useCallback } from "react";
import { Link, useSearchParams } from "react-router-dom";
import apiClient from "../api/apiClient";
import "bootstrap/dist/css/bootstrap.min.css";
import defaultMoviePoster from "../images/BasePoster.png";

const API_BASE_URL = "http://localhost:8080/movie-project";

function MoviesPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [movies, setMovies] = useState([]);
  const [totalPages, setTotalPages] = useState(0);
  const [isFilterOpen, setIsFilterOpen] = useState(false);

  const page = parseInt(searchParams.get("page") || "0", 10);
  const searchQuery = searchParams.get("query") || "";
  const genreId = searchParams.get("genreId") || "";
  const tagId = searchParams.get("tagId") || "";
  const countryId = searchParams.get("countryId") || "";
  const year = searchParams.get("year") || "";
  const sortBy = searchParams.get("sortBy") || "name";
  const sortOrder = searchParams.get("sortOrder") || "desc";

  const [genres, setGenres] = useState([]);
  const [tags, setTags] = useState([]);
  const [countries, setCountries] = useState([]);

  useEffect(() => {
    const fetchFilters = async () => {
      try {
        const [genresRes, tagsRes, countriesRes] = await Promise.all([
          apiClient.get("/genres"),
          apiClient.get("/tags"),
          apiClient.get("/countries"),
        ]);

        setGenres(genresRes.data);
        setTags(tagsRes.data);

        const validCountries = countriesRes.data.filter(
          (c) => c.name.trim() !== "",
        );
        setCountries(validCountries);
      } catch (err) {
        console.error("Ошибка загрузки фильтров", err);
      }
    };
    fetchFilters();
  }, []);

  const updateFilters = (newParams) => {
    const current = Object.fromEntries([...searchParams]);
    if (!newParams.hasOwnProperty("page")) {
      newParams.page = 0;
    }
    setSearchParams({ ...current, ...newParams });
  };

  const fetchMovies = useCallback(async () => {
    try {
      const params = new URLSearchParams(searchParams);
      if (!params.has("size")) params.set("size", "20");
      if (!params.has("sortBy")) params.set("sortBy", sortBy);
      if (!params.has("sortOrder")) params.set("sortOrder", sortOrder);

      const res = await apiClient.get(`/movies?${params.toString()}`);

      setMovies(res.data.content || []);
      setTotalPages(res.data.totalPages || res.data.page?.totalPages || 0);
    } catch (error) {
      console.error("Ошибка загрузки фильмов:", error);
    }
  }, [searchParams, sortBy, sortOrder]);

  useEffect(() => {
    fetchMovies();
  }, [fetchMovies]);

  const onSearchSubmit = (e) => {
    if (e) e.preventDefault();

    fetchMovies();
  };

  return (
    <div className="wrapper movie-page-bg">
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

      <main className="content px-4">
        <section className="d-flex flex-column align-items-center mb-5">
          <form
            onSubmit={onSearchSubmit}
            className="position-relative w-100 mb-3"
            style={{ maxWidth: "450px" }}
          >
            <input
              type="text"
              className="search-input-pill"
              placeholder="Поиск по названию"
              value={searchQuery}
              onChange={(e) => updateFilters({ query: e.target.value })}
            />
            <i
              className="fa fa-search search-icon-pill"
              onClick={onSearchSubmit}
            ></i>
          </form>

          <div className="d-flex flex-wrap justify-content-center gap-2 mb-4">
            <select
              className="filter-pill-select"
              value={genreId}
              onChange={(e) => updateFilters({ genreId: e.target.value })}
            >
              <option value="">Жанр</option>
              {genres
                .filter((c) => c.name && c.name.trim() !== "")
                .map((genre) => (
                  <option key={genre.id} value={genre.id}>
                    {genre.name}
                  </option>
                ))}
            </select>

            <select
              className="filter-pill-select"
              value={tagId}
              onChange={(e) => updateFilters({ tagId: e.target.value })}
            >
              <option value="">Тег</option>
              {tags
                .filter((c) => c.name && c.name.trim() !== "")
                .map((tag) => (
                  <option key={tag.id} value={tag.id}>
                    {tag.name}
                  </option>
                ))}
            </select>

            <select
              className="filter-pill-select"
              value={countryId}
              onChange={(e) => updateFilters({ countryId: e.target.value })}
            >
              <option value="">Страна</option>
              {countries
                .filter((c) => c.name && c.name.trim() !== "")
                .map((country) => (
                  <option key={country.id} value={country.id}>
                    {country.name}
                  </option>
                ))}
            </select>

            <input
              type="number"
              className="filter-pill-select year-input"
              placeholder="Год"
              value={year}
              onChange={(e) => updateFilters({ year: e.target.value })}
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
                  <label className="text-white-50 small d-block mb-1">
                    Сортировать по
                  </label>
                  <select
                    className="filter-select-custom"
                    value={sortBy}
                    onChange={(e) => updateFilters({ sortBy: e.target.value })}
                  >
                    <option value="name">Названию</option>
                    <option value="popularity">Популярности</option>
                    <option value="year">Году</option>
                  </select>
                </div>
                <div className="mb-3">
                  <label className="text-white-50 small d-block mb-1">
                    Порядок
                  </label>
                  <select
                    className="filter-select-custom"
                    value={sortOrder}
                    onChange={(e) =>
                      updateFilters({ sortOrder: e.target.value })
                    }
                  >
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
                const displayName =
                  movie.name || movie.originalName || "Без названия";
                const posterPath = movie.posterUrl
                  ? `${API_BASE_URL}${movie.posterUrl}`
                  : defaultMoviePoster;
                return (
                  <Link
                    to={`/movies/${movie.id}`}
                    key={movie.id}
                    className="movie-card text-decoration-none"
                  >
                    <div className="movie-card-poster-wrapper">
                      <div className="badge-overlay">
                        {movie.ratingKinopoisk || "—"}
                      </div>
                      <img
                        src={posterPath}
                        alt={displayName}
                        className="poster-img"
                      />
                    </div>
                    <div className="movie-card-info">
                      <p className="text-white text-center mt-2 mb-1 fw-bold">
                        {displayName}
                      </p>
                      <p className="text-white-50 text-center small">
                        {movie.releaseYear || movie.year}
                      </p>
                    </div>
                  </Link>
                );
              })
            ) : (
              <h4 className="text-white text-center w-100 opacity-50">
                Фильмы не найдены
              </h4>
            )}
          </div>

          {totalPages > 1 && (
            <div className="d-flex justify-content-center align-items-center gap-2 mt-5 mb-5">
              <button
                className="pag-circle"
                onClick={() => updateFilters({ page: Math.max(0, page - 1) })}
                disabled={page === 0}
              >
                &lt;
              </button>

              <button className="pag-circle active">{page + 1}</button>

              <button
                className="pag-circle"
                onClick={() => updateFilters({ page: page + 1 })}
                disabled={page >= totalPages - 1}
              ></button>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default MoviesPage;
