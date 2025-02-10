package com.mars.app.domain.user.repository;

import com.mars.common.model.user.UserNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {
    @Query(value = """
        {
            '$and': [
                { 'timestamp': { '$gte': ?0 } },
                { 'userId': ?1 }
            ]
        }
        """,
        sort = "{ 'timestamp': -1 }")
    List<UserNotification> findNotificationsSince(LocalDateTime timestamp, Long userId);

    @Query(value = "{ 'userId': ?0, 'isRead': false }", count = true)
    long countByUserIdAndIsReadFalse(Long userId);

    @Query(value = "{ 'userId': ?0, 'isRead': false }", delete = false)
    @Update("{ '$set': { 'isRead': true }}")
    void markAllAsReadByUserId(Long userId);

    @Query(value = "{ 'userId': ?0 }", count = true)
    long countByUserId(Long userId);

    @Query(value = "{ 'userId': ?0 }", delete = true)
    void deleteByUserId(Long userId);
}
