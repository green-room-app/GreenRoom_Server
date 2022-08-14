package com.greenroom.modulecommon.entity.user;

import com.greenroom.modulecommon.entity.AuditingCreateUpdateEntity;
import com.greenroom.modulecommon.entity.category.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

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

    @Column(nullable = false, length = 8)
    private String name;

    @Column(length = 20)
    private String profileImage;

    public void update(String name) {
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= 20, "name은 20자 이하여야 합니다.");

        this.name = name;
    }

    public void update(Category category) {
        checkArgument(category != null, "category 값은 필수입니다.");

        this.category = category;
    }

    public void update(Category category, String name) {
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= 20, "name은 20자 이하여야 합니다.");

        this.category = category;
        this.name = name;
    }

    @Builder
    private User(Long id, OAuthType oauthType, String oauthId, Category category, String name) {
        checkArgument(oauthType != null, "oauthType 값은 필수입니다.");
        checkArgument(isNotEmpty(oauthId), "oauthId 값은 필수입니다.");
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");

        this.id = id;
        this.oauthType = oauthType;
        this.oauthId = oauthId;
        this.category = category;
        this.name = name;
    }
}
