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

import app.wnh.planet.generator.Shading;
import app.wnh.planet.generator.Projection;
import app.wnh.planet.generator.Planet;
import app.wnh.planet.generator.ColorScheme;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class PlanetModel {

    private String _title;
    private transient StringProperty titleProperty;
    private Projection _projection;
    private transient ObjectProperty<Projection> projectionProperty;
    private Integer _width;
    private transient IntegerProperty widthProperty;
    private Integer _height;
    private transient IntegerProperty heightProperty;
    private Double _scale;
    private transient DoubleProperty scaleProperty;
    private Shading _shading;
    private transient ObjectProperty<Shading> shadingProperty;
    private Boolean _transparent;
    private transient BooleanProperty transparentProperty;
    private Double _seed;
    private transient DoubleProperty seedProperty;
    private Double _hgrid;
    private transient DoubleProperty hgridProperty;
    private Double _vgrid;
    private transient DoubleProperty vgridProperty;
    private Double _longitude;
    private transient DoubleProperty longitudeProperty;
    private Double _latitude;
    private transient DoubleProperty latitudeProperty;
    private ColorSchemeModel _colorScheme;
    private transient ObjectProperty<ColorSchemeModel> colorSchemeProperty;
    private Boolean _outline;
    private transient BooleanProperty outlineProperty;
    private Integer _contourLines;
    private transient IntegerProperty contourLinesProperty;
    private Integer _coastalContourLines;
    private transient IntegerProperty coastalContourLinesProperty;
    private Boolean _blackAndWhite;
    private transient BooleanProperty blackAndWhiteProperty;
    private Integer _latitudeColor;
    private transient IntegerProperty latitudeColorProperty;
    private Boolean _temperature;
    private transient BooleanProperty temperatureProperty;
    private Boolean _biome;
    private transient BooleanProperty biomeProperty;
    private Double _base;
    private transient DoubleProperty baseProperty;
    private Integer _diameter;
    private transient IntegerProperty diameterProperty;
    private String _name;
    private transient StringProperty nameProperty;
    private String _location;
    private transient StringProperty locationProperty;
    private String _profile;
    private transient StringProperty profileProperty;
    private Integer _waterPercentage;
    private transient IntegerProperty waterPercentageProperty;
    private Boolean _lockSeed;
    private transient BooleanProperty lockSeedProperty;
    private Double _shadeLongitude;
    private transient DoubleProperty shadeLongitudeProperty;
    private Double _shadeLatitude;
    private transient DoubleProperty shadeLatitudeProperty;

    public PlanetModel() {
    }

    public static PlanetModel getDefaultModel() {
        PlanetModel result = new PlanetModel();
        result.setSeed(0.123);
        result.setProjection(Projection.PETER);
        result.setLongitude(0.);
        result.setLatitude(0.);
        result.setScale(1.);
        result.setHeight(400);
        result.setWidth(400);
        result.setVgrid(0.);
        result.setHgrid(0.);
        result.setShading(Shading.NONE);
        result.setTransparent(false);
        result.setColorScheme($.colorSchemeManager.getDefault());
        result.setBlackAndWhite(Boolean.FALSE);
        result.setOutline(Boolean.FALSE);
        result.setContourLines(0);
        result.setCoastalContourLines(0);
        result.setLatitudeColor(0);
        result.setTemperature(Boolean.FALSE);
        result.setBiome(Boolean.FALSE);
        result.setBase(-0.02);
        result.setDiameter(0);
        result.setName("Unnamed");
        result.setLocation(null);
        result.setProfile(null);
        result.setWaterPercentage(0);
        result.setLockSeed(Boolean.FALSE);
        result.setShadeLongitude(150.0);
        result.setShadeLatitude(20.0);

        return result;
    }

    public PlanetModel(PlanetModel model) {
        this._title = model._title;
        this.titleProperty = Util.copyProperty(model.titleProperty);
        this._projection = model._projection;
        this.projectionProperty = Util.copyProperty(model.projectionProperty);
        this._width = model._width;
        this.widthProperty = Util.copyProperty(model.widthProperty);
        this._height = model._height;
        this.heightProperty = Util.copyProperty(model.heightProperty);
        this._scale = model._scale;
        this.scaleProperty = Util.copyProperty(model.scaleProperty);
        this._shading = model._shading;
        this.shadingProperty = Util.copyProperty(model.shadingProperty);
        this._transparent = model._transparent;
        this.transparentProperty = Util.copyProperty(model.transparentProperty);
        this._seed = model._seed;
        this.seedProperty = Util.copyProperty(model.seedProperty);
        this._hgrid = model._hgrid;
        this.hgridProperty = Util.copyProperty(model.hgridProperty);
        this._vgrid = model._vgrid;
        this.vgridProperty = Util.copyProperty(model.vgridProperty);
        this._longitude = model._longitude;
        this.longitudeProperty = Util.copyProperty(model.longitudeProperty);
        this._latitude = model._latitude;
        this.latitudeProperty = Util.copyProperty(model.latitudeProperty);
        this._colorScheme = model._colorScheme;
        this.colorSchemeProperty = Util.copyProperty(model.colorSchemeProperty);
        this._blackAndWhite = model._blackAndWhite;
        this.blackAndWhiteProperty = Util.copyProperty(model.blackAndWhiteProperty);
        this._contourLines = model._contourLines;
        this.contourLinesProperty = Util.copyProperty(model.contourLinesProperty);
        this._coastalContourLines = model._coastalContourLines;
        this.coastalContourLinesProperty = Util.copyProperty(model.coastalContourLinesProperty);
        this._outline = model._outline;
        this.outlineProperty = Util.copyProperty(model.outlineProperty);
        this._latitudeColor = model._latitudeColor;
        this.latitudeColorProperty = Util.copyProperty(model.latitudeColorProperty);
        this._temperature = model._temperature;
        this.temperatureProperty = Util.copyProperty(model.temperatureProperty);
        this._biome = model._biome;
        this.biomeProperty = Util.copyProperty(model.biomeProperty);
        this._base = model._base;
        this.baseProperty = Util.copyProperty(model.baseProperty);
        this._diameter = model._diameter;
        this.diameterProperty = Util.copyProperty(model.diameterProperty);
        this._name = model._name;
        this.nameProperty = Util.copyProperty(model.nameProperty);
        this._location = model._location;
        this.locationProperty = Util.copyProperty(model.locationProperty);
        this._profile = model._profile;
        this.profileProperty = Util.copyProperty(model.profileProperty);
        this._waterPercentage = model._waterPercentage;
        this.waterPercentageProperty = Util.copyProperty(model.waterPercentageProperty);
        this._lockSeed = model._lockSeed;
        this.lockSeedProperty = Util.copyProperty(model.lockSeedProperty);
        this._shadeLongitude = model._shadeLongitude;
        this.shadeLongitudeProperty = Util.copyProperty(model.shadeLongitudeProperty);
        this._shadeLatitude = model._shadeLatitude;
        this.shadeLatitudeProperty = Util.copyProperty(model.shadeLatitudeProperty);
    }

    public Image renderImage() {
        InputStream is = null;
//        try {
//            is = $.resources.getResourceAsStream(getColorScheme().getPath());
//        } catch (IOException ex) {
//            throw new RuntimeException("Unknown exception trying to load colorScehem " + getColorScheme());
//        }
        Planet planet = new Planet();
        planet.setPlanetModel(this);
//        planet.colorSchemeModel = $.colorSchemeManager.colorSchemesMap.get(colorschemeProperty().get().getName());
        planet.colorSchemeModel = new ColorSchemeModel(getColorScheme());
//        planet.colorFileStream = is;

        planet.start(null);
        BufferedImage img = planet.toImage();
        Image image = SwingFXUtils.toFXImage(img, null);
        setWaterPercentage((int) (100 * planet.waterPercent));
        return image;
    }

    public void findWaterWorld(int hydroValue) {
        Random rnd = new Random();
        PlanetModel model = PlanetModel.getDefaultModel();
        model.setProjection(Projection.PETER);
        model.setHeight(50);
        model.setWidth(50);
        boolean found = false;
        for (int i = 0; i < 1000; i++) {
            model.setSeed(rnd.nextDouble());
            model.renderImage();
            int water = (int) (model.getWaterPercentage() / 10);
            if (water == hydroValue) {
                found = true;
                break;
            }
        }

        setSeed(model.getSeed());
    }

    public String getTitle() {
        if (titleProperty == null) {
            return _title;
        } else {
            return titleProperty.get();
        }
    }

    public void setTitle(String title) {
        if (titleProperty == null) {
            _title = title;
        } else {
            titleProperty.set(title);
        }
    }

    public StringProperty titleProperty() {
        if (titleProperty == null) {
            titleProperty = new SimpleStringProperty(this, "title", _title);
        }
        return titleProperty;
    }

    public Projection getProjection() {
        if (projectionProperty == null) {
            return _projection;
        } else {
            return projectionProperty.get();
        }
    }

    public void setProjection(Projection projection) {
        if (projectionProperty == null) {
            _projection = projection;
        } else {
            projectionProperty.set(projection);
        }
    }

    public ObjectProperty<Projection> projectionProperty() {
        if (projectionProperty == null) {
            projectionProperty = new SimpleObjectProperty<Projection>(this, "projection", _projection);
        }
        return projectionProperty;
    }

    public Integer getWidth() {
        if (widthProperty == null) {
            return _width;
        } else {
            return widthProperty.get();
        }
    }

    public void setWidth(Integer width) {
        if (widthProperty == null) {
            _width = width;
        } else {
            widthProperty.set(width);
        }
    }

    public IntegerProperty widthProperty() {
        if (widthProperty == null) {
            widthProperty = new SimpleIntegerProperty(this, "width", _width);
        }
        return widthProperty;
    }

    public Integer getHeight() {
        if (heightProperty == null) {
            return _height;
        } else {
            return heightProperty.get();
        }
    }

    public void setHeight(Integer height) {
        if (heightProperty == null) {
            _height = height;
        } else {
            heightProperty.set(height);
        }
    }

    public IntegerProperty heightProperty() {
        if (heightProperty == null) {
            heightProperty = new SimpleIntegerProperty(this, "height", _height);
        }
        return heightProperty;
    }

    public Double getScale() {
        Double result = 0.;
        if (scaleProperty == null) {
            result = _scale;
        } else {
            result = scaleProperty.get();
        }
        return result;
    }

    public void setScale(Double scale) {
        if (scaleProperty == null) {
            _scale = scale;
        } else {
            scaleProperty.set(scale);
        }
    }

    public DoubleProperty scaleProperty() {
        if (scaleProperty == null) {
            scaleProperty = new SimpleDoubleProperty(this, "scale", _scale);
        }
        return scaleProperty;
    }

    public Double getSeed() {
        if (seedProperty == null) {
            return _seed;
        } else {
            return seedProperty.get();
        }
    }

    public void setSeed(Double seed) {
        if (seedProperty == null) {
            _seed = seed;
        } else {
            seedProperty.set(seed);
        }
    }

    public DoubleProperty seedProperty() {
        if (seedProperty == null) {
            seedProperty = new SimpleDoubleProperty(this, "seed", _seed);
        }
        return seedProperty;
    }

    public Double getLongitude() {
        if (longitudeProperty == null) {
            return _longitude;
        } else {
            return longitudeProperty.get();
        }
    }

    public void setLongitude(Double longitued) {
        if (longitudeProperty == null) {
            _longitude = longitued;
        } else {
            longitudeProperty.set(longitued);
        }
    }

    public DoubleProperty longitudeProperty() {
        if (longitudeProperty == null) {
            longitudeProperty = new SimpleDoubleProperty(this, "longitued", _longitude);
        }
        return longitudeProperty;
    }

    public Double getLatitude() {
        if (latitudeProperty == null) {
            return _latitude;
        } else {
            return latitudeProperty.get();
        }
    }

    public void setLatitude(Double latitude) {
        if (latitudeProperty == null) {
            _latitude = latitude;
        } else {
            latitudeProperty.set(latitude);
        }
    }

    public DoubleProperty latitudeProperty() {
        if (latitudeProperty == null) {
            latitudeProperty = new SimpleDoubleProperty(this, "latitude", _latitude);
        }
        return latitudeProperty;
    }

    public Double getHgrid() {
        if (hgridProperty == null) {
            return _hgrid;
        } else {
            return hgridProperty.get();
        }
    }

    public void setHgrid(Double hgrid) {
        if (hgridProperty == null) {
            _hgrid = hgrid;
        } else {
            hgridProperty.set(hgrid);
        }
    }

    public DoubleProperty hgridProperty() {
        if (hgridProperty == null) {
            hgridProperty = new SimpleDoubleProperty(this, "hgrid", _hgrid);
        }
        return hgridProperty;
    }

    public Double getVgrid() {
        if (vgridProperty == null) {
            return _vgrid;
        } else {
            return vgridProperty.get();
        }
    }

    public void setVgrid(Double vgrid) {
        if (vgridProperty == null) {
            _vgrid = vgrid;
        } else {
            vgridProperty.set(vgrid);
        }
    }

    public DoubleProperty vgridProperty() {
        if (vgridProperty == null) {
            vgridProperty = new SimpleDoubleProperty(this, "vgrid", _vgrid);
        }
        return vgridProperty;
    }

    public ColorSchemeModel getColorScheme() {
        if (colorSchemeProperty == null) {
            return _colorScheme;
        } else {
            return colorSchemeProperty.get();
        }
    }

    public void setColorScheme(ColorSchemeModel colorScheme) {
        if (colorSchemeProperty == null) {
            _colorScheme = colorScheme;
        } else {
            colorSchemeProperty.set(colorScheme);
        }
    }

    public ObjectProperty<ColorSchemeModel> colorschemeProperty() {
        if (colorSchemeProperty == null) {
            colorSchemeProperty = new SimpleObjectProperty<ColorSchemeModel>(this, "colorScheme", _colorScheme);
        }
        return colorSchemeProperty;
    }

    public Shading getShading() {
        if (shadingProperty == null) {
            return _shading;
        } else {
            return shadingProperty.get();
        }
    }

    public void setShading(Shading shading) {
        if (shadingProperty == null) {
            _shading = shading;
        } else {
            shadingProperty.set(shading);
        }
    }

    public ObjectProperty<Shading> shadingProperty() {
        if (shadingProperty == null) {
            shadingProperty = new SimpleObjectProperty<Shading>(this, "shading", _shading);
        }
        return shadingProperty;
    }

    public Boolean getTransparent() {
        if (transparentProperty == null) {
            return _transparent;
        } else {
            return transparentProperty.get();
        }
    }

    public void setTransparent(Boolean transparent) {
        if (transparentProperty == null) {
            _transparent = transparent;
        } else {
            transparentProperty.set(transparent);
        }
    }

    public BooleanProperty transparentProperty() {
        if (transparentProperty == null) {
            transparentProperty = new SimpleBooleanProperty(this, "transparent", _transparent);
        }
        return transparentProperty;
    }

    public Boolean getOutline() {
        if (outlineProperty == null) {
            return _outline;
        } else {
            return outlineProperty.get();
        }
    }

    public void setOutline(Boolean outline) {
        if (outlineProperty == null) {
            _outline = outline;
        } else {
            outlineProperty.set(outline);
        }
    }

    public BooleanProperty outlineProperty() {
        if (outlineProperty == null) {
            outlineProperty = new SimpleBooleanProperty(this, "outline", _outline);
        }
        return outlineProperty;
    }

    public Boolean getBlackAndWhite() {
        if (blackAndWhiteProperty == null) {
            return _blackAndWhite;
        } else {
            return blackAndWhiteProperty.get();
        }
    }

    public void setBlackAndWhite(Boolean blackAndWhite) {
        if (blackAndWhiteProperty == null) {
            _blackAndWhite = blackAndWhite;
        } else {
            blackAndWhiteProperty.set(blackAndWhite);
        }
    }

    public BooleanProperty blackAndWhiteProperty() {
        if (blackAndWhiteProperty == null) {
            blackAndWhiteProperty = new SimpleBooleanProperty(this, "blackAndWhite", _blackAndWhite);
        }
        return blackAndWhiteProperty;
    }

    public Integer getContourLines() {
        if (contourLinesProperty == null) {
            return _contourLines;
        } else {
            return contourLinesProperty.get();
        }
    }

    public void setContourLines(Integer contourLines) {
        if (contourLinesProperty == null) {
            _contourLines = contourLines;
        } else {
            contourLinesProperty.set(contourLines);
        }
    }

    public IntegerProperty contourLinesProperty() {
        if (contourLinesProperty == null) {
            contourLinesProperty = new SimpleIntegerProperty(this, "contourLines", _contourLines);
        }
        return contourLinesProperty;
    }

    public Integer getCoastalContourLines() {
        if (coastalContourLinesProperty == null) {
            return _coastalContourLines;
        } else {
            return coastalContourLinesProperty.get();
        }
    }

    public void setCoastalContourLines(Integer coastalContourLines) {
        if (coastalContourLinesProperty == null) {
            _coastalContourLines = coastalContourLines;
        } else {
            coastalContourLinesProperty.set(coastalContourLines);
        }
    }

    public IntegerProperty coastalContourLinesProperty() {
        if (coastalContourLinesProperty == null) {
            coastalContourLinesProperty = new SimpleIntegerProperty(this, "coastalContourLines", _coastalContourLines);
        }
        return coastalContourLinesProperty;
    }

    public Integer getLatitudeColor() {
        if (latitudeColorProperty == null) {
            return _latitudeColor;
        } else {
            return latitudeColorProperty.get();
        }
    }

    public void setLatitudeColor(Integer latitudeColor) {
        if (latitudeColorProperty == null) {
            _latitudeColor = latitudeColor;
        } else {
            latitudeColorProperty.set(latitudeColor);
        }
    }

    public IntegerProperty latitudeColorProperty() {
        if (latitudeColorProperty == null) {
            latitudeColorProperty = new SimpleIntegerProperty(this, "latitudeColor", _latitudeColor);
        }
        return latitudeColorProperty;
    }

    public Boolean getTemperature() {
        if (temperatureProperty == null) {
            return _temperature;
        } else {
            return temperatureProperty.get();
        }
    }

    public void setTemperature(Boolean temperature) {
        if (temperatureProperty == null) {
            _temperature = temperature;
        } else {
            temperatureProperty.set(temperature);
        }
    }

    public BooleanProperty temperatureProperty() {
        if (temperatureProperty == null) {
            temperatureProperty = new SimpleBooleanProperty(this, "temperature", _temperature);
        }
        return temperatureProperty;
    }

    public Boolean getBiome() {
        if (biomeProperty == null) {
            return _biome;
        } else {
            return biomeProperty.get();
        }
    }

    public void setBiome(Boolean biome) {
        if (biomeProperty == null) {
            _biome = biome;
        } else {
            biomeProperty.set(biome);
        }
    }

    public BooleanProperty biomeProperty() {
        if (biomeProperty == null) {
            biomeProperty = new SimpleBooleanProperty(this, "biome", _biome);
        }
        return biomeProperty;
    }

    public Double getBase() {
        Double result = 0.;
        if (baseProperty == null) {
            result = _base;
        } else {
            result = baseProperty.get();
        }
        return result;
    }

    public void setBase(Double base) {
        if (baseProperty == null) {
            _base = base;
        } else {
            baseProperty.set(base);
        }
    }

    public DoubleProperty baseProperty() {
        if (baseProperty == null) {
            baseProperty = new SimpleDoubleProperty(this, "base", _base);
        }
        return baseProperty;
    }

    public Integer getDiameter() {
        if (diameterProperty == null) {
            return _diameter;
        } else {
            return diameterProperty.get();
        }
    }

    public void setDiameter(Integer diameter) {
        if (diameterProperty == null) {
            _diameter = diameter;
        } else {
            diameterProperty.set(diameter);
        }
    }

    public IntegerProperty diameterProperty() {
        if (diameterProperty == null) {
            diameterProperty = new SimpleIntegerProperty(this, "diameter", _diameter);
        }
        return diameterProperty;
    }

    public String getName() {
        if (nameProperty == null) {
            return _name;
        } else {
            return nameProperty.get();
        }
    }

    public void setName(String name) {
        if (nameProperty == null) {
            _name = name;
        } else {
            nameProperty.set(name);
        }
    }

    public StringProperty nameProperty() {
        if (nameProperty == null) {
            nameProperty = new SimpleStringProperty(this, "name", _name);
        }
        return nameProperty;
    }

    public String getLocation() {
        if (locationProperty == null) {
            return _location;
        } else {
            return locationProperty.get();
        }
    }

    public void setLocation(String location) {
        if (locationProperty == null) {
            _location = location;
        } else {
            locationProperty.set(location);
        }
    }

    public StringProperty locationProperty() {
        if (locationProperty == null) {
            locationProperty = new SimpleStringProperty(this, "location", _location);
        }
        return locationProperty;
    }

    public String getProfile() {
        if (profileProperty == null) {
            return _profile;
        } else {
            return profileProperty.get();
        }
    }

    public void setProfile(String profile) {
        if (profileProperty == null) {
            _profile = profile;
        } else {
            profileProperty.set(profile);
        }
    }

    public StringProperty profileProperty() {
        if (profileProperty == null) {
            profileProperty = new SimpleStringProperty(this, "profile", _profile);
        }
        return profileProperty;
    }

    public Integer getWaterPercentage() {
        if (waterPercentageProperty == null) {
            return _waterPercentage;
        } else {
            return waterPercentageProperty.get();
        }
    }

    public void setWaterPercentage(Integer waterpercentage) {
        if (waterPercentageProperty == null) {
            _waterPercentage = waterpercentage;
        } else {
            waterPercentageProperty.set(waterpercentage);
        }
    }

    public IntegerProperty waterPercentageProperty() {
        if (waterPercentageProperty == null) {
            waterPercentageProperty = new SimpleIntegerProperty(this, "waterPercentage", _waterPercentage);
        }
        return waterPercentageProperty;
    }

    public Boolean getLockSeed() {
        if (lockSeedProperty == null) {
            return _lockSeed;
        } else {
            return lockSeedProperty.get();
        }
    }

    public void setLockSeed(Boolean lockSeed) {
        if (lockSeedProperty == null) {
            _lockSeed = lockSeed;
        } else {
            lockSeedProperty.set(lockSeed);
        }
    }

    public BooleanProperty lockSeedProperty() {
        if (lockSeedProperty == null) {
            lockSeedProperty = new SimpleBooleanProperty(this, "lockSeed", _lockSeed);
        }
        return lockSeedProperty;
    }

    public Double getShadeLongitude() {
        Double result = 0.;
        if (shadeLongitudeProperty == null) {
            result = _shadeLongitude;
        } else {
            result = shadeLongitudeProperty.get();
        }
        return result;
    }

    public void setShadeLongitude(Double shade) {
        if (shadeLongitudeProperty == null) {
            _shadeLongitude = shade;
        } else {
            shadeLongitudeProperty.set(shade);
        }
    }

    public DoubleProperty shadeLongitudeProperty() {
        if (shadeLongitudeProperty == null) {
            shadeLongitudeProperty = new SimpleDoubleProperty(this, "shade", _shadeLongitude);
        }
        return shadeLongitudeProperty;
    }

    public Double getShadeLatitude() {
        Double result = 0.;
        if (shadeLatitudeProperty == null) {
            result = _shadeLatitude;
        } else {
            result = shadeLatitudeProperty.get();
        }
        return result;
    }

    public void setShadeLatitude(Double shade) {
        if (shadeLatitudeProperty == null) {
            _shadeLatitude = shade;
        } else {
            shadeLatitudeProperty.set(shade);
        }
    }

    public DoubleProperty shadeLatitudeProperty() {
        if (shadeLatitudeProperty == null) {
            shadeLatitudeProperty = new SimpleDoubleProperty(this, "shade", _shadeLatitude);
        }
        return shadeLatitudeProperty;
    }

}
