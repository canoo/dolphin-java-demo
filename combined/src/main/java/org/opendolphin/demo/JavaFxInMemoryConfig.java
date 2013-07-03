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

import groovy.lang.MetaClass;
import org.opendolphin.core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler;
import org.opendolphin.core.comm.DefaultInMemoryConfig;

class JavaFxInMemoryConfig extends DefaultInMemoryConfig {

    JavaFxInMemoryConfig() {
        getClientDolphin().getClientConnector().setUiThreadHandler(new JavaFXUiThreadHandler());
        getServerDolphin().registerDefaultActions();
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getProperty(String propertyName) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setProperty(String propertyName, Object newValue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MetaClass getMetaClass() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
