package com.greenroom.modulecommon.entity.user;

import com.greenroom.modulecommon.entity.AuditingCreateUpdateEntity;
import com.greenroom.modulecommon.entity.category.Category;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.constant.EntityConstant.User.NAME_LENGTH;
import static com.greenroom.modulecommon.constant.EntityConstant.User.PROFILE_IMAGE_LENGTH;
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

    @Column(nullable = false, length = NAME_LENGTH)
    private String name;

    @Column(length = PROFILE_IMAGE_LENGTH)
    private String profileImage;

    private boolean used;

    private LocalDate withdrawalDate;

    public void update(String name) {
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 값은 %s자 이하여야 합니다.", NAME_LENGTH));

        this.name = name;
    }

    public void update(Category category) {
        checkArgument(category != null, "category 값은 필수입니다.");

        this.category = category;
    }

    public void update(Category category, String name) {
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 값은 %s자 이하여야 합니다.", NAME_LENGTH));

        this.category = category;
        this.name = name;
    }

    public void updateProfileImage(String profileImage) {
        checkArgument(isNotEmpty(profileImage), "profileImage 값은 필수입니다.");
        checkArgument(profileImage.length() <= PROFILE_IMAGE_LENGTH,
            String.format("profileImage 값은 %s자 이하여야 합니다.", PROFILE_IMAGE_LENGTH));

        this.profileImage = profileImage;
    }

    public void delete() {
        this.used = false;
        this.withdrawalDate = LocalDate.now();
    }

    public boolean isNotUsed() {
        return !this.used;
    }

    @Builder
    private User(Long id, OAuthType oauthType, String oauthId, Category category, String name) {
        checkArgument(oauthType != null, "oauthType 값은 필수입니다.");
        checkArgument(isNotEmpty(oauthId), "oauthId 값은 필수입니다.");
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(name), "name 값은 필수입니다.");
        checkArgument(name.length() <= NAME_LENGTH, String.format("name 값은 %s자 이하여야 합니다.", NAME_LENGTH));

        this.id = id;
        this.oauthType = oauthType;
        this.oauthId = oauthId;
        this.category = category;
        this.name = name;
        this.used = true;
    }
}
