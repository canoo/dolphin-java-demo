package org.opendolphin.demo.lazy;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.DataCommand;
import org.opendolphin.core.comm.NamedCommand;
import org.opendolphin.core.server.comm.NamedCommandHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullDataRequestCommandHandler implements NamedCommandHandler {

    private final int numEntries;

    public FullDataRequestCommandHandler(int numEntries) {
        this.numEntries = numEntries;
    }

    @Override
    public void handleCommand(NamedCommand command, List<Command> response) {
        for (int i=0; i < numEntries; i++) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("id", i);
            response.add(new DataCommand(data));
        }
    }
}
