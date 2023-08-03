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

import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class IntegerTextFormatter extends TextFormatter<Number> {
    
    public IntegerTextFormatter() {
        super(new StringConverter<Number>() {
            @Override
            public String toString(Number t) {
                return "" + t;
            }

            @Override
            public Integer fromString(String string) {
                int result;
                try {
                    result = Integer.parseInt(string);
                } catch (NumberFormatException ex) {
                    result = 0;
                }
                return result;
            }
        });
    }
    
}
