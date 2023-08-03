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
import app.wnh.planet.generator.Shading;
import app.wnh.planet.generator.Projection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;
import javax.imageio.ImageIO;

public class PlanetFormController implements Initializable {

    Stage stage;
    PlanetModel planetModel;
    ObjectProperty<File> planetFileProperty = new SimpleObjectProperty<>();
    DirtyManager dirtyMgr = new DirtyManager();

    Random rnd = new Random();

    @FXML
    Parent view;
    @FXML
    Accordion accordion;
    @FXML
    TitledPane titledPane;
    @FXML
    private Button defaultsButton;
    @FXML
    private Button renderButton;
    @FXML
    private Button copyButton;
    @FXML
    private Button exportButton;
    @FXML
    private TextField widthField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField latitudeField;
    @FXML
    private TextField longitudeField;
    @FXML
    private ChoiceBox<Projection> projectionChoiceBox;
    @FXML
    private Button randomSeedButton;
    @FXML
    private TextField scaleField;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField seedField;
    @FXML
    private CheckBox lockSeedCheckBox;
    @FXML
    private TextField vgridField;
    @FXML
    private TextField hgridField;
    @FXML
    private ChoiceBox<Shading> shadingChoiceBox;
    @FXML
    private CheckBox transparentCheckBox;
    @FXML
    private ChoiceBox<ColorSchemeModel> colorSchemeChoiceBox;
    @FXML
    private Slider longitudeSlider;
    @FXML
    private Slider latitudeSlider;
    @FXML
    private TextField contourLinesField;
    @FXML
    private TextField coastalContourLinesField;
    @FXML
    private TextField latitudeColorField;
    @FXML
    private CheckBox outlineCheckbox;
    @FXML
    private CheckBox blackAndWhiteCheckbox;
    @FXML
    private CheckBox temperatureCheckbox;
    @FXML
    private CheckBox biomeCheckbox;
    @FXML
    private TextField baseField;
    @FXML
    private ChoiceBox<String> hydroPercentChoiceBox;
    @FXML
    private Button searchWaterWorldButton;
    @FXML
    private TextField diameterField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField profileField;
    @FXML
    private TextField waterPercentageField;
    @FXML
    private Label waterPercentageLabel;
    @FXML
    private TextField shadeLongitudeField;
    @FXML
    private TextField shadeLatitudeField;
    @FXML
    private Slider shadeLongitudeSlider;
    @FXML
    private Slider shadeLatitudeSlider;

    @FXML
    private MenuItem fileSaveMenuItem;
    @FXML
    private MenuItem fileRevertMenuItem;

    private TextFormatter<Number> widthFormatter;
    private TextFormatter<Number> heightFormatter;
    private TextFormatter<Number> latitudeFormatter;
    private TextFormatter<Number> longitudeFormatter;
    private TextFormatter<Number> scaleFormatter;
    private TextFormatter<Number> seedFormatter;
    private TextFormatter<Number> vgridFormatter;
    private TextFormatter<Number> hgridFormatter;
    private TextFormatter<Number> contourLinesFormatter;
    private TextFormatter<Number> coastalContourLinesFormatter;
    private TextFormatter<Number> latitudeColorFormatter;
    private TextFormatter<Number> baseFormatter;
    private TextFormatter<Number> diameterFormatter;
    private TextFormatter<Number> shadeLongitudeFormatter;
    private TextFormatter<Number> shadeLatitudeFormatter;

    Image image;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        projectionChoiceBox.getItems().addAll(Projection.values());
        projectionChoiceBox.setValue(Projection.PETER);
        shadingChoiceBox.getItems().addAll(Shading.values());
        shadingChoiceBox.setValue(Shading.NONE);
//        colorSchemeChoiceBox.getItems().addAll($.colorSchemeManager.colorSchemes);
        colorSchemeChoiceBox.setItems($.colorSchemeManager.colorSchemes());
        colorSchemeChoiceBox.setValue($.colorSchemeManager.getByName("Olsson"));
        hydroPercentChoiceBox.getItems().addAll(
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
        hydroPercentChoiceBox.selectionModelProperty().get().select(6);

        widthFormatter = new IntegerTextFormatter();
        heightFormatter = new IntegerTextFormatter();
        latitudeFormatter = new DoubleTextFormatter();
        longitudeFormatter = new DoubleTextFormatter();
        scaleFormatter = new DoubleTextFormatter();
        seedFormatter = new DoubleTextFormatter();
        vgridFormatter = new DoubleTextFormatter();
        hgridFormatter = new DoubleTextFormatter();
        contourLinesFormatter = new IntegerTextFormatter();
        coastalContourLinesFormatter = new IntegerTextFormatter();
        latitudeColorFormatter = new IntegerTextFormatter();
        baseFormatter = new DoubleTextFormatter();
        diameterFormatter = new IntegerTextFormatter();
        shadeLongitudeFormatter = new DoubleTextFormatter();
        shadeLatitudeFormatter = new DoubleTextFormatter();

        widthField.setTextFormatter(widthFormatter);
        heightField.setTextFormatter(heightFormatter);
        latitudeField.setTextFormatter(latitudeFormatter);
        longitudeField.setTextFormatter(longitudeFormatter);
        scaleField.setTextFormatter(scaleFormatter);
        seedField.setTextFormatter(seedFormatter);
        vgridField.setTextFormatter(vgridFormatter);
        hgridField.setTextFormatter(hgridFormatter);
        contourLinesField.setTextFormatter(contourLinesFormatter);
        coastalContourLinesField.setTextFormatter(coastalContourLinesFormatter);
        latitudeColorField.setTextFormatter(latitudeColorFormatter);
        baseField.setTextFormatter(baseFormatter);
        diameterField.setTextFormatter(diameterFormatter);
        shadeLongitudeField.setTextFormatter(shadeLongitudeFormatter);
        shadeLatitudeField.setTextFormatter(shadeLatitudeFormatter);

        latitudeFormatter.valueProperty().bindBidirectional(latitudeSlider.valueProperty());
        longitudeFormatter.valueProperty().bindBidirectional(longitudeSlider.valueProperty());
        shadeLongitudeFormatter.valueProperty().bindBidirectional(shadeLongitudeSlider.valueProperty());
        shadeLatitudeFormatter.valueProperty().bindBidirectional(shadeLatitudeSlider.valueProperty());

        planetFileProperty.addListener((prop, oldValue, newValue) -> {
            if (stage != null) {
                String title = "Planet - ";
                if (newValue == null) {
                    title = title + "Untitled";
                } else {
                    title = title + newValue.getName();
                }
                stage.setTitle(title);
            }
        });

        waterPercentageLabel.visibleProperty().bind(projectionChoiceBox.valueProperty().isEqualTo(Projection.PETER));
        waterPercentageField.visibleProperty().bind(projectionChoiceBox.valueProperty().isEqualTo(Projection.PETER));

        seedField.disableProperty().bind(lockSeedCheckBox.selectedProperty());
        randomSeedButton.disableProperty().bind(lockSeedCheckBox.selectedProperty());
        hydroPercentChoiceBox.disableProperty().bind(lockSeedCheckBox.selectedProperty());
        searchWaterWorldButton.disableProperty().bind(lockSeedCheckBox.selectedProperty());

        dirtyMgr.addAll(widthField,
                heightField,
                latitudeField,
                longitudeField,
                projectionChoiceBox,
                scaleField,
                seedField,
                vgridField,
                hgridField,
                shadingChoiceBox,
                transparentCheckBox,
                colorSchemeChoiceBox,
                contourLinesField,
                coastalContourLinesField,
                outlineCheckbox,
                blackAndWhiteCheckbox,
                latitudeColorField,
                temperatureCheckbox,
                biomeCheckbox,
                baseField,
                diameterField,
                nameField,
                locationField,
                profileField,
                lockSeedCheckBox,
                shadeLongitudeField,
                shadeLatitudeField);
        dirtyMgr.reset();

        fileRevertMenuItem.disableProperty().bind(Bindings.and(dirtyMgr.dirtyProperty(), Bindings.isNotNull(planetFileProperty)).not());
        fileSaveMenuItem.disableProperty().bind(dirtyMgr.dirtyProperty().not());

        accordion.setExpandedPane(titledPane);
    }

    public void setPlanetModel(PlanetModel model) {
        this.planetModel = model;
        widthFormatter.setValue(model.getWidth());
        heightFormatter.setValue(model.getHeight());
        latitudeFormatter.setValue(model.getLatitude());
        longitudeFormatter.setValue(model.getLongitude());
        seedFormatter.setValue(model.getSeed());
        scaleFormatter.setValue(model.getScale());
        vgridFormatter.setValue(model.getVgrid());
        hgridFormatter.setValue(model.getHgrid());
        colorSchemeChoiceBox.setValue(model.getColorScheme());
        projectionChoiceBox.setValue(model.getProjection());
        shadingChoiceBox.setValue(model.getShading());
        transparentCheckBox.setSelected(model.getTransparent());
        contourLinesFormatter.setValue(model.getContourLines());
        coastalContourLinesFormatter.setValue(model.getCoastalContourLines());
        outlineCheckbox.setSelected(model.getOutline());
        blackAndWhiteCheckbox.setSelected(model.getBlackAndWhite());
        latitudeColorFormatter.setValue(model.getLatitudeColor());
        temperatureCheckbox.setSelected(model.getTemperature());
        biomeCheckbox.setSelected(model.getBiome());
        baseFormatter.setValue(model.getBase());
        diameterFormatter.setValue(model.getDiameter());
        nameField.setText(model.getName());
        locationField.setText(model.getLocation());
        profileField.setText(model.getProfile());
        waterPercentageField.setText("" + model.getWaterPercentage());
        lockSeedCheckBox.setSelected(model.getLockSeed());
        shadeLongitudeFormatter.setValue(model.getShadeLongitude());
        shadeLatitudeFormatter.setValue(model.getShadeLatitude());
        bindModel();
        dirtyMgr.reset();
    }

    public void setStage(Stage stage) {
        this.stage = stage;

        stage.setOnCloseRequest((t) -> {
            if (dirtyMgr.isDirty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "File has changed. Save file?", ButtonType.CANCEL, ButtonType.NO, ButtonType.YES);
                alert.setHeaderText("Save changes?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get().equals(ButtonType.YES)) {
                        onFileSave(null);
                    }
                    if (result.get().equals(ButtonType.CANCEL)) {
                        t.consume();
                    }
                }
            }
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

    public File getPlanetFile() {
        return planetFileProperty.get();
    }

    public void setPlanetFile(File file) {
        planetFileProperty.set(file);
    }

    public ObjectProperty<File> planetFileProperty() {
        return planetFileProperty;
    }

    public void makeDirty() {
        dirtyMgr.set();
    }

    @FXML
    public void render(ActionEvent event) {
        image = planetModel.renderImage();
        ImageView view = new ImageView(image);
        view.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = view.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putImage(image);
                db.setContent(content);

                event.consume();
            }
        });

        scrollPane.setContent(view);
    }

    @FXML
    public void copyImage(ActionEvent event) {
        if (image != null) {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putImage(image);
            clipboard.setContent(content);
        }
    }

    @FXML
    public void exportImage(ActionEvent event) {
        if (image != null) {
            FileChooser chooser = new FileChooser();
            FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG Format", "*.png");
            chooser.getExtensionFilters().addAll(pngFilter);
            chooser.setTitle("Export image");
            File file = chooser.showSaveDialog(null);
            if (file != null) {
                String extension = "png";
                try {
                    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                    boolean success = ImageIO.write(bufferedImage, extension, file);
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error writing file: " + e, ButtonType.OK);
                    Optional<ButtonType> result = alert.showAndWait();
                }
            }
        }
    }

    @FXML
    public void randomSeed(ActionEvent event) {
        seedField.setText(rnd.nextDouble() + "");
    }

    @FXML
    public void findWaterWorld(ActionEvent event) {
        String hydroSelection = hydroPercentChoiceBox.getValue();
        int hydroValue = hydroSelection.charAt(0) - Character.valueOf('0');
        PlanetModel model = PlanetModel.getDefaultModel();
        model.findWaterWorld(hydroValue);
        seedField.setText(model.getSeed() + "");
        render(event);
    }

    @FXML
    void onFileNew(ActionEvent event) {
        Stage newStage = new Stage();
        PlanetFormController controller = PlanetFormController.getControllerWithStageModel(newStage, PlanetModel.getDefaultModel());
        newStage.show();
    }

    @FXML
    void onFileOpen(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open planet");
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Planet JSON Files", "*.json");
        chooser.getExtensionFilters().add(filter);
        File file = chooser.showOpenDialog(null);
        if (file != null) {
            Stage newStage = new Stage();
            PlanetFormController controller = PlanetFormController.getControllerWithStageFile(newStage, file);
            newStage.show();
        }
    }

    @FXML
    void onFileClose(ActionEvent event) {
        if (dirtyMgr.isDirty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "File has changed. Save file?", ButtonType.NO, ButtonType.YES);
            alert.setHeaderText("Save changes?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.equals(ButtonType.YES)) {
                onFileSave(null);
            }
        }
        stage.close();
    }

    @FXML
    void onFileRevert(ActionEvent event) {
        PlanetModel newModel = readPlanetFile(planetFileProperty.get());
        setPlanetModel(newModel);
        dirtyMgr.reset();
    }

    @FXML
    void onFileSave(ActionEvent event) {
        if (planetFileProperty.get() == null) {
            onFileSaveAs(event);
        } else {
            saveFile();
        }
    }

    @FXML
    void onFileSaveAs(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save planet");
        FileChooser.ExtensionFilter jsonFilter = new FileChooser.ExtensionFilter("JSON Format", "*.json");
        chooser.getExtensionFilters().addAll(jsonFilter);
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            planetFileProperty.set(file);
            saveFile();
        }
    }

    @FXML
    void onOpenPlanetFinder(ActionEvent event) {
        Stage planetFinderStage = $.stageManager.getPlanetFinderStage();
        if (planetFinderStage == null) {
            planetFinderStage = new Stage();
            PlanetFinderController controller = PlanetFinderController.getControllerWithStage(planetFinderStage);
        }
        planetFinderStage.show();
        planetFinderStage.toFront();
    }

    @FXML
    void onPrintMap(ActionEvent event) {
        Stage newStage = new Stage();
        PrintMapController controller = PrintMapController.getControllerWithStageModel(newStage, planetModel);
        newStage.show();
    }

    @FXML
    void onShow3d(ActionEvent event) {
        Stage newStage = new Stage();
        ThreeDMapController controller = ThreeDMapController.getControllerWithStageModel(newStage, planetModel);
        newStage.show();
    }

    @FXML
    void onColorSchemeEditor(ActionEvent event) {
        Stage newStage = new Stage();
        ColorEditorController controller = ColorEditorController.getControllerWithStage(newStage);
        newStage.show();
    }

    @FXML
    void onAbout(ActionEvent event) {
        Dialog d = AboutDialogController.getDialog();
        d.showAndWait();
    }

    @FXML
    void onHelp(ActionEvent event) {
        Stage newStage = new Stage();
        HTMLController controller = HTMLController.getControllerWithStageModel(newStage, planetModel);
        newStage.show();
    }

    @FXML
    void restoreDefaults(ActionEvent event) {
        if (dirtyMgr.isDirty()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Properties have changed. Reset to default?", ButtonType.CANCEL, ButtonType.YES);
            alert.setHeaderText("Reset to default?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get().equals(ButtonType.CANCEL)) {
                    return;
                }
            }
        } else {
            if (planetModel.getLockSeed()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Seed is locked. Reset to default?", ButtonType.CANCEL, ButtonType.YES);
                alert.setHeaderText("Reset to default?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get().equals(ButtonType.CANCEL)) {
                        return;
                    }
                }
            }
        }
        planetModel = PlanetModel.getDefaultModel();
        setPlanetModel(planetModel);
    }

    private static PlanetModel readPlanetFile(File file) {
        PlanetModel newPlanet = null;
        try (FileReader fi = new FileReader(file)) {
            ColorSchemeAdapter csa = new ColorSchemeAdapter();
            JsonbConfig config = new JsonbConfig()
                    .withFormatting(true)
                    .withAdapters(csa);
            var jsonb = JsonbBuilder.create(config);

            newPlanet = jsonb.fromJson(fi, PlanetModel.class);
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unknown error reading file " + file + ": " + ex.getMessage(), ButtonType.CLOSE);
            alert.showAndWait();
        }

        return newPlanet;
    }

    private void saveFile() {
        try (FileWriter fo = new FileWriter(planetFileProperty.get())) {
            ColorSchemeAdapter csa = new ColorSchemeAdapter();
            JsonbConfig config = new JsonbConfig()
                    .withFormatting(true)
                    .withAdapters(csa);
            var jsonb = JsonbBuilder.create(config);
            jsonb.toJson(planetModel, fo);
            dirtyMgr.reset();
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Unknown error saving file to file " + planetFileProperty.get() + ": " + ex.getMessage(), ButtonType.CLOSE);
            alert.showAndWait();
        }
    }

    public static PlanetFormController getControllerWithStageFile(Stage stage, File file) {
        PlanetModel model = readPlanetFile(file);
        PlanetFormController controller = getControllerWithStageModel(stage, model);
        controller.setPlanetFile(file);

        return controller;
    }

    public static PlanetFormController getControllerWithStageModel(Stage stage, PlanetModel model) {
        PlanetFormController controller = getController();
        Scene newScene = new Scene(controller.getView(), 800, 600);
        controller.setPlanetModel(model);
        controller.setStage(stage);
        Rectangle2D stageRect = $.stageManager.getNewStageRect();
        stage.setX(stageRect.getMinX());
        stage.setY(stageRect.getMinY());
        stage.setWidth(stageRect.getWidth());
        stage.setHeight(stageRect.getHeight());
        stage.focusedProperty().addListener((ov, orig, current) -> {
            $.stageManager.changingFocus(stage, current);
        });
        // bit of a hack to trigger file change listener to set the title on the stage
        controller.setPlanetFile(new File("temp.tmp"));
        controller.setPlanetFile(null);
        stage.setScene(newScene);

        return controller;
    }

    public static PlanetFormController getController() {
        PlanetFormController controller = null;
        FXMLLoader loader = new FXMLLoader($.resources.getResource("PlanetForm.fxml"));
        try {
            Parent parent = loader.load();
            parent.getStylesheets().add($.resources.getRootCSSURLString());
            controller = loader.getController();
        } catch (IOException ex) {
            throw new RuntimeException("Unknown error trying to load PlanetFormController", ex);
        }

        return controller;
    }

    private void bindModel() {
        planetModel.projectionProperty().bind(projectionChoiceBox.valueProperty());
        planetModel.widthProperty().bind(widthFormatter.valueProperty());
        planetModel.heightProperty().bind(heightFormatter.valueProperty());
        planetModel.scaleProperty().bind(scaleFormatter.valueProperty());
        planetModel.seedProperty().bind(seedFormatter.valueProperty());
        planetModel.longitudeProperty().bind(longitudeFormatter.valueProperty());
        planetModel.latitudeProperty().bind(latitudeFormatter.valueProperty());
        planetModel.hgridProperty().bind(hgridFormatter.valueProperty());
        planetModel.vgridProperty().bind(vgridFormatter.valueProperty());
        planetModel.shadingProperty().bind(shadingChoiceBox.valueProperty());
        planetModel.transparentProperty().bind(transparentCheckBox.selectedProperty());
        planetModel.colorschemeProperty().bind(colorSchemeChoiceBox.valueProperty());
        planetModel.contourLinesProperty().bind(contourLinesFormatter.valueProperty());
        planetModel.coastalContourLinesProperty().bind(coastalContourLinesFormatter.valueProperty());
        planetModel.outlineProperty().bind(outlineCheckbox.selectedProperty());
        planetModel.blackAndWhiteProperty().bind(blackAndWhiteCheckbox.selectedProperty());
        planetModel.latitudeColorProperty().bind(latitudeColorFormatter.valueProperty());
        planetModel.temperatureProperty().bind(temperatureCheckbox.selectedProperty());
        planetModel.biomeProperty().bind(biomeCheckbox.selectedProperty());
        planetModel.baseProperty().bind(baseFormatter.valueProperty());
        planetModel.diameterProperty().bind(diameterFormatter.valueProperty());
        planetModel.nameProperty().bind(nameField.textProperty());
        planetModel.locationProperty().bind(locationField.textProperty());
        planetModel.profileProperty().bind(profileField.textProperty());
        planetModel.lockSeedProperty().bind(lockSeedCheckBox.selectedProperty());
        waterPercentageField.textProperty().bindBidirectional(planetModel.waterPercentageProperty(), new NumberStringConverter());
        planetModel.shadeLongitudeProperty().bind(shadeLongitudeFormatter.valueProperty());
        planetModel.shadeLatitudeProperty().bind(shadeLatitudeFormatter.valueProperty());
    }

    private Dialog<String> getAboutDialog() {
        return null;
    }

    private static class ColorSchemeAdapter implements JsonbAdapter<ColorSchemeModel, JsonObject> {

        public ColorSchemeAdapter() {
        }

        @Override
        public JsonObject adaptToJson(ColorSchemeModel obj) throws Exception {
            return Json.createObjectBuilder()
                    .add("colorSchemeName", obj.getName())
                    .build();
        }

        @Override
        public ColorSchemeModel adaptFromJson(JsonObject obj) throws Exception {
            String name = obj.getString("colorSchemeName");
            ColorSchemeModel csm = $.colorSchemeManager.getByName(name);
            if (csm == null) {
                csm = $.colorSchemeManager.getDefault();
            }

            return csm;
        }
    }

}
