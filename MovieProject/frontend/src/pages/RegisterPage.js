import React, { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import apiClient from "../api/apiClient";
import taksaLogo from "../images/такса.svg";
import "bootstrap/dist/css/bootstrap.min.css";

function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [error, setError] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (form.password !== form.confirmPassword) {
      return setError("Пароли не совпадают");
    }

    try {
      await apiClient.post("/auth/register", {
        username: form.username.trim(),
        email: form.email.trim(),
        password: form.password,
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
                onChange={(e) => setForm({ ...form, username: e.target.value })}
                required
              />
            </div>

            <div className="input-container mb-3">
              <input
                type="email"
                className="search-input mb-3"
                placeholder="Email"
                onChange={(e) => setForm({ ...form, email: e.target.value })}
                required
              />
            </div>

            <div className="input-container mb-3">
              <input
                type="password"
                className="search-input mb-3"
                placeholder="Пароль"
                onChange={(e) => setForm({ ...form, password: e.target.value })}
                required
              />
            </div>

            <div className="input-container mb-4">
              <input
                type="password"
                className="search-input mb-3"
                placeholder="Повторите пароль"
                onChange={(e) =>
                  setForm({ ...form, confirmPassword: e.target.value })
                }
                required
              />
            </div>

            {error && <div className="text-danger mb-3">{error}</div>}

            <button type="submit" className="custom-btn w-100">
              Зарегистрироваться
            </button>
          </form>
        </div>

        <div className="mt-4 text-center">
          <span className="text-white-50 fs-5">Уже есть аккаунт?</span>
          <Link to="/login" className="register-link fs-5 ms-1">
            Войти
          </Link>
        </div>
      </main>
    </div>
  );
}

export default RegisterPage;
