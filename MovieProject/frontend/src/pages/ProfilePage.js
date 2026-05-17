import React, { useEffect, useState, useCallback, useRef } from "react";
import apiClient from "../api/apiClient";
import avatarDefault from "../images/такса.svg";
import "bootstrap/dist/css/bootstrap.min.css";
import { Link, useNavigate, useParams } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import LikedContent from "../components/LikedContent";
import { getImageUrl } from "../utils/getImageUrl";

function Pagination({ currentPage, totalItems, itemsPerPage, onPageChange }) {
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

function ProfilePage() {
  const { username: urlUsername } = useParams();
  const navigate = useNavigate();

  const [user, setUser] = useState(null);
  const [compilations, setCompilations] = useState([]);
  const [subscriptions, setSubscriptions] = useState([]);
  const [activeTab, setActiveTab] = useState("my");
  const [loading, setLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState("");
  const [isMyProfile, setIsMyProfile] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);

  const [followers, setFollowers] = useState([]);
  const [followings, setFollowings] = useState([]);
  const [isFollowing, setIsFollowing] = useState(false);
  const [reviews, setReviews] = useState([]);

  const ITEMS_PER_PAGE = 20;
  const [myPage, setMyPage] = useState(1);
  const [subPage, setSubPage] = useState(1);
  const [reviewsPage, setReviewsPage] = useState(1);

  const isFetchingRef = useRef(false);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };
  const handleTabChange = (tabName) => {
    setActiveTab(tabName);
    setMyPage(1);
    setSubPage(1);
    setReviewsPage(1);
  };

  const fetchProfileData = useCallback(async () => {
    if (isFetchingRef.current) return;
    isFetchingRef.current = true;

    try {
      setLoading(true);
      const token = localStorage.getItem("token");
      let myIdFromToken = null;

      if (token) {
        const decoded = jwtDecode(token);
        console.log("Содержимое JWT токена:", decoded);
        myIdFromToken = decoded.userId;
        setIsAdmin(
          decoded.roles?.includes("ADMIN") || decoded.role === "ADMIN",
        );
      }

      const isDirectMyPath = !urlUsername || urlUsername === "profile";
      const userEndpoint = isDirectMyPath
        ? "/users/me"
        : `/users/${urlUsername}`;

      const userRes = await apiClient.get(userEndpoint);
      const userData = userRes.data;
      setUser(userData);

      const profileId = userData.id || userData.userId;

      console.log("Мой ID из токена:", myIdFromToken);
      console.log("ID профиля из БД:", profileId);

      const isMy =
        isDirectMyPath ||
        (myIdFromToken !== null &&
          profileId !== undefined &&
          Number(myIdFromToken) === Number(profileId));

      console.log("ИТОГ СРАВНЕНИЯ (isMyProfile):", isMy);
      setIsMyProfile(isMy);

      if (isMy) {
        const [compRes, subRes, followersRes, followingsRes, reviewsRes] =
          await Promise.all([
            apiClient.get("/compilations/my"),
            apiClient.get("/compilations/my/subscriptions"),
            apiClient.get("/users/me/followers"),
            apiClient.get("/users/me/followings"),
            apiClient.get("/reviews/my"),
          ]);
        setCompilations(compRes.data);
        setSubscriptions(subRes.data.content || subRes.data || []);
        setFollowers(followersRes.data || []);
        setFollowings(followingsRes.data || []);
        setReviews(reviewsRes.data || []);
      } else {
        const [compRes, subRes, followersRes, followingsRes, reviewsRes] =
          await Promise.all([
            apiClient.get(`/compilations/user/${profileId}`),
            apiClient.get(`/compilations/user/${profileId}/subscriptions`),
            apiClient.get(`/users/${userData.username}/followers`),
            apiClient.get(`/users/${userData.username}/followings`),
            apiClient.get(`/reviews/user/${profileId}`),
          ]);

        const allCompilations = compRes.data.content || compRes.data;
        setCompilations(
          Array.isArray(allCompilations)
            ? allCompilations.filter((c) => c.isPublic)
            : [],
        );

        setSubscriptions(subRes.data.content || subRes.data || []);

        setFollowers(followersRes.data || []);
        setFollowings(followingsRes.data || []);

        setIsFollowing(
          followersRes.data.some((f) => Number(f.id) === Number(myIdFromToken)),
        );
        setReviews(reviewsRes.data.content || reviewsRes.data || []);
      }
      const canEdit =
        isMy ||
        (token &&
          (jwtDecode(token).role === "ADMIN" ||
            jwtDecode(token).roles?.includes("ADMIN")));
    } catch (error) {
      console.error("Ошибка в ProfilePage:", error);
      setErrorMsg("Ошибка загрузки");
    } finally {
      setLoading(false);
      isFetchingRef.current = false;
    }
  }, [urlUsername]);

  const toggleFollow = async () => {
    try {
      const profileId = user.id || user.userId;

      const token = localStorage.getItem("token");
      let myData = { id: null };
      if (token) {
        const decoded = jwtDecode(token);
        myData = { id: Number(decoded.userId), username: decoded.sub || "me" };
      }

      if (isFollowing) {
        await apiClient.delete(`/users/follow/${profileId}`);
        setIsFollowing(false);
        setFollowers((prev) =>
          prev.filter((f) => Number(f.id) !== Number(myData.id)),
        );
      } else {
        await apiClient.post(`/users/follow/${profileId}`);
        setIsFollowing(true);
        setFollowers((prev) => [...prev, myData]);
      }
    } catch (err) {
      console.error(err);
      alert(
        "Не удалось изменить статус подписки. Возможно, вы не зарегистрированны.",
      );
    }
  };

  useEffect(() => {
    fetchProfileData();
  }, [fetchProfileData]);

  const indexOfLastMy = myPage * ITEMS_PER_PAGE;
  const indexOfFirstMy = indexOfLastMy - ITEMS_PER_PAGE;
  const currentCompilations = compilations.slice(indexOfFirstMy, indexOfLastMy);

  const indexOfLastSub = subPage * ITEMS_PER_PAGE;
  const indexOfFirstSub = indexOfLastSub - ITEMS_PER_PAGE;
  const currentSubscriptions = subscriptions.slice(indexOfFirstSub, indexOfLastSub);

  const indexOfLastReview = reviewsPage * ITEMS_PER_PAGE;
  const indexOfFirstReview = indexOfLastReview - ITEMS_PER_PAGE;
  const currentReviews = reviews.slice(indexOfFirstReview, indexOfLastReview);

  const renderGrid = (items, emptyMessage) => (
    <div className="row row-cols-2 row-cols-md-3 row-cols-lg-5 g-4 mt-2">
      {items.length > 0 ? (
        items.map((comp) => (
          <div className="col" key={comp.id}>
            <Link
              to={`/compilations/${comp.id}`}
              className="coll-card d-block text-decoration-none"
            >
              <div
                className="img-box rounded-4 overflow-hidden mb-3 shadow-sm position-relative"
                style={{ aspectRatio: "1/1" }}
              >
                {!comp.isPublic && isMyProfile && (
                  <div
                    className="card-badge"
                    style={{
                      position: "absolute",
                      top: "10px",
                      left: "10px",
                      color: "white",
                      zIndex: 2,
                    }}
                  >
                    <i className="fa-solid fa-lock"></i>
                  </div>
                )}
                <img
                  src={comp.coverUrl ? `${getImageUrl(comp.coverUrl)}` : avatarDefault}
                  alt={comp.title}
                  className="w-100 h-100 object-fit-cover"
                />
              </div>
              <p className="text-light m-0 fw-bold">{comp.title}</p>
            </Link>
          </div>
        ))
      ) : (
        <div className="w-100 text-center text-white-50 py-4">
          {emptyMessage}
        </div>
      )}
    </div>
  );

  if (loading)
    return <div className="text-white text-center mt-5">Загрузка...</div>;
  if (errorMsg)
    return <div className="text-danger text-center mt-5">{errorMsg}</div>;
  if (!user)
    return (
      <div className="text-white text-center mt-5">Пользователь не найден</div>
    );

  return (
    <div className="container-wrapper">
      <header className="header-sticky d-flex justify-content-center mb-5 mt-4">
        <nav className="custom-navbar d-flex align-items-center px-4 py-2 gap-2">
          <Link to="/" className="nav-btn">
            Главная
          </Link>
          <Link to="/movies" className="nav-btn">
            Фильмы
          </Link>
          <Link to="/collections" className="nav-btn">
            Подборки
          </Link>
          <Link to="/reviews" className="nav-btn">
            Рецензии
          </Link>
          <Link to="/searchuser" className="nav-btn">
            Пользователи
          </Link>
          <Link to="/profile" className="nav-btn">
            Моя страница
          </Link>

          {isMyProfile && (
            <button
              onClick={handleLogout}
              className="nav-btn border-0 bg-transparent text-danger fw-bold"
            >
              Выйти
            </button>
          )}
        </nav>
      </header>

      <main className="container-xl px-4 px-md-5 mt-5">
        <div style={{ maxWidth: "1100px", margin: "0 auto" }}>
          <div className="row align-items-start mb-5 g-0">
            <div className="col-auto d-flex flex-column align-items-center">
              <div
                className="rounded-circle overflow-hidden mb-4 shadow"
                style={{ width: "220px", height: "220px" }}
              >
                <img
                  src={
                    user.avatarUrl
                      ? `${getImageUrl(user.avatarUrl)}`
                      : avatarDefault
                  }
                  className="img-fluid w-100 h-100 object-fit-cover"
                  alt="Avatar"
                  onError={(e) => {
                    e.target.src = avatarDefault;
                  }}
                />
              </div>

              <div className="d-flex flex-row align-items-center justify-content-center flex-wrap gap-3 mt-3">
                {(isMyProfile || isAdmin) && (
                  <Link
                    to={
                      isMyProfile
                        ? "/edit-profile"
                        : `/edit-profile/${user.id || user.userId}`
                    }
                    className="custom-btn user-pill py-3 px-4 text-decoration-none text-center"
                    style={{ minWidth: "200px", whiteSpace: "nowrap" }}
                  >
                    Редактировать
                  </Link>
                )}

                {!isMyProfile && (
                  <button
                    onClick={toggleFollow}
                    className="custom-btn user-pill py-3 px-4 text-decoration-none"
                    style={{ minWidth: "200px", whiteSpace: "nowrap" }}
                  >
                    {isFollowing ? "Отписаться" : "Подписаться"}
                  </button>
                )}
              </div>
            </div>

            <div className="col ps-md-5 pt-1">
              <span className="text-white fs-3">@{user.username}</span>
              <div
                className="ms-1 mt-1"
                style={{
                  height: "2px",
                  backgroundColor: "white",
                  width: "100%",
                }}
              ></div>
              <p className="text-white fs-5 mt-3">
                {user.aboutMe || "Информация отсутствует"}
              </p>
            </div>
          </div>

          <div className="d-flex gap-4 my-3">
            <div className="d-flex gap-4 my-3">
              <Link
                to={`/users/${user.username}/followings`}
                className="text-decoration-none text-white d-flex align-items-center gap-2"
              >
                <span className="fw-bold fs-5">
                  {followings ? followings.length : 0}
                </span>
                <span className="opacity-75 small">Подписки</span>
              </Link>

              <Link
                to={`/users/${user.username}/followers`}
                className="text-decoration-none text-white d-flex align-items-center gap-2"
              >
                <span className="fw-bold fs-5">
                  {followers ? followers.length : 0}
                </span>
                <span className="opacity-75 small">Подписчики</span>
              </Link>
            </div>
          </div>
          {/* табы переключения  */}
          <div className="d-flex justify-content-center border-bottom border-secondary mb-4">
            <button
              className={`px-4 py-2 bg-transparent border-0 text-white fs-5 ${activeTab === "my" ? "border-bottom border-3 border-white fw-bold" : "opacity-50"}`}
              onClick={() => handleTabChange("my")}
            >
              {isMyProfile ? "Мои подборки" : "Подборки пользователя"}
            </button>

            <button
              className={`px-4 py-2 bg-transparent border-0 text-white fs-5 ${activeTab === "subscribed" ? "border-bottom border-3 border-white fw-bold" : "opacity-50"}`}
              onClick={() => handleTabChange("subscribed")}
            >
              Отслеживаемые
            </button>
            <button
              className={`px-4 py-2 bg-transparent border-0 text-white fs-5 ${activeTab === "reviews" ? "border-bottom border-3 border-white fw-bold" : "opacity-50"}`}
              onClick={() => handleTabChange("reviews")}
            >
              Рецензии
            </button>
            <button
              className={`px-4 py-2 bg-transparent border-0 text-white fs-5 ${activeTab === "liked" ? "border-bottom border-3 border-white fw-bold" : "opacity-50"}`}
              onClick={() => handleTabChange("liked")}
            >
              Понравилось
            </button>
          </div>
          <section className="mb-5">
            {activeTab === "liked" && (
              <LikedContent
                userId={user.id || user.userId}
                isMyProfile={isMyProfile}
                renderGrid={renderGrid}
                itemsPerPage={ITEMS_PER_PAGE}
              />
            )}
           {activeTab === "my" && (
              <>
                {isMyProfile && (
                  <div className="text-center mb-4">
                    <Link to="/create-compilation" className="custom-btn py-2 px-4 text-decoration-none">+ новая</Link>
                  </div>
                )}
                {renderGrid(currentCompilations, isMyProfile ? "У вас пока нет созданных подборок." : "Нет публичных подборок.")}
                <Pagination currentPage={myPage} totalItems={compilations.length} itemsPerPage={ITEMS_PER_PAGE} onPageChange={setMyPage} />
              </>
            )}

            {activeTab === "subscribed" && (
              <>
                {renderGrid(currentSubscriptions, "Тут пока пусто :(")}
                <Pagination currentPage={subPage} totalItems={subscriptions.length} itemsPerPage={ITEMS_PER_PAGE} onPageChange={setSubPage} />
              </>
            )}

            {activeTab === "reviews" && (
              <div className="mt-3">
                {currentReviews.length > 0 ? (
                  currentReviews.map((rev) => (
                    <div key={rev.id} className="stats-card p-4 text-white mb-5" style={{ border: "2px solid white", borderRadius: "20px", background: "rgba(255,255,255,0.05)" }}>
                      {isMyProfile && (
                        <div className="mb-3 d-flex align-items-center gap-2">
                          {rev.status === "DRAFT" ? (
                            <span className="text-warning"><i className="fa-solid fa-pen me-2"></i><small className="text-uppercase fw-bold">Черновик</small></span>
                          ) : (
                            <span className="text-success"><i className="fa-solid fa-check me-2"></i><small className="text-uppercase fw-bold">Опубликовано</small></span>
                          )}
                        </div>
                      )}
                      <div className="d-flex justify-content-between">
                        <Link to={`/reviews/${rev.id}`} className="text-decoration-none text-white hover-opacity">
                          <h5 className="fw-bold m-0">{rev.title || "Название рецензии"}</h5>
                        </Link>
                        <span className="badge rounded-pill d-flex align-items-center gap-2 px-3 py-2">
                          <i className="fa-solid fa-heart"></i>
                          <span>{rev.likesCount || 0}</span>
                        </span>
                      </div>
                      <div>
                        <Link to={`/movies/${rev.movieId}`} className="text-decoration-none text-white-50 hover-opacity">
                          <h6 className="fw-bold m-0">{rev.movieName || "Название фильма"}</h6>
                        </Link>
                      </div>
                      <p className="mt-2">{rev.text}</p>
                      <small className="text-white-50">Опубликовано: {new Date(rev.createdAt).toLocaleDateString()}</small>
                    </div>
                  ))
                ) : (
                  <div className="w-100 text-center text-white-50 py-4">Рецензий пока нет.</div>
                )}
                <Pagination currentPage={reviewsPage} totalItems={reviews.length} itemsPerPage={ITEMS_PER_PAGE} onPageChange={setReviewsPage} />
              </div>
            )}
          </section>
          <div className="border-bottom border-secondary mb-5"></div>
        </div>
      </main>
    </div>
  );
}

export default ProfilePage;
