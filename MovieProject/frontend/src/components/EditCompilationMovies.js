import React from "react";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";
import ConfirmDeleteModal from "./ConfirmDeleteModal";
import  { useState } from "react";

const API_BASE_URL = "http://localhost:8080/movie-project";


const EditCompilationMovies = ({ compilationId, movies, onMovieRemoved }) => {
    const [showConfirm, setShowConfirm] = useState(false);
  const [movieToDelete, setMovieToDelete] = useState(null);
  
  const handleRemove = async (movieId) => {
    if (!window.confirm("Вы уверены, что хотите удалить этот фильм из подборки?")) return;

    try {
      await apiClient.delete(`/compilations/${compilationId}/movie/${movieId}`);
      onMovieRemoved(movieId);
    } catch (error) {
      console.error("Ошибка при удалении фильма", error);
      alert("Не удалось удалить фильм");
    }
  };
  const handleConfirmDelete = async () => {
    if (!movieToDelete) return;

    try {
      await apiClient.delete(`/compilations/${compilationId}/movie/${movieToDelete.id}`);
      onMovieRemoved(movieToDelete.id);
    } catch (error) {
      console.error("Ошибка при удалении", error);
      alert("Не удалось удалить фильм");
    } finally {
      setShowConfirm(false);
      setMovieToDelete(null);
    }
  };

  if (!movies || movies.length === 0) {
    return <p className="text-white-50 mt-4 text-center">В этой подборке пока нет фильмов.</p>;
  }
  const openConfirm = (movie) => {
    setMovieToDelete(movie);
    setShowConfirm(true);
  };

  return (
    <div className="mt-5">
      <h3 className="text-white mb-4">Фильмы в подборке ({movies.length})</h3>
      <div className="d-flex flex-column gap-2">
        {movies.map((movie) => (
  <div 
    key={movie.id} 
    className="d-flex align-items-center justify-content-between p-3"
    style={{ 
      background: "rgba(255,255,255,0.05)", 
      border: "1px solid rgba(255,255,255,0.1)",
      borderRadius: "12px"
    }}
  >
    <div className="d-flex align-items-center gap-3">
      <img 
        src={movie.posterUrl ? `${movie.posterUrl}` : avatarDefault} 
        alt={movie.name}
        style={{ width: "45px", height: "65px", objectFit: "cover", borderRadius: "6px" }}
      />
      <div>
        <div className="text-white fw-bold">{movie.name}</div>
        <div className="text-white-50 small">{movie.releaseYear}</div>
      </div>
    </div>
    
    {/* Заменяем ссылку на кнопку с явным стилем */}
    <button 
      type="button"
      className="btn btn-danger btn-sm rounded-pill px-3"
      onClick={() => openConfirm(movie)}
      style={{ 
        fontSize: "0.8rem",
        fontWeight: "600",
        border: "none"
      }}
    >
      Удалить
    </button>
  </div>
))}
      </div>
      <ConfirmDeleteModal 
        show={showConfirm}
        title="Удаление фильма"
        message={`Вы действительно хотите удалить "${movieToDelete?.name}" из этой подборки?`}
        onConfirm={handleConfirmDelete}
        onCancel={() => setShowConfirm(false)}
      />
    </div>
   
  );
};

export default EditCompilationMovies;