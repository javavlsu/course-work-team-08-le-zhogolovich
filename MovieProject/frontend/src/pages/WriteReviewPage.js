import React, { useState, useEffect, useRef } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
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
  const [initialContent, setInitialContent] = useState("");
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
          alert("Доступ запрещен. Только для рецензентов.");
          navigate("/");
        } else {
          setIsCheckingRole(false);
        }
      } catch (err) {
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
          // Режим редактирования
          const reviewRes = await apiClient.get(`/reviews/${reviewId}`);
          const reviewData = reviewRes.data;
          
          setTitle(reviewData.title);
          setInitialContent(reviewData.content);

          // Загружаем данные фильма отдельно, чтобы получить название и нормальный постер
          const movieRes = await apiClient.get(`/movies/${reviewData.movieId}`);
          setMovie(movieRes.data);
        } else if (movieId) {
          const movieRes = await apiClient.get(`/movies/${movieId}`);
          setMovie(movieRes.data);
        }
      } catch (err) {
        console.error("Ошибка загрузки данных:", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [movieId, reviewId, isCheckingRole]);

  useEffect(() => {
    if (!loading && editorRef.current && initialContent) {
      editorRef.current.innerHTML = initialContent;
    }
  }, [loading, initialContent]);

  const handlePaste = (e) => {
    e.preventDefault();
    const text = e.clipboardData.getData("text/plain");
    document.execCommand("insertText", false, text);
  };

  const handleFormat = (command, value = null) => {
    document.execCommand(command, false, value);
    editorRef.current?.focus();
  };

  const handleSave = async (isPublish) => {
    if (!editorRef.current) return;

    const tempDiv = document.createElement("div");
    tempDiv.innerHTML = editorRef.current.innerHTML;
    const elementsWithStyle = tempDiv.querySelectorAll('[style]');
    elementsWithStyle.forEach(el => el.removeAttribute('style'));
    
    const cleanContent = tempDiv.innerHTML;

    if (!title || !cleanContent || cleanContent === "<br>") {
      alert("Заполните заголовок и текст рецензии");
      return;
    }

    const payload = {
      movieId: movie?.id,
      title: title,
      content: cleanContent,
      isPublish: isPublish 
    };

    console.log("Отправка данных:", payload); 

    try {
      if (reviewId) {
        await apiClient.patch(`/reviews/${reviewId}`, payload);
      } else {
        await apiClient.post("/reviews", payload);
      }
      navigate("/reviews");
    } catch (err) {
      console.error("Ошибка при сохранении:", err);
      alert("Ошибка при сохранении");
    }
  };

  if (isCheckingRole) return <div className="text-white text-center mt-5">Проверка прав...</div>;
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
          <i className="fa-solid fa-arrow-left"></i> Назад
        </button>

        <div className="row align-items-start mb-5 g-0">
          <div className="d-flex gap-4 align-items-start mb-5">
            {/* Постер фильма */}
            <div style={{ minWidth: "250px", width: "250px" }}>
              <div className="ratio ratio-4x3">
                <div className="cover-upload d-flex align-items-center justify-content-center border border-secondary rounded overflow-hidden bg-dark">
                  <img 
                    src={`${API_BASE_URL}${movie.posterUrl}` || avatarDefault} 
                    className="w-100 h-100 object-fit-cover" 
                    alt="Постер" 
                  />
                </div>
              </div>
            </div>

            <div className="d-flex flex-column gap-2 w-100">
              <h3 className="mb-2 ">
                {movie?.name || movie?.title || "Название фильма"}
              </h3>
              <textarea 
                className="form-control text-white border-secondary" 
                style={{ height: "100px", resize: "none", backgroundColor: "#2b2d33" }}
                placeholder="Заголовок вашей рецензии"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            </div>
          </div>

          <section className="mt-2">
            {/* Панель инструментов */}
            <div className="btn-toolbar mb-2 d-flex gap-2 p-2 bg-dark border border-secondary rounded">
              <button type="button" onClick={() => handleFormat('bold')} className="btn btn-sm btn-outline-light"><b>B</b></button>
              <button type="button" onClick={() => handleFormat('italic')} className="btn btn-sm btn-outline-light"><i>I</i></button>
              <button type="button" onClick={() => handleFormat('underline')} className="btn btn-sm btn-outline-light"><u>U</u></button>
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
              onPaste={handlePaste}
              className="form-control text-white border-secondary" 
              style={{ 
                minHeight: "400px", 
                height: "auto", 
                padding: "20px", 
                backgroundColor: "#2b2d33",
                outline: "none",
                color: "white"
              }}
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