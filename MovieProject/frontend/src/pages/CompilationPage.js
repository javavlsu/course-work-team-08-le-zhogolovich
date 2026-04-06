import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import axios from "axios";

const API_BASE_URL = "http://localhost:8080/movie-project";

const CompilationPage = () => {
    const { id } = useParams(); 
    const navigate = useNavigate();
    
    const [compilation, setCompilation] = useState(null);
    const [currentUser, setCurrentUser] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isLiked, setIsLiked] = useState(false);

    useEffect(() => {
    const token = localStorage.getItem("token");
  

    const fetchData = async () => {
        try {
            const compRes = await axios.get(`${API_BASE_URL}/compilations/${id}`, {
                headers: token ? { Authorization: `Bearer ${token}` } : {}
            });
            setCompilation(compRes.data);
            
            if (compRes.data.likedByCurrentUser !== undefined) {
                setIsLiked(compRes.data.likedByCurrentUser);
            }

            if (token) {
                try {
                    const userRes = await axios.get(`${API_BASE_URL}/users/me`, {
                        headers: { Authorization: `Bearer ${token}` }
                    });
                    setCurrentUser(userRes.data);
                } catch (e) {
                    console.error("Не удалось загрузить данные пользователя", e);
                }
            }
        } catch (error) {
            console.error("Ошибка загрузки подборки:", error);
            if (error.response?.status === 401) {
                localStorage.removeItem("token");
            }
        } finally {
            setLoading(false);
        }
    };
    fetchData();
}, [id]);

    const checkAuth = () => {
      const token = localStorage.getItem("token");
      if (!token) {
        if (
          window.confirm(
            "Войдите в аккаунт, чтобы взаимодействовать с подборками. Перейти к входу?",
          )
        ) {
          navigate("/login");
        }
        return false;
      }
      return true;
    };

      const handleLike = async () => {
        if (!checkAuth()) return;
      const token = localStorage.getItem("token");
      const previousIsLiked = isLiked;
      const previousLikesCount = compilation.likesCount;

    
    setIsLiked(!previousIsLiked);
    setCompilation({
        ...compilation,
        likesCount: previousIsLiked ? previousLikesCount - 1 : previousLikesCount + 1
    });

    try {
        if (previousIsLiked) {
            await axios.delete(`${API_BASE_URL}/compilations/${id}/like`, {
                headers: { Authorization: `Bearer ${token}` }
            });
        } else {
            await axios.post(`${API_BASE_URL}/compilations/${id}/like`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });
        }
    } catch (e) {
        console.error("Ошибка при смене лайка:", e);
        setIsLiked(previousIsLiked);
        setCompilation({ ...compilation, likesCount: previousLikesCount });
        alert("Не удалось сохранить лайк. Попробуйте позже.");
    }
};
    const handleDelete = async () => {
      if (!checkAuth()) return;
    if (!window.confirm("Вы уверены, что хотите удалить эту подборку?")) {
        return;
    }

    const token = localStorage.getItem("token");
    try {
        await axios.delete(`${API_BASE_URL}/compilations/${id}`, {
            headers: { Authorization: `Bearer ${token}` }
        });
        alert("Подборка удалена");
        navigate("/profile"); 
    } catch (error) {
        console.error("Ошибка при удалении:", error);
        alert("Не удалось удалить подборку");
    }
};

    if (loading) return <div className="text-center mt-5 text-white">Загрузка...</div>;
    if (!compilation) return <div className="text-center mt-5 text-white">Подборка не найдена</div>;

    const isOwner = currentUser && compilation.authorId === currentUser.id;

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
            <Link to="/profile" className="nav-btn">
              Моя страница
            </Link>
          </nav>
        </header>

        <main className="container-xl px-4 px-md-5">
          <h1 className="text-center mb-5 text-white">{compilation.title}</h1>

          <div className="row justify-content-center g-5 mb-5">
            <div className="col-lg-2 col-md-2 text-center">
              <div
                className="movie-card-static mb-4 mx-auto"
                style={{ maxWidth: "300px" }}
              >
                <img
                  src={
                    compilation.coverUrl
                      ? `${API_BASE_URL}/backend${compilation.coverUrl}`
                      : "/images/default_coll.jpg"
                  }
                  alt="Обложка"
                  className="img-fluid rounded-3 shadow"
                />
              </div>
            </div>

            <div className="col-lg-6 d-flex flex-column gap-4">
              <div
                className="article-container p-4"
                style={{
                  background: "rgba(255,255,255,0.05)",
                  borderRadius: "15px",
                }}
              >
                <p className="text-white fs-5 m-0">
                  {compilation.description || "Описание отсутствует"}
                </p>
              </div>

              <div className="form-actions d-flex justify-content-between align-items-center mt-auto">
                <div className="d-flex align-items-center gap-3 text-white">
                  <span className="fs-3 fw-light">
                    {compilation.likesCount || 0}
                  </span>
                  <button
                    onClick={handleLike} 
                    className={`like-btn ${isLiked ? "active" : ""}`}
                    style={{
                      background: "none",
                      border: "none",
                      color: isLiked ? "#ff4d4d" : "white",
                      fontSize: "1.5rem",
                    }}
                  >
                    <i
                      className={
                        isLiked ? "fa-solid fa-heart" : "fa-regular fa-heart"
                      }
                    ></i>
                  </button>
                </div>

                {isOwner ? (
                  <div className="d-flex gap-2">
                    <Link
                      to={`/compilations/${id}/edit`}
                      className="custom-btn text-decoration-none d-inline-block"
                    >
                      Редактировать
                    </Link>
                    <button
                      onClick={handleDelete}
                      className="btn btn-outline-danger rounded-pill px-4"
                      style={{ border: "2px solid" }}
                    >
                      Удалить
                    </button>
                  </div>
                ) : (
                  <button className="custom-btn">Добавить себе</button>
                )}
              </div>
            </div>
          </div>

          <div className="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-4 px-4">
            {compilation.movies?.map((movie) => (
              <div className="col" key={movie.id}>
                <Link
                  to={`/movies/${movie.id}`}
                  className="movie-card d-block text-decoration-none"
                >
                  <div className="badge-overlay">{movie.avgRating}</div>
                  <img
                    src={movie.posterUrl}
                    alt={movie.name}
                    className="img-fluid rounded-3 mb-3"
                  />
                  <p className="card-title text-white">{movie.name}</p>
                  <p className="card-year text-white-50">{movie.releaseYear}</p>
                </Link>
              </div>
            ))}
          </div>
        </main>
      </div>
    );
};

export default CompilationPage;