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

// This strange class is the holder for all singletons

import java.io.File;
import java.nio.file.Path;


public class $ {

    public static StageManager stageManager;
    public static Resources resources;
    public static ColorSchemeManager colorSchemeManager;

    public static void init() {
        stageManager = new StageManager();
        resources = new Resources();
        colorSchemeManager = new ColorSchemeManager();     
        Path dataPath = ApplicationDirectories.dataDir("Planet");
        File dirPath = dataPath.toFile();
        dirPath.mkdirs();
        File colorsFile = dataPath.resolve("colors.json").toFile();
        colorSchemeManager.init(colorsFile);
    }

    public static void shutDown() {

    }
}
