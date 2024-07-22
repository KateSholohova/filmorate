package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    private static final String FIND_ALL_QUERY = """
            SELECT  review_id,
                    content,
                    is_positive,
                    user_id,
                    film_id,
                    useful
            FROM reviews
            """;

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"review_id"});
                    ps.setString(1, review.getContent());
                    ps.setBoolean(2, review.getIsPositive());
                    ps.setInt(3, review.getUserId());
                    ps.setInt(4, review.getFilmId());
                    ps.setInt(5, 0);
                    return ps;
                }, keyHolder);
        int id = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return findById(id).orElse(null);
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ?, user_id = ?, film_id = ?, useful = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUserId(),
                review.getFilmId(),
                review.getUseful(),
                review.getId()
        );
        return findById(review.getId()).orElse(null);
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Review> findAll(int limit) {
        String sql = FIND_ALL_QUERY + " LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), limit);
    }

    public List<Review> findByFilmId(int filmId, int limit) {
        String sql = FIND_ALL_QUERY + " WHERE film_id = ? LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), filmId, limit);
    }

    @Override
    public Optional<Review> findById(int id) {
        String sql = FIND_ALL_QUERY + " WHERE review_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), id).stream().findFirst();
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .id(rs.getInt("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }

    @Override
    public boolean isAlreadyExists(Review review) {
        String sql = FIND_ALL_QUERY + " WHERE user_id = ? AND film_id = ?";
        return !jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), review.getUserId(), review.getFilmId()).isEmpty();
    }

    @Override
    @Transactional
    public void addLike(int reviewId, int userId) {
        deleteDislike(reviewId, userId);

        String lastUserAction = getLastUserAction(reviewId, userId);
        if (lastUserAction == null) {
            String sql = "INSERT INTO review_actions (review_id, user_id, action) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, reviewId, userId, UserAction.LIKE.toString());
            usefulIncrement(reviewId);
        }
    }

    @Override
    @Transactional
    public void addDislike(int reviewId, int userId) {
        deleteLike(reviewId, userId);

        String lastUserAction = getLastUserAction(reviewId, userId);
        if (lastUserAction == null) {
            String sql = "INSERT INTO review_actions (review_id, user_id, action) VALUES (?, ?, ?)";
            jdbcTemplate.update(sql, reviewId, userId, UserAction.DISLIKE.toString());
            usefulDecrement(reviewId);
        }
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        String lastUserAction = getLastUserAction(reviewId, userId);
        if (UserAction.LIKE.toString().equals(lastUserAction)) {
            String sql = "DELETE FROM review_actions WHERE review_id = ? AND user_id = ? AND action = ?";
            jdbcTemplate.update(sql, reviewId, userId, UserAction.LIKE.toString());
            usefulDecrement(reviewId);
        }
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        String lastUserAction = getLastUserAction(reviewId, userId);
        if (UserAction.DISLIKE.toString().equals(lastUserAction)) {
            String sql = "DELETE FROM review_actions WHERE review_id = ? AND user_id = ? AND action = ?";
            jdbcTemplate.update(sql, reviewId, userId, UserAction.DISLIKE.toString());
            usefulIncrement(reviewId);
        }
    }

    private String getLastUserAction(int id, int userId) {
        String sql = "SELECT action FROM review_actions WHERE review_id = ? AND user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, id, userId);
        } catch (DataAccessException e) {
            return null;
        }
    }

    private void usefulIncrement(int id) {
        String sql = "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private void usefulDecrement(int id) {
        String sql = "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    private enum UserAction {
        LIKE, DISLIKE
    }
}
