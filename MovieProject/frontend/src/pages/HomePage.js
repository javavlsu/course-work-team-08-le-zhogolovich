import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";

function HomePage() {
  const [movies, setMovies] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("accessToken");


    if (!token) {
      navigate("/login");
      return;
    }

    fetch("http://localhost:8080/movie-project/movies", {
      headers: {
        Authorization: "Bearer " + token
      }
    })
      .then(response => response.json())
      .then(data => setMovies(data))
      .catch(error => console.error("Ошибка загрузки фильмов:", error));

  }, [navigate]);

  return (
    <div className="wrapper">
      {/* Меню */}
            <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
              <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
                <Link to="/" className="nav-btn">Главная</Link>
                <Link to="/movies" className="nav-btn">Фильмы</Link>
                <Link to="/collections" className="nav-btn">Подборки</Link>
                <Link to="/reviews" className="nav-btn">Рецензии</Link>
                <Link to="/profile" className="nav-btn">Моя страница</Link>
              </nav>
            </header>

      <main className="content">
        <section className="section">
          <h2 className="section-title">Лучшие фильмы за месяц</h2>

          <div className="movie-grid">
            {movies.map(movie => (
              <div key={movie.id} className="movie-card">
                <img src={movie.posterUrl} alt={movie.title}/>
                <p>{movie.title}</p>
              </div>
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

export default HomePage;