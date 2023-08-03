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
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public class HTMLController implements Initializable {

    Stage stage;

    WebView webView;
   
    ObservableList<WebHistory.Entry> entryList;
    IntegerProperty historySize;
    
    PlanetModel planetModel;

    Parent view;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Stage getStage() {
        return stage;
    }

    public Parent getView1() {
        return webView;
    }

    public Parent getView() {
        if (view == null) {
            this.webView = new WebView();
            WebEngine webEngine = webView.getEngine();

            webEngine.load($.resources.getResource("html/doc.html").toExternalForm());

            webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                @Override
                public void changed(ObservableValue ov, State oldState, State newState) {
                    if (newState == Worker.State.SUCCEEDED) {
                        // note next classes are from org.w3c.dom domain
                        EventListener listener = new EventListener() {
                            public void handleEvent(Event ev) {
                                String href = ((Element) ev.getTarget()).getAttribute("href");
                                URI uri = null;
                                try {
                                    uri = new URI(href);
                                } catch (URISyntaxException ex) {
                                    // ignore it
                                }
                                if (href.startsWith("http")) {
                                    try {
                                        Desktop.getDesktop().browse(uri);
                                    } catch (IOException ex) {
                                    }
                                    ev.preventDefault();
                                }
                            }
                        };

                        Document doc = webEngine.getDocument();
                        Element el = doc.getElementById("a");
                        NodeList lista = doc.getElementsByTagName("a");
                        for (int i = 0; i < lista.getLength(); i++) {
                            ((EventTarget) lista.item(i)).addEventListener("click", listener, false);
                        }
                    }
                }
            });
            Button backButton = new Button("<");
            backButton.setOnAction((event) -> {
                final WebHistory history = webEngine.getHistory();
                ObservableList<WebHistory.Entry> entryList = history.getEntries();
                int currentIndex = history.getCurrentIndex();

                Platform.runLater(()
                        -> {
                    history.go(entryList.size() > 1
                            && currentIndex > 0
                                    ? -1
                                    : 0);
                });
            });
            backButton.disableProperty().bind(webEngine.getHistory().currentIndexProperty().isEqualTo(0));
            Button forwardButton = new Button(">");
            forwardButton.setOnAction((event) -> {
                final WebHistory history = webEngine.getHistory();
                ObservableList<WebHistory.Entry> entryList = history.getEntries();
                int currentIndex = history.getCurrentIndex();

                Platform.runLater(()
                        -> {
                    history.go(entryList.size() > 1
                            && currentIndex < entryList.size() - 1
                            ? 1
                            : 0);
                });
            });
            // this convoluted mess is the only way to monitor the size of
            // an ObservableList. Stand up an int property, and have a 
            // ListChangeListener keep it up to date.
            historySize = new SimpleIntegerProperty();
            WebHistory history = webEngine.getHistory();
            entryList = history.getEntries();
            entryList.addListener((ListChangeListener<WebHistory.Entry>) change -> {
                while(change.next()) {
                    if (change.wasAdded() || change.wasRemoved()) {
                        historySize.set(entryList.size());                        
                    }
                }
            });
            forwardButton.disableProperty().bind(history.currentIndexProperty().isEqualTo(historySize.add(-1)));
            FlowPane fp = new FlowPane(backButton, forwardButton);
            VBox box = new VBox(fp, webView);
            view = box;
        }
        return view;
    }

    public static HTMLController getControllerWithStageModel(Stage stage, PlanetModel model) {
        HTMLController controller = getController(model);
        Scene newScene = new Scene(controller.getView(), 800, 600);
        controller.setStage(stage);
        stage.setTitle("Help");
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

    public static HTMLController getController(PlanetModel model) {
        HTMLController controller = new HTMLController();
//        controller.setView(new WebView());
        return controller;
    }
}
