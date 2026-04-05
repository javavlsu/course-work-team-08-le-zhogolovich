import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import avatarDefault from "../images/такса.svg"; // дефолтный аватар
const API_BASE_URL = "http://localhost:8080/movie-project/backend";

const MoviePage = () => {
    const { id } = useParams();
    const navigate = useNavigate();

    // Состояния для фильма
    const [movie, setMovie] = useState(null);
    const [loading, setLoading] = useState(true);
    
    // Состояния для текущего пользователя (автора комментария)
    const [currentUser, setCurrentUser] = useState({
        username: '',
        avatarUrl: ''
    });

    // Логика интерфейса
    const [label, setLabel] = useState("Добавить метку");
    const [voted, setVoted] = useState(false);
    const [userRating, setUserRating] = useState(0);
    const [isAdmin, setIsAdmin] = useState(false);

    // Константы путей
    const avatarDefault = '../images/такса.svg';
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            navigate("/login");
            return;
        }

        // 1. Проверка роли админа
        try {
            const decoded = jwtDecode(token);
            if (decoded.role === 'ADMIN' || decoded.roles?.includes('ROLE_ADMIN')) {
                setIsAdmin(true);
            }
        } catch (e) {
            console.error("Ошибка декодирования токена", e);
        }

        // 2. Загрузка данных фильма и пользователя параллельно
        const fetchData = async () => {
            try {
                const [movieRes, userRes] = await Promise.all([
                    axios.get(`http://localhost:8080/movie-project/movies/${id}`, {
                        headers: { Authorization: `Bearer ${token}` }
                    }),
                    axios.get("http://localhost:8080/movie-project/users/me", {
                        headers: { Authorization: `Bearer ${token}` }
                    })
                ]);

                setMovie(movieRes.data);
                setUserRating(movieRes.data.rating || 0);
                setCurrentUser(userRes.data);
                setLoading(false);
            } catch (error) {
                console.error("Ошибка при загрузке данных", error);
                if (error.response?.status === 401) navigate('/login');
                setLoading(false);
            }
        };

        fetchData();
    }, [id, navigate, token]);

    const handleRatingChange = (e) => {
        const val = parseFloat(e.target.value);
        setUserRating(val);
        if (!voted) {
            setVoted(true);
            // Здесь можно добавить axios.post для сохранения рейтинга
        }
    };

    if (loading) return <div className="text-white text-center mt-5">Загрузка...</div>;
    if (!movie) return <div className="text-white text-center mt-5">Фильм не найден</div>;

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

            <main className="container-xl px-4">
                

                <div className="justify-content-center row g-5 mb-5">
                    {/* Постер */}
                    <div className="col-lg-3 col-md-5 text-center">
                        <div className="movie-card-static mb-4 mx-auto" style={{ maxWidth: "300px" }}>
                            <img src={movie.posterUrl} alt={movie.name} className="img-fluid rounded-3" />
                        </div>
                        <div className="dropdown">
                            <button className="custom-btn w-70 dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                {label}
                            </button>
                            <ul className="dropdown-menu dropdown-menu-dark custom-dropdown text-center">
                                <li><button className="dropdown-item" onClick={() => setLabel("Буду смотреть")}>Буду смотреть</button></li>
                                <li><button className="dropdown-item" onClick={() => setLabel("Просмотрено")}>Просмотрено</button></li>
                                <li><button className="dropdown-item" onClick={() => setLabel("Любимое")}>Любимое</button></li>
                                <li><hr className="dropdown-divider border-secondary" /></li>
                                <li><button className="dropdown-item text-danger" onClick={() => setLabel("Добавить метку")}>Убрать метку</button></li>
                            </ul>
                        </div>
                    </div>

                    {/* Описание и Жанры */}
                    <div className="col-lg-6 d-flex flex-column gap-4 position-relative">
                        <h1 className="text-center mb-5 text-white">{movie.name}</h1>
                        <div className="article-container p-3 mt-auto">
                            <p className="text-white fs-5 m-0">{movie.overview || movie.description}</p>
                        </div>

                        <div className="d-flex flex-wrap justify-content-center gap-2 mt-0 mb-auto">
                            {movie.genres?.map((genre, index) => (
                                <span key={index} className="tag-pill">{genre}</span>
                            ))}
                            {isAdmin && (
                                <button className="tag-pill tag-add-btn"><i className="fa-solid fa-plus"></i></button>
                            )}
                        </div>

                        {/* Рейтинг звездочками */}
<div className="d-flex align-items-center justify-content-start gap-4 mt-3">
    <div className="star-rating-interactive">
        {/* Скрытый ползунок, который управляет значением */}
        <input 
            type="range" 
            className="star-range-input" 
            min="0" 
            max="5" 
            step="0.5" // Шаг в пол-звезды, можно поставить 1
            value={userRating} 
            onChange={handleRatingChange} 
        />
        {/* Визуальные звезды */}
        <div className="stars-display">
            {[1, 2, 3, 4, 5].map((star) => (
                <i 
                    key={star} 
                    className={`fa-star ${userRating >= star ? 'fa-solid' : userRating >= star - 0.5 ? 'fa-solid fa-star-half-stroke' : 'fa-regular'}`}
                    style={{ color: userRating >= star - 0.5 ? '#FFD700' : '#444' }}
                ></i>
            ))}
        </div>
    </div>
    
    <div className="text-secondary fs-4">
        <strong className="text-white">{userRating}</strong> 
        <span className="ms-2" style={{ opacity: 0.6 }}>
            ({voted ? (movie.votesCount || 0) + 1 : (movie.votesCount || 0)} оценили)
        </span>
    </div>
</div>
                    </div>
                </div>

                {/* Секция комментариев */}
                <section className="mb-5">
                    <h2 className="section-title fw-light text-center mb-5">Комментарии</h2>
                    
                    {/* Форма нового комментария с данными ТЕКУЩЕГО пользователя */}
                    <div className="p-4 mb-5 mx-auto" style={{ maxWidth: "800px" }}>
                        <div className="d-flex align-items-center gap-3 mb-3">
                            <img 
                                 src={
                    currentUser.avatarUrl
                      ? `${API_BASE_URL}${currentUser.avatarUrl}`
                      : avatarDefault
                  }
                                className="rounded-circle" 
                                width="40" 
                                height="40" 
                                alt="My Avatar" 
                                style={{ objectFit: 'cover' }}
                            />
                            <Link to="/profile" className="text-decoration-none text-white">
                                @{currentUser.username || 'loading...'}
                            </Link>
                        </div>
                        <textarea className="form-control text-white custom-input mb-3" style={{ minHeight: "100px" }} placeholder="Введите текст..."></textarea>
                        <div className="d-flex justify-content-end">
                            <button className="custom-btn py-2 px-4">Отправить</button>
                        </div>
                    </div>

                    {/* Список всех комментариев */}
                    <div className="comment-list d-flex flex-column gap-4 mx-auto" style={{ maxWidth: "800px" }}>
                        <p>Комментариев пока нет</p>
                    </div>
                </section>
            </main>
        </div>
    );
};

export default MoviePage;