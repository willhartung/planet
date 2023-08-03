package app.wnh.planet.generator;

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

public enum Projection {
    AZIMUTH("Azimuth", "Area preserving azimuthal projection", 'a'),
    CONICAL("Conical", "Conical projection (conformal)", 'c'),
    GNOMONIC("Gnomonic", "Gnomonic projection", 'g'),
    ICOSAHEDRAL("Icosahedral", "Icosahedral projection", 'i'),
    MERCATOR("Mercator", "Mercator projection", 'm'),
    MOLLWEIDE("Mollweide", "Mollweide projection (area preserving)", 'M'),
    ORTHOGRAPHIC("Orthographic", "Orthographic projection", 'o'),
    PETER("Peter", "Peters projection (area preserving cylindrical)", 'p'),
    SINUSOID("Sinusoid", "Sinusoid projection (area preserving)", 'S'),
    SQUARE("Square", "Square projection (equidistant latitudes)", 'q'),
    STEREO("Stereo", "Stereographic projection", 's');

    String label;
    String description;
    char view;

    private Projection(String label, String description, char view) {
        this.label = label;
        this.description = description;
        this.view = view;
    }
    
    public String toString() {
        return label;
    }
    
    public char getView() {
        return view;
    }

}
