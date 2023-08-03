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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public abstract class ValidatingTextField  extends TextField implements ChangeListener<String> {

    private BooleanProperty isValidProperty = new SimpleBooleanProperty();
    protected StringProperty errorMessageProperty = new SimpleStringProperty();

    public ValidatingTextField() {
        textProperty().addListener(this);
//        isValidProperty.set(validate("", ""));
        isValidProperty.set(true);
        isValidProperty.addListener((prop, oldValue, newValue) -> {
            if(newValue) {
                setStyle("-fx-text-fill: black");
            } else {
                setStyle("-fx-text-fill: red");
            }
        });
    }
    
    public abstract boolean validate(String oldText, String newText);

    @Override
    public void changed(ObservableValue<? extends String> ov, String t, String t1) {
        isValidProperty.setValue(validate(t, t1));
    }

    public boolean isValid() {
        return isValidProperty.get();
    }

    public void setIsValid(boolean isValid) {
        isValidProperty.set(isValid);
    }

    public String getErrorMessage() {
        return errorMessageProperty.get();
    }

    public void setErrorMessage(String errorMessage) {
        errorMessageProperty.set(errorMessage);
    }

    public BooleanProperty isValidProperty() {
        return isValidProperty;
    }

    public StringProperty errorMessageProperty() {
        return errorMessageProperty;
    }
}
