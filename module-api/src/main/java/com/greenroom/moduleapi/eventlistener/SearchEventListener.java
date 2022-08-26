package com.greenroom.moduleapi.eventlistener;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.greenroom.modulecommon.event.SearchEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SearchEventListener implements AutoCloseable {

    private final EventBus eventBus;

    //private final SearchService

    public SearchEventListener(EventBus eventBus) {
        this.eventBus = eventBus;

        eventBus.register(this);
    }

    @Subscribe
    public void handleSearchEvent(SearchEvent event) {

    }

    @Override
    public void close() throws Exception {
        eventBus.unregister(this);
    }
}
