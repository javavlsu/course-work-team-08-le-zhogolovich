import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

const CreateCompilationPage = () => {
    const navigate = useNavigate();
    
    // Состояния полей формы
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [isPublic, setIsPublic] = useState(true);
    const [imageFile, setImageFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [loading, setLoading] = useState(false);

    // Обработка выбора файла (картинки)
    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setImageFile(file);
            // Создаем локальную ссылку для превью
            const reader = new FileReader();
            reader.onload = (event) => {
                setPreviewUrl(event.target.result);
            };
            reader.readAsDataURL(file);
        }
    };

    // Отправка данных на сервер
    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!title.trim()) {
            alert("Пожалуйста, введите название подборки");
            return;
        }

        setLoading(true);
        const token = localStorage.getItem("token");

        // Создаем FormData для передачи Multipart данных
        const formData = new FormData();
        formData.append("title", title);
        formData.append("description", description);
        formData.append("isPublic", isPublic);

        // Ключ "image" должен совпадать с именем поля в вашем Java DTO (CreateCompilationRequest)
        if (imageFile) {
            formData.append("cover", imageFile);
        }

        try {
            const response = await axios.post("http://localhost:8080/movie-project/compilations", formData, {
                headers: {
                    "Authorization": `Bearer ${token}`
                    // ВАЖНО: Content-Type НЕ ПИШЕМ, Axios выставит его сам с нужным boundary
                },
            });

            console.log("Успешно создано:", response.data);
            navigate("/profile"); // Возврат в профиль после создания
        } catch (error) {
            console.error("Ошибка при создании подборки:", error);
            const errorMsg = error.response?.data?.message || "Не удалось сохранить подборку";
            alert("Ошибка: " + errorMsg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container-wrapper">
            <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
                <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
                    <Link to="/" className="nav-btn">Главная</Link>
                    <Link to="/movies" className="nav-btn">Фильмы</Link>
                    <Link to="/collections" className="nav-btn">Подборки</Link>
                    <Link to="/reviews" className="nav-btn">Рецензии</Link>
                    <Link to="/profile" className="nav-btn">Моя страница</Link>
                </nav>
            </header>

            <main className="container-xl px-4 px-md-5">
                <form className="collection-form mx-auto" style={{ maxWidth: "900px" }} onSubmit={handleSubmit}>
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
                            {/* Загрузка обложки */}
                            <label className="cover-upload d-flex flex-column align-items-center justify-content-center text-center position-relative overflow-hidden" 
                                   style={{ width: "180px", height: "180px", cursor: "pointer", border: "1px dashed #555", borderRadius: "10px", background: "#222" }}>
                                <input 
                                    type="file" 
                                    accept="image/*" 
                                    hidden 
                                    onChange={handleFileChange}
                                />
                                {previewUrl ? (
                                    <img src={previewUrl} alt="Превью" className="w-100 h-100 object-fit-cover" />
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

                    {/* Настройка публичности */}
                    <div className="form-check mb-5">
                        <input 
                            className="form-check-input" 
                            type="checkbox" 
                            id="isPublicCheck" 
                            checked={isPublic} 
                            onChange={(e) => setIsPublic(e.target.checked)} 
                        />
                        <label className="form-check-label text-white" htmlFor="isPublicCheck">
                            Сделать подборку доступной для всех
                        </label>
                    </div>

                    <div className="sticky-actions-wrapper">
                        <div className="form-actions d-flex justify-content-between px-2">
                            <Link to="/movies" className="text-decoration-none action-btn custom-btn">
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