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

public enum ColorScheme {

    BATHYMETRIC("Bathymetric", "Bathymetric.col"),
    BLACKBODY("Blackbody", "Blackbody.col"),
    BLUE("Blue", "blue.col"),
    LEFEBVRE("Lefebvre", "Lefebvre.col"),
    LEFEBVRE2("Lefebvre2", "Lefebvre2.col"),
    OLSSON("Olsson", "Olsson.col"),
    OLSSON1("Olsson1", "Olsson1.col"),
    OLSSON2("Olsson2", "Olsson2.col"),
    OLSSONLIGHT("OlssonLight", "OlssonLight.col"),
    BURROWS("Burrows", "burrows.col"),
    BURROWSB("BurrowsB", "burrowsB.col"),
    DEFAULT("Default", "default.col"),
    DEFAULTB("DefaultB", "defaultB.col"),
    GREYSCALE("Greyscale", "greyscale.col"),
    LIGHT("Light", "light.col"),
    MARS("Mars", "mars.col"),
    WHITE("White", "white.col"),
    WOOD("Wood", "wood.col"),
    YELLOW("Yellow", "yellow.col");

    String name;
    String path;

    private ColorScheme(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String toString() {
        return name;
    }
}
