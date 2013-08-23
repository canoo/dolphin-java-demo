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

import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadowBuilder;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Light;
import javafx.scene.effect.LightingBuilder;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.opendolphin.logo.DolphinLogoBuilder;

public class StartLogoDemo extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Group logo = new DolphinLogoBuilder().width(400).height(300).build();
        logo.setEffect(createDropShadow());
        doShuffle(logo);

        StackPane pane = new StackPane();
        pane.getChildren().add(logo);

        Scene scene = new Scene(pane);
        stage.setWidth(500);
        stage.setHeight(400);
        stage.setScene(scene);
        stage.setTitle("Dolphin logo demo");
        stage.show();
    }

    private Effect createDropShadow() {
        Light.Distant distantLight = new Light.Distant();
        distantLight.setAzimuth(-135.0);
        Effect lighting = LightingBuilder.create().light(distantLight).build();
        return DropShadowBuilder.create().offsetY(2).offsetX(2).radius(3).color(Color.GREY).input(lighting).build();
    }

    private static void doShuffle(Group logo) {
        final ParallelTransition allAnimations = new ParallelTransition();
        for (Node node : logo.getChildren()) {
            node.setRotate(Math.random() * 360);
            RotateTransition rotateTransition = new RotateTransition(Duration.seconds(3), node);
            rotateTransition.setToAngle(0);
            allAnimations.getChildren().add(rotateTransition);
        }
        logo.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                allAnimations.playFromStart();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        Application.launch(StartLogoDemo.class);
    }

}
