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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class StageManager {
    private static double DEFAULT_WIDTH = 800;
    private static double DEFAULT_HEIGHT = 600;

    ObjectProperty<Stage> focusedStageProperty = new SimpleObjectProperty<>();
    ObjectProperty<Stage> planetFinderStageProperty = new SimpleObjectProperty<>();
    ObjectProperty<Stage> colorEditorStageProperty = new SimpleObjectProperty<>();

    public StageManager() {
    }

    
    public void changingFocus(Stage stage, boolean focus) {
        if (stage == focusedStageProperty.get() && !focus) {
            focusedStageProperty.set(null);
        } else if (focus) {
            focusedStageProperty.set(stage);
        }
    }
    
    public Stage getFocusedStage() {
        return focusedStageProperty.get();
    }    
    
    public ObjectProperty<Stage> focusedStageProperty() {
        return focusedStageProperty;
    }
    
    public Stage getPlanetFinderStage() {
        return planetFinderStageProperty.get();
    }
    
    public void setPlanetFinderStage(Stage stage) {
        planetFinderStageProperty.set(stage);
    }
    
    public ObjectProperty<Stage> planetFinderStageProperty() {
        return planetFinderStageProperty;
    }
    
    public Stage getColorEditorState() {
        return colorEditorStageProperty.get();
    }
    
    public void setColorEditorStage(Stage stage) {
        colorEditorStageProperty.set(stage);
    }
    
    public ObjectProperty<Stage> colorEditorStageProperty() {
        return colorEditorStageProperty;
    }
    
    public Rectangle2D getNewStageRect() {        
        Rectangle2D result = null;
        Rectangle2D screenRect = Screen.getPrimary().getBounds();
            double cx = screenRect.getWidth() / 2;
            double cy = screenRect.getHeight() / 2;
            double sx = cx - DEFAULT_WIDTH / 2;
            double sy = cy - DEFAULT_HEIGHT / 2;
            Stage stage = focusedStageProperty.get();
            
        if (stage == null) {
             cx = screenRect.getWidth() / 2;
             cy = screenRect.getHeight() / 2;
             sx = cx - DEFAULT_WIDTH / 2;
             sy = cy - DEFAULT_HEIGHT / 2;
        } else {
            sx = stage.getX() + 20;
            sy = stage.getY() + 20;
        }
        result = new Rectangle2D(sx, sy, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        return fitRectangle(result, screenRect);

    }

    private Rectangle2D fitRectangle(Rectangle2D rect, Rectangle2D screenRect) {
        if (screenRect.contains(rect)) {
            return rect;
        }

        double sx = rect.getMinX();
        double sy = rect.getMinY();
        double width = rect.getWidth();
        double height = rect.getHeight();
        
        // Rect doesn't fit somehow. First check if it will fit at all.
        if (width > screenRect.getWidth()) {
            width = screenRect.getWidth();            
        }
        
        if (height > screenRect.getHeight()) {
            height = screenRect.getHeight();
        }
        
        // Now lets try to move the window to the top, since screens are wide today
        sy = screenRect.getMinY();
        rect = new Rectangle2D(sx, sy, width, height);
        if (screenRect.contains(rect)) {
            return rect;
        }
        
        // ok, lets move it to the left
        sx = screenRect.getMinX();
        rect = new Rectangle2D(sx, sy, width, height);
        if (screenRect.contains(rect)) {
            return rect;
        }

        // somehow that didn't work either, so we'll just return the screenRect
        
        return screenRect;
    }
}
