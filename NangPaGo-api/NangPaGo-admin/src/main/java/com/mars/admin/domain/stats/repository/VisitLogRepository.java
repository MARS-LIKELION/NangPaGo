package com.mars.admin.domain.stats.repository;

import com.mars.admin.domain.dashboard.dto.DailyUserStatsDto;
import com.mars.common.model.stats.VisitLog;
import java.util.List;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitLogRepository extends MongoRepository<VisitLog, String> {
    @Aggregation(pipeline = {
        "{ $group: { _id: { $dateToString: { format: '%Y-%m-%d', date: '$timestamp' } }, users: { $addToSet: '$userId' } } }",
        "{ $project: { _id: 0, date: '$_id', users: { $size: '$users' } } }",
        "{ $sort: { date: 1 } }"
    })
    List<DailyUserStatsDto> getDailyUserStats();
}
