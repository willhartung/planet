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

import app.wnh.planet.generator.ColorScheme;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class ColorSchemeManager {

    public Map<String, ColorSchemeModel> colorSchemesMap = new HashMap<>();

    ObservableList<ColorSchemeModel> colorSchemes = FXCollections.observableArrayList();

    File colorSchemeFile;

    public void init(File colorSchemeFile) {
        if (colorSchemeFile == null) {
            for (ColorScheme cs : ColorScheme.values()) {
                ColorSchemeModel csm = createColorSchemeModel(cs);
                addNoSave(csm);
            }
            save();
        } else {
            this.colorSchemeFile = colorSchemeFile;
            try (FileReader fr = new FileReader(colorSchemeFile)) {
                ColorSchemeModel[] schemes = readSchemesFromJson(fr);
                for (ColorSchemeModel m : schemes) {
                    addNoSave(m);
                }
            } catch (IOException ex) {
                // some kind of problem loading the file, so we're going 
                // to punt and load again from the defaults.
                init(null);
            }
        }
    }

    public ColorSchemeModel getByName(String name) {
        return colorSchemesMap.get(name);
    }

    public void addNoSave(ColorSchemeModel csm) {
        colorSchemes.add(csm);
        colorSchemesMap.put(csm.getName(), csm);
        Collections.sort(colorSchemes);
    }
    public void add(ColorSchemeModel csm) {
        addNoSave(csm);
        save();
    }

    public void delete(ColorSchemeModel csm) {
        colorSchemes.remove(csm);
        colorSchemesMap.remove(csm.getName());
        save();
    }

    public void update(ColorSchemeModel csm) {
        ColorSchemeModel m = getByName(csm.getName());
        m.copy(csm);
        Collections.sort(colorSchemes);
        save();
    }

    public void save() {
        if (colorSchemeFile != null) {
            try (FileWriter fw = new FileWriter(colorSchemeFile)) {
                writeSchemesToJson(colorSchemes, fw);
            } catch (IOException ex) {
                System.out.println("Error writing colorscheme file " + colorSchemeFile + " - " + ex);
            }
        }
    }

    public ColorSchemeModel renameColorScheme(String oldName, String newName) {
        ColorSchemeModel csm = getByName(oldName);
        if (csm != null) {
            delete(csm);
            csm.setName(newName);
            add(csm);
        }

        return csm;
    }

    public ObservableList<ColorSchemeModel> colorSchemes() {
        return colorSchemes;
    }

    private ColorSchemeModel createColorSchemeModel(ColorScheme cs) {
        try {
            InputStream is = $.resources.getResourceAsStream(cs.getPath());
            ColorSchemeModel result = readcolors(is);
            result.setName(cs.getName());
            return result;
        } catch (IOException ex) {
            throw new RuntimeException("Unknown exception trying to load colorScehem " + cs);
        }
    }

    private ColorSchemeModel readcolors(InputStream colorStream) throws IOException {
        ColorSchemeModel result = new ColorSchemeModel();
        try (InputStreamReader fr = new InputStreamReader(colorStream); BufferedReader br = new BufferedReader(fr)) {
            String line = br.readLine();
            while (line != null) {
                if (line.isBlank()) {
                    line = br.readLine();
                    continue;
                }
                Scanner s = new Scanner(line);
                int cNum = s.nextInt();
                int rValue = s.nextInt();
                int gValue = s.nextInt();
                int bValue = s.nextInt();

                Color color = new Color(rValue / 255.0, gValue / 255.0, bValue / 255.0, 1.0);
                switch (cNum) {
                    case 0:
                    case 1:
                        // skip colors 0 and 1
                        break;
                    case 2:
                        result.setBackgroundColor(color);
                        break;
                    case 3:
                        result.setLatLongColor(color);
                        break;
                    case 4:
                        result.setOutline1Color(color);
                        break;
                    case 5:
                        result.setOutline2Color(color);
                        break;
                    default:
                        AltitudeColorModel altModel = new AltitudeColorModel(cNum, color);
                        result.altitudeColors.add(altModel);
                }
                line = br.readLine();
            }
        }

        return result;
    }

    private ColorSchemeModel[] readSchemesFromJson(Reader reader) {
        ColorAdapter ca = new ColorAdapter();
        JsonbConfig config = new JsonbConfig()
                .withAdapters(ca)
                .withFormatting(true);
        Jsonb jsonb = JsonbBuilder.create(config);
        ColorSchemeModel[] schemes = jsonb.fromJson(reader, ColorSchemeModel[].class);
        return schemes;
    }

    private void writeSchemesToJson(List<ColorSchemeModel> schemes, Writer writer) {
        ColorAdapter ca = new ColorAdapter();
        JsonbConfig config = new JsonbConfig()
                .withAdapters(ca)
                .withFormatting(true);
        Jsonb jsonb = JsonbBuilder.create(config);
        jsonb.toJson(schemes, writer);
    }

    @Override
    public String toString() {
        return "ColorSchemeManager{" + "colorSchemes=" + colorSchemesMap + '}';
    }

    public ColorSchemeModel createColorScheme(String name) {
        ColorSchemeModel csm = new ColorSchemeModel(name);
        add(csm);

        return csm;
    }

    public boolean nameExists(String name) {
        return colorSchemesMap.containsKey(name);
    }

    public ColorSchemeModel getDefault() {
        ColorSchemeModel csm = getByName("Olsson");
        if (csm == null && !colorSchemes.isEmpty()) {
            csm = colorSchemes.get(0);
        }

        return csm;
    }

    private static class ColorAdapter implements JsonbAdapter<Color, JsonObject> {

        public ColorAdapter() {
        }

        @Override
        public JsonObject adaptToJson(Color color) throws Exception {
            return Json.createObjectBuilder()
                    .add("red", color.getRed())
                    .add("green", color.getGreen())
                    .add("blue", color.getBlue())
                    .add("opacity", color.getOpacity())
                    .build();
        }

        @Override
        public Color adaptFromJson(JsonObject obj) throws Exception {
            double red = obj.getJsonNumber("red").doubleValue();
            double green = obj.getJsonNumber("green").doubleValue();
            double blue = obj.getJsonNumber("blue").doubleValue();
            double opacity = obj.getJsonNumber("opacity").doubleValue();

            return new Color(red, green, blue, opacity);
        }
    }

}
