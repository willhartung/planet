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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

public class PrintMapController implements Initializable {

    Stage stage;

    Pane printView;

    @FXML
    Parent view;
    @FXML
    private Button printButton;
    @FXML
    private ScrollPane scrollPane;

    PlanetModel planetModel;

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
        return view;
    }

    public void setView(Parent view) {
        this.view = view;
    }

    public void setPlanetModel(PlanetModel planetModel) {
        this.planetModel = planetModel;
        updateView();
    }

    @FXML
    public void print(ActionEvent event) {
        PrinterJob job = PrinterJob.createPrinterJob();

        if (job != null && job.showPrintDialog(null)) {
            Printer printer = job.getPrinter();
            PageLayout pageLayout = printer.createPageLayout(Paper.NA_LETTER, PageOrientation.LANDSCAPE, Printer.MarginType.DEFAULT);

            Pane pane = getView7(pageLayout.getPrintableWidth(), pageLayout.getPrintableHeight(), new PlanetModel(planetModel));
            pane.getStylesheets().add($.resources.getRootCSSURLString());
//            ScrollPane sp = new ScrollPane();
//            sp.setContent(pane);
            Scene s = new Scene(pane);
            pane.applyCss();
            pane.layout();

            boolean success = job.printPage(pageLayout, pane);
            if (success) {
                job.endJob();
            }
        }

    }

    @FXML
    public void export(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        FileChooser.ExtensionFilter pngFilter = new FileChooser.ExtensionFilter("PNG Format", "*.png");
        chooser.getExtensionFilters().addAll(pngFilter);
        chooser.setTitle("Export image");
        File file = chooser.showSaveDialog(null);
        if (file != null) {
            int width = (int) (10.5 * 72);
            double tl = width / 5.5;
            double th = tl * Math.sin(60 * Math.PI / 180);
            int height = (int) (th * 3);
            PlanetModel model = new PlanetModel(planetModel);

            Pane pane = getView7(width, height, model);
            pane.getStylesheets().add($.resources.getRootCSSURLString());
            Scene s = new Scene(pane);
            pane.applyCss();
            pane.layout();
            WritableImage image = pane.snapshot(new SnapshotParameters(), null);
            String extension = "png";
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                ImageIO.write(bufferedImage, extension, file);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error writing file: " + e, ButtonType.OK);
                Optional<ButtonType> result = alert.showAndWait();
            }
        }
    }

    public static PrintMapController getControllerWithStageModel(Stage stage, PlanetModel model) {
        PrintMapController controller = getController();
        Scene newScene = new Scene(controller.getView(), 800, 600);
        controller.setStage(stage);
        stage.setTitle("Print Map");
        Rectangle2D stageRect = $.stageManager.getNewStageRect();
        stage.setX(stageRect.getMinX());
        stage.setY(stageRect.getMinY());
        stage.setWidth(stageRect.getWidth());
        stage.setHeight(stageRect.getHeight());
        stage.focusedProperty().addListener((ov, orig, current) -> {
            $.stageManager.changingFocus(stage, current);
        });

        stage.setScene(newScene);
        controller.setPlanetModel(model);

        return controller;
    }

    public static PrintMapController getController() {
        PrintMapController controller = null;
        FXMLLoader loader = new FXMLLoader($.resources.getResource("PrintMap.fxml"));
        try {
            Parent parent = loader.load();
            parent.getStylesheets().add($.resources.getRootCSSURLString());
            controller = loader.getController();
        } catch (IOException ex) {
            throw new RuntimeException("Unknown error trying to load PrintMapController", ex);
        }

        return controller;
    }

    private Pane getView7(double width, double height, PlanetModel model) {
        /*
        This is fiddly in a couple of ways.
        
        First, we're relying on the rendered image. With the rendering, the 
        edges are not clean and precise. Error in the rendering makes them a 
        little fuzzy. It's also easier to render entire hexes on the edges, than 
        partial hexes.
        
        The "perfect" way to do this is set up a clipping region the shape of 
        the map outline, and then add the image and draw the hexes. But there's 
        a bug in FX that when you have an an non-rectangular clip region, it 
        drops the resolution of everything down to 72 DPI, which looks horrible.
        
        However, with the FX Shapes, you can "subtract" one shape from another. 
        That is, we can put a hole into another shape. So, we make a large 
        rectangle, put a map shaped hole in it, and layer that down on top of 
        the image. 
        
        This effectively clips the rendering done below. Then we draw borders on
        top to clean things up.
         */

        width = width - 10;
        double tl = width / 5.5;
        double th = tl * Math.sin(60 * Math.PI / 180);
        height = th * 3;

        model.setProjection(Projection.ICOSAHEDRAL);
        model.setWidth((int) width);
        model.setHeight((int) height);
        Image image = model.renderImage();
        Pane pane = new Pane();
        Line line = new Line(0, height / 2, width, height / 2);
        pane.getChildren().add(new ImageView(image));
        pane.getChildren().add(line);

        // overarching clip
        Rectangle rect = new Rectangle(0, 0, width, height);
        pane.setClip(rect);

        // external clip/overlay region
        Polygon poly = new Polygon();
        ObservableList<Double> e = poly.getPoints();
        double ox = 1;
        double oy = height / 2 - th / 2;
        e.addAll(ox, oy);
        for (int i = 0; i < 4; i++) {
            e.addAll(ox + tl / 2, oy - th);
            ox = ox + tl;
            e.addAll(ox, oy);
        }
        e.addAll(ox + tl / 2, oy - th);
        ox = ox + tl * 1.5;
        oy = oy + th;
        e.addAll(ox, oy);
        for (int i = 0; i < 4; i++) {
            e.addAll(ox - tl / 2, oy + th);
            ox = ox - tl;
            e.addAll(ox, oy);
        }
        e.addAll(ox - tl / 2, oy + th);

        // interior triangles and lines
        ox = 1;
        oy = height / 2 - th / 2;
        Polyline polyLine = new Polyline();
        e = polyLine.getPoints();
        e.addAll(ox, oy);
        for (int i = 0; i < 5; i++) {
            e.addAll(ox + tl / 2, oy + th);
            ox = ox + tl;
            e.addAll(ox, oy);
        }
        pane.getChildren().add(polyLine);
        ox = 1;
        oy = height / 2 - th / 2;
        pane.getChildren().add(new Line(ox, oy, ox + tl * 5, oy));
        ox = ox + tl / 2;
        oy = oy + th;
        pane.getChildren().add(new Line(ox, oy, ox + tl * 5, oy));

        // hexes
        double hexWidth = tl / 7;
        double dw = hexWidth / 2;
        double offset = hexOffset(hexWidth);
        ox = 1 - dw;
        oy = height / 2 - th * 1.5;
        for (int row = 0; row < 22; row++) {
            for (int col = 0; col < 40; col++) {
                pane.getChildren().add(hex(ox + col * hexWidth, oy, hexWidth));
            }
            ox = ox + (row % 2 == 0 ? -dw : dw);
            oy = oy + offset;
        }
        pane.setPrefSize(width, height);

        // clipping overlay
        Pane overlay = new Pane();
        rect = new Rectangle(0, 0, width, height);
        poly.setStroke(Color.BLACK);
        poly.setFill(Color.WHITE);
        // make the hole        
        Shape cutout = Shape.subtract(rect, poly);
        cutout.setFill(Color.WHITE);
        cutout.setStroke(null);
        // map borders
        poly.setFill(null);
        rect.setFill(null);
        rect.setStroke(Color.BLACK);
        overlay.getChildren().addAll(cutout, poly);
        overlay.setPrefSize(width, height);
        StackPane stack = new StackPane(pane, overlay);
        stack.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                new BorderWidths(2))));

        // Labeling
        GridPane grid = new GridPane();
        grid.setPrefWidth(width);
        grid.paddingProperty().set(new Insets(0, 5, 0, 5));
        Label nameLabel = new Label("Name: ");
        nameLabel.getStyleClass().add("printLabel");
        Label nameValue = new Label(model.getName());
        nameValue.getStyleClass().add("printValue");

        Label locationLabel = new Label("Location: ");
        locationLabel.getStyleClass().add("printLabel");
        Label locationValue = new Label(model.getLocation());
        locationValue.getStyleClass().add("printValue");

        Label profileLabel = new Label("Profile: ");
        profileLabel.getStyleClass().add("printLabel");
        Label profileValue = new Label(model.getProfile());
        profileValue.getStyleClass().add("printValue");

        Label diameterLabel = new Label("Diameter: ");
        diameterLabel.getStyleClass().add("printLabel");
        Label diameterValue = new Label(model.getDiameter() + " (" + (model.getDiameter() / 35) + "km/hex)");
        diameterValue.getStyleClass().add("printValue");

        grid.add(new VBox(nameLabel, nameValue), 0, 0);
        grid.add(new VBox(locationLabel, locationValue), 1, 0);
        grid.add(new VBox(profileLabel, profileValue), 2, 0);
        grid.add(new VBox(diameterLabel, diameterValue), 3, 0);
        for (int i = 0; i < 4; i++) {
            ColumnConstraints c = new ColumnConstraints();
            c.setPercentWidth(25);
            grid.getColumnConstraints().add(c);
        }

        Pane topPane = new Pane(grid);
        topPane.setBackground(Background.fill(Color.WHITE));
        topPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY,
                new BorderWidths(2, 2, 0, 2))));

        BorderPane bp = new BorderPane();
        bp.setCenter(stack);
        bp.setTop(topPane);
        AnchorPane ap = new AnchorPane(bp);
        return ap;
    }

    private void updateView() {
        int width = (int) (10.5 * 72);
        double tl = width / 5.5;
        double th = tl * Math.sin(60 * Math.PI / 180);
        int height = (int) (th * 3);
        PlanetModel model = new PlanetModel(planetModel);

        Pane pane = getView7(width, height, model);
        Image image = getImage(width, height, model);
        pane.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Dragboard db = pane.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putImage(image);
                db.setContent(content);

                event.consume();
            }
        });

        scrollPane.setContent(pane);
    }

    private Polygon getPoly(double height, int tl, int th) {
        // external clip/overlay region
        Polygon poly = new Polygon();
        ObservableList<Double> e = poly.getPoints();
        double ox = 0;
        double oy = height / 2 - th / 2;
        e.addAll(ox, oy);
        for (int i = 0; i < 4; i++) {
            e.addAll(ox + tl / 2, oy - th);
            ox = ox + tl;
            e.addAll(ox, oy);
        }
        e.addAll(ox + tl / 2, oy - th);
        ox = ox + tl * 1.5;
        oy = oy + th;
        e.addAll(ox, oy);
        for (int i = 0; i < 4; i++) {
            e.addAll(ox - tl / 2, oy + th);
            ox = ox - tl;
            e.addAll(ox, oy);
        }
        e.addAll(ox - tl / 2, oy + th);
        poly.setStroke(Color.BLACK);
        poly.setFill(null);
        return poly;
    }

    private Polygon hex(double cx, double cy, double width) {
        double deg30 = 30 * Math.PI / 180;
        double length = width / 2 / Math.cos(deg30);
        double sinLength = length * Math.sin(deg30);
        Polygon poly = new Polygon();
        ObservableList<Double> pts = poly.getPoints();
        double ox = cx - width / 2;
        double oy = cy + length / 2;
        pts.addAll(ox, oy);
        oy = oy - length;
        pts.addAll(ox, oy);
        pts.addAll(ox + width / 2, oy - sinLength);
        pts.addAll(ox + width, oy);
        oy = oy + length;
        pts.addAll(ox + width, oy);
        pts.addAll(ox + width / 2, oy + sinLength);
        pts.addAll(ox, oy);
        poly.setStroke(Color.BLACK);
        poly.setStrokeWidth(.2);
        poly.setFill(null);

        return poly;
    }

    private double hexOffset(double width) {
        double deg30 = 30 * Math.PI / 180;
        double length = width / 2 / Math.cos(deg30);
        double sinLength = length * Math.sin(deg30);

        return length + sinLength;
    }

    private void drawPoly(GraphicsContext gc, Polygon poly) {
        List<Double> pts = poly.getPoints();
        gc.moveTo(pts.get(0), pts.get(1));
        for (int i = 2; i < pts.size(); i += 2) {
            gc.lineTo(pts.get(i), pts.get(i + 1));
        }
    }

    public Image copyBits(Image src, Rectangle2D bounds) {
        int ox = (int) bounds.getMinX();
        int oy = (int) bounds.getMinY();
        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();
        WritableImage img = new WritableImage(width, height);
        PixelReader pr = src.getPixelReader();
        PixelWriter pw = img.getPixelWriter();
        pw.setPixels(0, 0, width, height, pr, ox, oy);

        return img;
    }

    public Rectangle2D getBounds(Image img) {
        Rectangle2D result;
        int minX;
        int maxX;
        int minY;
        int maxY;
        PixelReader pr = img.getPixelReader();
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();

        minX = 0;
        boolean scanning = true;
        while (scanning && minX < width) {
            for (int y = 0; y < height; y++) {
                int pixel = pr.getArgb(minX, y);
                if (pixel != -1) {
                    scanning = false;
                    break;
                }
            }
            if (scanning) {
                minX = minX + 1;
            }
        }
        maxX = width - 1;
        scanning = true;
        while (scanning && maxX >= 0) {
            for (int y = 0; y < height; y++) {
                int pixel = pr.getArgb(maxX, y);
                if (pixel != -1) {
                    scanning = false;
                    break;
                }
            }
            if (scanning) {
                maxX = maxX - 1;
            }
        }
        minY = 0;
        scanning = true;
        while (scanning && minY < height) {
            for (int x = 0; x < width; x++) {
                int pixel = pr.getArgb(x, minY);
                if (pixel != -1) {
                    scanning = false;
                    break;
                }
            }
            if (scanning) {
                minY = minY + 1;
            }
        }
        maxY = height - 1;
        scanning = true;
        while (scanning && maxY >= 0) {
            for (int x = 0; x < width; x++) {
                int pixel = pr.getArgb(x, maxY);
                if (pixel != -1) {
                    scanning = false;
                    break;
                }
            }
            if (scanning) {
                maxY = maxY - 1;
            }
        }

        return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
    }

    private Image getImage(int width, int height, PlanetModel model) {
        Pane pane = getView7(width, height, model);
        pane.getStylesheets().add($.resources.getRootCSSURLString());
        Scene s = new Scene(pane);
        pane.applyCss();
        pane.layout();
        WritableImage image = pane.snapshot(new SnapshotParameters(), null);
        return image;
    }
}
