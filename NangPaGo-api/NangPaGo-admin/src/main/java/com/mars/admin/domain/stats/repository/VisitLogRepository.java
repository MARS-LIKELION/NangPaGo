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
        "{ $group: { " +
            "    _id: { " +
            "        date: { $dateToString: { format: '%Y-%m-%d', date: '$timestamp' } } " +
            "    }, " +
            "    loggedInUsers: { $addToSet: { $cond: [ { $ne: ['$userId', -1] }, '$userId', null ] } }, " +
            "    guestIps: { $addToSet: { $cond: [ { $eq: ['$userId', -1] }, '$ip', null ] } } " +
            "} }",
        "{ $project: { " +
            "    _id: 0, " +
            "    date: '$_id.date', " +
            "    users: { $ifNull: [ { $size: { $filter: { input: '$loggedInUsers', as: 'user', cond: { $ne: ['$$user', null] } } } }, 0 ] }, " +
            "    unregisteredUsers: { $ifNull: [ { $size: { $filter: { input: '$guestIps', as: 'ip', cond: { $ne: ['$$ip', null] } } } }, 0 ] } " +
            "} }",
        "{ $sort: { date: 1 } }"
    })
    List<DailyUserStatsDto> getDailyUserStats();
}
