/*
 * Copyright 2012-2013 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.demo;

import javafx.application.Application;
import org.opendolphin.core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.comm.DefaultInMemoryConfig;
import org.opendolphin.demo.lazy.FullDataRequestCommandHandler;
import org.opendolphin.demo.push.PushView;
import org.opendolphin.demo.push.VehiclePushActions;

public class StartPushDemo {

    private static final int SLEEP_MILLIS = 0;
    private static final int NUM_ENTRIES = 10000;

    public static void main(String[] args) throws Exception {
        DefaultInMemoryConfig config = new JavaFxInMemoryConfig();
        ((InMemoryClientConnector) config.getClientDolphin().getClientConnector()).setSleepMillis(SLEEP_MILLIS);
        config.getServerDolphin().action("fullDataRequest", new FullDataRequestCommandHandler(NUM_ENTRIES));
        config.getServerDolphin().register(new VehiclePushActions());
        PushView.setClientDolphin(config.getClientDolphin());
        Application.launch(PushView.class);
    }

}
