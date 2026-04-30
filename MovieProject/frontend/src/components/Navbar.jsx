import React from "react";
import { NavLink } from "react-router-dom";

function Navbar({ onLogout, isMyProfile }) {
  // Функция для применения классов активной ссылки
  const getNavLinkClass = ({ isActive }) => 
    `nav-btn text-decoration-none ${isActive ? "active-nav" : ""}`;

  return (
    <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
      <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
        <NavLink to="/" className={getNavLinkClass}>
          Главная
        </NavLink>
        <NavLink to="/movies" className={getNavLinkClass}>
          Фильмы
        </NavLink>
        <NavLink to="/collections" className={getNavLinkClass}>
          Подборки
        </NavLink>
        <NavLink to="/reviews" className={getNavLinkClass}>
          Рецензии
        </NavLink>
        <NavLink to="/profile" className={getNavLinkClass}>
          Моя страница
        </NavLink>

        {isMyProfile && onLogout && (
          <button
            onClick={onLogout}
            className="nav-btn border-0 bg-transparent text-danger fw-bold"
          >
            Выйти
          </button>
        )}
      </nav>
    </header>
  );
}

export default Navbar;