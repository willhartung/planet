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

public enum Shading {
    NONE("None", "No shading", 0),
    BUMP("Land and Sea", "Land and Sea bump map shading", 1),
    BUMPLAND("Land only", "Land only bump map shading", 2),
    DAYTIME("Daylight", "Daylight shading", 3);

    String label;
    String description;
    int shade;

    private Shading(String label, String description, int shade) {
        this.label = label;
        this.description = description;
        this.shade = shade;
    }
    
    public String toString() {
        return label;
    }
    
    public int getShade() {
        return shade;
    }
}
