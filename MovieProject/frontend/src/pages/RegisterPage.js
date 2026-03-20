import React, { useState } from "react";
import axios from "axios";
import { useNavigate, Link } from "react-router-dom";
import "bootstrap/dist/css/bootstrap.min.css";
import taksaLogo from "../images/такса.svg"

function RegisterPage() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [email, setEmail] = useState(""); 
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
      setError("Пароли не совпадают");
      return;
    }

    try {
      await axios.post("http://localhost:8080/movie-project/auth/register", {
        username: username,
        email: email,           
        password: password  
      });


      navigate("/login");
    } catch (err) {
      setError(err.response?.data?.message || "Ошибка регистрации");
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

        <div className="container text-center">
          <img src={taksaLogo} className="img-fluid w-25" alt="logo" />
        </div>

        <div className="container mt-4 d-flex justify-content-center">

          <form className="login-form" onSubmit={handleSubmit}>

            <div className="input-container mb-3">
              <input
                type="text"
                className="search-input mb-3"
                placeholder="Логин"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />
            </div>

            <div className="input-container mb-3">
              <input
                type="email"
                className="search-input mb-3"
                placeholder="Email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="input-container mb-3">
              <input
                type="password"
                className="search-input mb-3"
                placeholder="Пароль"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <div className="input-container mb-4">
              <input
                type="password"
                className="search-input mb-3"
                placeholder="Повторите пароль"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>

            {error && (
              <div className="text-danger mb-3">
                {error}
              </div>
            )}

            <button type="submit" className="custom-btn w-100">
              Зарегистрироваться
            </button>

          </form>

        </div>

        <div className="mt-4 text-center">
          <span className="text-white-50 fs-5">
            Уже есть аккаунт?
          </span>
          <Link to="/login" className="register-link fs-5 ms-1">
            Войти
          </Link>
        </div>

      </main>
    </div>
  );
}

export default RegisterPage;