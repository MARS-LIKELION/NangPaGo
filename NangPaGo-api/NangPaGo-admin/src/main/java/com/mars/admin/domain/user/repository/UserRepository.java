package com.mars.admin.domain.user.repository;

import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.enums.user.UserStatus;
import com.mars.common.model.user.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {

    String QUERY_SELECT_USERS = """
    SELECT u FROM User u 
    WHERE u.role <> 'ROLE_ADMIN'
    AND (:statuses IS NULL OR u.userStatus IN :statuses)
    AND (:providers IS NULL OR u.oauth2Provider IN :providers)
""";

    Optional<User> findByEmail(String email);

    @Query(QUERY_SELECT_USERS)
    Page<User> findByRoleNotAdminWithFilters(
        @Param("statuses") List<UserStatus> statuses,
        @Param("providers") List<OAuth2Provider> providers,
        Pageable pageable);

    @Query(QUERY_SELECT_USERS +
        """
        ORDER BY 
            FUNCTION('REGEXP_REPLACE', u.nickname, '[0-9]+$', ''),
            CAST(FUNCTION('REGEXP_SUBSTR', u.nickname, '[0-9]+$') AS int) ASC
    """)
    Page<User> findByRoleNotAdminWithFiltersOrderByNicknameAsc(
        @Param("statuses") List<UserStatus> statuses,
        @Param("providers") List<OAuth2Provider> providers,
        Pageable pageable);

    @Query(QUERY_SELECT_USERS +
        """
        ORDER BY 
            FUNCTION('REGEXP_REPLACE', u.nickname, '[0-9]+$', '') DESC,
            CAST(FUNCTION('REGEXP_SUBSTR', u.nickname, '[0-9]+$') AS int) DESC
    """)
    Page<User> findByRoleNotAdminWithFiltersOrderByNicknameDesc(
        @Param("statuses") List<UserStatus> statuses,
        @Param("providers") List<OAuth2Provider> providers,
        Pageable pageable);
}
