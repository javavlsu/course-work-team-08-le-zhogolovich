import React, { useState, useEffect, useCallback } from "react";
import apiClient from "../api/apiClient";

const MovieTagsModal = (props) => {
  const { show, movieId, onClose } = props;
  
  const [currentTags, setCurrentTags] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [activeTypeFilter, setActiveTypeFilter] = useState("ALL"); // Новый стейт для фильтра
  
  const [newTagName, setNewTagName] = useState("");
  const [newTagType, setNewTagType] = useState("THEME"); 
  const [isSearching, setIsSearching] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  const tagTypes = ["THEME", "TROPE", "STYLE", "MOOD", "CHARACTER"];

  const fetchCurrentTags = useCallback(async () => {
    if (!movieId) return;
    setIsLoading(true);
    try {
      const response = await apiClient.get(`/movies/${movieId}/tags`);
      setCurrentTags(response.data || []);
    } catch (error) {
      console.error("Ошибка загрузки тегов фильма:", error);
    } finally {
      setIsLoading(false);
    }
  }, [movieId]);

  useEffect(() => {
    if (show && movieId) fetchCurrentTags();
  }, [show, movieId, fetchCurrentTags]);

const searchTags = useCallback(async () => {
  setIsSearching(true);
  try {
    const response = await apiClient.get(`/tags/search`, {
      params: { 
        query: searchQuery, // передаем поисковый запрос
        page: 0 
      }
    });

    let results = response.data.content || [];

    // КЛИЕНТСКАЯ ФИЛЬТРАЦИЯ: 
    // Если выбрана конкретная вкладка (не ALL), фильтруем результаты по типу
    if (activeTypeFilter !== "ALL") {
      results = results.filter(tag => tag.type === activeTypeFilter);
    }

    setSearchResults(results);
  } catch (error) {
    console.error("Ошибка поиска:", error);
  } finally {
    setIsSearching(false);
  }
}, [searchQuery, activeTypeFilter]);

useEffect(() => {

  if (searchQuery.trim() !== "" || activeTypeFilter !== "ALL") {
    const delayDebounceFn = setTimeout(() => {
      searchTags();
    }, 300);
    return () => clearTimeout(delayDebounceFn);
  } else {
    setSearchResults([]);
  }
}, [searchQuery, activeTypeFilter, searchTags]);

  const handleAttachTag = async (tagId) => {
    try {
      await apiClient.post(`/movies/${movieId}/tags/${tagId}`);
      fetchCurrentTags();
    } catch (error) {
      alert("Тег уже добавлен");
    }
  };

  const handleDetachTag = async (tagId) => {
    try {
      await apiClient.delete(`/movies/${movieId}/tags/${tagId}`);
      fetchCurrentTags();
    } catch (error) {
      alert("Не удалось удалить тег");
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
      alert("Ошибка при создании");
    }
  };

  if (!show) return null;

  return (
    <div className="modal-overlay" style={styles.overlay} onClick={onClose}>
      <div className="modal-content" style={styles.content} onClick={e => e.stopPropagation()}>
        <h2 className="text-white text-center mb-4">Теги фильма</h2>

        {/* Текущие теги */}
        <div className="tags-container mb-4" style={styles.tagsBox}>
          {isLoading ? (
            <div className="text-white text-center">Загрузка...</div>
          ) : currentTags.length > 0 ? (
            currentTags.map((tag) => (
              <div key={tag.id} className="d-flex justify-content-between align-items-center mb-2 p-2" style={styles.tagItem}>
                <div className="d-flex flex-column">
                  <span className="text-dark fw-bold" style={{lineHeight: '1.2'}}>{tag.name}</span>
                  <small className="text-muted" style={{fontSize: '0.7rem'}}>{tag.type}</small>
                </div>
                <button className="btn btn-danger btn-sm rounded-pill px-3" onClick={() => handleDetachTag(tag.id)}>удалить</button>
              </div>
            ))
          ) : <div className="text-white-50 text-center small">Тегов нет</div>}
        </div>

        {/* поиск */}
        <div className="search-section p-3 mb-4" style={styles.searchBox}>
          <input 
            type="text" 
            className="form-control mb-3" 
            placeholder="Поиск по базе..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={styles.input}
          />
          
          {/* Переключатели типов */}
          <div className="d-flex flex-wrap gap-1 mb-2 justify-content-center">
            <button 
              style={activeTypeFilter === "ALL" ? styles.typeBadgeActive : styles.typeBadge}
              onClick={() => setActiveTypeFilter("ALL")}
            >Все</button>
            {tagTypes.map(type => (
              <button 
                key={type}
                style={activeTypeFilter === type ? styles.typeBadgeActive : styles.typeBadge}
                onClick={() => setActiveTypeFilter(type)}
              >
                {type}
              </button>
            ))}
          </div>

          <div className="dropdown-results" style={styles.results}>
            {isSearching && <div className="text-white-50 small text-center">Загрузка...</div>}
            {searchResults.map(tag => (
              <div key={tag.id} className="p-2 search-item d-flex justify-content-between align-items-center" onClick={() => handleAttachTag(tag.id)} style={styles.searchItem}>
                <span>{tag.name}</span>
                <small className="opacity-50">{tag.type}</small>
              </div>
            ))}
          </div>
        </div>

        {/* создание тега */}
        <div className="create-section text-center">
          <h4 className="text-white mb-2" style={{fontSize: '1rem'}}>Новый тег</h4>
          <div className="p-3" style={styles.searchBox}>
            <input type="text" className="form-control mb-2" placeholder="Название" value={newTagName} onChange={(e) => setNewTagName(e.target.value)} style={styles.input} />
            <select className="form-select mb-3" value={newTagType} onChange={(e) => setNewTagType(e.target.value)} style={styles.select}>
              {tagTypes.map(type => <option key={type} value={type}>{type}</option>)}
            </select>
            <button className="btn btn-success rounded-pill px-4 w-100" onClick={handleCreateTag}>Создать и привязать</button>
          </div>
        </div>

        <button className="btn btn-outline-light mt-4 rounded-pill w-100" onClick={onClose}>Закрыть</button>
      </div>
    </div>
  );
};

const styles = {
  overlay: { position: 'fixed', top: 0, left: 0, width: '100%', height: '100%', backgroundColor: 'rgba(0,0,0,0.8)', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 1050, backdropFilter: 'blur(5px)' },
  content: { backgroundColor: '#2b2d3d', padding: '30px', borderRadius: '20px', width: '100%', maxWidth: '450px', maxHeight: '90vh', overflowY: 'auto' },
  tagsBox: { backgroundColor: '#5d5e6d', padding: '15px', borderRadius: '15px', maxHeight: '150px', overflowY: 'auto' },
  tagItem: { backgroundColor: '#fff5f5', borderRadius: '10px' },
  searchBox: { backgroundColor: '#5d5e6d', borderRadius: '15px' },
  results: { maxHeight: '120px', overflowY: 'auto' },
  input: { backgroundColor: '#d9d9d9', border: 'none', borderRadius: '20px', textAlign: 'center' },
  select: { backgroundColor: '#d9d9d9', border: 'none', borderRadius: '20px', textAlign: 'center' },
  searchItem: { cursor: 'pointer', color: 'white', borderBottom: '1px solid rgba(255,255,255,0.1)' },
  
  typeBadge: {
    padding: '4px 10px',
    borderRadius: '15px',
    fontSize: '0.65rem',
    backgroundColor: 'rgba(255,255,255,0.1)',
    color: '#ccc',
    border: '1px solid rgba(255,255,255,0.2)',
    cursor: 'pointer',
    transition: '0.2s'
  },
  typeBadgeActive: {
    padding: '4px 10px',
    borderRadius: '15px',
    fontSize: '0.65rem',
    backgroundColor: '#ffc107', 
    color: '#000',
    border: '1px solid #ffc107',
    fontWeight: 'bold',
    cursor: 'pointer'
  }
};

export default MovieTagsModal;