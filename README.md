## Курсовое проектирование по дисциплине "Распределённые программные системы" на тему: "Социальная программная система для каталогизации, рейтинга и рекомендации фильмов"

# Документация API: Movie Project

Данный документ содержит описание эндпойнтов бэкенда, основанное на спецификации OpenAPI.

## Общая информация

- **Base URL:** `http://localhost:8080/movie-project`
- **Формат данных:** В основном `application/json`, для загрузки файлов используется `multipart/form-data`.

---

## 1. Аутентификация (auth-controller)

Эндпойнты для управления сессиями и регистрацией пользователей.

| Метод    | Путь                 | Описание          | Входные данные                                                        | Ответ                                        |
| :------- | :------------------- | :---------------- | :-------------------------------------------------------------------- | :------------------------------------------- |
| **POST** | `/auth/register`     | Регистрация       | **Body**: `username` (3-50 симв.), `email`, `password` (мин. 8 симв.) | `String`                                     |
| **POST** | `/auth/login`        | Вход в систему    | **Body**: `login`, `password`                                         | `JwtAuthenticationDto` (token, refreshToken) |
| **POST** | `/auth/refreshToken` | Обновление токена | **Body**: `refreshToken`                                              | `JwtAuthenticationDto`                       |

---

## 2. Фильмы (movie-controller)

Работа с каталогом фильмов, рейтингами и тегами.

| Метод      | Путь                             | Описание            | Параметры                               | Body / Примечания                       |
| :--------- | :------------------------------- | :------------------ | :-------------------------------------- | :-------------------------------------- |
| **GET**    | `/movies`                        | Список фильмов      | **Query**: `page` (def: 0), `size` (20) | Пагинированный список                   |
| **GET**    | `/movies/{id}`                   | Страница фильма     | **Path**: `id` (int64)                  | Возвращает `MovieFullDto`               |
| **POST**   | `/movies/{id}/rating`            | Поставить оценку    | **Path**: `id`                          | **Body**: `rating` (double, 1.0 - 10.0) |
| **POST**   | `/movies/{id}/compilations`      | Добавить в подборки | **Path**: `id`                          | **Body**: `compilationIds` (Array)      |
| **GET**    | `/movies/{movieId}/tags`         | Теги фильма         | **Path**: `movieId`                     | Список названий тегов                   |
| **POST**   | `/movies/{movieId}/tags/{tagId}` | Привязать тег       | **Path**: `movieId`, `tagId`            | —                                       |
| **DELETE** | `/movies/{movieId}/tags/{tagId}` | Отвязать тег        | **Path**: `movieId`, `tagId`            | —                                       |

---

## 3. Подборки (compilation-controller)

Создание и управление коллекциями фильмов.

| Метод      | Путь                                            | Описание           | Параметры                 | Body / Примечания                                          |
| :--------- | :---------------------------------------------- | :----------------- | :------------------------ | :--------------------------------------------------------- |
| **GET**    | `/compilations`                                 | Публичные подборки | **Query**: `page`, `size` | Пагинированный список                                      |
| **POST**   | `/compilations`                                 | Создать подборку   | —                         | **Multipart**: `title`, `description`, `isPublic`, `cover` |
| **GET**    | `/compilations/my`                              | Мои подборки       | —                         | Список `CompilationDto`                                    |
| **GET**    | `/compilations/{id}`                            | Страница подборки  | **Path**: `id`            | Включает список фильмов                                    |
| **PATCH**  | `/compilations/{id}`                            | Редактировать инфо | **Path**: `id`            | **Body**: `UpdateCompilationRequest`                       |
| **DELETE** | `/compilations/{id}`                            | Удалить подборку   | **Path**: `id`            | —                                                          |
| **PATCH**  | `/compilations/{id}/cover`                      | Обновить обложку   | **Path**: `id`            | **Multipart**: `file` (binary)                             |
| **POST**   | `/compilations/{id}/subscribe`                  | Подписаться        | **Path**: `id`            | —                                                          |
| **DELETE** | `/compilations/{id}/subscribe`                  | Отписаться         | **Path**: `id`            | —                                                          |
| **POST**   | `/compilations/{id}/like`                       | Лайкнуть           | **Path**: `id`            | —                                                          |
| **DELETE** | `/compilations/{compilationId}/movie/{movieId}` | Убрать фильм       | **Path**: `id`, `movieId` | —                                                          |

---

## 4. Рецензии (review-controller)

Управление рецензиями на фильмы.

| Метод      | Путь                 | Описание             | Параметры                 | Body / Примечания                                    |
| :--------- | :------------------- | :------------------- | :------------------------ | :--------------------------------------------------- |
| **GET**    | `/reviews`           | Список всех рецензий | **Query**: `page`, `size` | —                                                    |
| **POST**   | `/reviews`           | Написать рецензию    | —                         | **Body**: `movieId`, `title`, `content`, `isPublish` |
| **GET**    | `/reviews/{id}`      | Страница Рецензии    | **Path**: `id`            | —                                                    |
| **PATCH**  | `/reviews/{id}`      | Редактировать        | **Path**: `id`            | **Body**: `EditReviewRequest`                        |
| **DELETE** | `/reviews/{id}`      | Удалить              | **Path**: `id`            | —                                                    |
| **POST**   | `/reviews/{id}/like` | Лайкнуть             | **Path**: `id`            | —                                                    |

---

## 5. Комментарии (comment-controller)

Обсуждение фильмов пользователями.

| Метод      | Путь                       | Описание             | Параметры                              | Body                |
| :--------- | :------------------------- | :------------------- | :------------------------------------- | :------------------ |
| **GET**    | `/comment/movie/{movieId}` | Комментарии к фильму | **Path**: `movieId`, **Query**: `page` | —                   |
| **POST**   | `/comment/movie/{movieId}` | Добавить комментарий | **Path**: `movieId`                    | **Body**: `content` |
| **PATCH**  | `/comment/{commentId}`     | Редактировать        | **Path**: `commentId`                  | **Body**: `content` |
| **DELETE** | `/comment/{commentId}`     | Удалить              | **Path**: `commentId`                  | —                   |

---

## 6. Пользователи (user-controller)

Управление профилем и просмотр других участников.

| Метод     | Путь                | Описание              | Входные данные                           |
| :-------- | :------------------ | :-------------------- | :--------------------------------------- |
| **GET**   | `/users/me`         | Мой профиль           | —                                        |
| **PATCH** | `/users/me`         | Редактировать профиль | **Body**: `username`, `email`, `aboutMe` |
| **PATCH** | `/users/me/avatar`  | Сменить аватар        | **Multipart**: `file` (binary)           |
| **GET**   | `/users`            | Список всех юзеров    | —                                        |
| **GET**   | `/users/{id}`       | Профиль по id         | **Path**: `id`                           |
| **GET**   | `/users/{username}` | Профиль по username   | **Path**: `username`                     |

---

## 7. Теги (tag-controller)

Служебные категории для фильтрации.

| Метод      | Путь           | Описание          | Параметры                                |
| :--------- | :------------- | :---------------- | :--------------------------------------- |
| **POST**   | `/tags`        | Создать новый тег | **Body**: `name`, `type`                 |
| **GET**    | `/tags/search` | Поиск тегов       | **Query**: `query`, `page`               |
| **PATCH**  | `/tags/{id}`   | Изменить тег      | **Path**: `id`, **Body**: `name`, `type` |
| **DELETE** | `/tags/{id}`   | Удалить тег       | **Path**: `id`                           |

---

## 8. Импорт (import-controller)

Админские функции для наполнения базы.

- **POST** `/api/import/movies`: Запуск процесса импорта фильмов.
- **POST** `/api/import/filters`: Импорт фильтров (жанров/стран).
