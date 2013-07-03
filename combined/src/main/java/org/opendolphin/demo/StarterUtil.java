package org.opendolphin.demo;

import org.opendolphin.LogConfig;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.comm.JsonCodec;

import java.lang.System;

class StarterUtil {

    static ClientDolphin setupForRemote() {
        LogConfig.logCommunication();
        ClientDolphin dolphin = new ClientDolphin();
        dolphin.setClientModelStore(new ClientModelStore(dolphin));
        String remoteProperty = System.getProperty("remote");
        String url = (remoteProperty != null) ? remoteProperty : "http://localhost:8080/dolphin-grails/dolphin/";
        System.out.println(" connecting to  $url ");
        System.out.println("use -Dremote=... to override");
        HttpClientConnector connector = new HttpClientConnector(dolphin, url);
        connector.setCodec(new JsonCodec());
        dolphin.setClientConnector(connector);
        return dolphin;
    }
}
