package app.wnh.planet;

/*-
 * #%L
 * FXPlanet
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2023 Will Hartung
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import app.wnh.planet.generator.Projection;
import app.wnh.planet.generator.ColorScheme;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PlanetFinderController implements Initializable {

    Stage stage;

    Random rnd = new Random();

    @FXML
    Parent view;
    @FXML
    private Button generateButton;
    @FXML
    private ChoiceBox<Projection> projectionChoiceBox;
    @FXML
    private ChoiceBox<ColorSchemeModel> colorSchemeChoiceBox;
    @FXML
    private ChoiceBox<String> hydroPercentChoiceBox;
    @FXML
    private ChoiceBox<Integer> generateNumberChoiceBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private GridPane gridPane;

    Image image;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectionChoiceBox.getItems().addAll(Projection.values());
        projectionChoiceBox.setValue(Projection.PETER);
//        colorSchemeChoiceBox.getItems().addAll(ColorScheme.values());
        colorSchemeChoiceBox.setItems($.colorSchemeManager.colorSchemes());
        colorSchemeChoiceBox.setValue($.colorSchemeManager.getDefault());
        hydroPercentChoiceBox.getItems().addAll(
                "Any",
                "0-9%",
                "10-19%",
                "20-29%",
                "30-39%",
                "40-49%",
                "50-59%",
                "60-69%",
                "70-79%",
                "80-89%",
                "90-99%");
        hydroPercentChoiceBox.selectionModelProperty().get().select(0);
        generateNumberChoiceBox.getItems().addAll(20, 40, 60);
        generateNumberChoiceBox.selectionModelProperty().get().select(0);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest((t) -> {
            $.stageManager.setPlanetFinderStage(null);
        });
    }

    public Stage getStage() {
        return stage;
    }

    public Parent getView() {
        return view;
    }

    public void setView(Parent view) {
        this.view = view;
    }

    @FXML
    public void generate(ActionEvent event) {
        int numberToMake = generateNumberChoiceBox.getValue();
        generateButton.setDisable(true);
        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                List<ImageView> images = new ArrayList<>();
                for (int i = 0; i < numberToMake; i++) {
                    PlanetModel model = PlanetModel.getDefaultModel();
                    model.setHeight(150);
                    model.setWidth(150);
                    model.setProjection(projectionChoiceBox.getValue());
                    model.setColorScheme(colorSchemeChoiceBox.getValue());
                    String waterStr = hydroPercentChoiceBox.getValue();
                    if (!"Any".equals(waterStr)) {
                        int hydroValue = waterStr.charAt(0) - Character.valueOf('0');
                        model.findWaterWorld(hydroValue);
                    } else {
                        model.setSeed(rnd.nextDouble());
                    }
                    ImageView imageView = new ImageView(model.renderImage());
                    imageView.setOnMouseClicked((e) -> {
                        if (e.getButton().equals(MouseButton.PRIMARY)) {
                            if (e.getClickCount() == 2) {
                                model.setHeight(400);
                                model.setWidth(400);
                                Stage newStage = new Stage();
                                PlanetFormController controller = PlanetFormController.getControllerWithStageModel(newStage, model);
                                controller.makeDirty();
                                newStage.show();
                                controller.render(event);
                            }
                        }
                    });
                    images.add(imageView);
                    updateProgress(i, numberToMake);
                }
                Platform.runLater(() -> {
                    GridPane gp = new GridPane();
                    gp.setHgap(10);
                    gp.setVgap(10);
                    for (int i = 0; i < images.size(); i++) {
                        int row = i / 4;
                        int col = i % 4;
                        gp.add(images.get(i), col, row);
                    }
                    scrollPane.setContent(gp);
                    generateButton.setDisable(false);
                });
                return null;
            }
        };
        ProgressBar pb = new ProgressBar();
        pb.progressProperty().bind(task.progressProperty());
        scrollPane.setContent(pb);
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }

    public static PlanetFinderController getControllerWithStage(Stage stage) {
        PlanetFinderController controller = getController();
        Scene newScene = new Scene(controller.getView(), 800, 600);
        controller.setStage(stage);
        $.stageManager.setPlanetFinderStage(stage);
        stage.setTitle("Planet Finder");
        Rectangle2D stageRect = $.stageManager.getNewStageRect();
        stage.setX(stageRect.getMinX());
        stage.setY(stageRect.getMinY());
        stage.setWidth(stageRect.getWidth());
        stage.setHeight(stageRect.getHeight());
        stage.focusedProperty().addListener((ov, orig, current) -> {
            $.stageManager.changingFocus(stage, current);
        });

        stage.setScene(newScene);

        return controller;
    }

    public static PlanetFinderController getController() {
        PlanetFinderController controller = null;
        FXMLLoader loader = new FXMLLoader($.resources.getResource("PlanetFinder.fxml"));
        try {
            Parent parent = loader.load();
            parent.getStylesheets().add($.resources.getRootCSSURLString());
            controller = loader.getController();
        } catch (IOException ex) {
            throw new RuntimeException("Unknown error trying to load PlanetFormController", ex);
        }

        return controller;
    }
}
