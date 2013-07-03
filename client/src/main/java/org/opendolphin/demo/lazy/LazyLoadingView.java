package org.opendolphin.demo.lazy;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.GridPaneBuilder;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.labs.scene.control.gauge.Gauge;
import jfxtras.labs.scene.control.gauge.Radial;
import jfxtras.labs.scene.control.gauge.StyleModel;
import org.opendolphin.binding.JFXBinder;
import org.opendolphin.core.ModelStoreEvent;
import org.opendolphin.core.ModelStoreListener;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.comm.OnFinishedHandlerAdapter;
import org.opendolphin.core.client.comm.WithPresentationModelHandler;
import org.opendolphin.demo.FX;

import java.util.*;

import static org.opendolphin.demo.lazy.LazyLoadingConstants.ATT.*;
import static org.opendolphin.demo.lazy.LazyLoadingConstants.TYPE.*;

/**
 * A demo that shows how to easily do lazy loading with the standard Dolphin on-board means.
 * Simply start and see how the table on the left-hand-side fills - depending on the sleepMillis
 * more or less quickly.
 * The details show how big the "imaginary" table is and how many items are loaded lazily from the server.
 * Clicking on any row will display the details in the textField - loaded as a presentation model.
 * Clicking in a not-yet-lazily loaded row does nothing.
 */
public class LazyLoadingView extends Application {

    private static ClientDolphin dolphin;

    public static void setClientDolphin(ClientDolphin clientDolphin) {
        dolphin = clientDolphin;
    }

    @Override
    public void start(Stage stage) throws Exception {

        List<String> attributeNames = Arrays.asList(ID, FIRST_LAST, LAST_FIRST, CITY, PHONE);
        final ClientPresentationModel dataMold = dolphin.presentationModel("dataMold", attributeNames);

        final ObservableList<Map> observableList = FXCollections.observableArrayList();

        final Radial gauge = new Radial(new StyleModel());
        gauge.getStyleModel().setFrameDesign(Gauge.FrameDesign.CHROME);
        gauge.setTitle("Real Load %");
        gauge.setLcdDecimals(3);
        gauge.setPrefSize(250, 250);
        gauge.setEffect(DropShadowBuilder.create().radius(20).color(new Color(0,0,0,0.4)).build());


        TableColumn nameCol = TableColumnBuilder.create().text("Name").prefWidth(150).build();
        nameCol.setSortable(false); // sorting needs all data to be loaded
        TableColumn cityCol = TableColumnBuilder.create().text("City").prefWidth(150).build();
        cityCol.setSortable(false); // sorting needs all data to be loaded
        TableView table = TableViewBuilder.create().id("table").prefWidth(300).build();
        table.getColumns().add(nameCol);
        table.getColumns().add(cityCol);
        table.setItems(observableList);

        // cell values are lazily requested from JavaFX and must return an observable value
        nameCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures cellDataFeatures) {
                Map valueMap = (Map) cellDataFeatures.getValue();
                String lazyId = valueMap.get("id").toString(); // it.value['id'];
                final SimpleStringProperty placeholder = new SimpleStringProperty("...");
                dolphin.getClientModelStore().withPresentationModel(lazyId, new WithPresentationModelHandler() {
                    public void onFinished(ClientPresentationModel presentationModel) {
                        String value = presentationModel.getAt(LAST_FIRST).getValue().toString();
                        placeholder.setValue(value); // fill async lazily
                    }
                });
                return placeholder;
            }
        });
        cityCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures, ObservableValue>() {
            @Override
            public ObservableValue call(TableColumn.CellDataFeatures cellDataFeatures) {
                Map valueMap = (Map) cellDataFeatures.getValue();
                String lazyId = valueMap.get("id").toString(); // it.value['id'];
                final SimpleStringProperty placeholder = new SimpleStringProperty("...");
                dolphin.getClientModelStore().withPresentationModel(lazyId, new WithPresentationModelHandler() {
                    public void onFinished(ClientPresentationModel presentationModel) {
                        String value = presentationModel.getAt(CITY).getValue().toString();
                        placeholder.setValue(value); // fill async lazily
                    }
                });
                return placeholder;
            }
        });

        // when a table row is selected, we fill the mold and the detail view gets updated
        table.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue selected, Object o, Object oldVal) {
                Map selectedPm = (Map) selected.getValue();
                String pmId = (selectedPm == null) ? null : selectedPm.get("id").toString();
                dolphin.getClientModelStore().withPresentationModel(pmId, new WithPresentationModelHandler() {
                    public void onFinished(ClientPresentationModel presentationModel) {
                        dolphin.apply(presentationModel).to(dataMold);
                    }
                });
            }
        });

        final TextField nameField = TextFieldBuilder.create().id("nameField").prefColumnCount(10).build();
        final TextField cityField = TextFieldBuilder.create().id("cityField").prefColumnCount(10).build();
        final TextField phoneField = TextFieldBuilder.create().id("phoneField").prefColumnCount(10).build();
        final Label lazilyLoadedField = LabelBuilder.create().id("lazilyLoadedField").build();
        final Label tableSizeField = LabelBuilder.create().id("tableSizeField").build();
        final Label selectedIndexField = LabelBuilder.create().id("selectedIndexField").build();

        GridPane centerView = GridPaneBuilder.create().hgap(10).vgap(10).padding(new Insets(20, 20, 20, 20)).build();
        centerView.getColumnConstraints().add(ColumnConstraintsBuilder.create().halignment(HPos.RIGHT).build());
        centerView.add(new Label("Name"), 0, 0);
        centerView.add(nameField, 1, 0);
        centerView.add(new Label("City"), 0, 1);
        centerView.add(cityField, 1, 1);
        centerView.add(new Label("Phone"), 0, 2);
        centerView.add(phoneField, 1, 2);
        centerView.add(new Label("table size"), 0, 3);
        centerView.add(tableSizeField, 1, 3);
        centerView.add(new Label("lazily loaded"), 0, 4);
        centerView.add(lazilyLoadedField, 1, 4);
        centerView.add(new Label("selected index"), 0, 5);
        centerView.add(selectedIndexField, 1, 5);
        centerView.add(gauge, 1, 6);

        // all the bindings ...
        JFXBinder.bind(ID).of(dataMold).to(FX.TEXT).of(selectedIndexField);
        JFXBinder.bind(FIRST_LAST).of(dataMold).to(FX.TEXT).of(nameField);
        JFXBinder.bind(CITY).of(dataMold).to(FX.TEXT).of(cityField);
        JFXBinder.bind(PHONE).of(dataMold).to(FX.TEXT).of(phoneField);
        JFXBinder.bind(PHONE).of(dataMold).to(FX.TEXT).of(phoneField);

        // count the number of lazily loaded pms by listing to the model store
        final Counter counter = new Counter();

        dolphin.addModelStoreListener(LAZY, new ModelStoreListener() {
            public void modelStoreChanged(ModelStoreEvent evt) {
                if (evt.getType() == ModelStoreEvent.Type.ADDED) {
                    counter.increment();
                    lazilyLoadedField.setText(String.valueOf(counter.getValue()));
                }
            }
        });

        // count the number of lazily loaded pms by listing to the model store
        dolphin.addModelStoreListener(LAZY, new ModelStoreListener() {
            public void modelStoreChanged(ModelStoreEvent evt) {
                if (evt.getType() == ModelStoreEvent.Type.ADDED) {
                    gauge.setValue(100d * counter.getValue() / observableList.size());
                }
            }
        });

        // when starting, first fill the table with pm ids
        dolphin.send("fullDataRequest", new OnFinishedHandlerAdapter() {
            @Override
            public void onFinishedData(List<Map> data) {
                for (Map map : data) {
                    observableList.add(map);
                }
                tableSizeField.setText(String.valueOf(observableList.size()));
            }
        });

        // TODO set margins of 10
        BorderPane borderPane = new BorderPane();
        borderPane.setLeft(table);
        borderPane.setCenter(centerView);


        Scene scene = new Scene(borderPane, 700, 500);
        scene.setFill(Color.GRAY);
        org.opendolphin.demo.DemoStyle.blueStyle(scene);
        stage.setScene(scene);
        stage.setTitle("Dolphin lazy loading demo");
        stage.show();
    }

}
