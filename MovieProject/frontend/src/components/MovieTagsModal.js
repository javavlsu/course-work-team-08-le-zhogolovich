import React, { useState, useEffect } from "react";
import apiClient from "../api/apiClient";

const MovieTagsModal = (props) => {
  const { show, movieId, currentTags, onClose, onTagsUpdated } = props;
  
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [newTagName, setNewTagName] = useState("");
  //  состояние для типа тега (по умолчанию THEME) удалить потом
  const [newTagType, setNewTagType] = useState("THEME"); 
  const [isSearching, setIsSearching] = useState(false);

  // Список типов удалить потом
  const tagTypes = ["THEME", "TROPE", "STYLE", "MOOD", "CHARACTER"];

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      if (searchQuery.trim()) {
        searchTags();
      } else {
        setSearchResults([]);
      }
    }, 300);
    return () => clearTimeout(delayDebounceFn);
  }, [searchQuery]);

  const searchTags = async () => {
    setIsSearching(true);
    try {
      const response = await apiClient.get(`/tags/search`, {
        params: { query: searchQuery, page: 0 }
      });
      setSearchResults(response.data.content || []);
    } catch (error) {
      console.error("Ошибка поиска тегов:", error);
    } finally {
      setIsSearching(false);
    }
  };

  const handleAttachTag = async (tagId) => {
    try {
      await apiClient.post(`/movies/${movieId}/tags/${tagId}`);
      onTagsUpdated();
    } catch (error) {
      alert("Не удалось привязать тег");
    }
  };

  const handleDetachTag = async (tagId) => {
    try {
      await apiClient.delete(`/movies/${movieId}/tags/${tagId}`);
      onTagsUpdated();
    } catch (error) {
      alert("Не удалось отвязать тег");
    }
  };

  const handleCreateTag = async () => {
    if (!newTagName.trim()) return;
    try {
      const response = await apiClient.post(`/tags`, { 
        name: newTagName, 
        type: newTagType 
      });
      await handleAttachTag(response.data.id);
      setNewTagName("");
    } catch (error) {
      console.error(error);
      alert("Ошибка при создании тега. Проверьте правильность типа.");
    }
  };

  if (!show) return null;

  return (
    <div className="modal-overlay" style={styles.overlay} onClick={onClose}>
      <div className="modal-content" style={styles.content} onClick={e => e.stopPropagation()}>
        <h2 className="text-white text-center mb-4">Теги фильма</h2>

        {/* Список текущих тегов */}
        <div className="tags-container mb-4" style={styles.tagsBox}>
  {currentTags && currentTags.map((tagName, index) => (
    <div key={index} className="d-flex justify-content-between align-items-center mb-2 p-2" style={styles.tagItem}>
      <span className="text-dark fw-bold">{tagName}</span>
      <button 
        className="btn btn-danger btn-sm rounded-pill px-3"
        // исправить удаление, сделать по Id когда починят бэк
        onClick={() => handleDetachTag(tagName)} 
      >
        удалить
      </button>
    </div>
  ))}
</div>

        {/* Поиск */}
        <div className="search-section p-3 mb-4" style={styles.searchBox}>
          <input 
            type="text" 
            className="form-control mb-2" 
            placeholder="поиск существующих тегов"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={styles.input}
          />
          <div className="dropdown-results" style={styles.results}>
            {isSearching ? <div className="text-white-50 small">Поиск...</div> : null}
            {searchResults.map(tag => (
              <div key={tag.id} className="p-2 search-item" onClick={() => handleAttachTag(tag.id)} style={styles.searchItem}>
                {tag.name} <small className="opacity-50">({tag.type})</small>
              </div>
            ))}
          </div>
        </div>

        {/* Создание нового тега */}
        <div className="create-section text-center">
          <h4 className="text-white mb-3">Создание нового тега</h4>
          <div className="p-3" style={styles.searchBox}>
            <input 
              type="text" 
              className="form-control mb-2" 
              placeholder="Название тега"
              value={newTagName}
              onChange={(e) => setNewTagName(e.target.value)}
              style={styles.input}
            />
            
            {/* Выбор типа тега */}
            <select 
              className="form-select mb-3" 
              value={newTagType} 
              onChange={(e) => setNewTagType(e.target.value)}
              style={styles.select}
            >
              {tagTypes.map(type => (
                <option key={type} value={type}>{type}</option>
              ))}
            </select>

            <button className="btn btn-success rounded-pill px-4" onClick={handleCreateTag}>
              создать и добавить
            </button>
          </div>
        </div>

        <button className="btn btn-outline-light mt-4 rounded-pill w-100" onClick={onClose}>
          Закрыть
        </button>
      </div>
    </div>
  );
};

const styles = {
  overlay: {
    position: 'fixed', top: 0, left: 0, width: '100%', height: '100%',
    backgroundColor: 'rgba(0,0,0,0.8)', display: 'flex', justifyContent: 'center',
    alignItems: 'center', zIndex: 1050, backdropFilter: 'blur(5px)'
  },
  content: {
    backgroundColor: '#2b2d3d', padding: '30px', borderRadius: '20px',
    width: '100%', maxWidth: '450px', border: '1px solid rgba(255,255,255,0.1)',
    maxHeight: '90vh', overflowY: 'auto'
  },
  tagsBox: { backgroundColor: '#5d5e6d', padding: '15px', borderRadius: '15px',maxHeight: '100px', 
    overflowY: 'auto' },
  tagItem: { backgroundColor: '#fff5f5', borderRadius: '10px' },
  searchBox: { backgroundColor: '#5d5e6d', borderRadius: '15px' },
  results: {
    maxHeight: '150px',
    overflowY: 'auto',
    marginTop: '5px'
  },
  input: { backgroundColor: '#d9d9d9', border: 'none', borderRadius: '20px', textAlign: 'center' },
  select: { backgroundColor: '#d9d9d9', border: 'none', borderRadius: '20px', textAlign: 'center', cursor: 'pointer' },
  searchItem: { cursor: 'pointer', color: 'white', borderBottom: '1px solid rgba(255,255,255,0.1)' }
};

export default MovieTagsModal;