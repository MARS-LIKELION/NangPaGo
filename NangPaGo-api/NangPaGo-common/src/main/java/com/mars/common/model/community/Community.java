package com.mars.common.model.community;

import com.mars.common.model.BaseEntity;
import com.mars.common.model.comment.community.CommunityComment;
import com.mars.common.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "community")
public class Community extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isPublic;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityComment> comments;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommunityLike> likes;

    public static Community of(User user, String title, String content, String imageUrl, boolean isPublic) {
        return Community.builder()
            .user(user)
            .title(title)
            .content(content)
            .imageUrl(imageUrl)
            .isPublic(isPublic)
            .build();
    }

    public void update(String title, String content, boolean isPublic, String imageUrl) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.imageUrl = imageUrl;
    }

    public boolean isPrivate() {
        return !this.isPublic;
    }
}
