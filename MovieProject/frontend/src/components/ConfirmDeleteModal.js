import React from "react";

const ConfirmDeleteModal = ({ show, title, message, onConfirm, onCancel }) => {
  if (!show) return null;

  return (
    <div className="modal-overlay" onClick={onCancel} style={{
      position: "fixed", top: 0, left: 0, width: "100%", height: "100%",
      backgroundColor: "rgba(0, 0, 0, 0.85)", display: "flex",
      justifyContent: "center", alignItems: "center", zIndex: 1100,
      backdropFilter: "blur(4px)"
    }}>
      <div 
        className="custom-modal-content p-4" 
        onClick={(e) => e.stopPropagation()}
        style={{
          width: "100%", maxWidth: "400px", backgroundColor: "#1a1a1a",
          border: "1px solid rgba(255, 255, 255, 0.1)", borderRadius: "20px",
          textAlign: "center"
        }}
      >
        <div className="mb-3">
          <i className="fa-solid fa-triangle-exclamation text-warning fs-1"></i>
        </div>
        <h4 className="text-white mb-2">{title || "Вы уверены?"}</h4>
        <p className="text-white-50 mb-4">{message}</p>
        
        <div className="d-flex gap-3">
          <button 
            className="btn btn-outline-light rounded-pill flex-grow-1 py-2"
            onClick={onCancel}
          >
            Отмена
          </button>
          <button 
            className="btn btn-danger rounded-pill flex-grow-1 py-2"
            onClick={onConfirm}
          >
            Удалить
          </button>
        </div>
      </div>
    </div>
  );
};

export default ConfirmDeleteModal;