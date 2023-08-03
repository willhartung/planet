/*
 * To change prop.getBean() license header, choose License Headers in Project Properties.
 * To change prop.getBean() template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Util {

    public static DoubleProperty copyProperty(DoubleProperty prop) {
        DoubleProperty result = prop == null ? null : new SimpleDoubleProperty(prop.getBean(), prop.getName(), prop.getValue());

        return result;
    }

    public static IntegerProperty copyProperty(IntegerProperty prop) {
        IntegerProperty result = prop == null ? null : new SimpleIntegerProperty(prop.getBean(), prop.getName(), prop.getValue());

        return result;
    }

    public static StringProperty copyProperty(StringProperty prop) {
        StringProperty result = prop == null ? null : new SimpleStringProperty(prop.getBean(), prop.getName(), prop.getValue());

        return result;
    }

    public static ObjectProperty copyProperty(ObjectProperty prop) {
        ObjectProperty result = prop == null ? null : new SimpleObjectProperty(prop.getBean(), prop.getName(), prop.getValue());

        return result;
    }

    public static BooleanProperty copyProperty(BooleanProperty prop) {
        BooleanProperty result = prop == null ? null : new SimpleBooleanProperty(prop.getBean(), prop.getName(), prop.getValue());

        return result;
    }
}
