import React, { useState, useEffect } from "react";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";

const API_BASE_URL = "http://localhost:8080/movie-project";

const AddToCompilationModal = ({ movieId, onClose, onSuccess }) => {
  const [compilations, setCompilations] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchMyCompilations = async () => {
      setLoading(true);
      try {
        const res = await apiClient.get("/compilations/my");
        setCompilations(res.data);
      } catch (e) {
        console.error("Ошибка загрузки подборок", e);
      } finally {
        setLoading(false);
      }
    };
    fetchMyCompilations();
  }, []);

  const toggleSelection = (id) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
    );
  };

  const handleSave = async () => {
    if (selectedIds.length === 0) return;
    try {
      await apiClient.post(`/movies/${movieId}/compilations`, {
        compilationIds: selectedIds,
      });
      onSuccess(selectedIds.length);
      onClose();
    } catch (e) {
      alert("Ошибка при добавлении");
    }
  };

  const filtered = compilations.filter((c) =>
    c.title.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="custom-modal-content" onClick={(e) => e.stopPropagation()}>
        <div className="modal-header-custom d-flex justify-content-between align-items-center mb-4">
          <h4 className="text-white m-0">Добавить в подборки</h4>
          <button className="btn-close btn-close-white" onClick={onClose}></button>
        </div>

        <input
          type="text"
          className="form-control custom-input2 mb-4"
          placeholder="Поиск..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />

        <div style={{ maxHeight: "350px", overflowY: "auto" }} className="mb-4">
          {loading ? (
            <p className="text-center text-white-50">Загрузка...</p>
          ) : (
            filtered.map((comp) => {
              const isSelected = selectedIds.includes(comp.id);
              return (
                <div
                  key={comp.id}
                  onClick={() => toggleSelection(comp.id)}
                  className="d-flex align-items-center justify-content-between p-3 mb-2"
                  style={{
                    cursor: "pointer",
                    border: isSelected ? "1px solid white" : "1px solid transparent",
                    backgroundColor: isSelected ? "rgba(255, 255, 255, 0.15)" : "rgba(255,255,255,0.05)",
                    borderRadius: "12px",
                    transition: "0.2s"
                  }}
                >
                  <div className="d-flex align-items-center gap-3">
                    <img
                      src={comp.coverUrl ? `${API_BASE_URL}${comp.coverUrl}` : avatarDefault}
                      alt="cover"
                      style={{ width: "45px", height: "45px", objectFit: "cover", borderRadius: "8px" }}
                    />
                    <div className={isSelected ? "text-white" : "text-white-50"}>{comp.title}</div>
                  </div>
                  <i className={`fs-4 ${isSelected ? "fa-solid fa-circle-check text-white" : "fa-regular fa-circle text-secondary"}`}></i>
                </div>
              );
            })
          )}
        </div>

        <button 
          className="custom-btn w-100 py-3" 
          disabled={selectedIds.length === 0}
          onClick={handleSave}
          style={{ opacity: selectedIds.length === 0 ? 0.5 : 1, border: "1px solid white", background: "transparent", color: "white" }}
        >
          Добавить ({selectedIds.length})
        </button>
      </div>
    </div>
  );
};

export default AddToCompilationModal;