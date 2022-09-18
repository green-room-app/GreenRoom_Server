package com.greenroom.moduleapi.service.greenroom.scrap;

import com.greenroom.moduleapi.service.greenroom.question.GreenRoomQuestionService;
import com.greenroom.moduleapi.service.user.UserService;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestion;
import com.greenroom.modulecommon.entity.greenroom.GreenRoomQuestionScrap;
import com.greenroom.modulecommon.entity.user.User;
import com.greenroom.modulecommon.exception.ApiException;
import com.greenroom.modulecommon.repository.greenroom.scrap.GreenRoomQuestionScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.greenroom.modulecommon.exception.EnumApiException.NOT_FOUND;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class GreenRoomQuestionScrapServiceImpl implements GreenRoomQuestionScrapService {

    private final GreenRoomQuestionScrapRepository scrapRepository;
    private final GreenRoomQuestionService greenRoomQuestionService;
    private final UserService userService;

    @Override
    @Transactional
    public Long create(Long questionId, Long userId) {
        checkArgument(questionId != null, "questionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        if (scrapRepository.exist(questionId, userId)) {
            GreenRoomQuestionScrap scrap = getScrap(questionId, userId);

            if (scrap.isDeleted()) {
                return scrap.getId();
            }

            scrap.enable();
            return scrap.getId();
        }

        GreenRoomQuestion greenRoomQuestion = greenRoomQuestionService.getGreenRoomQuestion(questionId);
        User user = userService.getUser(userId);

        GreenRoomQuestionScrap scrap = GreenRoomQuestionScrap.builder()
                                                            .greenRoomQuestion(greenRoomQuestion)
                                                            .user(user)
                                                            .build();

        return scrapRepository.save(scrap).getId();
    }

    @Override
    public List<GreenRoomQuestionScrap> getScraps(Long userId, Pageable pageable) {
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return scrapRepository.findAll(userId, pageable);
    }

    @Override
    public GreenRoomQuestionScrap getScrap(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        return scrapRepository.find(id)
                .orElseThrow(
                    () -> new ApiException(NOT_FOUND, GreenRoomQuestionScrap.class, String.format("id = %s", id))
                );
    }

    @Override
    public GreenRoomQuestionScrap getScrap(Long questionId, Long userId) {
        checkArgument(questionId != null, "questionId 값은 필수입니다.");
        checkArgument(userId != null, "userId 값은 필수입니다.");

        return scrapRepository.find(questionId, userId)
                .orElseThrow(
                    () -> new ApiException(NOT_FOUND,
                    GreenRoomQuestionScrap.class,
                    String.format("questionId = %s, userId = %s", questionId, userId))
                );
    }

    @Override
    @Transactional
    public void delete(Long id) {
        checkArgument(id != null, "id 값은 필수입니다.");

        GreenRoomQuestionScrap scrap = getScrap(id);
        scrap.delete();
    }
}
