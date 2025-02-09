package com.mars.app.domain.user.repository;

import com.mars.common.model.user.UserNotification;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {
    @Query("""
        {
            '$and': [
                { 'timestamp': { '$gte': ?0 } },
                { 'userId': ?1 }
            ]
        }
        """)
    List<UserNotification> findNotificationsSince(LocalDateTime timestamp, Long userId);
}
