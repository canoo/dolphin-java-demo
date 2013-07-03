package org.opendolphin.demo.lazy;

import org.opendolphin.core.comm.*;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;
import org.opendolphin.demo.data.Address;
import org.opendolphin.demo.data.AddressGenerator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.opendolphin.demo.lazy.LazyLoadingConstants.ATT.*;
import static org.opendolphin.demo.lazy.LazyLoadingConstants.TYPE.*;

public class LazyLoadingAction extends DolphinServerAction {

    List<Address> addressList;

    public LazyLoadingAction(int numEntries) {
        addressList = new AddressGenerator().getAddressList(numEntries);
        // initial sorting
        Collections.sort(addressList, new Comparator<Address>() {
            @Override
            public int compare(Address address1, Address address2) {
                int result = address1.getLast().compareToIgnoreCase(address2.getLast());
                if (result == 0) {
                    result = address1.getFirst().compareToIgnoreCase(address2.getFirst());
                }
                if (result == 0) {
                    result = address1.getCity().compareToIgnoreCase(address2.getCity());
                }
                return result;
            }
        });
    }

    @Override
    public void registerIn(ActionRegistry registry) {
        registry.register(GetPresentationModelCommand.class, new CommandHandler<GetPresentationModelCommand>() {
            public void handleCommand(GetPresentationModelCommand cmd, List<Command> response) {
                String pmId = cmd.getPmId();
                if (pmId == null) {
                    return;
                }
                if (getServerDolphin().getAt(pmId) == null) {
                    initPresentationModel(pmId, response);
                }
            }
        });
    }

    private void initPresentationModel(String pmId, List<Command> response) {
        Address address = addressList.get(Integer.valueOf(pmId));
        response.add(createInitializeAttributeCommand(pmId, ID, pmId));
        response.add(createInitializeAttributeCommand(pmId, FIRST, address.getFirst()));
        response.add(createInitializeAttributeCommand(pmId, LAST, address.getLast()));
        response.add(createInitializeAttributeCommand(pmId, FIRST_LAST, address.getFirst() + " " + address.getLast()));
        response.add(createInitializeAttributeCommand(pmId, LAST_FIRST, address.getLast() + ", " + address.getFirst()));
        response.add(createInitializeAttributeCommand(pmId, CITY, address.getCity()));
        response.add(createInitializeAttributeCommand(pmId, PHONE, address.getPhone()));
    }

    private InitializeAttributeCommand createInitializeAttributeCommand(String pmId, String attributeName, Object attributeValue) {
        return new InitializeAttributeCommand(pmId, attributeName, null, attributeValue, LAZY);
    }
}
