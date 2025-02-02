package com.mars.common.model.userCommunity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserCommunityManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_community_id", nullable = false)
    private UserCommunity userCommunity;

    @Column(nullable = false)
    private Integer step; // 단계 번호

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description; // 설명

    @OneToMany(mappedBy = "userCommunityManual", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserCommunityManualImage> images = new ArrayList<>();

    public static UserCommunityManual of(UserCommunity userCommunity, Integer step, String description) {
        return UserCommunityManual.builder()
            .userCommunity(userCommunity)
            .step(step)
            .description(description)
            .images(new ArrayList<>())
            .build();
    }

    public void addImage(UserCommunityManualImage image) {
        this.images.add(image);
    }
}
