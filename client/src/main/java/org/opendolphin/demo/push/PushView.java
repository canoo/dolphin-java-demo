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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.effect.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradientBuilder;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.RectangleBuilder;
import javafx.scene.shape.StrokeType;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opendolphin.binding.Converter;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.binding.JavaFxUtil;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;
import org.opendolphin.demo.FX;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opendolphin.demo.DemoStyle.defaultStyle;

public class PushView extends Application {

    private static ClientDolphin dolphin;

    public static void setClientDolphin(ClientDolphin clientDolphin) {
        dolphin = clientDolphin;
    }

    private void longPoll() {
        dolphin.send(VehicleConstants.CMD_UPDATE, new OnFinishedHandlerAdapter() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                longPoll();
            }
        });
    }

    @Override
    public void start(Stage stage) throws Exception {

        final ClientPresentationModel selectedVehicle = dolphin.presentationModel(VehicleConstants.ID_SELECTED, VehicleConstants.ALL_ATTRIBUTES);

        final ObservableList<ClientPresentationModel> observableListOfPms = FXCollections.observableArrayList();
        final Map<String, Rectangle> pmIdsToRect = new HashMap<String, Rectangle>(); // pmId to rectangle

        Rectangle selRect = newVehicle(null);

        final TextField selX = new TextField();
        final TextField selY = new TextField();

        Rectangle selAngle = new Rectangle();
        selAngle.setWidth(26);
        selAngle.setHeight(5);
        selAngle.setFill(LinearGradientBuilder.create().stops(new Stop(0.6, Color.WHITE), new Stop(1, Color.RED)).build());

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER);
        header.setPrefWidth(700);
        header.setPrefHeight(40);
        header.setSpacing(10);
        header.getChildren().add(new Label("Selected"));
        header.getChildren().add(selRect);
        header.getChildren().add(new Label("X"));
        header.getChildren().add(selX);
        header.getChildren().add(new Label("Y"));
        header.getChildren().add(selY);
        header.getChildren().add(new Label("Angle"));
        header.getChildren().add(selAngle);

        TableColumn columnColor = new TableColumn("Color");
        columnColor.setPrefWidth(50);
        TableColumn columnX = new TableColumn("X");
        columnX.setPrefWidth(40);
        TableColumn columnY = new TableColumn("Y");
        columnY.setPrefWidth(40);
        TableColumn columnA = new TableColumn("Angle");
        columnA.setPrefWidth(40);

        final TableView table = new TableView();
//        table.setOpacity(0.2);
        table.getColumns().add(columnColor);
        table.getColumns().add(columnX);
        table.getColumns().add(columnY);
        table.getColumns().add(columnA);
        table.setItems(observableListOfPms);

        JavaFxUtil.value(VehicleConstants.ATT_COLOR, columnColor);
        JavaFxUtil.value(VehicleConstants.ATT_X, columnX);
        JavaFxUtil.value(VehicleConstants.ATT_Y, columnY);
        JavaFxUtil.value(VehicleConstants.ATT_ROTATE, columnA);

//        // TODO: Logo can currently only added using GroovyFX
//        DolphinLogo logo = new DolphinLogo();
//        logo.setWidth(401);
//        logo.setHeight(257);
//            logo.opacity = 0.1d

        final Group parent = new Group();
        parent.setEffect(createDropShadow());
        parent.getChildren().add(RectangleBuilder.create().width(400).height(400).fill(Color.TRANSPARENT).build()); // rigid area

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20, 20, 20, 20));
        borderPane.setTop(header);
        borderPane.setLeft(table);
        borderPane.setCenter(parent);

        // when a new pm is added to the list, create the rectangles along with their animations
        observableListOfPms.addListener(new ListChangeListener<ClientPresentationModel>() {
            @Override
            public void onChanged(Change<? extends ClientPresentationModel> listChange) {
                while (listChange.next()) { /*sigh*/
                    for (final ClientPresentationModel pm : listChange.getAddedSubList()) {
                        final Rectangle rectangle = newVehicle(pm.getId());
                        rectangle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                            @Override
                            public void handle(MouseEvent mouseEvent) {
                                dolphin.apply(pm).to(selectedVehicle);
                            }
                        });
                        pmIdsToRect.put(pm.getId(), rectangle);

                        for (Attribute attribute : pm.getAttributes()) {
                            final String prop = attribute.getPropertyName();
                            if (prop.equals(VehicleConstants.ATT_COLOR))
                                break; // only for the moment - until we convert types

                            rectangle.getProperties().put(prop, pm.getAt(prop).getValue());
                            pm.getAt(prop).addPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void propertyChange(PropertyChangeEvent evt) {
                                    if (evt.getPropertyName().equals("value")) {
                                        Timeline timeline = new Timeline();
                                        ClientAttribute attr = (ClientAttribute) evt.getSource();
                                        Double newValue = Double.valueOf(evt.getNewValue().toString());
                                        if (attr.getPropertyName().equals(VehicleConstants.ATT_X)) {
                                            KeyValue xKeyValue = new KeyValue(rectangle.xProperty(), newValue);
                                            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), xKeyValue));
                                        }
                                        if (attr.getPropertyName().equals(VehicleConstants.ATT_Y)) {
                                            KeyValue yKeyValue = new KeyValue(rectangle.yProperty(), newValue);
                                            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), yKeyValue));
                                        }
                                        if (attr.getPropertyName().equals(VehicleConstants.ATT_ROTATE)) {
                                            KeyValue rotateKeyValue = new KeyValue(rectangle.rotateProperty(), newValue);
                                            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.5), rotateKeyValue));
                                        }
                                        timeline.play();
//                                        sgb.timeline {
//                                            at(0.5.s) { change(rectangle, prop) to evt.newValue tween "ease_both" }
//                                        }.play()
                                    }
                                }
                            });
                        }
                        parent.getChildren().add(rectangle);
                    }
                }
            }
        });


        // startup and main loop

        dolphin.send(VehicleConstants.CMD_PULL, new OnFinishedHandlerAdapter() {
            @Override
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                for (ClientPresentationModel pm : presentationModels) {
                    observableListOfPms.add(pm);
                }
//                    fadeTransition(1.s, node:table, to:1).playFromStart()
                longPoll();
            }
        });

        // all the bindings ...
        JFXBinder.bind(VehicleConstants.ATT_X).of(selectedVehicle).to(FX.TEXT).of(selX); // simple binding + action
        selX.addEventHandler(EventType.ROOT, new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                selectedVehicle.getAt(VehicleConstants.ATT_X).setValue(new StringToIntegerConverter().convert(selX.getText()));
            }
        });

        JFXBinder.bind(VehicleConstants.ATT_Y).of(selectedVehicle).to(FX.TEXT).of(selY); // example of a "bidirectional" binding
        JFXBinder.bind(FX.TEXT).of(selY).using(new StringToIntegerConverter()).to(VehicleConstants.ATT_Y).of(selectedVehicle);

        JFXBinder.bind(VehicleConstants.ATT_COLOR).of(selectedVehicle).using(new StringToColorConverter()).to(FX.FILL).of(selRect);
        JFXBinder.bind(VehicleConstants.ATT_ROTATE).of(selectedVehicle).using(new StringToDoubleConverter()).to(FX.ROTATE).of(selAngle);

        // bind 'selectedItem' of table.selectionModel to { ... }
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldValue, Object newValue) {
                ClientPresentationModel selectedPm = (ClientPresentationModel) newValue;
                dolphin.apply(selectedPm).to(selectedVehicle);
            }
        });

        // bind COLOR of selectedVehicle to { ... }
        selectedVehicle.getAt(VehicleConstants.ATT_COLOR).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Object from = evt.getOldValue();
                Object to = evt.getNewValue();
                if (from != null) {
                    pmIdsToRect.get(from.toString()).setStrokeWidth(0);
                }
                if (to != null) {
                    pmIdsToRect.get(to.toString()).setStrokeWidth(3);
                }
            }
        });

        selectedVehicle.getAt(VehicleConstants.ATT_COLOR).addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                Object to = evt.getNewValue();
                if (to != null) {
                    table.getSelectionModel().select(dolphin.findPresentationModelById(to.toString()));
                }
            }
        });

        Scene scene = new Scene(borderPane, 700, 500);
        defaultStyle(scene);
        stage.setScene(scene);
        stage.setTitle("Dolphin push demo");
        stage.show();

    }

    private DropShadow createDropShadow() {
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0);
        Lighting lighting = new Lighting();
        lighting.setLight(light);
        return DropShadowBuilder.create().offsetY(2).offsetX(2).radius(3).input(lighting).build();
    }

    private Rectangle newVehicle(String id) {
        Rectangle selRect = new Rectangle();
        selRect.setFill(id == null ? null : Paint.valueOf(id));
        selRect.setArcWidth(10);
        selRect.setArcHeight(10);
        selRect.setWidth(74);
        selRect.setHeight(20);
        selRect.setStroke(Paint.valueOf("cyan"));
        selRect.setStrokeWidth(2);
        selRect.setStrokeType(StrokeType.OUTSIDE);
        selRect.setEffect(DropShadowBuilder.create().offsetY(2).offsetX(2).radius(3).build());  // input: lighting{distant(azimuth: -135.0)})
        return selRect;
    }

    private class StringToColorConverter implements Converter<String, Paint> {
        @Override
        public Paint convert(String value) {
            return (value == null) ? Color.TRANSPARENT : Paint.valueOf(value);
        }
    }

    private class StringToIntegerConverter implements Converter<String, Integer> {
        @Override
        public Integer convert(String value) {
            return (value == null) ? 0 : Integer.valueOf(value);
        }
    }

    private class StringToDoubleConverter implements Converter<String, Double> {
        @Override
        public Double convert(String value) {
            return (value == null) ? 0 : Double.valueOf(value);
        }
    }

}
