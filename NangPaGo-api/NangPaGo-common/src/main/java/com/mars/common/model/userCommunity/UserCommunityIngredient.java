package com.mars.common.model.userCommunity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserCommunityIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_community_id", nullable = false)
    private UserCommunity userCommunity;

    @Column(nullable = false)
    private String name; // 재료 이름

    @Column(nullable = false)
    private String amount; // 재료 양

    public static UserCommunityIngredient of(UserCommunity userCommunity, String name, String amount) {
        return UserCommunityIngredient.builder()
            .userCommunity(userCommunity)
            .name(name)
            .amount(amount)
            .build();
    }
}
