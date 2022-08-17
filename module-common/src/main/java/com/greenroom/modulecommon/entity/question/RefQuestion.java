package com.greenroom.modulecommon.entity.question;

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
@Table(name = "ref_questions")
public class RefQuestion extends AuditingCreateUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String question;

    @Builder
    private RefQuestion(Long id, Category category, String question) {
        checkArgument(category != null, "category 값은 필수입니다.");
        checkArgument(isNotEmpty(question), "question 값은 필수입니다.");

        this.id = id;
        this.category = category;
        this.question = question;
    }
}
