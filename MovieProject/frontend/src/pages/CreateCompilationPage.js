import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import apiClient from "../api/apiClient";
import "bootstrap/dist/css/bootstrap.min.css";

const CreateCompilationPage = () => {
  const navigate = useNavigate();

  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [isPublic, setIsPublic] = useState(true);
  const [imageFile, setImageFile] = useState(null);
  const [previewUrl, setPreviewUrl] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    if (!file) return;

    setImageFile(file);

    const reader = new FileReader();
    reader.onload = (event) => setPreviewUrl(event.target.result);
    reader.readAsDataURL(file);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!title.trim()) {
      alert("Введите название");
      return;
    }

    setLoading(true);

    const formData = new FormData();
    formData.append("title", title);
    formData.append("description", description);
    formData.append("isPublic", isPublic);

    if (imageFile) {
      formData.append("cover", imageFile);
    }

    try {
      await apiClient.post("/compilations", formData);
      navigate("/profile");
    } catch (error) {
      const msg = error.response?.data?.message || "Ошибка";
      alert(msg);
    } finally {
      setLoading(false);
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
          <Link to="/reviews" className="nav-btn">
            Рецензии
          </Link>
          <Link to="/profile" className="nav-btn">
            Моя страница
          </Link>
        </nav>
      </header>

      <main className="container-xl px-4 px-md-5">
        <form
          className="collection-form mx-auto"
          style={{ maxWidth: "900px" }}
          onSubmit={handleSubmit}
        >
          <div className="mb-4">
            {/* Название */}
            <input
              type="text"
              className="form-control custom-input mb-4"
              placeholder="Введите название..."
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              required
            />

            <div className="d-flex gap-4 align-items-start">
              <label
                className="cover-upload d-flex flex-column align-items-center justify-content-center text-center position-relative overflow-hidden"
                style={{
                  width: "180px",
                  height: "180px",
                  cursor: "pointer",
                  border: "1px dashed #555",
                  borderRadius: "10px",
                  background: "#222",
                }}
              >
                <input
                  type="file"
                  accept="image/*"
                  hidden
                  onChange={handleFileChange}
                />
                {previewUrl ? (
                  <img
                    src={previewUrl}
                    alt="Превью"
                    className="w-100 h-100 object-fit-cover"
                  />
                ) : (
                  <span className="text-white">Добавить обложку</span>
                )}
              </label>

              {/* Описание */}
              <textarea
                className="form-control custom-input h-auto"
                style={{ height: "180px", resize: "none" }}
                placeholder="Введите описание..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
              ></textarea>
            </div>
          </div>

          {/* Настройка приватности */}
          <div className="form-check mb-5">
            <input
              className="form-check-input"
              type="checkbox"
              id="isPublicCheck"
              checked={isPublic}
              onChange={(e) => setIsPublic(e.target.checked)}
            />
            <label
              className="form-check-label text-white"
              htmlFor="isPublicCheck"
            >
              Сделать подборку доступной для всех
            </label>
          </div>

          <div className="sticky-actions-wrapper">
            <div className="form-actions d-flex justify-content-between px-2">
              <Link
                to="/movies"
                className="text-decoration-none action-btn custom-btn"
              >
                К списку фильмов
              </Link>
              <button
                type="submit"
                className="action-btn custom-btn"
                disabled={loading}
              >
                {loading ? "Сохранение..." : "Сохранить"}
              </button>
            </div>
          </div>
        </form>
      </main>
    </div>
  );
};

export default CreateCompilationPage;
