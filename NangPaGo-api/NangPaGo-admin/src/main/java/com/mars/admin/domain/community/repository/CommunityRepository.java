package com.mars.admin.domain.community.repository;

import com.mars.common.model.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {

}
