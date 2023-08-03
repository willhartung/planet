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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Resources {

    private URL rootCSSUrl;

    public Resources() {
        rootCSSUrl = getResource("root.css");
    }

    public URL getResource(String path) {
        return getClass().getResource(path);
    }

    public InputStream getResourceAsStream(String path) throws IOException {
        return getResource(path).openStream();
    }

    public URL getRootCSS() {
        return rootCSSUrl;
    }

    public String getRootCSSURLString() {
        return rootCSSUrl.toExternalForm();
    }
}
