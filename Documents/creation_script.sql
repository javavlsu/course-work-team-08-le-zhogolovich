USE film_catalog;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('user','reviewer','admin') NOT NULL DEFAULT 'user',
  avatar_url VARCHAR(1000),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_users_username UNIQUE (username),
  CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS user_follow (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  follower_id BIGINT UNSIGNED NULL,
  followed_user_id BIGINT UNSIGNED NULL,
  followed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_follow_follower
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_follow_followed
    FOREIGN KEY (followed_user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS movies (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  kinopoisk_id INT NULL,
  name VARCHAR(255) NOT NULL,
  original_name VARCHAR(255),
  release_year INT,
  poster_url VARCHAR(500),
  overview TEXT,
  INDEX idx_movies_tmdb_id (kinopoisk_id),
  INDEX idx_movies_title (name),
  FULLTEXT INDEX ft_movies_name_description (name, overview)
);

CREATE TABLE IF NOT EXISTS genres (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  kinopoisk_id INT NULL,
  name VARCHAR(255) NOT NULL,
  UNIQUE KEY uq_genres_kinopoisk_id (kinopoisk_id),
  UNIQUE KEY uq_genres_name (name)
);

CREATE TABLE IF NOT EXISTS movie_genres (
  movie_id BIGINT UNSIGNED NOT NULL,
  genre_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (movie_id, genre_id),
  INDEX idx_movie_genres_genre (genre_id),
  CONSTRAINT fk_movie_genres_movie
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_movie_genres_genre
    FOREIGN KEY (genre_id) REFERENCES genres(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS countries (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  kinopoisk_id INT NULL,
  name VARCHAR(255) NOT NULL,
  UNIQUE KEY uq_countries_kinopoisk_id (kinopoisk_id),
  UNIQUE KEY uq_countries_name (name)
);

CREATE TABLE IF NOT EXISTS movie_countries (
  movie_id BIGINT UNSIGNED NOT NULL,
  country_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (movie_id, country_id),
  INDEX idx_movie_countries_country (country_id),
  CONSTRAINT fk_movie_countries_movie
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_movie_countries_country
    FOREIGN KEY (country_id) REFERENCES countries(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS tags (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  type ENUM('theme','trope','style','mood','character') NOT NULL DEFAULT 'theme',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_tags_name UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS movie_tags (
  movie_id BIGINT UNSIGNED NOT NULL,
  tag_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (movie_id, tag_id),
  INDEX idx_movie_tags_tag (tag_id),
  CONSTRAINT fk_movie_tags_movie
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_movie_tags_tag
    FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS movie_ratings (
  user_id BIGINT UNSIGNED NOT NULL,
  movie_id BIGINT UNSIGNED NOT NULL,
  rating INT CHECK (rating BETWEEN 1 AND 10),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, movie_id),
  INDEX idx_movie_ratings_movie (movie_id),
  CONSTRAINT fk_movie_ratings_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_movie_ratings_movie
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS reviews (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  movie_id BIGINT UNSIGNED NOT NULL,
  user_id BIGINT UNSIGNED NOT NULL,
  title VARCHAR(500) NULL,
  content TEXT NOT NULL,
  rating INT CHECK (rating BETWEEN 1 AND 10),
  status ENUM('draft','published') NOT NULL DEFAULT 'draft',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_reviews_movie (movie_id),
  INDEX idx_reviews_author (user_id),
  CONSTRAINT fk_reviews_movie
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_reviews_author
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL,
  movie_id BIGINT UNSIGNED NULL,
  content TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_comments_author (user_id),
  INDEX idx_comments_movie (movie_id),
  CONSTRAINT fk_comments_author
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_comments_movie
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS collections (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL,
  title VARCHAR(500) NOT NULL,
  description TEXT NULL,
  is_public TINYINT(1) NOT NULL DEFAULT 1,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP,
  INDEX idx_collections_author (user_id),
  CONSTRAINT fk_collections_author
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS collection_movies (
  collection_id BIGINT UNSIGNED NOT NULL,
  movie_id BIGINT UNSIGNED NOT NULL,
  order_index INT NULL,
  PRIMARY KEY (collection_id, movie_id),
  INDEX idx_collection_movies_movie (movie_id),
  CONSTRAINT fk_collection_movies_collection
    FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_collection_movies_movie
    FOREIGN KEY (movie_id) REFERENCES movies(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS collection_ratings (
  user_id BIGINT UNSIGNED NOT NULL,
  collection_id BIGINT UNSIGNED NOT NULL,
  rating INT CHECK (rating BETWEEN 1 AND 10),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, collection_id),
  INDEX idx_collection_ratings_collection (collection_id),
  CONSTRAINT fk_collection_ratings_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT fk_collection_ratings_collection
    FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS user_saved_collections (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  collection_id BIGINT UNSIGNED NOT NULL,
  saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_saved_collections_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_saved_collections_collections
	FOREIGN KEY (collection_id) REFERENCES collections(id) ON DELETE CASCADE
);
