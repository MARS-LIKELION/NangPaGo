package com.mars.common.model.userCommunity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserCommunityManualImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_community_manual_id", nullable = false)
    private UserCommunityManual userCommunityManual;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String imageUrl; // 조리 과정 이미지 URL

    public static UserCommunityManualImage of(UserCommunityManual userCommunityManual, String imageUrl) {
        return UserCommunityManualImage.builder()
            .userCommunityManual(userCommunityManual)
            .imageUrl(imageUrl)
            .build();
    }
}
