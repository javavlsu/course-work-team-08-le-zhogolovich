import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";

const API_BASE_URL = "http://localhost:8080/movie-project";

const MovieReviews = ({ movieId }) => {
  const [reviews, setReviews] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchReviews = async () => {
      try {
        const res = await apiClient.get(`/reviews/movie/${movieId}`);
        setReviews(res.data.content || res.data || []);
      } catch (e) {
        console.error("Ошибка при загрузке рецензий:", e);
      } finally {
        setLoading(false);
      }
    };
    fetchReviews();
  }, [movieId]);

  const getStrippedContent = (content) => {
    return content
      ?.replace(/<\/p>|<\/div>|<\/h3>|<br\s*\/?>/gi, "\n")
      ?.replace(/<[^>]+>/g, "") || "";
  };

  if (loading) return <div className="text-white-50 py-4 text-center">Загрузка рецензий...</div>;

  return (
    <div className="reviews-list mt-4">
      {reviews.length > 0 ? (
        reviews.map((rev) => (
          <div
            className="article-container p-4 mx-auto text-start mb-4"
            key={rev.id}
            style={{ maxWidth: "900px" }}
          >
            <div className="d-flex flex-column flex-md-row gap-4 mb-4 align-items-stretch">
              {/* Постер фильма или обложка рецензии */}
              <img
                src={
                  rev.movieCover
                    ? `${API_BASE_URL}${rev.movieCover}`
                    : avatarDefault
                }
                className="article-img rounded-1 object-fit-cover"
                alt="Review cover"
                style={{ width: "180px", height: "260px" }}
              />

              <div className="d-flex flex-column gap-3 flex-grow-1">
                <div className="d-flex justify-content-between align-items-start">
                  <Link
                    to={`/users/${rev.authorName}`}
                    className="user-pill custom-btn align-self-start text-decoration-none"
                  >
                    @{rev.authorName || "user"}
                  </Link>

                  
                </div>

                <Link
                  to={`/reviews/${rev.id}`}
                  className="text-decoration-none text-white hover-opacity"
                >
                  <h3 className="fw-bold m-0">
                    {rev.title || "Название рецензии"}
                  </h3>
                </Link>

                <div className="d-flex align-items-center gap-3">
                  <div className="d-flex align-items-center gap-2">
                    <span className="fs-4 text-white fw-light">
                      {rev.likesCount || 0}
                    </span>
                    <i className="fa-regular text-white fa-heart fs-4"></i>
                  </div>
                  <small className="text-white-50 ms-auto">
                    {new Date(rev.createdAt).toLocaleDateString()}
                  </small>
                </div>
              </div>
            </div>

            <div className="article-content">
              <p className="article-text text-secondary mt-2" style={{ lineHeight: '1.6' }}>
                {getStrippedContent(rev.content).substring(0, 450)}...
              </p>
              <div className="d-flex justify-content-end">
                <Link
                  to={`/reviews/${rev.id}`}
                  className="arrow-btn text-decoration-none"
                >
                   ⟶
                </Link>
              </div>
            </div>
          </div>
        ))
      ) : (
        <div className="text-center py-5 text-white-50 italic">
          К этому фильму пока никто не написал рецензию. Станьте первым!
        </div>
      )}
    </div>
  );
};

export default MovieReviews;