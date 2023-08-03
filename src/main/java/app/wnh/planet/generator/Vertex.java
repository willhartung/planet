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

public class Vertex {

    /* altitude */
    public double h;
    /* seed */
    public double s;
    /* coordinates */
    public double x, y, z;
    /* approximate rain shadow */
    public double shadow;
    
    public Vertex() {
        
    }
    public Vertex(Vertex v) {
        this.h = v.h;
        this.s = v.s;
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.shadow = v.shadow;
    }
}
