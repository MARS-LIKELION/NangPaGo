package com.mars.admin.domain.stats.repository;

import com.mars.common.model.stats.VisitLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitLogRepository extends MongoRepository<VisitLog, String> {

}
