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

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;

public class DirtyManager {

    List<ObservableValue> values = new ArrayList<>();
    BooleanProperty dirtyProperty = new SimpleBooleanProperty();
    IntegerProperty changeCountProperty = new SimpleIntegerProperty();
    ChangeListener<Object> listener = new ChangeListener<Object>() {
        @Override
        public void changed(ObservableValue<? extends Object> ov, Object t, Object t1) {
            dirtyProperty.set(true);
            changeCountProperty.set(changeCountProperty.get() + 1);
        }
    };

    public void clear() {
        for(ObservableValue ov : values) {
            ov.removeListener(listener);
        }
        values.clear();
        reset();
    }
    
    public void add(Control control) {
        ObservableValue ov = getObservableValue(control);

        if (ov != null) {
            add(ov);
        }
    }

    public void addAll(Control... controls) {
        for (Control c : controls) {
            add(c);
        }
    }
    
    public void add(ObservableValue v) {
        v.addListener(listener);
        values.add(v);
    }
    
    public void addAll(ObservableValue... values) {
        for(ObservableValue ov : values) {
            add(ov);
        }
    }

    public void remove(Control control) {
        ObservableValue ov = getObservableValue(control);
        if (ov != null) {
            ov.removeListener(listener);
        }
    }
    
    public void remove(ObservableValue ov) {
        ov.removeListener(listener);
        values.remove(ov);
    }
    
    public void set() {
        dirtyProperty.set(true);
    }

    public void reset() {
        dirtyProperty.set(false);
    }

    public boolean isDirty() {
        return dirtyProperty.get();
    }

    public ReadOnlyBooleanProperty dirtyProperty() {
        return dirtyProperty;
    }

    public ReadOnlyIntegerProperty changeCountProperty() {
        return changeCountProperty;
    }
    
    private ObservableValue getObservableValue(Control control) {
        ObservableValue ov = null;
        if (control instanceof TextField) {
            ov = ((TextField) control).textProperty();
        } else if (control instanceof ChoiceBox) {
            ov = ((ChoiceBox) control).valueProperty();
        } else if (control instanceof CheckBox) {
            ov = ((CheckBox) control).selectedProperty();
        } else if (control instanceof ColorPicker) {
            ov = ((ColorPicker) control).valueProperty();
        } else {
            throw new IllegalArgumentException("Can not get ObservableValue from " + control);
        }

        return ov;
    }

}
