import { useEffect, useState, useRef } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/apiClient";
import "bootstrap/dist/css/bootstrap.min.css";
import avatarDefault from "../images/такса.svg";
import { getImageUrl } from "../utils/getImageUrl";

function HomePage() {
  const [movies, setMovies] = useState([]);
  const [topCompilations, setTopCompilations] = useState([]);
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  const movieScrollRef = useRef(null);
  const compScrollRef = useRef(null);
  const reviewScrollRef = useRef(null);

  const scroll = (ref, direction, isSingle = false) => {
    if (ref.current) {
      const { scrollLeft, clientWidth } = ref.current;
      const scrollAmount = isSingle ? clientWidth : clientWidth * 0.8;

      ref.current.scrollTo({
        left:
          direction === "left"
            ? scrollLeft - scrollAmount
            : scrollLeft + scrollAmount,
        behavior: "smooth",
      });
    }
  };

  useEffect(() => {
    const fetchHomeData = async () => {
      try {
        setLoading(true);
        const [moviesRes, compsRes, reviewsRes] = await Promise.all([
          apiClient.get("/movies/top/top10"),
          apiClient.get("/compilations/top/top10"),
          apiClient.get("/reviews/top/top10"),
        ]);
        setMovies(moviesRes.data);
        setTopCompilations(compsRes.data);
        setReviews(reviewsRes.data);
      } catch (error) {
        console.error("Ошибка загрузки:", error);
      } finally {
        setLoading(false);
      }
    };
    fetchHomeData();
  }, []);

  if (loading)
    return <div className="text-white text-center mt-5">Загрузка...</div>;

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
          <Link to="/searchuser" className="nav-btn">
            Пользователи
          </Link>
          <Link to="/profile" className="nav-btn">
            Моя страница
          </Link>
        </nav>
      </header>

      <main className="container-xl px-4 px-md-5">
        {/* Фильмы */}
        <section className="section-divider mb-5">
          <h2 className="section-title fw-light mb-4">
            Лучшие фильмы за месяц
          </h2>
          <div className="slider-area">
            <button
              className="scroll-arrow left"
              onClick={() => scroll(movieScrollRef, "left")}
            >
              ‹
            </button>
            <div className="horizontal-scroll-container" ref={movieScrollRef}>
              {movies.map((movie) => (
                <div className="scroll-item-movie" key={movie.id}>
                  <Link
                    to={`/movies/${movie.id}`}
                    className="movie-card d-block text-decoration-none"
                  >
                    <div className="position-relative mb-3">
                      {movie.ratingKinopoisk && (
                        <div className="badge-overlay">
                          {movie.ratingKinopoisk}
                        </div>
                      )}
                      <img
                        src={
                          movie.posterUrl?.startsWith("http")
                            ? movie.posterUrl
                            : `${getImageUrl(movie.posterUrl)}`
                        }
                        alt={movie.name}
                        className="img-fluid rounded-3 shadow-sm"
                        style={{ aspectRatio: "2/3", objectFit: "cover" }}
                      />
                    </div>
                    <p className="text-light m-0 text-truncate small">
                      {movie.name}
                    </p>
                  </Link>
                </div>
              ))}
            </div>
            <button
              className="scroll-arrow right"
              onClick={() => scroll(movieScrollRef, "right")}
            >
              ›
            </button>
          </div>
        </section>

        {/* Подборки */}
        <section className="section-divider mb-5">
          <h2 className="section-title fw-light mb-4">
            Лучшие подборки за месяц
          </h2>
          <div className="slider-area">
            <button
              className="scroll-arrow left"
              onClick={() => scroll(compScrollRef, "left")}
            >
              ‹
            </button>
            <div className="horizontal-scroll-container" ref={compScrollRef}>
              {topCompilations.map((comp) => (
                <div className="scroll-item-comp" key={comp.id}>
                  <Link
                    to={`/compilations/${comp.id}`}
                    className="coll-card d-block text-decoration-none"
                  >
                    <div className="img-box rounded-4 overflow-hidden mb-3">
                      <img
                        src={
                          comp.coverUrl
                            ? `${getImageUrl(comp.coverUrl)}`
                            : avatarDefault
                        }
                        alt={comp.title}
                        className="w-100 h-100 object-fit-cover"
                        style={{ aspectRatio: "1/1" }}
                      />
                    </div>
                    <p className="text-light m-0 text-truncate small">
                      {comp.title}
                    </p>
                    <small className="text-white-50">
                      Автор: @{comp.authorName || "user"}
                    </small>
                  </Link>
                </div>
              ))}
            </div>
            <button
              className="scroll-arrow right"
              onClick={() => scroll(compScrollRef, "right")}
            >
              ›
            </button>
          </div>
        </section>

        {/* Рецензии */}
        <section className="section-divider mb-5 pt-4">
          <h2 className="section-title fw-light mb-5 text-center">
            Популярные рецензии
          </h2>
          <div className="slider-area">
            <button
              className="scroll-arrow left"
              onClick={() => scroll(reviewScrollRef, "left", true)}
            >
              ‹
            </button>
            <div
              className="horizontal-scroll-container"
              ref={reviewScrollRef}
              style={{ scrollSnapType: "x mandatory" }}
            >
              {reviews.map((rev) => {
                const strippedContent =
                  rev.content
                    ?.replace(/<\/p>|<\/div>|<\/h3>|<br\s*\/?>/gi, "\n")
                    ?.replace(/<[^>]+>/g, "") || "";

                return (
                  <div
                    className="scroll-item-review"
                    key={rev.id}
                    style={{ flex: "0 0 100%", scrollSnapAlign: "start" }}
                  >
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
                            to={`users/${rev.authorName}`}
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
                            <i className="fa-regular text-white fa-heart fs-3"></i>
                          </div>
                        </div>
                      </div>
                      <div className="article-content">
                        <p className="article-text text-secondary mt-2">
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
            <button
              className="scroll-arrow right"
              onClick={() => scroll(reviewScrollRef, "right", true)}
            >
              ›
            </button>
          </div>
        </section>
      </main>
    </div>
  );
}

export default HomePage;
