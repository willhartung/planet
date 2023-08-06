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
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ThreeDMapController implements Initializable {

    Stage stage;

    @FXML
    Group group;

    PlanetModel planetModel;
    Timeline animation;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public Parent getView() {
        return group;
    }

    public void setView(Parent view) {
        this.group = (Group) view;
    }

    public void setPlanetModel(PlanetModel planetModel) {
        this.planetModel = planetModel;
        updateView();
    }

    public static ThreeDMapController getControllerWithStageModel(Stage stage, PlanetModel model) {
        ThreeDMapController controller = getController(model);
        Scene newScene = new Scene(controller.getView(), 800, 600);
        controller.setStage(stage);
        stage.setTitle("3D Map View");
        Rectangle2D stageRect = $.stageManager.getNewStageRect();
        stage.setX(stageRect.getMinX());
        stage.setY(stageRect.getMinY());
        stage.setWidth(stageRect.getWidth());
        stage.setHeight(stageRect.getHeight());
        stage.focusedProperty().addListener((ov, orig, current) -> {
            $.stageManager.changingFocus(stage, current);
        });
        stage.setOnHidden((event) -> {
            if (controller.getAnimation() != null) {
                controller.stopAnimation();
            }
        });

        stage.setScene(newScene);
        controller.setPlanetModel(model);

        return controller;
    }

    public static ThreeDMapController getController(PlanetModel model) {
        ThreeDMapController controller = new ThreeDMapController();
        controller.setView(new Group());
        return controller;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void stopAnimation() {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }

    public void updateView() {
        // Create a sphere
        Sphere sphere = new Sphere(200);

        PlanetModel renderingModel = new PlanetModel(planetModel);

        renderingModel.setHeight(380);
        renderingModel.setWidth(600);
        renderingModel.setProjection(Projection.PETER);
//        renderingModel.setColorScheme(ColorScheme.OLSSON);
        Image img = renderingModel.renderImage();

        // Create a material
        PhongMaterial material = new PhongMaterial();

        material.setDiffuseMap(img);
        sphere.setMaterial(material);

        sphere.setTranslateX(400);
        sphere.setTranslateY(300);

        // Create a rotation transformation
        Rotate rotate = new Rotate(0, Rotate.Y_AXIS);
        sphere.getTransforms().add(rotate);

        // Create a timeline animation
        if (animation == null) {
            animation = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(rotate.angleProperty(), 0)),
                    new KeyFrame(Duration.seconds(5), new KeyValue(rotate.angleProperty(), 360))
            );
            animation.setCycleCount(Timeline.INDEFINITE);
            animation.play();
        }

        // Create a group and add the sphere
        group.getChildren().add(sphere);

    }
}
