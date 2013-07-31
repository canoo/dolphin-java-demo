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

package org.opendolphin.demo.push;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.NamedCommandHandler;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VehiclePushActions extends DolphinServerAction {

    private static List<String> vehicles = Arrays.asList("red", "blue", "green", "orange");

    @Override
    public void registerIn(ActionRegistry registry) {
        registry.register(VehicleConstants.CMD_PULL, new NamedCommandHandler() {
            public void handleCommand(NamedCommand cmd, List<Command> response) {
                for (String pmId : vehicles) {
                    presentationModel(pmId, VehicleConstants.TYPE_VEHICLE, new DTO(
                            new Slot(VehicleConstants.ATT_X, rand(), VehicleConstants.qualify(pmId, VehicleConstants.ATT_X)),
                            new Slot(VehicleConstants.ATT_Y, rand(), VehicleConstants.qualify(pmId, VehicleConstants.ATT_Y)),
                            new Slot(VehicleConstants.ATT_WIDTH, 80),
                            new Slot(VehicleConstants.ATT_HEIGHT, 25),
                            new Slot(VehicleConstants.ATT_ROTATE, rand(), VehicleConstants.qualify(pmId, VehicleConstants.ATT_ROTATE)),
                            new Slot(VehicleConstants.ATT_COLOR, pmId, VehicleConstants.qualify(pmId, VehicleConstants.ATT_COLOR))
                    ));
                }
            }
        });
        registry.register(VehicleConstants.CMD_UPDATE, new NamedCommandHandler() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                sleep(Double.valueOf(Math.random() * 1000).intValue()); // long-polling: server sleeps until new info is available
                Collections.shuffle(vehicles);
                ServerPresentationModel pm = getServerDolphin().getAt(vehicles.get(0));
                changeValue(pm.getAt(VehicleConstants.ATT_X),        rand());
                changeValue(pm.getAt(VehicleConstants.ATT_Y),        rand());
                changeValue(pm.getAt(VehicleConstants.ATT_ROTATE),   rand());
            }
        });
    }

    private void sleep(int sleepMillis) {
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            throw new RuntimeException("sleep has been interrupted");
        }
    }

    private int rand() {
        return Double.valueOf(Math.random() * 350).intValue();
    }
}
