package org.opendolphin.demo;

import javafx.application.Application;
import org.opendolphin.core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.comm.DefaultInMemoryConfig;
import org.opendolphin.demo.lazy.FullDataRequestCommandHandler;
import org.opendolphin.demo.lazy.LazyLoadingAction;
import org.opendolphin.demo.lazy.LazyLoadingView;

public class StartLazyLoadingDemo {

    private static final int SLEEP_MILLIS = 0;
    private static final int NUM_ENTRIES = 10000;

    public static void main(String[] args) throws Exception {
        DefaultInMemoryConfig config = new JavaFxInMemoryConfig();
        ((InMemoryClientConnector)config.getClientDolphin().getClientConnector()).setSleepMillis(SLEEP_MILLIS);
        config.getServerDolphin().action("fullDataRequest", new FullDataRequestCommandHandler(NUM_ENTRIES));
        config.getServerDolphin().register(new LazyLoadingAction(NUM_ENTRIES));
        LazyLoadingView.setClientDolphin(config.getClientDolphin());
        Application.launch(LazyLoadingView.class);
    }

}
