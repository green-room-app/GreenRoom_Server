package com.greenroom.moduleapi.service.scrap;

import com.greenroom.moduleapi.service.question.UserQuestionService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.question.UserQuestion;
import com.greenroom.modulecommon.entity.scrap.Scrap;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.repository.scrap.ScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScrapServiceImpl implements ScrapService {

    private final ScrapRepository scrapRepository;
    private final UserQuestionService userQuestionService;
    private final UserService userService;

    @Override
    @Transactional
    public Long create(Long userId, Long userQuestionId) {
        checkArgument(userId != null, "userId 값은 필수입니다.");
        checkArgument(userQuestionId != null, "userQuestionId 값은 필수입니다.");

        User user = userService.getUser(userId);
        UserQuestion userQuestion = userQuestionService.getUserQuestion(userQuestionId);

        Scrap scrap = Scrap.builder()
                            .user(user)
                            .userQuestion(userQuestion)
                            .build();

        scrapRepository.save(scrap);

        return scrap.getId();
    }

    @Override
    public List<Scrap> getScraps(Long userId, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return scrapRepository.findAll(userId, pageable);
    }
}
