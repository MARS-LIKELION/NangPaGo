package com.mars.admin.domain.audit.repository;

import com.mars.admin.domain.dashboard.dto.HourlyUserActionCountDto;
import com.mars.common.model.audit.AuditLog;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);
    List<AuditLog> findByUserIdOrderByTimestampDesc(String userId);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    List<AuditLog> findByAction(String action);

    @Aggregation(pipeline = {
        "{ '$match': { 'timestamp': { '$gte': ?0 } } }",
        "{ '$addFields': { 'kstTimestamp': { '$add': ['$timestamp', { '$multiply': [9, 60, 60, 1000] } ] } } }",
        "{ '$group': { '_id': { '$hour': '$kstTimestamp' }, 'count': { '$sum': 1 } } }",
        "{ '$sort': { '_id': 1 } }",
        "{ '$project': { 'hour': '$_id', 'count': 1, '_id': 0 } }"
    })
    List<HourlyUserActionCountDto> findHourlyActionCounts(Date oneMonthAgoDate);
}
