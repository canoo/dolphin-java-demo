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

import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraintsBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;

import static javafx.geometry.HPos.LEFT;
import static javafx.geometry.HPos.RIGHT;
import static javafx.scene.layout.Priority.ALWAYS;

public class DemoStyle {

    public static void blueStyle(Scene scene){
        Stop firstStop = new Stop(0, Color.LIGHTGRAY);
        Stop secondStop = new Stop(1, Color.DARKGRAY);
        scene.setFill(RadialGradientBuilder.create().stops(firstStop, secondStop).build()); //stops: [groovyblue.brighter(), groovyblue.darker()]
        scene.getStylesheets().add("demo.css");
    }

    public static void style(Scene scene) {
        blueStyle(scene);

        GridPane grid = (GridPane) scene.getRoot();
        grid.getStyleClass().add("form");
        grid.setHgap(5);  // for some reason, the gaps are not taken from the css
        grid.setVgap(10);
        grid.getColumnConstraints().add(ColumnConstraintsBuilder.create().halignment(RIGHT).hgrow(ALWAYS).build());
        grid.getColumnConstraints().add(ColumnConstraintsBuilder.create().halignment(LEFT).hgrow(ALWAYS).build());

//        translateTransition(1.s, node: grid, fromY: -100, toY: 0).play()
    }
}
