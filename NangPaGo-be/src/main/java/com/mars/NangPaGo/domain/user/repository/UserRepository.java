package com.mars.NangPaGo.domain.user.repository;

import com.mars.NangPaGo.domain.user.dto.MyPageSubQueryCountDto;
import com.mars.NangPaGo.domain.user.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(
        "SELECT new com.mars.NangPaGo.domain.user.dto.MyPageSubQueryCountDto(" +
            "(SELECT COUNT(l) FROM RecipeLike l WHERE l.user.id = u.id), " +
            "(SELECT COUNT(f) FROM RecipeFavorite f WHERE f.user.id = u.id), " +
            "(SELECT COUNT(c) FROM RecipeComment c WHERE c.user.id = u.id), " +
            "(SELECT COUNT(r) FROM Refrigerator r WHERE r.user.id = u.id)) " +
            "FROM User u WHERE u.email = :email"
    )
    MyPageSubQueryCountDto findUserStatsByEmail(@Param("email") String email);

    Optional<User> findByEmail(String email);
}
