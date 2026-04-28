import React, { useState, useEffect, useCallback } from "react";
import { Link } from "react-router-dom";
import apiClient from "../api/apiClient";

const API_BASE_URL = "http://localhost:8080/movie-project";

const CommentItem = ({ comment, currentUser, onDelete, onEdit, avatarDefault }) => {
  const [isEditing, setIsEditing] = useState(false);
  const [editText, setEditText] = useState(comment.content);

  const authorName = comment.authorName || "Аноним";

  return (
    <div className="p-3 rounded-4 mb-3" style={{ background: "rgba(255,255,255,0.05)" }}>
      <div className="d-flex justify-content-between align-items-start mb-2">
        <div className="d-flex align-items-center gap-2">
          <Link to={authorName !== "Аноним" ? `/users/${authorName}` : "#"}>
            <img
              src={comment.authorAvatar ? `${API_BASE_URL}${comment.authorAvatar}` : avatarDefault}
              width="35" height="35" className="rounded-circle"
              style={{ objectFit: "cover" }} alt="avatar"
            />
          </Link>
          <Link to={authorName !== "Аноним" ? `/users/${authorName}` : "#"} className="text-decoration-none fw-bold text-info">
            @{authorName}
          </Link>
        </div>

        {currentUser && currentUser.username === authorName && (
          <div className="d-flex gap-2">
            <button className="btn btn-sm btn-outline-light border-0" onClick={() => setIsEditing(true)}>
              <i className="fa-solid fa-pen"></i>
            </button>
            <button className="btn btn-sm btn-outline-danger border-0" onClick={() => onDelete(comment.id)}>
              <i className="fa-solid fa-trash"></i>
            </button>
          </div>
        )}
      </div>

      {isEditing ? (
        <div className="mt-2">
          <textarea
            className="form-control custom-input text-white mb-2"
            value={editText}
            onChange={(e) => setEditText(e.target.value)}
          />
          <div className="d-flex gap-2 justify-content-end">
            <button className="btn btn-sm btn-link text-white text-decoration-none" onClick={() => setIsEditing(false)}>
              Отмена
            </button>
            <button
              className="custom-btn py-1 px-3"
              onClick={() => {
                if (!editText.trim()) return alert("Комментарий не может быть пустым");
                onEdit(comment.id, editText);
                setIsEditing(false);
              }}
            >
              Сохранить
            </button>
          </div>
        </div>
      ) : (
        <p className="m-0 ps-1" style={{ whiteSpace: "pre-wrap" }}>{comment.content}</p>
      )}
    </div>
  );
};

const MovieComments = ({ movieId, currentUser, isAuth, avatarDefault }) => {
  const [comments, setComments] = useState([]);
  const [commentText, setCommentText] = useState("");

  const fetchComments = useCallback(async () => {
    try {
      const res = await apiClient.get(`/comment/movie/${movieId}`);
      setComments(res.data.content || res.data || []);
    } catch (e) {
      console.error("Ошибка загрузки комментариев", e);
      setComments([]);
    }
  }, [movieId]);

  useEffect(() => {
    fetchComments();
  }, [fetchComments]);

  const handleSendComment = async () => {
    if (!commentText.trim()) return;
    try {
      await apiClient.post(`/comment/movie/${movieId}`, { content: commentText });
      setCommentText("");
      fetchComments();
    } catch (e) {
      alert("Не удалось отправить комментарий");
    }
  };

  const handleDeleteComment = async (commentId) => {
    if (!window.confirm("Удалить комментарий?")) return;
    try {
      await apiClient.delete(`/comment/${commentId}`);
      setComments((prev) => prev.filter((c) => c.id !== commentId));
    } catch (e) {
      alert("Ошибка при удалении");
    }
  };

  const handleSaveEdit = async (commentId, newContent) => {
    try {
      await apiClient.patch(`/comment/${commentId}`, { content: newContent });
      fetchComments();
    } catch (e) {
      alert("Ошибка при сохранении");
    }
  };

  return (
    <section className="mb-5">
      <h2 className="section-title fw-light text-center mb-5">Комментарии</h2>

      {isAuth ? (
        <div className="p-4 mb-5 mx-auto" style={{ maxWidth: "800px" }}>
          <div className="d-flex align-items-center gap-3 mb-3">
            <img
              src={currentUser.avatarUrl ? `${API_BASE_URL}${currentUser.avatarUrl}` : avatarDefault}
              className="rounded-circle" width="40" height="40" alt="Avatar" style={{ objectFit: "cover" }}
            />
            <Link to="/profile" className="text-decoration-none text-white">
              @{currentUser.username || "loading..."}
            </Link>
          </div>
          <textarea
            className="form-control text-white custom-input mb-3"
            style={{ minHeight: "100px" }}
            placeholder="Введите текст..."
            value={commentText}
            onChange={(e) => setCommentText(e.target.value)}
          />
          <div className="d-flex justify-content-end">
            <button onClick={handleSendComment} className="custom-btn py-2 px-4">Отправить</button>
          </div>
        </div>
      ) : (
        <div className="p-5 mb-5 mx-auto text-center border border-secondary rounded-4" style={{ maxWidth: "800px", background: "rgba(255,255,255,0.05)" }}>
          <p className="text-white-50 fs-5 mb-4">Вы пока не можете оставлять комментарии</p>
          <div className="d-flex justify-content-center gap-3">
            <Link to="/register" className="custom-btn py-2 px-4 text-decoration-none">Зарегистрироваться</Link>
            <Link to="/login" className="btn btn-outline-light rounded-pill py-2 px-4">Войти</Link>
          </div>
        </div>
      )}

      <div className="comment-list d-flex flex-column gap-2 mx-auto text-white" style={{ maxWidth: "800px" }}>
        {comments.length > 0 ? (
          comments.map((comment) => (
            <CommentItem
              key={comment.id}
              comment={comment}
              currentUser={currentUser}
              avatarDefault={avatarDefault}
              onDelete={handleDeleteComment}
              onEdit={handleSaveEdit}
            />
          ))
        ) : (
          <p className="text-center text-white-50">Комментариев пока нет</p>
        )}
      </div>
    </section>
  );
};

export default MovieComments;