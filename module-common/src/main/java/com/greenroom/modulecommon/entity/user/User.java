package com.greenroom.modulecommon.entity.user;

import com.greenroom.modulecommon.entity.AuditingCreateUpdateEntity;
import com.greenroom.modulecommon.entity.category.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false)
    private OAuthType oauthType;

    @Column(nullable = false)
    private String oauthId;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(length = 20)
    private String profileImage;

    @Builder
    private User(Long id, Category category, OAuthType oauthType, String oauthId, String name) {
        this.id = id;
        this.category = category;
        this.oauthType = oauthType;
        this.oauthId = oauthId;
        this.name = name;
    }
}
