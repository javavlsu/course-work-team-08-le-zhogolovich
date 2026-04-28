import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate, Link } from "react-router-dom"; // Добавили Link
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";
import "bootstrap/dist/css/bootstrap.min.css";

const API_BASE_URL = "http://localhost:8080/movie-project";

function WriteReviewPage() {
  const { movieId, reviewId } = useParams();
  const navigate = useNavigate();
  const editorRef = useRef(null);

  const [movie, setMovie] = useState(null);
  const [title, setTitle] = useState("");
  const [loading, setLoading] = useState(true);
  const [isCheckingRole, setIsCheckingRole] = useState(true);

  useEffect(() => {
    const checkAccess = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login");
        return;
      }
      try {
        const res = await apiClient.get("/users/me");
        if (res.data.role !== "REVIEWER") {
          alert("Доступ запрещен. Эта страница только для рецензентов.");
          navigate("/");
        } else {
          setIsCheckingRole(false);
        }
      } catch (err) {
        console.error("Ошибка проверки роли:", err);
        navigate("/");
      }
    };
    checkAccess();
  }, [navigate]);

  useEffect(() => {
    const fetchData = async () => {
      if (isCheckingRole) return;

      try {
        if (reviewId) {
          const res = await apiClient.get(`/reviews/${reviewId}`);
          const review = res.data;
          setTitle(review.title);
          if (editorRef.current) {
            editorRef.current.innerHTML = review.content;
          }
          if (review.movie) setMovie(review.movie);
        } else if (movieId) {
          const res = await apiClient.get(`/movies/${movieId}`);
          setMovie(res.data);
        }
      } catch (err) {
        console.error("Ошибка загрузки данных:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [movieId, reviewId, isCheckingRole]);

  const handleFormat = (command, value = null) => {
    document.execCommand(command, false, value);
    if (editorRef.current) {
      editorRef.current.focus();
    }
  };

  const handleSave = async (isPublish) => {
    const content = editorRef.current ? editorRef.current.innerHTML : "";
    if (!title || !content || content === "<br>") {
      alert("Заполните заголовок и текст рецензии");
      return;
    }

    const payload = {
      movieId: movie?.id,
      title: title,
      content: content,
      isPublish: isPublish
    };

    try {
      if (reviewId) {
        await apiClient.patch(`/reviews/${reviewId}`, payload);
      } else {
        await apiClient.post("/reviews", payload);
      }
      navigate("/reviews");
    } catch (err) {
      alert("Ошибка при сохранении");
    }
  };

  if (isCheckingRole) return <div className="text-white text-center mt-5">Проверка прав доступа...</div>;
  if (loading) return <div className="text-white text-center mt-5">Загрузка данных...</div>;

  return (
    <div className="container-wrapper text-white">
   
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
          <Link to="/" className="nav-btn">Главная</Link>
          <Link to="/movies" className="nav-btn">Фильмы</Link>
          <Link to="/collections" className="nav-btn">Подборки</Link>
          <Link to="/reviews" className="nav-btn">Рецензии</Link>
          <Link to="/profile" className="nav-btn">Моя страница</Link>
        </nav>
      </header>

      <main className="container-xl px-4" style={{ maxWidth: "900px" }}>
        
        <button 
          onClick={() => navigate(-1)} 
          className="btn btn-link text-white-50 text-decoration-none mb-4 p-0 d-flex align-items-center gap-2"
        >
          <i className="fa-solid fa-arrow-left"></i> Назад к фильму
        </button>

        <div className="row align-items-start mb-5 g-0">
          <div className="d-flex gap-4 align-items-start mb-5">
            <div style={{ minWidth: "250px", width: "250px" }}>
              <div className="ratio ratio-4x3">
                <div className="cover-upload d-flex align-items-center justify-content-center border rounded overflow-hidden bg-dark">
                  <img 
                    src={movie?.posterUrl ? `${movie.posterUrl}` : avatarDefault} 
                    className="w-100 h-100 object-fit-cover" 
                    alt="Фильм" 
                  />
                </div>
              </div>
            </div>

            <div className="d-flex flex-column gap-2 w-100">
              <h3 className="mb-2 text-warning">{movie?.title || movie?.name || "Название фильма"}</h3>
              <textarea 
                className="form-control text-white bg-transparent border-secondary custom-input" 
                style={{ height: "100px", resize: "none" }}
                placeholder="Заголовок вашей рецензии..."
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            </div>
          </div>

          <section className="mt-2">
            {/* Редактор */}
            <div className="btn-toolbar mb-2 d-flex gap-2 p-2 bg-dark border border-secondary rounded">
              <button type="button" onClick={() => handleFormat('bold')} className="btn btn-sm btn-outline-light"><b>B</b></button>
              <button type="button" onClick={() => handleFormat('italic')} className="btn btn-sm btn-outline-light"><i>I</i></button>
              <button type="button" onClick={() => handleFormat('underline')} className="btn btn-sm btn-outline-light"><u>U</u></button>
              <button type="button" onClick={() => handleFormat('strikeThrough')} className="btn btn-sm btn-outline-light"><s>S</s></button>
              
              <select 
                className="form-select form-select-sm w-auto bg-dark text-white border-secondary"
                onChange={(e) => handleFormat('formatBlock', e.target.value)}
              >
                <option value="P">Текст</option>
                <option value="H1">Заголовок 1</option>
                <option value="H2">Заголовок 2</option>
                <option value="H3">Заголовок 3</option>
              </select>
            </div>

            <div 
              ref={editorRef}
              contentEditable="true" 
              className="form-control text-white  border-secondary t" 
              style={{ minHeight: "400px", height: "auto", padding: "20px" }}
              onInput={(e) => {
                if (e.currentTarget.innerHTML === '<br>') e.currentTarget.innerHTML = '';
              }}
            />

            <div className="form-actions d-flex justify-content-end mt-4 gap-3">
              <button 
                type="button"
                onClick={() => handleSave(false)} 
                className="btn btn-outline-secondary px-4 py-2 rounded-pill"
              >
                В черновик
              </button>
              <button 
                type="button"
                onClick={() => handleSave(true)} 
                className="custom-btn px-5 py-2"
              >
                Опубликовать
              </button>
            </div>
          </section>
        </div>
      </main>
    </div>
  );
}

export default WriteReviewPage;