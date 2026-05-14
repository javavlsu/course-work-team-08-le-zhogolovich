import React, { useEffect, useState, useCallback } from "react";
import { Link, useSearchParams } from "react-router-dom";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg"; // Твой импорт
const API_BASE_URL = "http://localhost:8080/movie-project";

function UsersPage() {
  const [searchParams, setSearchParams] = useSearchParams();
  const [users, setUsers] = useState([]);
  const [pagination, setPagination] = useState({ totalPages: 0, number: 0 });
  const [loading, setLoading] = useState(false);

  const query = searchParams.get("query") || "";
  const page = parseInt(searchParams.get("page") || "0", 10);
  const sortOrder = searchParams.get("sortOrder") || "desc";

  const updateFilters = (newParams) => {
    const current = Object.fromEntries([...searchParams]);
    if (!newParams.hasOwnProperty("page") && newParams.hasOwnProperty("query")) {
      newParams.page = 0;
    }
    setSearchParams({ ...current, ...newParams });
  };

  const fetchUsers = useCallback(async () => {
    setLoading(true);
    try {
      const res = await apiClient.get("/users", {
        params: { query, page, sortOrder, size: 20 },
      });
      setUsers(res.data.content || []);
      setPagination(res.data.page || { totalPages: 0, number: 0 });
    } catch (error) {
      console.error("Ошибка при загрузке пользователей:", error);
    } finally {
      setLoading(false);
    }
  }, [query, page, sortOrder]);

  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  const renderAvatar = (user) => {
    const src = user.avatarUrl 
      ? `${API_BASE_URL}${user.avatarUrl}` 
      : avatarDefault;

    return (
      <div className="user-avatar-container">
        <img
          src={src}
          alt={user.username}
          className="user-avatar-img"
        />
      </div>
    );
  };

  return (
    <div className="container-wrapper movie-page-bg">
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
                <Link to="/searchuser" className="nav-btn">
                 Пользователи
                </Link>
                <Link to="/profile" className="nav-btn">
                  Моя страница
                </Link>    
              </nav>
            </header>

      <main className="content px-4">
        <section className="d-flex flex-column align-items-center mb-5">
          <div className="position-relative w-100 mb-4" style={{ maxWidth: "500px" }}>
            <input
              type="text"
              className="search-input-pill"
              placeholder="Найти пользователя..."
              value={query}
              onChange={(e) => updateFilters({ query: e.target.value })}
            />
            <i className="fa-solid fa-magnifying-glass search-icon-pill"></i>
          </div>

          <div className="d-flex align-items-center gap-3">
            <span className="text-white-50 small">Сортировка:</span>
            <select
              className="filter-pill-select"
              value={sortOrder}
              onChange={(e) => updateFilters({ sortOrder: e.target.value })}
            >
              <option value="desc">Сначала новые</option>
              <option value="asc">Сначала старые</option>
            </select>
          </div>
        </section>

        <section className="container">
          {loading ? (
            <div className="text-center text-white opacity-50">Загрузка...</div>
          ) : (
            <div className="row g-4 justify-content-center">
              {users.length > 0 ? (
                users.map((user) => (
                  <div key={user.id} className="col-12 col-md-6 col-lg-4">
                    <Link to={`/users/${user.username}`} className="text-decoration-none">
                      <div className="user-card-pill">
                        <div className="d-flex align-items-start gap-3">
                          {renderAvatar(user)}
                          <div className="user-details w-100">
                            <div className="d-flex align-items-center justify-content-between gap-2 mb-1">
                              <span className="user-name text-white">
                                {user.username}
                              </span>
                            </div>
                            
                            {user.aboutMe && (
                              <div className="user-about-me text-light opacity-75 small">
                                {user.aboutMe}
                              </div>
                            )}
                          </div>
                        </div>
                      </div>
                    </Link>
                  </div>
                ))
              ) : (
                <div className="text-center text-white-50 py-5">Никого не нашли</div>
              )}
            </div>
          )}

          {/* Пагинация */}
          {pagination.totalPages > 1 && (
            <div className="d-flex justify-content-center gap-2 mt-5 mb-5">
              <button
                className="pag-circle"
                disabled={page === 0}
                onClick={() => updateFilters({ page: page - 1 })}
              >
                <i className="fa-solid fa-chevron-left"></i>
              </button>
              <div className="pag-circle active">{page + 1}</div>
              <button
                className="pag-circle"
                disabled={page >= pagination.totalPages - 1}
                onClick={() => updateFilters({ page: page + 1 })}
              >
                <i className="fa-solid fa-chevron-right"></i>
              </button>
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default UsersPage;