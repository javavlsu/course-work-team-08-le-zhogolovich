import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const EditProfile = () => {
    const [user, setUser] = useState({
        username: '',
        email: '',
        avatarUrl: '', 
        aboutMe: ''
    });
    
    const [selectedFile, setSelectedFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const API_BASE_URL = "http://localhost:8080/movie-project";
    const avatarDefault = '../images/такса.svg';

    
    useEffect(() => {
        const fetchUserData = async () => {
            try {
                const token = localStorage.getItem('token');
                const response  = await axios.get(
          "http://localhost:8080/movie-project/users/me",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
                setUser(response.data);
            } catch (error) {
                console.error("Ошибка при получении данных пользователя", error);
                if (error.response?.status === 401) navigate('/login');
            }
        };
        fetchUserData();
    }, [navigate]);


    const getAvatarImage = () => {
        if (previewUrl) return previewUrl; 
        if (user.avatarUrl) return `${API_BASE_URL}${user.avatarUrl}`; 
        return avatarDefault; 
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setSelectedFile(file);
         
            setPreviewUrl(URL.createObjectURL(file)); 
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            const token = localStorage.getItem('token');


            if (selectedFile) {
                const formData = new FormData();
                formData.append('file', selectedFile);

                await axios.patch(`${API_BASE_URL}/users/me/avatar`, formData, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });
            }

            await axios.patch(
              `${API_BASE_URL}/users/me`,
              {
                username: user.username,
                aboutMe: user.aboutMe,
              },
              {
                headers: { Authorization: `Bearer ${token}` },
              },
            );
            navigate('/profile'); 
        } catch (error) {
            console.error("Ошибка при сохранении", error);
            alert("Не удалось сохранить изменения. Вероятно такой пользоваетль уже существует");
        } finally {
            setLoading(false);
        }
    };

    return (
        
        <div className="container-wrapper">
            {/* Меню */}
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
          <Link to="/" className="nav-btn">Главная</Link>
          <Link to="/movies" className="nav-btn">Фильмы</Link>
          <Link to="/collections" className="nav-btn">Подборки</Link>
          <Link to="/reviews" className="nav-btn">Рецензии</Link>
          <Link to="/profile" className="nav-btn">Моя страница</Link>
        </nav>
      </header>
            <main className="container-xl px-4 px-md-5 mt-5">
                <div style={{ maxWidth: '1100px', margin: '0 auto' }}>
                    <form onSubmit={handleSubmit}>
                        <div className="row align-items-start g-0">
                            
                   
                            <div className="col-auto d-flex flex-column align-items-center">
                                <label htmlFor="avatar-input" className="d-flex flex-column align-items-center" style={{ cursor: 'pointer' }}>
                                    <div className="rounded-circle overflow-hidden mb-4 shadow avatar-container" 
                                         style={{ width: '220px', height: '220px', border: '3px solid white', position: 'relative' }}>
                                        
                                        <img 
                                            src={getAvatarImage()} 
                                            className="img-fluid" 
                                            alt="Avatar" 
                                            style={{ width: '100%', height: '100%', objectFit: 'cover' }} 
                                        />
                                        
                                        <div className="avatar-overlay d-flex align-items-center justify-content-center position-absolute top-0 start-0 w-100 h-100"
                                             style={{ background: 'rgba(0,0,0,0.4)', opacity: 0, transition: '0.3s' }}>
                                            <i className="fa-solid fa-camera text-white fs-1"></i>
                                        </div>
                                    </div>
                                    
                                    <div className="custom-btn user-pill py-3 px-5 text-center" 
                                         style={{ fontSize: '1.1rem', width: 'fit-content', minWidth: '180px' }}>
                                        Изменить
                                    </div>

                                    <input 
                                        type="file" 
                                        id="avatar-input" 
                                        accept="image/*" 
                                        className="d-none" 
                                        onChange={handleFileChange}
                                    />
                                </label>
                            </div>


                            <div className="col ps-md-5 pt-1">
                                <div className="mb-4">
                                    <label className="text-white-50 mb-2 d-block" style={{ fontSize: '1.1rem' }}>
                                        Никнейм
                                    </label>
                                    <input 
                                        type="text" 
                                        className="form-control bg-transparent text-white rounded-3 py-2 px-3 fs-4" 
                                        value={user.username || ''}
                                        onChange={(e) => setUser({...user, username: e.target.value})}
                                        style={{ maxWidth: '400px', borderWidth: '2px', flexShrink: 0 }}
                                    />
                                </div>

                                <div className="mb-5">
                                    <label className="text-white-50 mb-2 d-block" style={{ fontSize: '1.1rem' }}>
                                        О себе
                                    </label>
                                    <textarea 
                                        className="form-control bg-transparent text-white border-white rounded-3 py-2 px-3 fs-5" 
                                        rows="4" 
                                        value={user.aboutMe || ''}
                                        onChange={(e) => setUser({...user, aboutMe: e.target.value})}
                                        style={{ maxWidth: '600px', borderWidth: '2px', resize: 'none' }}
                                    />
                                </div>
                            </div>
                        </div>

                        <div className="text-center mt-5 mb-5">
                            <button 
                                type="submit" 
                                className="custom-btn user-pill py-3 px-5" 
                                disabled={loading}
                                style={{ fontSize: '1.2rem', minWidth: '220px' }}
                            >
                                {loading ? 'Сохранение...' : 'Сохранить'}
                            </button>
                        </div>
                    </form>
                </div>
            </main>
        </div>
    );
};

export default EditProfile;