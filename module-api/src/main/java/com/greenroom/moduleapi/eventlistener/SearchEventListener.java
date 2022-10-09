package com.greenroom.moduleapi.eventlistener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.greenroom.moduleapi.service.greenroom.question.search.GreenRoomQuestionSearchService;
import com.greenroom.modulecommon.event.SearchEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchEventListener implements AutoCloseable {

    private final EventBus eventBus;
    private final GreenRoomQuestionSearchService greenRoomQuestionSearchService;

    public SearchEventListener(EventBus eventBus, GreenRoomQuestionSearchService greenRoomQuestionSearchService) {
        this.eventBus = eventBus;
        this.greenRoomQuestionSearchService = greenRoomQuestionSearchService;

        eventBus.register(this);
    }

    @Subscribe
    public void handleSearchEvent(SearchEvent event) {
        greenRoomQuestionSearchService.addWordsCount(event.getSearchWord());
    }

    @Override
    public void close() throws Exception {
        eventBus.unregister(this);
    }
}
