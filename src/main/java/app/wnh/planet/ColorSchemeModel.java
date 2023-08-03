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
import jakarta.json.bind.annotation.JsonbTransient;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;

public class ColorSchemeModel implements Comparable<ColorSchemeModel> {

    StringProperty nameProperty = new SimpleStringProperty();
    ObjectProperty<Color> backgroundColorProperty = new SimpleObjectProperty<>();
    ObjectProperty<Color> latLongColorProperty = new SimpleObjectProperty<>();
    ObjectProperty<Color> outline1ColorProperty = new SimpleObjectProperty<>();
    ObjectProperty<Color> outline2ColorProperty = new SimpleObjectProperty<>();
    IntegerProperty highestProperty = new SimpleIntegerProperty();
    IntegerProperty lowestProperty = new SimpleIntegerProperty();
    IntegerProperty seaProperty = new SimpleIntegerProperty();
    IntegerProperty landProperty = new SimpleIntegerProperty();
    BooleanProperty makeBiomesProperty = new SimpleBooleanProperty();
    ObservableList<AltitudeColorModel> altitudeColors = FXCollections.observableArrayList();

    int nocols = 65536;
    int rtable[];
    int gtable[];
    int btable[];

    public ColorSchemeModel() {
        addListeners();
    }

    public ColorSchemeModel(String name) {
        this.nameProperty.set(name);
        this.backgroundColorProperty.set(Color.WHITE);
        this.latLongColorProperty.set(Color.BLACK);
        this.outline1ColorProperty.set(Color.BLACK);
        this.outline2ColorProperty.set(Color.RED);
        addListeners();
    }

    public ColorSchemeModel(ColorSchemeModel model) {
        copy(model);
        resetColors();
        addListeners();
    }

    public void copy(ColorSchemeModel model) {
        nameProperty.set(model.nameProperty.get());
        backgroundColorProperty.set(model.backgroundColorProperty.get());
        latLongColorProperty.set(model.latLongColorProperty.get());
        outline1ColorProperty.set(model.outline1ColorProperty.get());
        outline2ColorProperty.set(model.outline2ColorProperty.get());
        List<AltitudeColorModel> colors = new ArrayList<>();
        for (AltitudeColorModel acm : model.altitudeColors) {
            colors.add(new AltitudeColorModel(acm));
        }
        altitudeColors.clear();
        altitudeColors.addAll(colors);
        resetColors();
    }

    public String getName() {
        return nameProperty.get();
    }

    public void setName(String name) {
        nameProperty.set(name);
    }

    public StringProperty nameProperty() {
        return nameProperty;
    }

    public Color getBackgroundColor() {
        return backgroundColorProperty.get();
    }

    public void setBackgroundColor(Color backgroundColor) {
        backgroundColorProperty.set(backgroundColor);
    }

    public ObjectProperty<Color> backgroundColorProperty() {
        return backgroundColorProperty;
    }

    public Color getLatLongColor() {
        return latLongColorProperty.get();
    }

    public void setLatLongColor(Color latLongColor) {
        latLongColorProperty.set(latLongColor);
    }

    public ObjectProperty<Color> latLongColorProperty() {
        return latLongColorProperty;
    }

    public Color getOutline1Color() {
        return outline1ColorProperty.get();
    }

    public void setOutline1Color(Color outline1Color) {
        outline1ColorProperty.set(outline1Color);
    }

    public ObjectProperty<Color> outline1ColorProperty() {
        return outline1ColorProperty;
    }

    public Color getOutline2Color() {
        return outline2ColorProperty.get();
    }

    public void setOutline2Color(Color outline2Color) {
        outline2ColorProperty.set(outline2Color);
    }

    public ObjectProperty<Color> outline2ColorProperty() {
        return outline2ColorProperty;
    }

    public int getHighest() {
        return highestProperty.get();
    }

    public void setHighest(int highest) {
        highestProperty.set(highest);
    }

    public IntegerProperty highestProperty() {
        return highestProperty;
    }

    public int getLowest() {
        return lowestProperty.get();
    }

    public void setLowest(int lowest) {
        lowestProperty.set(lowest);
    }

    public IntegerProperty lowestProperty() {
        return lowestProperty;
    }

    public int getSea() {
        return seaProperty.get();
    }

    public void setSea(int sea) {
        seaProperty.set(sea);
    }

    public IntegerProperty seaProperty() {
        return seaProperty;
    }

    public int getLand() {
        return landProperty.get();
    }

    public void setLand(int land) {
        landProperty.set(land);
    }

    public IntegerProperty landProperty() {
        return landProperty;
    }

    public boolean getMakeBiomes() {
        return makeBiomesProperty.get();
    }

    public void setMakeBiomes(boolean makeBiomes) {
        makeBiomesProperty.set(makeBiomes);
    }

    public BooleanProperty makeBiomesProperty() {
        return makeBiomesProperty;
    }

    public void setAltitudeColors(List<AltitudeColorModel> colors) {
        altitudeColors.setAll(colors);
    }

    public List<AltitudeColorModel> getAltitudeColors() {
        return new ArrayList<>(altitudeColors);
    }

    public ObservableList<AltitudeColorModel> altitudeColors() {
        return altitudeColors;
    }

    public void addAlitudeColor(AltitudeColorModel acm) {
        altitudeColors.add(acm);
        Collections.sort(altitudeColors);
    }

    public void removeAltitudeColor(AltitudeColorModel acm) {
        altitudeColors.remove(acm);
    }

    @JsonbTransient
    public int[] getRtable() {
        if (rtable == null) {
            generateColors();
        }
        return rtable;
    }

    @JsonbTransient
    public int[] getGtable() {
        if (gtable == null) {
            generateColors();
        }
        return gtable;
    }

    @JsonbTransient
    public int[] getBtable() {
        if (btable == null) {
            generateColors();
        }
        return btable;
    }

    public void generateColors() {

        /* Format of colour file is a sequence of lines       */
 /* each consisting of four integers:                  */
 /*   colour_number red green blue                     */
 /* where 0 <= colour_number <= 65535                  */
 /* and 0<= red, green, blue <= 255                    */
 /* The colour numbers must be increasing              */
 /* The first colours have special uses:               */
 /* 0 is usually black (0,0,0)                         */
 /* 1 is usually white (255,255,255)                   */
 /* 2 is the background colour                         */
 /* 3 is used for latitude/longitude grid lines        */
 /* 4 and 5 are used for outlines and contour lines    */
 /* 6 upwards are used for altitudes                   */
 /* Halfway between 6 and the max colour is sea level  */
 /* Shallowest sea is (max+6)/2 and land is above this */
 /* With 65536 colours, (max+6)/2 = 32770              */
 /* Colours between specified are interpolated         */
        // Colors 0 and 1 are always black and white
        int BLACK = 0;
        int WHITE = 1;
        int BACK = 2;
        int GRID = 3;
        int OUTLINE1 = 4;
        int OUTLINE2 = 5;
        int LOWEST = 6;
        int SEA = 7;
        int LAND = 8;
        int HIGHEST = 9;

        nocols = 65535;
        rtable = new int[nocols];
        gtable = new int[nocols];
        btable = new int[nocols];

        assignColor(BLACK, Color.BLACK);
        assignColor(WHITE, Color.WHITE);
        assignColor(BACK, backgroundColorProperty.get());
        assignColor(GRID, latLongColorProperty.get());
        assignColor(OUTLINE1, outline1ColorProperty.get());
        assignColor(OUTLINE2, outline2ColorProperty.get());
        int oldcNum;
        int cNum = OUTLINE2;
        for (AltitudeColorModel acm : altitudeColors) {
            oldcNum = cNum;
            cNum = acm.getValue();
            int rValue = (int) (acm.getColor().getRed() * 255);
            int gValue = (int) (acm.getColor().getGreen() * 255);
            int bValue = (int) (acm.getColor().getBlue() * 255);
            if (cNum < oldcNum) {
                cNum = oldcNum;
            }
            if (cNum > (nocols - 1)) {
                cNum = nocols - 1;
            }
            rtable[cNum] = rValue;
            gtable[cNum] = gValue;
            btable[cNum] = bValue;
            /* interpolate colours between oldcNum and cNum */
            for (int i = oldcNum + 1; i < cNum; i++) {
                rtable[i] = (rtable[oldcNum] * (cNum - i) + rtable[cNum] * (i - oldcNum))
                        / (cNum - oldcNum + 1);
                gtable[i] = (gtable[oldcNum] * (cNum - i) + gtable[cNum] * (i - oldcNum))
                        / (cNum - oldcNum + 1);
                btable[i] = (btable[oldcNum] * (cNum - i) + btable[cNum] * (i - oldcNum))
                        / (cNum - oldcNum + 1);
            }
        }

        nocols = cNum + 1;
        if (nocols < 10) {
            nocols = 10;
        }

        HIGHEST = nocols - 1;
        SEA = (HIGHEST + LOWEST) / 2;
        LAND = SEA + 1;

        for (int i = cNum + 1; i < nocols; i++) {
            /* fill up rest of colour table with last read colour */
            rtable[i] = rtable[cNum];
            gtable[i] = gtable[cNum];
            btable[i] = btable[cNum];
        }

        if (makeBiomesProperty.get()) {
            /* make biome colours */
            rtable['T' - 64 + LAND] = 210;
            gtable['T' - 64 + LAND] = 210;
            btable['T' - 64 + LAND] = 210;
            rtable['G' - 64 + LAND] = 250;
            gtable['G' - 64 + LAND] = 215;
            btable['G' - 64 + LAND] = 165;
            rtable['B' - 64 + LAND] = 105;
            gtable['B' - 64 + LAND] = 155;
            btable['B' - 64 + LAND] = 120;
            rtable['D' - 64 + LAND] = 220;
            gtable['D' - 64 + LAND] = 195;
            btable['D' - 64 + LAND] = 175;
            rtable['S' - 64 + LAND] = 225;
            gtable['S' - 64 + LAND] = 155;
            btable['S' - 64 + LAND] = 100;
            rtable['F' - 64 + LAND] = 155;
            gtable['F' - 64 + LAND] = 215;
            btable['F' - 64 + LAND] = 170;
            rtable['R' - 64 + LAND] = 170;
            gtable['R' - 64 + LAND] = 195;
            btable['R' - 64 + LAND] = 200;
            rtable['W' - 64 + LAND] = 185;
            gtable['W' - 64 + LAND] = 150;
            btable['W' - 64 + LAND] = 160;
            rtable['E' - 64 + LAND] = 130;
            gtable['E' - 64 + LAND] = 190;
            btable['E' - 64 + LAND] = 25;
            rtable['O' - 64 + LAND] = 110;
            gtable['O' - 64 + LAND] = 160;
            btable['O' - 64 + LAND] = 170;
            rtable['I' - 64 + LAND] = 255;
            gtable['I' - 64 + LAND] = 255;
            btable['I' - 64 + LAND] = 255;
        }

        highestProperty.set(HIGHEST);
        lowestProperty.set(LOWEST);
        landProperty.set(LAND);
        seaProperty.set(SEA);
    }

    private void assignColor(int idx, Color color) {

        int rValue = (int) (color.getRed() * 255);
        int gValue = (int) (color.getGreen() * 255);
        int bValue = (int) (color.getBlue() * 255);

        rtable[idx] = rValue;
        gtable[idx] = gValue;
        btable[idx] = bValue;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(ColorSchemeModel o) {
        return getName().compareTo(o.getName());
    }

    private void resetColors() {
        rtable = null;
        gtable = null;
        btable = null;
    }

    private void addListeners() {
        backgroundColorProperty.addListener((observable) -> {
            resetColors();
        });
        latLongColorProperty.addListener((observable) -> {
            resetColors();
        });
        outline1ColorProperty.addListener((observable) -> {
            resetColors();
        });
        outline2ColorProperty.addListener((observable) -> {
            resetColors();
        });
        makeBiomesProperty.addListener((observable) -> {
            resetColors();
        });
        altitudeColors.addListener(new ListChangeListener<AltitudeColorModel>() {
            @Override
            public void onChanged(ListChangeListener.Change<? extends AltitudeColorModel> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        for (AltitudeColorModel acm : c.getAddedSubList()) {
                            acm.colorProperty.addListener((observable) -> {
                                resetColors();
                            });
                            acm.valueProperty.addListener((observable) -> {
                                resetColors();
                            });
                        }
                    }
                }
            }
        });
    }

}
