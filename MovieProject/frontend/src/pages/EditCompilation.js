import React, { useState, useEffect } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import apiClient from "../api/apiClient";
import "bootstrap/dist/css/bootstrap.min.css";

const API_BASE_URL = "http://localhost:8080/movie-project";

const EditCompilation = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [compilation, setCompilation] = useState({
    title: "",
    description: "",
    coverUrl: "",
    isPublic: true,
  });

  const [selectedFile, setSelectedFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [loading, setLoading] = useState(false);
  const [uploadingCover, setUploadingCover] = useState(false);

  useEffect(() => {
    const fetchCompilation = async () => {
      try {
        const response = await apiClient.get(`/compilations/${id}`);
        setCompilation(response.data);
      } catch (error) {
        console.error("Ошибка загрузки", error);
        alert("Не удалось загрузить подборку");
      }
    };

    fetchCompilation();
  }, [id, navigate]);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

     // Проверка типа файла
    if (!file.type.startsWith("image/")) {
      alert("Можно загружать только изображения");
      return;
    }

    // Проверка размера (макс 5MB)
    if (file.size > 5 * 1024 * 1024) {
      alert("Файл не должен превышать 5MB");
      return;
    }
    setSelectedFile(file);
    setPreviewUrl(URL.createObjectURL(file));
  };

  const getCoverImage = () => {
    if (previewUrl) return previewUrl;
    if (compilation.coverUrl) return `${API_BASE_URL}${compilation.coverUrl}`;
    return "/images/default-collection.png";
  };

  const updateCompilationInfo = async () => {
    const updateData = {
      title: compilation.title,
      description: compilation.description,
      isPublic: compilation.isPublic,
    };

    await apiClient.patch(`/compilations/${id}`, updateData);
  };

  const updateCompilationCover = async () => {
    if (!selectedFile) return;

    const formData = new FormData();
    formData.append("file", selectedFile); // ВАЖНО: поле должно называться "file"

    await apiClient.patch(`/compilations/${id}/cover`, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      // Сначала обновляем текстовую информацию
      await updateCompilationInfo();

      // Если есть новый файл, обновляем обложку
      if (selectedFile) {
        setUploadingCover(true);
        await updateCompilationCover();
      }

      alert("Подборка успешно обновлена!");
      navigate(`/compilations/${id}`);
    } catch (error) {
      console.error("Ошибка сохранения", error);

      let errorMessage = "Не удалось сохранить изменения";
      
      if (error.response?.status === 413) {
        errorMessage = "Файл слишком большой";
      } else if (error.response?.data?.message) {
        errorMessage = error.response.data.message;
      }

      alert(errorMessage);
    } finally {
      setLoading(false);
      setUploadingCover(false);
    }
  };

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
            Профиль
          </Link>
        </nav>
      </header>

      <main className="container-xl px-4 mt-5">
        <div style={{ maxWidth: "900px", margin: "0 auto" }}>
          <h2 className="text-white mb-5">Редактирование подборки</h2>

          <form onSubmit={handleSubmit} className="row">
            {/* Секция фото */}
            <div className="col-md-4 d-flex flex-column align-items-center mb-4">
              <div
                className="shadow mb-3"
                style={{
                  width: "100%",
                  aspectRatio: "2/3",
                  borderRadius: "15px",
                  overflow: "hidden",
                  border: "2px solid #444",
                }}
              >
                <img
                  src={getCoverImage()}
                  alt="Cover"
                  style={{ width: "100%", height: "100%", objectFit: "cover" }}
                />
              </div>
              <label className="custom-btn user-pill py-2 px-4 text-center cursor-pointer">
                Сменить обложку
                <input
                  type="file"
                  className="d-none"
                  accept="image/*"
                  onChange={handleFileChange}
                />
              </label>
            </div>

            {/* Секция полей */}
            <div className="col-md-8">
              <div className="mb-4">
                <label className="text-white-50 mb-2 d-block">Название</label>
                <input
                  type="text"
                  className="form-control bg-transparent text-white border-white rounded-3 fs-4"
                  value={compilation.title}
                  onChange={(e) =>
                    setCompilation({ ...compilation, title: e.target.value })
                  }
                  required
                />
              </div>

              <div className="mb-4">
                <label className="text-white-50 mb-2 d-block">Описание</label>
                <textarea
                  className="form-control bg-transparent text-white border-white rounded-3 fs-5"
                  rows="6"
                  value={compilation.description}
                  onChange={(e) =>
                    setCompilation({
                      ...compilation,
                      description: e.target.value,
                    })
                  }
                  style={{ resize: "none" }}
                />
              </div>
              <div
                className="mb-4 d-flex align-items-center gap-3 p-3 rounded-3"
                style={{ background: "rgba(255,255,255,0.05)" }}
              >
                <div className="form-check form-switch m-0">
                  <input
                    className="form-check-input cursor-pointer"
                    type="checkbox"
                    id="privacySwitch"
                    style={{ width: "40px", height: "20px" }}
                    // Если НЕ публичная (isPublic === false), значит чекбокс "Приватная" активен
                    checked={!compilation.isPublic}
                    onChange={(e) =>
                      setCompilation({
                        ...compilation,
                        isPublic: !e.target.checked,
                      })
                    }
                  />
                  <label
                    className="form-check-label text-white ms-2 cursor-pointer"
                    htmlFor="privacySwitch"
                    style={{ fontSize: "1.1rem" }}
                  >
                    Приватная подборка
                  </label>
                </div>
                <small className="text-white-50">
                  {compilation.isPublic
                    ? "(Видна всем пользователям)"
                    : "(Видна только вам)"}
                </small>
              </div>

              <div className="d-flex gap-3 mt-5">
                <button
                  type="submit"
                  className="custom-btn user-pill py-3 px-5 flex-grow-1"
                  disabled={loading}
                >
                  {loading ? "Сохранение..." : "Сохранить изменения"}
                </button>
                <button
                  type="button"
                  className="btn btn-outline-secondary rounded-pill px-4"
                  onClick={() => navigate(-1)}
                >
                  Отмена
                </button>
              </div>
            </div>
          </form>
        </div>
      </main>
    </div>
  );
};

export default EditCompilation;
