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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class AltitudeColorModel implements Comparable<AltitudeColorModel> {

    IntegerProperty valueProperty = new SimpleIntegerProperty();
    ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>();

    public AltitudeColorModel() {
    }   

    public AltitudeColorModel(int value, Color color) {
        this.valueProperty.set(value);
        this.colorProperty.set(color);
    }
    
    public AltitudeColorModel(AltitudeColorModel model) {
        this.valueProperty.set(model.getValue());
        this.colorProperty.set(model.getColor());
    }

    public int getValue() {
        return valueProperty.get();
    }

    public void setValue(int value) {
        valueProperty.set(value);
    }
    
    public IntegerProperty valueProperty() {
        return valueProperty;
    }

    public Color getColor() {
        return colorProperty.get();
    }

    public void setColor(Color color) {
        colorProperty.set(color);
    }
    
    public ObjectProperty<Color> colorProperty() {
        return colorProperty;
    }

    @Override
    public String toString() {
        return "AltitudeColorModel{" + "valueProperty=" + valueProperty + ", colorProperty=" + colorProperty + '}';
    }

    @Override
    public int compareTo(AltitudeColorModel o) {
        return valueProperty.get() - o.valueProperty.get();
    }
}
