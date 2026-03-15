import { useEffect, useState } from "react";

function HomePage() {
  const [movies, setMovies] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/movie-project/movies")
      .then(response => response.json())
      .then(data => setMovies(data))
      .catch(error => console.error("Ошибка загрузки фильмов:", error));
  }, []);

  return (
    <div className="wrapper">
      <header className="header">
        <nav className="navbar">
          <a href="/" className="nav-btn">Главная</a>
          <a href="/catalog" className="nav-btn">Каталог</a>
          <a href="/collections" className="nav-btn">Подборки</a>
          <a href="/reviews" className="nav-btn">Рецензии</a>
          <a href="/profile" className="nav-btn">Моя страница</a>
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