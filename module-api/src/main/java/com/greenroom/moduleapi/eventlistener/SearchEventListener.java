package com.greenroom.moduleapi.eventlistener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.greenroom.moduleapi.service.question.search.QuestionSearchService;
import com.greenroom.modulecommon.event.SearchEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchEventListener implements AutoCloseable {

    private final EventBus eventBus;
    private final QuestionSearchService questionSearchService;

    public SearchEventListener(EventBus eventBus, QuestionSearchService questionSearchService) {
        this.eventBus = eventBus;
        this.questionSearchService = questionSearchService;

        eventBus.register(this);
    }

    @Subscribe
    public void handleSearchEvent(SearchEvent event) {
        questionSearchService.addWordsCount(event.getSearchWord());
    }

    @Override
    public void close() throws Exception {
        eventBus.unregister(this);
    }
}
