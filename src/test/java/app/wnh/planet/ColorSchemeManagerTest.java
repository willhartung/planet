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
import app.wnh.planet.generator.Projection;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;

/*
As a general rule, I'm not big on tests. Instead, I use tests to write quick
one-off tests or utilities that run in the context of the application
instead of, say, adding a quick button and doing it that way.
 */
public class ColorSchemeManagerTest {

    Random r = new Random();

    public ColorSchemeManagerTest() {
    }

    public void testInit() {
        $.init();
        System.out.println("init");
        ColorSchemeManager instance = new ColorSchemeManager();
        instance.init(null);
        System.out.println("instance " + instance.colorSchemesMap.get("Blackbody"));
        System.out.println("instance " + instance.colorSchemesMap.get("Default"));
        StringWriter sw = new StringWriter();
        ColorAdapter ca = new ColorAdapter();
        JsonbConfig config = new JsonbConfig()
                .withAdapters(ca)
                .withFormatting(true);
        Jsonb jsonb = JsonbBuilder.create(config);
        jsonb.toJson(instance.colorSchemes, sw);
        System.out.println(sw);
        StringReader sr = new StringReader(sw.toString());
        List<ColorSchemeModel> o = jsonb.fromJson(sr, ArrayList.class);
        System.out.println(o);
    }

    public void genIcons() {
        List<BufferedImage> images = new ArrayList<>();
        $.init();
        PlanetModel model = PlanetModel.getDefaultModel();
        model.setLongitude(75.);
        model.setProjection(Projection.ORTHOGRAPHIC);
        model.setTransparent(true);
        int res = 32;
        for (int i = 0; i < 6; i++) {
            model.setHeight(res);
            model.setWidth(res);

            Image image = model.renderImage();
            ImageView view = new ImageView(image);
            File file = new File("/tmp/icon" + res + ".png");
            System.out.println("file = " + file);
            String extension = "png";
            try {
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
                boolean success = ImageIO.write(bufferedImage, extension, file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            res = res * 2;
        }
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
