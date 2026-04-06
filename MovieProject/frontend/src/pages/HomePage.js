import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";

function HomePage() {
  const [movies, setMovies] = useState([]);
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  

  useEffect(() => {
    const token = localStorage.getItem("token");

    const headers = {};
  if (token) {
    headers["Authorization"] = "Bearer " + token;
  }

    fetch(`http://localhost:8080/movie-project/movies?page=${page}&size=20`, {
      headers,
    })
      .then((response) => response.json())
      .then((data) => {
      console.log("Данные с сервера:", data); 
      setMovies(data.content || []);
      
      setTotalPages(data.page.totalPages || 0);
    setPage(data.page.number);
    })
      .catch((error) => console.error("Ошибка загрузки фильмов:", error));
  }, [page, navigate]);

  return (
    <div className="wrapper">
      {/* Меню */}
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

      <main className="content">
        <section className="section">
          <h2 className="section-title">Лучшие фильмы за месяц</h2>

          <div className="movie-grid">
            {movies.map((movie) => (
              <Link
                to={`/movies/${movie.id}`}
                key={movie.id}
                className="movie-card text-decoration-none"
              >
                <img src={movie.posterUrl} alt={movie.name} />
                <p className="text-white mt-2 text-center">{movie.name}</p>
              </Link>
            ))}
          </div>


          {/* Блок пагинации */}
    <div className="d-flex justify-content-center align-items-center gap-3 mt-5 mb-5">
      <button 
        className="custom-btn" 
        onClick={() => setPage(prev => Math.max(0, prev - 1))}
        disabled={page === 0}
      >
        <i className="fa-solid fa-chevron-left me-2"></i> Назад
      </button>

      <span className="text-white fs-5">
        Страница <strong>{page + 1}</strong> из {totalPages}
      </span>

      <button 
        className="custom-btn" 
        onClick={() => setPage(prev => prev + 1)}
        disabled={page >= totalPages - 1}
      >
        Вперед <i className="fa-solid fa-chevron-right ms-2"></i>
      </button>
    </div>
        </section>
      </main>
    </div>
  );
}

export default HomePage;
