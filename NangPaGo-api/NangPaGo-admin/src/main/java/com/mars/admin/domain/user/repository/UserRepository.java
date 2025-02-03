package com.mars.admin.domain.user.repository;

import com.mars.common.model.user.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.role <> 'ROLE_ADMIN'")
    Page<User> findByRoleNotAdmin(Pageable pageable);

    @Query("""
        SELECT u FROM User u 
        WHERE u.role <> 'ROLE_ADMIN' 
        ORDER BY 
            FUNCTION('REGEXP_REPLACE', u.nickname, '[0-9]+$', ''),
            CAST(FUNCTION('REGEXP_SUBSTR', u.nickname, '[0-9]+$') AS int) ASC
    """)
    Page<User> findByRoleNotAdminOrderByNicknameAsc(Pageable pageable);

    @Query("""
        SELECT u FROM User u 
        WHERE u.role <> 'ROLE_ADMIN' 
        ORDER BY 
            FUNCTION('REGEXP_REPLACE', u.nickname, '[0-9]+$', '') DESC,
            CAST(FUNCTION('REGEXP_SUBSTR', u.nickname, '[0-9]+$') AS int) DESC
    """)
    Page<User> findByRoleNotAdminOrderByNicknameDesc(Pageable pageable);
}
