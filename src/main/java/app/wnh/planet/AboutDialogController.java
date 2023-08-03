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
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class AboutDialogController implements Initializable {

    @FXML
    Parent view;

    @FXML
    Pane pane;

    Group group;

    Timeline animation;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public Parent getView() {
        if (group == null) {
            group = get3DSphere();
            pane.getChildren().add(group);
        }
        return view;
    }

    public Group get3DSphere() {
        Group group = new Group();

        Sphere sphere = new Sphere(75);

        PlanetModel renderingModel = PlanetModel.getDefaultModel();

        renderingModel.setHeight(380);
        renderingModel.setWidth(600);
        renderingModel.setProjection(Projection.PETER);
        Image img = renderingModel.renderImage();

        // Create a material
        PhongMaterial material = new PhongMaterial();

        material.setDiffuseMap(img);
        sphere.setMaterial(material);

        sphere.setTranslateX(75);
        sphere.setTranslateY(75);

        // Create a rotation transformation
        Rotate rotate = new Rotate(0, Rotate.Y_AXIS);
        sphere.getTransforms().add(rotate);

        if (animation == null) {
            // Create a timeline animation
            animation = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(rotate.angleProperty(), 0)),
                    new KeyFrame(Duration.seconds(5), new KeyValue(rotate.angleProperty(), 360))
            );
            animation.setCycleCount(Timeline.INDEFINITE);
            animation.play();
        }

        // Create a group and add the sphere
        group.getChildren().add(sphere);

        return group;
    }

    public void setView(Parent view) {
        this.view = view;
    }

    public Timeline getAnimation() {
        return animation;
    }

    public void stopAnimation() {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }

    public static AboutDialogController getController() {
        AboutDialogController controller = null;
        FXMLLoader loader = new FXMLLoader($.resources.getResource("AboutDialog.fxml"));
        try {
            Parent parent = loader.load();
            parent.getStylesheets().add($.resources.getRootCSSURLString());
            controller = loader.getController();
        } catch (IOException ex) {
            throw new RuntimeException("Unknown error trying to load AboutDialogController", ex);
        }

        return controller;
    }

    public static Dialog<String> getDialog() {
        AboutDialogController controller = getController();
        Dialog<String> dialog = new Dialog<>();
        DialogPane dp = new DialogPane();
        dp.getButtonTypes().addAll(ButtonType.OK);
        dp.setContent(controller.getView());
        dialog.setTitle("About Planet");
        dialog.setDialogPane(dp);
        dialog.setOnHidden((event) -> {
            controller.stopAnimation();
        });

        return dialog;
    }
}
