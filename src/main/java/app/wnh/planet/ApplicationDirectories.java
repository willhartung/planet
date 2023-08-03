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

import java.nio.file.Path;
import java.nio.file.Paths;


/* from https://stackoverflow.com/questions/35388882/find-place-for-dedicated-application-folder */
public class ApplicationDirectories {

//    private static final Logger logger
//            = Logger.getLogger(ApplicationDirectories.class.getName());

    private static final Path config;

    private static final Path data;

    private static final Path cache;

    static {
        String os = System.getProperty("os.name");
        String home = System.getProperty("user.home");

        if (os.contains("Mac")) {
            config = Paths.get(home, "Library", "Application Support");
            data = config;
            cache = Paths.get(home, "Library", "Caches");
        } else if (os.contains("Windows")) {
            String version = System.getProperty("os.version");
            if (version.startsWith("5.")) {
                config = getFromEnv("APPDATA", false,
                        Paths.get(home, "Application Data"));
                data = config;
                cache = Paths.get(home, "Local Settings", "Application Data");
            } else {
                config = getFromEnv("APPDATA", false,
                        Paths.get(home, "AppData", "Roaming"));
                data = config;
                cache = getFromEnv("LOCALAPPDATA", false,
                        Paths.get(home, "AppData", "Local"));
            }
        } else {
            config = getFromEnv("XDG_CONFIG_HOME", true,
                    Paths.get(home, ".config"));
            data = getFromEnv("XDG_DATA_HOME", true,
                    Paths.get(home, ".local", "share"));
            cache = getFromEnv("XDG_CACHE_HOME", true,
                    Paths.get(home, ".cache"));
        }
    }

    /**
     * Prevents instantiation.
     */
    private ApplicationDirectories() {
    }

    /**
     * Retrieves a path from an environment variable, substituting a default if
     * the value is absent or invalid.
     *
     * @param envVar name of environment variable to read
     * @param mustBeAbsolute whether enviroment variable's value should be
     * considered invalid if it's not an absolute path
     * @param defaultPath default to use if environment variable is absent or
     * invalid
     *
     * @return environment variable's value as a {@code Path}, or
     * {@code defaultPath}
     */
    private static Path getFromEnv(String envVar,
            boolean mustBeAbsolute,
            Path defaultPath) {
        Path dir;
        String envDir = System.getenv(envVar);
        if (envDir == null || envDir.isEmpty()) {
            dir = defaultPath;
//            logger.config("{0} not defined in environment, falling back on \"{1}\"", envVar, dir);
        } else {
            dir = Paths.get(envDir);
            if (mustBeAbsolute && !dir.isAbsolute()) {
                dir = defaultPath;
//                logger.config("{0} is not an absolute path, falling back on \"{1}\"", envVar, dir);
            }
        }
        return dir;
    }

    /**
     * Returns directory where the native system expects an application to store
     * configuration files for the current user. No attempt is made to create
     * the directory, and no checks are done to see if it exists.
     *
     * @param appName name of application
     */
    public static Path configDir(String appName) {
        return config.resolve(appName);
    }

    /**
     * Returns directory where the native system expects an application to store
     * implicit data files for the current user. No attempt is made to create
     * the directory, and no checks are done to see if it exists.
     *
     * @param appName name of application
     */
    public static Path dataDir(String appName) {
        return data.resolve(appName);
    }

    /**
     * Returns directory where the native system expects an application to store
     * cached data for the current user. No attempt is made to create the
     * directory, and no checks are done to see if it exists.
     *
     * @param appName name of application
     */
    public static Path cacheDir(String appName) {
        return cache.resolve(appName);
    }
}
