package com.mars.admin.domain.community.repository;

import com.mars.common.model.community.Community;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    @Query("SELECT DATE_FORMAT(c.createdAt, '%Y-%m-01') as month, COUNT(c) as count " +
        "FROM Community c " +
        "WHERE c.createdAt BETWEEN :start AND :end " +
        "GROUP BY DATE_FORMAT(c.createdAt, '%Y-%m-01') " +
        "ORDER BY month DESC")
    List<Object[]> getMonthPostCounts(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
