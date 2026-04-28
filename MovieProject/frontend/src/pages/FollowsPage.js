import React, { useEffect, useState } from "react";
import { useParams, Link, useLocation } from "react-router-dom";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";

const API_BASE_URL = "http://localhost:8080/movie-project";

function FollowsPage() {
  const { username } = useParams();
  const location = useLocation();
  
  const isFollowersPage = location.pathname.includes("followers");
  
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setLoading(true);
        let endpoint = "";
        if (username === "me" || !username) {
            endpoint = isFollowersPage ? "/users/me/followers" : "/users/me/followings";
        } else {
            endpoint = isFollowersPage ? `/users/${username}/followers` : `/users/${username}/followings`;
        }

        const res = await apiClient.get(endpoint);
        setUsers(res.data || []);
      } catch (err) {
        console.error("Ошибка загрузки списка:", err);
      } finally {
        setLoading(false);
      }
    };
    fetchUsers();
  }, [username, isFollowersPage]);

  if (loading) return <div className="text-white text-center mt-5">Загрузка...</div>;

  return (
    <div className="container mt-5 pt-5">
      <div className="mx-auto" style={{ maxWidth: "600px" }}>
        <h2 className="text-white mb-4 border-bottom pb-2 fw-light">
          {isFollowersPage ? `Подписчики @${username}` : `Подписки @${username}`}
        </h2>

        {users.length > 0 ? (
          <div className="d-flex flex-column gap-3">
            {users.map((u) => (
              <Link 
                key={u.id} 
                to={`/users/${u.username}`} 
                className="d-flex align-items-center gap-3 p-3 rounded-4 text-decoration-none transition-hover"
                style={{ background: "rgba(255,255,255,0.05)", border: "1px solid rgba(255,255,255,0.1)" }}
              >
                <img 
                  src={u.avatarUrl ? `${API_BASE_URL}${u.avatarUrl}` : avatarDefault} 
                  alt={u.username}
                  className="rounded-circle object-fit-cover"
                  style={{ width: "50px", height: "50px" }}
                />
                <div>
                  <div className="text-white fw-bold">@{u.username}</div>
                  <div className="text-white-50 small">{u.aboutMe?.substring(0, 50) || "Киноман"}</div>
                </div>
              </Link>
            ))}
          </div>
        ) : (
          <div className="text-center text-white-50 mt-5">
            Здесь пока пусто
          </div>
        )}
        
        <div className="mt-4 text-center">
            <Link to={username === 'me' ? '/profile' : `/users/${username}`} className="nav-btn text-white-50">
                ← Вернуться в профиль
            </Link>
        </div>
      </div>
    </div>
  );
}

export default FollowsPage;