import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/apiClient";

function LikedPagination({ currentPage, totalItems, itemsPerPage, onPageChange }) {
  const totalPages = Math.ceil(totalItems / itemsPerPage);
  if (totalPages <= 1) return null;

  return (
    <div className="d-flex justify-content-center align-items-center gap-2 mt-5 mb-5">
      <button
        className="pag-circle"
        onClick={() => onPageChange(Math.max(1, currentPage - 1))}
        disabled={currentPage === 1}
      >
        &lt;
      </button>

      <button className="pag-circle active">
        {currentPage}
      </button>

      <button
        className="pag-circle"
        onClick={() => onPageChange(currentPage + 1)}
        disabled={currentPage >= totalPages}
      >
        &gt;
      </button>
    </div>
  );
}

const LikedContent = ({ userId, isMyProfile, renderGrid, itemsPerPage }) => {
  const [likedCompilations, setLikedCompilations] = useState([]);
  const [likedReviews, setLikedReviews] = useState([]);
  const [subTab, setSubTab] = useState("compilations");
  const [loading, setLoading] = useState(true);
  const [compPage, setCompPage] = useState(1);
  const [revPage, setRevPage] = useState(1);

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

  const handleSubTabChange = (tab) => {
    setSubTab(tab);
    setCompPage(1);
    setRevPage(1);
  };
  if (loading)
    return (
      <div className="text-white-50 text-center py-4">Загрузка лайков...</div>
    );

  const indexOfLastComp = compPage * itemsPerPage;
  const indexOfFirstComp = indexOfLastComp - itemsPerPage;
  const currentLikedCompilations = likedCompilations.slice(indexOfFirstComp, indexOfLastComp);

  const indexOfLastRev = revPage * itemsPerPage;
  const indexOfFirstRev = indexOfLastRev - itemsPerPage;
  const currentLikedReviews = likedReviews.slice(indexOfFirstRev, indexOfLastRev);

  return (
    <div className="mt-3">
      {/* Переключатель внутри таба */}
      <div className="d-flex gap-3 justify-content-center mb-4">
        <button
          className={`custom-btn py-1 px-3 fs-6 ${subTab === "compilations" ? "active" : "opacity-50"}`}
          onClick={() => handleSubTabChange("compilations")}
        >
          Подборки
        </button>
        <button
          className={`custom-btn py-1 px-3 fs-6 ${subTab === "reviews" ? "active" : "opacity-50"}`}
          onClick={() => handleSubTabChange("reviews")}
        >
          Рецензии
        </button>
      </div>

      {subTab === "compilations" ? (
        <>
          {renderGrid(currentLikedCompilations, "Нет понравившихся подборок")}
          <LikedPagination currentPage={compPage} totalItems={likedCompilations.length} itemsPerPage={itemsPerPage} onPageChange={setCompPage} />
        </>
      ) : (
        <div className="reviews-list">
          {currentLikedReviews.length > 0 ? (
            <>
              {currentLikedReviews.map((rev) => (
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
                      <small className="text-white-70">Автор: @{rev.authorName}</small>
                      <Link to={`/reviews/${rev.id}`} className="text-decoration-none text-white">
                        <h5 className="fw-bold mb-1">{rev.title}</h5>
                      </Link>
                      <Link to={`/movies/${rev.movieId}`} className="text-decoration-none text-white-50 hover-opacity">
                        <h6 className="fw-bold m-0">{rev.movieName || "Название фильма"}</h6>
                      </Link>
                    </div>
                    <span className="badge rounded-pill bg-danger d-flex align-items-center gap-2">
                      <i className="fa-solid fa-heart"></i>
                      <span>{rev.likesCount || 0}</span>
                    </span>
                  </div>
                </div>
              ))}
              <LikedPagination currentPage={revPage} totalItems={likedReviews.length} itemsPerPage={itemsPerPage} onPageChange={setRevPage} />
            </>
          ) : (
            <div className="text-center text-white-50 py-4">Нет понравившихся рецензий</div>
          )}
        </div>
      )}
    </div>
  );
};

export default LikedContent;
