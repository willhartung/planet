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
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

public class ColorEditorController implements Initializable {

    Stage stage;

    @FXML
    Parent view;

    @FXML
    private Button addColorSchemeButton;
    @FXML
    private Button addColorSchemeValueButton;
    @FXML
    private ColorPicker backgroundColorPicker;
    @FXML
    private Button cancelColorSchemeButton;
    @FXML
    private ListView<ColorSchemeModel> colorSchemeListView;
    @FXML
    private TableView<AltitudeColorModel> colorSchemeTableView;
    @FXML
    private Button deleteColorSchemeButton;
    @FXML
    private Button deleteColorSchemeValueButton;
    @FXML
    private ColorPicker latLongColorPicker;
    @FXML
    private ColorPicker outline1ColorPicker;
    @FXML
    private ColorPicker outline2ColorPicker;
    @FXML
    private Button renameColorSchemeButton;
    @FXML
    private Button duplicateColorSchemeButton;
    @FXML
    private Button saveColorSchemeButton;
    @FXML
    private Label seaLevelValueLabel;

    ColorSchemeModel model;
    ColorSchemeModel currentSelection;
    boolean colorSchemeSelectionChanging = false;
    DirtyManager dirtyManager = new DirtyManager();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cancelColorSchemeButton.disableProperty().bind(dirtyManager.dirtyProperty().not());
        saveColorSchemeButton.disableProperty().bind(dirtyManager.dirtyProperty().not());
        addColorSchemeButton.disableProperty().bind(dirtyManager.dirtyProperty());
        deleteColorSchemeValueButton.disableProperty().bind(colorSchemeTableView.selectionModelProperty().get().selectedIndexProperty().isEqualTo(-1));
        deleteColorSchemeButton.disableProperty().bind(colorSchemeListView.selectionModelProperty().get()
                .selectedIndexProperty()
                .isEqualTo(-1)
                .or(dirtyManager.dirtyProperty()));
        renameColorSchemeButton.disableProperty().bind(colorSchemeListView.selectionModelProperty().get()
                .selectedIndexProperty()
                .isEqualTo(-1)
                .or(dirtyManager.dirtyProperty()));
        duplicateColorSchemeButton.disableProperty().bind(colorSchemeListView.selectionModelProperty().get()
                .selectedIndexProperty()
                .isEqualTo(-1)
                .or(dirtyManager.dirtyProperty()));

        colorSchemeListView.setCellFactory(list -> new ColorSchemeListCell());
        colorSchemeListView.setItems($.colorSchemeManager.colorSchemes());
        MultipleSelectionModel<ColorSchemeModel> selectionModel = colorSchemeListView.getSelectionModel();
        colorSchemeListView.selectionModelProperty().get().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (!colorSchemeSelectionChanging) {
                // capture that we're in "change" mode
                colorSchemeSelectionChanging = true;
                if (dirtyManager.isDirty()) {
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Color has been edited, if you continue, you will lose your changes.", ButtonType.CANCEL, ButtonType.OK);
                    a.setHeaderText("Lose your changes?");
                    Optional<ButtonType> bt = a.showAndWait();
                    if (bt.isEmpty() || (bt.isPresent() && bt.get().equals(ButtonType.CANCEL))) {
                        // if the current model is dirty, and the choose to not change
                        // we set the "colorSchemeSelectionChanging" flag because
                        // when we update the selection to the old value, this
                        // will be called again, and in that case we don't
                        // want to do anything.
                        selectionModel.select(oldValue);
                        // reset newValue to prevent the below code from firing.
                        newValue = null;
                    }
                }
                if (newValue != null) {
                    setModel(newValue);
                }
            }
            colorSchemeSelectionChanging = false;

        });
        colorSchemeTableView.getColumns().clear();
        colorSchemeTableView.setEditable(true);
        // Create TableColumn for valueProperty
        TableColumn<AltitudeColorModel, Integer> valueColumn = new TableColumn<>("Value");
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().valueProperty().asObject());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter() {
            @Override
            public Integer fromString(String value) {
                // Only allow parsing integers
                try {
                    return super.fromString(value);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        }));
        valueColumn.setOnEditCommit(event -> {
            AltitudeColorModel colorModel = event.getRowValue();
            if (event.getNewValue() != null) {
                colorModel.setValue(event.getNewValue());
                sortAndSelect(event.getNewValue());
            }
        });

        // Create TableColumn for colorProperty
        TableColumn<AltitudeColorModel, Color> colorColumn = new TableColumn<>("Color");
        colorColumn.setCellValueFactory(cellData -> cellData.getValue().colorProperty());
        colorColumn.setCellFactory(column -> {
            return new TableCell<AltitudeColorModel, Color>() {
                private Region colorRectangle;
                private ColorPicker colorPicker;

                {
                    colorRectangle = new Region();
                    colorRectangle.setMinWidth(30);
                    colorRectangle.setMinHeight(15);
                    colorRectangle.setMaxWidth(Double.MAX_VALUE);
                    colorRectangle.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

                    colorPicker = new ColorPicker();
                    colorPicker.setOnAction(event -> {
                        commitEdit(colorPicker.getValue());
                    });

                    setGraphic(colorRectangle);
                    setOnMouseClicked(event -> {
                        if (event.getClickCount() == 2) {
                            startEdit();
                            colorPicker.setValue(getItem());
                            setGraphic(colorPicker);
                        }
                    });
                }

                @Override
                protected void updateItem(Color item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        colorRectangle.setBackground(new Background(new BackgroundFill(item, null, null)));
                        setGraphic(colorRectangle);
                    }
                }

                @Override
                public void cancelEdit() {
                    super.cancelEdit();
                    setGraphic(colorRectangle);
                }
            };
        });
        colorColumn.setOnEditCommit(event -> {
            AltitudeColorModel model = event.getRowValue();
            model.setColor(event.getNewValue());
        });
        colorSchemeTableView.getColumns().addAll(valueColumn, colorColumn);
        dirtyManager.changeCountProperty().addListener((observable) -> {
            if (currentSelection != null) {
                Collections.sort(currentSelection.altitudeColors());
                currentSelection.generateColors();
            }
        });
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        stage.setOnCloseRequest((t) -> {
            $.stageManager.setColorEditorStage(null);
        });

        stage.setOnCloseRequest((t) -> {
            if (dirtyManager.isDirty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Color Scheme has changed. Save changes?", ButtonType.CANCEL, ButtonType.NO, ButtonType.YES);
                alert.setHeaderText("Save changes?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get().equals(ButtonType.YES)) {
                        saveColorScheme(null);
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

    public void setModel(ColorSchemeModel colorModel) {
        model = new ColorSchemeModel(colorModel);
        backgroundColorPicker.valueProperty().bindBidirectional(model.backgroundColorProperty());
        latLongColorPicker.valueProperty().bindBidirectional(model.latLongColorProperty());
        outline1ColorPicker.valueProperty().bindBidirectional(model.outline1ColorProperty());
        outline2ColorPicker.valueProperty().bindBidirectional(model.outline2ColorProperty());
        seaLevelValueLabel.textProperty().bind(model.seaProperty().asString());
        colorSchemeTableView.setItems(model.altitudeColors());
        dirtyManager.clear();
        dirtyManager.addAll(backgroundColorPicker, latLongColorPicker, outline1ColorPicker, outline2ColorPicker);
        for (AltitudeColorModel acm : model.altitudeColors()) {
            dirtyManager.addAll(acm.valueProperty(), acm.colorProperty());
        }
        model.generateColors();
        currentSelection = model;
    }

    @FXML
    void addColorScheme(ActionEvent event) {
        Dialog<String> dialog = getNamingDialog("Add new Color Scheme", false, null);
        Optional<String> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            ColorSchemeModel csm = $.colorSchemeManager.createColorScheme(opt.get());
            colorSchemeListView.selectionModelProperty().get().select(csm);
            colorSchemeListView.scrollTo(csm);
        }
    }

    @FXML
    void deleteColorScheme(ActionEvent event) {
        ColorSchemeModel csm = colorSchemeListView.selectionModelProperty().get().selectedItemProperty().get();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete color scheme " + csm.getName() + "?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Delete color scheme?");

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            $.colorSchemeManager.delete(csm);
        }
    }

    @FXML
    void renameColorScheme(ActionEvent event) {
        ColorSchemeModel csm = colorSchemeListView.selectionModelProperty().get().selectedItemProperty().get();
        String oldName = csm.getName();
        Dialog<String> dialog = getNamingDialog("Rename Color Scheme", true, oldName);
        Optional<String> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            csm = $.colorSchemeManager.renameColorScheme(oldName, opt.get());
            colorSchemeListView.selectionModelProperty().get().select(csm);
            colorSchemeListView.scrollTo(csm);
        }
    }

    @FXML
    void duplicateColorScheme(ActionEvent event) {
        ColorSchemeModel csm = colorSchemeListView.selectionModelProperty().get().selectedItemProperty().get();
        String oldName = csm.getName();
        Dialog<String> dialog = getNamingDialog("Duplicate Color Scheme", true, oldName);
        Optional<String> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            csm = new ColorSchemeModel(csm);
            csm.setName(opt.get());
            $.colorSchemeManager.add(csm);
        }
    }

    @FXML
    void addColorSchemeValue(ActionEvent event) {
        Dialog<AltitudeColorModel> dialog = getNewColorDialog();
        Optional<AltitudeColorModel> opt = dialog.showAndWait();
        if (opt.isPresent()) {
            AltitudeColorModel acm = opt.get();
            dirtyManager.addAll(acm.valueProperty(), acm.colorProperty());
            // tickle the color for the sake of the dirtymanager
            int value = acm.getValue();
            acm.setValue(value + 1);
            acm.setValue(value);
            model.addAlitudeColor(opt.get());
            sortAndSelect(acm.getValue());
        }
    }

    @FXML
    void deleteColorSchemeValue(ActionEvent event) {
        AltitudeColorModel acm = colorSchemeTableView.selectionModelProperty().get().getSelectedItem();
        model.altitudeColors.remove(acm);
        dirtyManager.set();
    }

    @FXML
    void cancelColorScheme(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Revert changes to color scheme?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Revert changes?");

        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            // the model in the list view has not changed, so we'll just reset
            // to that.
            ColorSchemeModel csm = colorSchemeListView.getSelectionModel().getSelectedItem();
            setModel(csm);
//            ColorSchemeModel csm = $.colorSchemeManager.getByName(model.getName());
//            // Selecting it again in the list resets the model
//            colorSchemeListView.selectionModelProperty().get().clearSelection();
//            colorSchemeListView.selectionModelProperty().get().select(csm);
        }
    }

    @FXML
    void saveColorScheme(ActionEvent event) {
        dirtyManager.reset();
        colorSchemeSelectionChanging = true;
        $.colorSchemeManager.update(model);
        colorSchemeSelectionChanging = false;
    }

    public static ColorEditorController getControllerWithStage(Stage stage) {
        ColorEditorController controller = getController();
        Scene newScene = new Scene(controller.getView());
        controller.setStage(stage);
        stage.setTitle("Color Scheme Editor");
        Rectangle2D stageRect = $.stageManager.getNewStageRect();
        stage.setX(stageRect.getMinX());
        stage.setY(stageRect.getMinY());
        stage.setWidth(stageRect.getWidth());
        stage.setHeight(stageRect.getHeight());
        stage.focusedProperty().addListener((ov, orig, current) -> {
            $.stageManager.changingFocus(stage, current);
        });

        stage.setScene(newScene);
        stage.sizeToScene();

        return controller;
    }

    public static ColorEditorController getController() {
        ColorEditorController controller = null;
        FXMLLoader loader = new FXMLLoader($.resources.getResource("ColorEditor.fxml"));
        try {
            Parent parent = loader.load();
            parent.getStylesheets().add($.resources.getRootCSSURLString());
            controller = loader.getController();
        } catch (IOException ex) {
            throw new RuntimeException("Unknown error trying to load ColorEditorController", ex);
        }

        return controller;
    }

    private void sortAndSelect(Integer newValue) {
        List<AltitudeColorModel> colors = model.getAltitudeColors();
        Collections.sort(model.altitudeColors());
        colorSchemeTableView.setItems(model.altitudeColors());
        Collections.sort(colorSchemeTableView.getItems());
        for (int i = 0; i < colors.size(); i++) {
            if (colors.get(i).getValue() == newValue) {
                colorSchemeTableView.scrollTo(colors.get(i));
                break;
            }
        }
    }

    private Dialog<String> getNamingDialog(String title, boolean isRename, String oldName) {
        GridPane gp = new GridPane();
        Label oldNameLabel = new Label("Original Name:");
        TextField oldNameTextField = new TextField(oldName);
        oldNameTextField.disableProperty().set(true);
        Label nameLabel = new Label("Name:");
        Label errorLabel = new Label("Name already taken.");
        BooleanProperty validProperty = new SimpleBooleanProperty(true);
        errorLabel.setTextFill(Color.RED);
        errorLabel.visibleProperty().bind(validProperty.not());
        TextField nameTextField = new TextField();
        nameTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if ($.colorSchemeManager.nameExists(newValue)) {
                validProperty.set(false);
            } else {
                validProperty.set(true);
            }
        });
        int row = 0;
        if (isRename) {
            gp.add(oldNameLabel, 0, row);
            gp.add(oldNameTextField, 1, row++);
        }
        gp.add(nameLabel, 0, row);
        gp.add(nameTextField, 1, row++);
        gp.add(errorLabel, 1, row);

        gp.setHgap(10);
        gp.setVgap(10);
        Dialog<String> dialog = new Dialog<>();
        DialogPane dp = new DialogPane();
        dp.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        Button okBtn = (Button) dp.lookupButton(ButtonType.OK);
        okBtn.disableProperty().bind(validProperty.not().or(nameTextField.textProperty().isEmpty()));
        dp.setContent(gp);
        Platform.runLater(() -> {
            nameTextField.requestFocus();
        });
        dialog.setTitle(title);
        dialog.setDialogPane(dp);
        dialog.setResultConverter((param) -> {
            if (param.equals(ButtonType.OK)) {
                return nameTextField.getText();
            }
            return null;
        });
        return dialog;
    }

    private Dialog<AltitudeColorModel> getNewColorDialog() {
        GridPane gp = new GridPane();
        Label valueLabel = new Label("Name:");
        TextField valueTextField = new TextField("0");
        IntegerTextFormatter formatter = new IntegerTextFormatter();
        formatter.setValue(0);
        valueTextField.setTextFormatter(formatter);
        Label colorLabel = new Label("Color:");
        ColorPicker colorPicker = new ColorPicker(Color.WHITE);
        int row = 0;
        gp.addRow(row++, valueLabel, valueTextField);
        gp.addRow(row++, colorLabel, colorPicker);

        gp.setHgap(10);
        gp.setVgap(10);
        Dialog<AltitudeColorModel> dialog = new Dialog<>();
        DialogPane dp = new DialogPane();
        dp.getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
        dp.setContent(gp);
        Platform.runLater(() -> {
            valueTextField.requestFocus();
        });
        dialog.setTitle("Add new Color");
        dialog.setDialogPane(dp);
        dialog.setResultConverter((param) -> {
            if (param.equals(ButtonType.OK)) {
                AltitudeColorModel acm = new AltitudeColorModel((int) formatter.getValue(), colorPicker.getValue());
                return acm;
            }
            return null;
        });
        return dialog;
    }

    private static class ColorSchemeListCell extends ListCell<ColorSchemeModel> {

        public ColorSchemeListCell() {
        }

        @Override
        protected void updateItem(ColorSchemeModel item, boolean empty) {
            super.updateItem(item, empty);
            if (item == null || empty) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getName());
            }
        }
    }
}
