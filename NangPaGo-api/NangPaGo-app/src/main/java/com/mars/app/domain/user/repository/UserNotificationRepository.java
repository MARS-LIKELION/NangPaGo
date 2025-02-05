package com.mars.app.domain.user.repository;

import com.mars.common.model.user.UserNotification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserNotificationRepository extends MongoRepository<UserNotification, String> {

}
