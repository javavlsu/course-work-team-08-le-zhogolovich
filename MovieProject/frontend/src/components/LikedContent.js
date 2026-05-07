import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";

const API_BASE_URL = "http://localhost:8080/movie-project";

const LikedContent = ({ userId, isMyProfile, renderGrid }) => {
  const [likedCompilations, setLikedCompilations] = useState([]);
  const [likedReviews, setLikedReviews] = useState([]);
  const [subTab, setSubTab] = useState("compilations");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchLikedData = async () => {
      try {
        setLoading(true);
        const compUrl = isMyProfile
          ? "/compilations/my/liked"
          : `/compilations/user/${userId}/liked`;

        const revUrl = isMyProfile
          ? "/reviews/my/liked"
          : `/reviews/user/${userId}/liked`;

        const [compRes, revRes] = await Promise.all([
          apiClient.get(compUrl),
          apiClient.get(revUrl),
        ]);

        setLikedCompilations(compRes.data || []);
        setLikedReviews(revRes.data.content || revRes.data || []);
      } catch (err) {
        console.error("Ошибка при загрузке лайков:", err);
      } finally {
        setLoading(false);
      }
    };

    if (userId) fetchLikedData();
  }, [userId, isMyProfile]);

  if (loading)
    return (
      <div className="text-white-50 text-center py-4">Загрузка лайков...</div>
    );

  return (
    <div className="mt-3">
      {/* Переключатель внутри таба */}
      <div className="d-flex gap-3 justify-content-center mb-4">
        <button
          className={`custom-btn py-1 px-3 fs-6 ${subTab === "compilations" ? "active" : "opacity-50"}`}
          onClick={() => setSubTab("compilations")}
        >
          Подборки
        </button>
        <button
          className={`custom-btn py-1 px-3 fs-6 ${subTab === "reviews" ? "active" : "opacity-50"}`}
          onClick={() => setSubTab("reviews")}
        >
          Рецензии
        </button>
      </div>

      {subTab === "compilations" ? (
        renderGrid(likedCompilations, "Нет понравившихся подборок")
      ) : (
        <div className="reviews-list">
          {likedReviews.length > 0 ? (
            likedReviews.map((rev) => (
              <div
                key={rev.id}
                className="stats-card p-4 text-white mb-4"
                style={{
                  border: "1px solid rgba(255,255,255,0.2)",
                  borderRadius: "15px",
                  background: "rgba(255,255,255,0.03)",
                }}
              >
                <div className="d-flex justify-content-between align-items-start">
                  <div>
                    <small className="text-white-70">
                      Автор: @{rev.authorName}
                    </small>

                    <Link
                      to={`/reviews/${rev.id}`}
                      className="text-decoration-none text-white"
                    >
                      <h5 className="fw-bold mb-1">{rev.title}</h5>
                    </Link>
                    <Link
                      to={`/movies/${rev.movieId}`}
                      className="text-decoration-none text-white-50 hover-opacity"
                    >
                      <h6 className="fw-bold m-0">
                        {rev.movieName || "Название фильма"}
                      </h6>
                    </Link>
                  </div>
                  <span className="badge rounded-pill bg-danger d-flex align-items-center gap-2">
                    <i className="fa-solid fa-heart"></i>
                    <span>{rev.likesCount || 0}</span>
                  </span>
                </div>
              </div>
            ))
          ) : (
            <div className="text-center text-white-50 py-4">
              Нет понравившихся рецензий
            </div>
          )}
        </div>
      )}
    </div>
  );
};

export default LikedContent;
