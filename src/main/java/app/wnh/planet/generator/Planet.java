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

/* A port of the code posted at: http://hjemmesider.diku.dk/~torbenm/Planet/ */
import app.wnh.planet.ColorSchemeModel;
import app.wnh.planet.PlanetModel;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Planet {

    private int BLACK = 0;
    private int WHITE = 1;
    private int BACK = 2;
    private int GRID = 3;
    private int OUTLINE1 = 4;
    private int OUTLINE2 = 5;
    private int LOWEST = 6;
    private int SEA = 7;
    private int LAND = 8;
    private int HIGHEST = 9;

    int nocols = 65536;
    public int rtable[] = new int[nocols];
    public int gtable[] = new int[nocols];
    public int btable[] = new int[nocols];

    boolean nonLinear;

    char view = 'm';
    /* T = tundra, G = grasslands, B = Taiga / boreal forest, D = desert,
   S = savanna, F = temperate forest, R = temperate rainforest,
   W = Xeric shrubland and dry forest, E = tropical dry forest,
   O = tropical rainforest, I = icecap */

 /* Whittaker diagram */
    String biomes[] = new String[]{
        "IIITTTTTGGGGGGGGDDDDDDDDDDDDDDDDDDDDDDDDDDDDD",
        "IIITTTTTGGGGGGGGDDDDGGDSDDSDDDDDDDDDDDDDDDDDD",
        "IITTTTTTTTTBGGGGGGGGGGGSSSSSSDDDDDDDDDDDDDDDD",
        "IITTTTTTTTBBBBBBGGGGGGGSSSSSSSSSWWWWWWWDDDDDD",
        "IITTTTTTTTBBBBBBGGGGGGGSSSSSSSSSSWWWWWWWWWWDD",
        "IIITTTTTTTBBBBBBFGGGGGGSSSSSSSSSSSWWWWWWWWWWW",
        "IIIITTTTTTBBBBBBFFGGGGGSSSSSSSSSSSWWWWWWWWWWW",
        "IIIIITTTTTBBBBBBFFFFGGGSSSSSSSSSSSWWWWWWWWWWW",
        "IIIIITTTTTBBBBBBBFFFFGGGSSSSSSSSSSSWWWWWWWWWW",
        "IIIIIITTTTBBBBBBBFFFFFFGGGSSSSSSSSWWWWWWWWWWW",
        "IIIIIIITTTBBBBBBBFFFFFFFFGGGSSSSSSWWWWWWWWWWW",
        "IIIIIIIITTBBBBBBBFFFFFFFFFFGGSSSSSWWWWWWWWWWW",
        "IIIIIIIIITBBBBBBBFFFFFFFFFFFFFSSSSWWWWWWWWWWW",
        "IIIIIIIIIITBBBBBBFFFFFFFFFFFFFFFSSEEEWWWWWWWW",
        "IIIIIIIIIITBBBBBBFFFFFFFFFFFFFFFFFFEEEEEEWWWW",
        "IIIIIIIIIIIBBBBBBFFFFFFFFFFFFFFFFFFEEEEEEEEWW",
        "IIIIIIIIIIIBBBBBBRFFFFFFFFFFFFFFFFFEEEEEEEEEE",
        "IIIIIIIIIIIIBBBBBBRFFFFFFFFFFFFFFFFEEEEEEEEEE",
        "IIIIIIIIIIIIIBBBBBRRRFFFFFFFFFFFFFFEEEEEEEEEE",
        "IIIIIIIIIIIIIIIBBBRRRRRFFFFFFFFFFFFEEEEEEEEEE",
        "IIIIIIIIIIIIIIIIIBRRRRRRRFFFFFFFFFFEEEEEEEEEE",
        "IIIIIIIIIIIIIIIIIRRRRRRRRRRFFFFFFFFEEEEEEEEEE",
        "IIIIIIIIIIIIIIIIIIRRRRRRRRRRRRFFFFFEEEEEEEEEE",
        "IIIIIIIIIIIIIIIIIIIRRRRRRRRRRRRRFRREEEEEEEEEE",
        "IIIIIIIIIIIIIIIIIIIIIRRRRRRRRRRRRRRRREEEEEEEE",
        "IIIIIIIIIIIIIIIIIIIIIIIRRRRRRRRRRRRRROOEEEEEE",
        "IIIIIIIIIIIIIIIIIIIIIIIIRRRRRRRRRRRROOOOOEEEE",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIRRRRRRRRRROOOOOOEEE",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIRRRRRRRRROOOOOOOEE",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIRRRRRRRROOOOOOOEE",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIRRRRRRROOOOOOOOE",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIRRRRROOOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIRROOOOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIROOOOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIROOOOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOO",
        "IIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIIOOOOOOO"
    };

    double PI = Math.PI;
    double DEG2RAD = Math.PI / 180;

    /* these three values can be changed to change world characteristica */
 /* initial altitude (slightly below sea level) */
    double M = -.02;
    /* weight for altitude difference */
    double dd1 = 0.45;
    /* power for altitude difference */
    double POWA = 1.0;
    /* weight for distance */
    double dd2 = 0.035;
    /* power for distance function */
    double POW = 0.47;

    /* Depth of subdivisions */
    int Depth;
    /* seeds */
    double r1, r2, r3, r4;
    double longi = 0.0, lat = 0.0, scale = 1.0;
    double vgrid = 0.0, hgrid = 0.0;

    /* flag for latitude based colour */
    int latic = 0;

    /* default map size */
    int width = 400; //800;
    int height = 400; // 600;

    /* colour array */
    short col[][];
    /* heightfield array */
    int heights[][];
    /* x,y,z arrays  (used for gridlines */
    double xxx[][], yyy[][], zzz[][];
    /* search map */
    int cl0[][] = new int[60][30];
    /* if 1, draw coastal outline */
    boolean do_outline = false;
    /* if 1, reduce map to black outline on white */
    boolean do_bw = false;
    /* if >0, # of contour lines */
    int contourLines = 0;
    /* if >0, # of coastal contour lines */
    int coastContourLines = 0;
    int outx[], outy[];

    int doshade = 0;
    int shade;
    /* shade array */
    short shades[][];
    /* angle of "light" on bumpmap
       with daylight shading, these two are
       longitude/latitude */
    double shade_angle = 150.0;
    double shade_angle2 = 20.0;
    double cla, sla, clo, slo, rseed = 0.123;

    /* if true, show temperatures based on latitude
			and altitude*/
    boolean temperature = false;
    double tempMin = 1000.0, tempMax = -1000.0;

    /* if true, calculate rainfall based on latitude
			and temperature */
    boolean rainfall = false;
    double rainMin = 1000.0, rainMax = -1000.0;

    /* approximate rain shadow */
    double rainShadow = 0.0;

    /* if true, make biome map */
    boolean makeBiomes = false;

    boolean matchMap = false;
    double matchSize = 0.1;

    /* For the vertices of the tetrahedron */
    Vertex tetra[] = new Vertex[4];

    double rotate1 = 0.0, rotate2 = 0.0;
    double cR1, sR1, cR2, sR2;
    private String mapFileName;
    public String colorFileName;
    public InputStream colorFileStream;
    public ColorSchemeModel colorSchemeModel;

    boolean transparent;

    private PlanetModel planetModel;
    public double waterPercent;

    public Planet() {
        setPlanetModel(PlanetModel.getDefaultModel());
    }

    private double dist2(Vertex a, Vertex b) {
        double abx, aby, abz;
        abx = a.x - b.x;
        aby = a.y - b.y;
        abz = a.z - b.z;
        return abx * abx + aby * aby + abz * abz;
    }

    private double log_2(double x) {
        return (Math.log(x) / Math.log(2.0));
    }

    public void start(String[] args) {
        double sq3 = Math.sqrt(3);

        for (int i = 0; i < 4; i++) {
            tetra[i] = new Vertex();
        }
        /* initialize vertices to slightly irregular tetrahedron */
        tetra[0].x = -sq3 - 0.20;
        tetra[0].y = -sq3 - 0.22;
        tetra[0].z = -sq3 - 0.23;

        tetra[1].x = -sq3 - 0.19;
        tetra[1].y = sq3 + 0.18;
        tetra[1].z = sq3 + 0.17;

        tetra[2].x = sq3 + 0.21;
        tetra[2].y = -sq3 - 0.24;
        tetra[2].z = sq3 + 0.15;

        tetra[3].x = sq3 + 0.24;
        tetra[3].y = sq3 + 0.22;
        tetra[3].z = -sq3 - 0.25;

        if (colorFileName != null) {
            readcolors(colorFileName);
        } else if (colorFileStream != null) {
            readcolors(colorFileStream);
        } else if (colorSchemeModel != null) {
            loadColors(colorSchemeModel);
        } else {
            throw new RuntimeException("Either colorfileName, colorFileStream, or colorSchemeModel must be assigned.");
        }

//        longi = 0.0;
//        lat = 0.0;
//        scale = 1.0;
//        rseed = 0.124;
//        view = 'm';
//        vgrid = hgrid = 0.0;
        longi = longi * DEG2RAD;
        lat = lat * DEG2RAD;

        sla = Math.sin(lat);
        cla = Math.cos(lat);
        slo = Math.sin(longi);
        clo = Math.cos(longi);

        rotate1 = -rotate1 * DEG2RAD;
        rotate2 = -rotate2 * DEG2RAD;

        sR1 = Math.sin(rotate1);
        cR1 = Math.cos(rotate1);
        sR2 = Math.sin(rotate2);
        cR2 = Math.cos(rotate2);

        for (int i = 0; i < 4; i++) {
            /* rotate around y axis */
            double tx = tetra[i].x;
            double ty = tetra[i].y;
            double tz = tetra[i].z;
            tetra[i].x = cR2 * tx + sR2 * tz;
            tetra[i].y = ty;
            tetra[i].z = -sR2 * tx + cR2 * tz;
        }

        for (int i = 0; i < 4; i++) {
            /* rotate around x axis */
            double tx = tetra[i].x;
            double ty = tetra[i].y;
            double tz = tetra[i].z;
            tetra[i].x = tx;
            tetra[i].y = cR1 * ty - sR1 * tz;
            tetra[i].z = sR1 * ty + cR1 * tz;
        }

        if (matchMap) {
            readmap();
        }

//  if (file_type == heightfield) {
//      heights = new int[width][height];
//  }
        col = new short[width][height];

        if (doshade > 0) {
            shades = new short[width][height];
        }

        if (vgrid != 0.0) {
            xxx = new double[width][height];
            zzz = new double[width][height];
        }

        if (hgrid != 0.0 || vgrid != 0.0) {
            yyy = new double[width][height];
        }

        if (view == 'c') {
            if (lat == 0) {
                view = 'm';
            }
            /* Conical approaches mercator when lat -> 0 */
            if (Math.abs(lat) >= PI - 0.000001) {
                view = 's';
            }
            /* Conical approaches stereo when lat -> +/- 90 */
        }

        Depth = 3 * ((int) (log_2(scale * height))) + 6;

        r1 = rseed;

        r1 = rand2(r1, r1);
        r2 = rand2(r1, r1);
        r3 = rand2(r1, r2);
        r4 = rand2(r2, r3);

        tetra[0].s = r1;
        tetra[1].s = r2;
        tetra[2].s = r3;
        tetra[3].s = r4;

        tetra[0].h = M;
        tetra[1].h = M;
        tetra[2].h = M;
        tetra[3].h = M;

        tetra[0].shadow = 0.0;
        tetra[1].shadow = 0.0;
        tetra[2].shadow = 0.0;
        tetra[3].shadow = 0.0;

//  if (debug && (view != 'f'))
//    fprintf(stderr, "+----+----+----+----+----+\n");
        switch (view) {

            case 'm':
                /* Mercator projection */
                mercator();
                break;

            case 'p':
                /* Peters projection (area preserving cylindrical) */
                peter();
                break;

            case 'q':
                /* Square projection (equidistant latitudes) */
                squarep();
                break;

            case 'M':
                /* Mollweide projection (area preserving) */
                mollweide();
                break;

            case 'S':
                /* Sinusoid projection (area preserving) */
                sinusoid();
                break;

            case 's':
                /* Stereographic projection */
                stereo();
                break;

            case 'o':
                /* Orthographic projection */
                orthographic();
                break;

            case 'g':
                /* Gnomonic projection */
                gnomonic();
                break;

            case 'i':
                /* Icosahedral projection */
                icosahedral();
                break;

            case 'a':
                /* Area preserving azimuthal projection */
                azimuth();
                break;

            case 'c':
                /* Conical projection (conformal) */
                conical();
                break;

            case 'h':
                /* heightfield (obsolete) */
                orthographic();
                break;
        }

        if (do_outline) {
            makeoutline(do_bw);
        }

        if (vgrid != 0.0) {
            /* draw longitudes */
            int i, j;
            for (i = 0; i < width - 1; i++) {
                for (j = 0; j < height - 1; j++) {
                    double t;
                    boolean g = false;
                    if (Math.abs(yyy[i][j]) == 1.0) {
                        g = true;
                    } else {
                        t = Math.floor((Math.atan2(xxx[i][j], zzz[i][j]) * 180 / PI + 360) / vgrid);
                        if (t != Math.floor((Math.atan2(xxx[i + 1][j], zzz[i + 1][j]) * 180 / PI + 360) / vgrid)) {
                            g = true;
                        }
                        if (t != Math.floor((Math.atan2(xxx[i][j + 1], zzz[i][j + 1]) * 180 / PI + 360) / vgrid)) {
                            g = true;
                        }
                    }
                    if (g) {
                        if (do_bw) {
                            col[i][j] = 0;
                        } else {
                            col[i][j] = (short) GRID;
                        }
                        if (doshade > 0) {
                            shades[i][j] = 255;
                        }
                    }
                }
            }
        }

        if (hgrid != 0.0) {
            /* draw latitudes */
            int i, j;
            for (i = 0; i < width - 1; i++) {
                for (j = 0; j < height - 1; j++) {
                    double t;
                    boolean g = false;
                    t = Math.floor((Math.asin(yyy[i][j]) * 180 / PI + 360) / hgrid);
                    if (t != Math.floor((Math.asin(yyy[i + 1][j]) * 180 / PI + 360) / hgrid)) {
                        g = true;
                    }
                    if (t != Math.floor((Math.asin(yyy[i][j + 1]) * 180 / PI + 360) / hgrid)) {
                        g = true;
                    }
                    if (g) {
                        if (do_bw) {
                            col[i][j] = 0;
                        } else {
                            col[i][j] = (short) GRID;
                        }
                        if (doshade > 0) {
                            shades[i][j] = 255;
                        }
                    }
                }
            }
        }

        if (doshade > 0) {
            smoothshades();
        }

//        if (debug) {
//            fprintf(stderr, "\n");
//        }
//
//        /* plot picture */
//        switch (file_type) {
//            case ppm:
//                if (do_bw) {
//                    printppmBW(outfile);
//                } else {
//                    printppm(outfile);
//                }
//                break;
//            case xpm:
//                if (do_bw) {
//                    printxpmBW(outfile);
//                } else {
//                    printxpm(outfile);
//                }
//                break;
//            case bmp:
//                if (do_bw) {
//                    printbmpBW(outfile);
//                } else {
//                    printbmp(outfile);
//                }
//                break;
//            case heightfield:
//                printheights(outfile);
//                break;
//        }
    }

    private void readcolors(String colorsName) {
        if (colorsName != null) {
            try {
                FileInputStream colorStream = new FileInputStream(colorsName);
                readcolors(colorFileStream);
            } catch (RuntimeException | IOException ex) {
                throw new RuntimeException("Unknown exception trying to read colors file " + colorsName);
            }
        }
    }

    private void readcolors(InputStream colorStream) {
        int crow, cNum = 0, oldcNum = 0, i;

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
        try (InputStreamReader fr = new InputStreamReader(colorStream); BufferedReader br = new BufferedReader(fr)) {
            String line = br.readLine();
            while (line != null) {
                if (line.isBlank()) {
                    line = br.readLine();
                    continue;
                }
                oldcNum = cNum;
                /* remember last colour number */
                Scanner s = new Scanner(line);
                cNum = s.nextInt();
                int rValue = s.nextInt();
                int gValue = s.nextInt();
                int bValue = s.nextInt();
                if (cNum < oldcNum) {
                    cNum = oldcNum;
                }
                if (cNum > 65535) {
                    cNum = 65535;
                }
                rtable[cNum] = rValue;
                gtable[cNum] = gValue;
                btable[cNum] = bValue;
                /* interpolate colours between oldcNum and cNum */
                for (i = oldcNum + 1; i < cNum; i++) {
                    rtable[i] = (rtable[oldcNum] * (cNum - i) + rtable[cNum] * (i - oldcNum))
                            / (cNum - oldcNum + 1);
                    gtable[i] = (gtable[oldcNum] * (cNum - i) + gtable[cNum] * (i - oldcNum))
                            / (cNum - oldcNum + 1);
                    btable[i] = (btable[oldcNum] * (cNum - i) + btable[cNum] * (i - oldcNum))
                            / (cNum - oldcNum + 1);
                }
                line = br.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error trying to read color stream", ex);
        }

        nocols = cNum + 1;
        if (nocols < 10) {
            nocols = 10;
        }

        HIGHEST = nocols - 1;
        SEA = (HIGHEST + LOWEST) / 2;
        LAND = SEA + 1;

        for (i = cNum + 1; i < nocols; i++) {
            /* fill up rest of colour table with last read colour */
            rtable[i] = rtable[cNum];
            gtable[i] = gtable[cNum];
            btable[i] = btable[cNum];
        }

        if (makeBiomes) {
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
    }

    private void loadColors(ColorSchemeModel colorSchemeModel) {
        colorSchemeModel.setMakeBiomes(makeBiomes);
        rtable = colorSchemeModel.getRtable();
        gtable = colorSchemeModel.getGtable();
        btable = colorSchemeModel.getBtable();

        HIGHEST = colorSchemeModel.getHighest();
        SEA = colorSchemeModel.getSea();
        LAND = colorSchemeModel.getLand();
    }

    private void readmap() {
        int i, j;
        double y;
        char c;
        int mapWidth, mapHeight;

        List<char[]> mapLines = new ArrayList<>();
        try (FileReader fr = new FileReader(mapFileName); BufferedReader br = new BufferedReader(fr)) {
            String line = br.readLine();
            while (line != null) {
                if (line.length() != 48) {
                    throw new RuntimeException("Invalid map file format, must have 24 lines of 48 characters.");
                }
                mapLines.add(line.toCharArray());
                line = br.readLine();
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error reading map file " + mapFileName, ex);
        }

        if (mapLines.size() != 24) {
            throw new RuntimeException("Invalid map file format, must have 24 lines of 48 characters.");
        }

        mapWidth = 48;
        mapHeight = 24;
        for (j = 0; j < mapHeight; j += 2) {
            for (i = 0; i < mapWidth; i += 2) {
                c = mapLines.get(j)[i];
                switch (c) {
                    case '.':
                        cl0[i][j] = -8;
                        break;
                    case ',':
                        cl0[i][j] = -6;
                        break;
                    case ':':
                        cl0[i][j] = -4;
                        break;
                    case ';':
                        cl0[i][j] = -2;
                        break;
                    case '-':
                        cl0[i][j] = 0;
                        break;
                    case '*':
                        cl0[i][j] = 2;
                        break;
                    case 'o':
                        cl0[i][j] = 4;
                        break;
                    case 'O':
                        cl0[i][j] = 6;
                        break;
                    case '@':
                        cl0[i][j] = 8;
                        break;
                    default:
                        throw new RuntimeException("Wrong map symbol: " + c);
                }
            }
        }
        /* interpolate */
        for (j = 1; j < mapHeight; j += 2) {
            for (i = 0; i < mapWidth; i += 2) {
                cl0[i][j] = (cl0[i][j - 1] + cl0[i][(j + 1)]) / 2;
            }
        }
        for (j = 0; j < mapHeight; j++) {
            for (i = 1; i < mapWidth; i += 2) {
                cl0[i][j] = (cl0[i - 1][j] + cl0[(i + 1) % mapWidth][j]) / 2;
            }
        }
    }

    private double rand2(double p, double q) {
        double r;
        r = (p + 3.14159265) * (q + 3.14159265);
        return (2. * (r - (int) r) - 1.);
    }

    private void mercator() {
        double y, scale1, cos2, theta1;
        int i, j, k;

        y = Math.sin(lat);
        y = (1.0 + y) / (1.0 - y);
        y = 0.5 * Math.log(y);
        k = (int) (0.5 * y * width * scale / PI + 0.5);
        for (j = 0; j < height; j++) {
//    if (debug && ((j % (height/25)) == 0))
//      {fprintf (stderr, "%c", view); fflush(stderr);}
            y = PI * (2.0 * (j - k) - height) / width / scale;
            y = Math.exp(2. * y);
            y = (y - 1.) / (y + 1.);
            scale1 = scale * width / height / Math.sqrt(1.0 - y * y) / PI;
            cos2 = Math.sqrt(1.0 - y * y);
            Depth = 3 * ((int) (log_2(scale1 * height))) + 3;
            for (i = 0; i < width; i++) {
                theta1 = longi - 0.5 * PI + PI * (2.0 * i - width) / width / scale;
                planet0(Math.cos(theta1) * cos2, y, -Math.sin(theta1) * cos2, i, j);
            }
        }
    }

    private void peter() {
        double y, cos2, theta1, scale1;
        int k, i, j, water, land;

        y = 2.0 * Math.sin(lat);
        k = (int) (0.5 * y * width * scale / PI + 0.5);
        water = land = 0;
        for (j = 0; j < height; j++) {
            y = 0.5 * PI * (2.0 * (j - k) - height) / width / scale;
            if (Math.abs(y) > 1.0) {
                for (i = 0; i < width; i++) {
                    col[i][j] = (short) BACK;
                    if (doshade > 0) {
                        shades[i][j] = 255;
                    }
                }
            } else {
                cos2 = Math.sqrt(1.0 - y * y);
                if (cos2 > 0.0) {
                    scale1 = scale * width / height / cos2 / PI;
                    Depth = 3 * ((int) (log_2(scale1 * height))) + 3;
                    for (i = 0; i < width; i++) {
                        theta1 = longi - 0.5 * PI + PI * (2.0 * i - width) / width / scale;
                        planet0(Math.cos(theta1) * cos2, y, -Math.sin(theta1) * cos2, i, j);
                        if (col[i][j] < LAND) {
                            water++;
                        } else {
                            land++;
                        }
                    }
                }
            }
        }
        waterPercent = (water * 1.0 / (water + land));
    }

    private void squarep() {
        double y, scale1, theta1, cos2;
        int k, i, j;

        k = (int) (0.5 * lat * width * scale / PI + 0.5);
        for (j = 0; j < height; j++) {
            y = (2.0 * (j - k) - height) / width / scale * PI;
            if (Math.abs(y + y) > PI) {
                for (i = 0; i < width; i++) {
                    col[i][j] = (short) BACK;
                    if (doshade > 0) {
                        shades[i][j] = 255;
                    }
                }
            } else {
                cos2 = Math.cos(y);
                if (cos2 > 0.0) {
                    scale1 = scale * width / height / cos2 / PI;
                    Depth = 3 * ((int) (log_2(scale1 * height))) + 3;
                    for (i = 0; i < width; i++) {
                        theta1 = longi - 0.5 * PI + PI * (2.0 * i - width) / width / scale;
                        planet0(Math.cos(theta1) * cos2, Math.sin(y), -Math.sin(theta1) * cos2, i, j);
                    }
                }
            }
        }
    }

    private void mollweide() {
        double y, y1, zz, scale1, cos2, theta1, theta2;
        int i, j, i1 = 1, k;

        for (j = 0; j < height; j++) {
            y1 = 2 * (2.0 * j - height) / width / scale;
            if (Math.abs(y1) >= 1.0) {
                for (i = 0; i < width; i++) {
                    col[i][j] = (short) BACK;
                    if (doshade > 0) {
                        shades[i][j] = 255;
                    }
                }
            } else {
                zz = Math.sqrt(1.0 - y1 * y1);
                y = 2.0 / PI * (y1 * zz + Math.asin(y1));
                cos2 = Math.sqrt(1.0 - y * y);
                if (cos2 > 0.0) {
                    scale1 = scale * width / height / cos2 / PI;
                    Depth = 3 * ((int) (log_2(scale1 * height))) + 3;
                    for (i = 0; i < width; i++) {
                        theta1 = PI / zz * (2.0 * i - width) / width / scale;
                        if (Math.abs(theta1) > PI) {
                            col[i][j] = (short) BACK;
                            if (doshade > 0) {
                                shades[i][j] = 255;
                            }
                        } else {
                            double x2, y2, z2, x3, y3, z3;
                            theta1 += -0.5 * PI;
                            x2 = Math.cos(theta1) * cos2;
                            y2 = y;
                            z2 = -Math.sin(theta1) * cos2;
                            x3 = clo * x2 + slo * sla * y2 + slo * cla * z2;
                            y3 = cla * y2 - sla * z2;
                            z3 = -slo * x2 + clo * sla * y2 + clo * cla * z2;

                            planet0(x3, y3, z3, i, j);
                        }
                    }
                }
            }
        }
    }

    private void sinusoid() {
        double y, theta1, theta2, cos2, l1, i1, scale1;
        int k, i, j, l, c;

        k = (int) (lat * width * scale / PI + 0.5);
        for (j = 0; j < height; j++) {
            y = (2.0 * (j - k) - height) / width / scale * PI;
            if (Math.abs(y + y) > PI) {
                for (i = 0; i < width; i++) {
                    col[i][j] = (short) BACK;
                    if (doshade > 0) {
                        shades[i][j] = 255;
                    }
                }
            } else {
                cos2 = Math.cos(y);
                if (cos2 > 0.0) {
                    scale1 = scale * width / height / cos2 / PI;
                    Depth = 3 * ((int) (log_2(scale1 * height))) + 3;
                    for (i = 0; i < width; i++) {
                        l = (int) (i * 12 / width / scale);
                        l1 = l * width * scale / 12.0;
                        i1 = i - l1;
                        theta2 = longi - 0.5 * PI + PI * (2.0 * l1 - width) / width / scale;
                        theta1 = (PI * (2.0 * i1 - width * scale / 12.0) / width / scale) / cos2;
                        if (Math.abs(theta1) > PI / 12.0) {
                            col[i][j] = (short) BACK;
                            if (doshade > 0) {
                                shades[i][j] = 255;
                            }
                        } else {
                            planet0(Math.cos(theta1 + theta2) * cos2, Math.sin(y), -Math.sin(theta1 + theta2) * cos2,
                                    i, j);
                        }
                    }
                }
            }
        }
    }

    private void stereo() {
        double x, y, ymin, ymax, z, zz, x1, y1, z1, theta1, theta2;
        int i, j;

        ymin = 2.0;
        ymax = -2.0;
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                x = (2.0 * i - width) / height / scale;
                y = (2.0 * j - height) / height / scale;
                z = x * x + y * y;
                zz = 0.25 * (4.0 + z);
                x = x / zz;
                y = y / zz;
                z = (1.0 - 0.25 * z) / zz;
                x1 = clo * x + slo * sla * y + slo * cla * z;
                y1 = cla * y - sla * z;
                z1 = -slo * x + clo * sla * y + clo * cla * z;
                if (y1 < ymin) {
                    ymin = y1;
                }
                if (y1 > ymax) {
                    ymax = y1;
                }

                /* for level-of-detail effect:
         Depth = 3*((int)(log_2(scale*height)/(1.0+x1*x1+y1*y1)))+6; */
                planet0(x1, y1, z1, i, j);
            }
        }
    }

    private void orthographic() {
        double x, y, z, x1, y1, z1, ymin, ymax, theta1, theta2, zz;
        int i, j;
        ymin = 2.0;
        ymax = -2.0;
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                x = (2.0 * i - width) / height / scale;
                y = (2.0 * j - height) / height / scale;
                if (x * x + y * y > 1.0) {
                    col[i][j] = (short) BACK;
                    if (doshade > 0) {
                        shades[i][j] = 255;
                    }
                } else {
                    z = Math.sqrt(1.0 - x * x - y * y);
                    x1 = clo * x + slo * sla * y + slo * cla * z;
                    y1 = cla * y - sla * z;
                    z1 = -slo * x + clo * sla * y + clo * cla * z;
                    if (y1 < ymin) {
                        ymin = y1;
                    }
                    if (y1 > ymax) {
                        ymax = y1;
                    }
                    planet0(x1, y1, z1, i, j);
                }
            }
        }
    }

    private void gnomonic() {
        double x, y, z, x1, y1, z1, zz, theta1, theta2, ymin, ymax;
        int i, j;

        ymin = 2.0;
        ymax = -2.0;
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                x = (2.0 * i - width) / height / scale;
                y = (2.0 * j - height) / height / scale;
                zz = Math.sqrt(1.0 / (1.0 + x * x + y * y));
                x = x * zz;
                y = y * zz;
                z = Math.sqrt(1.0 - x * x - y * y);
                x1 = clo * x + slo * sla * y + slo * cla * z;
                y1 = cla * y - sla * z;
                z1 = -slo * x + clo * sla * y + clo * cla * z;
                if (y1 < ymin) {
                    ymin = y1;
                }
                if (y1 > ymax) {
                    ymax = y1;
                }
                planet0(x1, y1, z1, i, j);
            }
        }
    }

    private void icosahedral() {
        double x, y, z, x1, y1, z1, zz, theta1, theta2, ymin, ymax;
        int i, j;
        double lat1, longi1, sla, cla, slo, clo, x0, y0, sq3_4, sq3;
        double L1, L2, S;

        ymin = 2.0;
        ymax = -2.0;
        sq3 = Math.sqrt(3.0);
        L1 = 10.812317;
        /* theoretically 10.9715145571469; */
        L2 = -52.622632;
        /* theoretically -48.3100310579607; */
        S = 55.6;
        /* found by experimentation */
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {

                x0 = 198.0 * (2.0 * i - width) / width / scale - 36;
                y0 = 198.0 * (2.0 * j - height) / width / scale - lat / DEG2RAD;

                longi1 = 0.0;
                lat1 = 500.0;
                if (y0 / sq3 <= 18.0 && y0 / sq3 >= -18.0) {
                    /* middle row of triangles */
 /* upward triangles */
                    if (x0 - y0 / sq3 < 144.0 && x0 + y0 / sq3 >= 108.0) {
                        lat1 = -L1;
                        longi1 = 126.0;
                    } else if (x0 - y0 / sq3 < 72.0 && x0 + y0 / sq3 >= 36.0) {
                        lat1 = -L1;
                        longi1 = 54.0;
                    } else if (x0 - y0 / sq3 < 0.0 && x0 + y0 / sq3 >= -36.0) {
                        lat1 = -L1;
                        longi1 = -18.0;
                    } else if (x0 - y0 / sq3 < -72.0 && x0 + y0 / sq3 >= -108.0) {
                        lat1 = -L1;
                        longi1 = -90.0;
                    } else if (x0 - y0 / sq3 < -144.0 && x0 + y0 / sq3 >= -180.0) {
                        lat1 = -L1;
                        longi1 = -162.0;
                    } /* downward triangles */ else if (x0 + y0 / sq3 < 108.0 && x0 - y0 / sq3 >= 72.0) {
                        lat1 = L1;
                        longi1 = 90.0;
                    } else if (x0 + y0 / sq3 < 36.0 && x0 - y0 / sq3 >= 0.0) {
                        lat1 = L1;
                        longi1 = 18.0;
                    } else if (x0 + y0 / sq3 < -36.0 && x0 - y0 / sq3 >= -72.0) {
                        lat1 = L1;
                        longi1 = -54.0;
                    } else if (x0 + y0 / sq3 < -108.0 && x0 - y0 / sq3 >= -144.0) {
                        lat1 = L1;
                        longi1 = -126.0;
                    } else if (x0 + y0 / sq3 < -180.0 && x0 - y0 / sq3 >= -216.0) {
                        lat1 = L1;
                        longi1 = -198.0;
                    }
                }

                if (y0 / sq3 > 18.0) {
                    /* bottom row of triangles */
                    if (x0 + y0 / sq3 < 180.0 && x0 - y0 / sq3 >= 72.0) {
                        lat1 = L2;
                        longi1 = 126.0;
                    } else if (x0 + y0 / sq3 < 108.0 && x0 - y0 / sq3 >= 0.0) {
                        lat1 = L2;
                        longi1 = 54.0;
                    } else if (x0 + y0 / sq3 < 36.0 && x0 - y0 / sq3 >= -72.0) {
                        lat1 = L2;
                        longi1 = -18.0;
                    } else if (x0 + y0 / sq3 < -36.0 && x0 - y0 / sq3 >= -144.0) {
                        lat1 = L2;
                        longi1 = -90.0;
                    } else if (x0 + y0 / sq3 < -108.0 && x0 - y0 / sq3 >= -216.0) {
                        lat1 = L2;
                        longi1 = -162.0;
                    }
                }
                if (y0 / sq3 < -18.0) {
                    /* top row of triangles */
                    if (x0 - y0 / sq3 < 144.0 && x0 + y0 / sq3 >= 36.0) {
                        lat1 = -L2;
                        longi1 = 90.0;
                    } else if (x0 - y0 / sq3 < 72.0 && x0 + y0 / sq3 >= -36.0) {
                        lat1 = -L2;
                        longi1 = 18.0;
                    } else if (x0 - y0 / sq3 < 0.0 && x0 + y0 / sq3 >= -108.0) {
                        lat1 = -L2;
                        longi1 = -54.0;
                    } else if (x0 - y0 / sq3 < -72.0 && x0 + y0 / sq3 >= -180.0) {
                        lat1 = -L2;
                        longi1 = -126.0;
                    } else if (x0 - y0 / sq3 < -144.0 && x0 + y0 / sq3 >= -252.0) {
                        lat1 = -L2;
                        longi1 = -198.0;
                    }
                }

                if (lat1 > 400.0) {
                    col[i][j] = (short) BACK;
                    if (doshade > 0) {
                        shades[i][j] = 255;
                    }
                } else {
                    x = (x0 - longi1) / S;
                    y = (y0 + lat1) / S;

                    longi1 = longi1 * DEG2RAD - longi;
                    lat1 = lat1 * DEG2RAD;

                    sla = Math.sin(lat1);
                    cla = Math.cos(lat1);
                    slo = Math.sin(longi1);
                    clo = Math.cos(longi1);

                    zz = Math.sqrt(1.0 / (1.0 + x * x + y * y));
                    x = x * zz;
                    y = y * zz;
                    z = Math.sqrt(1.0 - x * x - y * y);
                    x1 = clo * x + slo * sla * y + slo * cla * z;
                    y1 = cla * y - sla * z;
                    z1 = -slo * x + clo * sla * y + clo * cla * z;

                    if (y1 < ymin) {
                        ymin = y1;
                    }
                    if (y1 > ymax) {
                        ymax = y1;
                    }
                    planet0(x1, y1, z1, i, j);
                }
            }
        }
    }

    private void azimuth() {
        double x, y, z, x1, y1, z1, zz, theta1, theta2, ymin, ymax;
        int i, j;

        ymin = 2.0;
        ymax = -2.0;
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                x = (2.0 * i - width) / height / scale;
                y = (2.0 * j - height) / height / scale;
                zz = x * x + y * y;
                z = 1.0 - 0.5 * zz;
                if (z < -1.0) {
                    col[i][j] = (short) BACK;
                    if (doshade > 0) {
                        shades[i][j] = 255;
                    }
                } else {
                    zz = Math.sqrt(1.0 - 0.25 * zz);
                    x = x * zz;
                    y = y * zz;
                    x1 = clo * x + slo * sla * y + slo * cla * z;
                    y1 = cla * y - sla * z;
                    z1 = -slo * x + clo * sla * y + clo * cla * z;
                    if (y1 < ymin) {
                        ymin = y1;
                    }
                    if (y1 > ymax) {
                        ymax = y1;
                    }
                    planet0(x1, y1, z1, i, j);
                }
            }
        }
    }

    private void conical() {
        double k1, c, y2, x, y, zz, x1, y1, z1, theta1, theta2, ymin, ymax, cos2;
        int i, j;

        ymin = 2.0;
        ymax = -2.0;
        if (lat > 0) {
            k1 = 1.0 / Math.sin(lat);
            c = k1 * k1;
            y2 = Math.sqrt(c * (1.0 - Math.sin(lat / k1)) / (1.0 + Math.sin(lat / k1)));
            for (j = 0; j < height; j++) {
                for (i = 0; i < width; i++) {
                    x = (2.0 * i - width) / height / scale;
                    y = (2.0 * j - height) / height / scale + y2;
                    zz = x * x + y * y;
                    if (zz == 0.0) {
                        theta1 = 0.0;
                    } else {
                        theta1 = k1 * Math.atan2(x, y);
                    }
                    if (theta1 < -PI || theta1 > PI) {
                        col[i][j] = (short) BACK;
                        if (doshade > 0) {
                            shades[i][j] = 255;
                        }
                    } else {
                        theta1 += longi - 0.5 * PI;
                        /* theta1 is longitude */
                        theta2 = k1 * Math.asin((zz - c) / (zz + c));
                        /* theta2 is latitude */
                        if (theta2 > 0.5 * PI || theta2 < -0.5 * PI) {
                            col[i][j] = (short) BACK;
                            if (doshade > 0) {
                                shades[i][j] = 255;
                            }
                        } else {
                            cos2 = Math.cos(theta2);
                            y = Math.sin(theta2);
                            if (y < ymin) {
                                ymin = y;
                            }
                            if (y > ymax) {
                                ymax = y;
                            }
                            planet0(Math.cos(theta1) * cos2, y, -Math.sin(theta1) * cos2, i, j);
                        }
                    }
                }
            }
        } else {
            k1 = 1.0 / Math.sin(lat);
            c = k1 * k1;
            y2 = Math.sqrt(c * (1.0 - Math.sin(lat / k1)) / (1.0 + Math.sin(lat / k1)));
            for (j = 0; j < height; j++) {
                for (i = 0; i < width; i++) {
                    x = (2.0 * i - width) / height / scale;
                    y = (2.0 * j - height) / height / scale - y2;
                    zz = x * x + y * y;
                    if (zz == 0.0) {
                        theta1 = 0.0;
                    } else {
                        theta1 = -k1 * Math.atan2(x, -y);
                    }
                    if (theta1 < -PI || theta1 > PI) {
                        col[i][j] = (short) BACK;
                        if (doshade > 0) {
                            shades[i][j] = 255;
                        }
                    } else {
                        theta1 += longi - 0.5 * PI;
                        /* theta1 is longitude */
                        theta2 = k1 * Math.asin((zz - c) / (zz + c));
                        /* theta2 is latitude */
                        if (theta2 > 0.5 * PI || theta2 < -0.5 * PI) {
                            col[i][j] = (short) BACK;
                            if (doshade > 0) {
                                shades[i][j] = 255;
                            }
                        } else {
                            cos2 = Math.cos(theta2);
                            y = Math.sin(theta2);
                            if (y < ymin) {
                                ymin = y;
                            }
                            if (y > ymax) {
                                ymax = y;
                            }
                            planet0(Math.cos(theta1) * cos2, y, -Math.sin(theta1) * cos2, i, j);
                        }
                    }
                }
            }
        }
    }

    private void makeoutline(boolean do_bw) {
        int i, j, k, t;

        outx = new int[width * height];
        outy = new int[width * height];
        k = 0;
        for (i = 1; i < width - 1; i++) {
            for (j = 1; j < height - 1; j++) {
                if ((col[i][j] >= LOWEST && col[i][j] <= SEA)
                        && (col[i - 1][j] >= LAND || col[i + 1][j] >= LAND
                        || col[i][j - 1] >= LAND || col[i][j + 1] >= LAND
                        || col[i - 1][j - 1] >= LAND || col[i - 1][j + 1] >= LAND
                        || col[i + 1][j - 1] >= LAND || col[i + 1][j + 1] >= LAND)) {
                    /* if point is sea and any neighbour is not, add to outline */
                    outx[k] = i;
                    outy[k++] = j;
                }
            }
        }

        int contourstep = 0;

        if (contourLines > 0) {
            contourstep = (HIGHEST - LAND) / (contourLines + 1);
            for (i = 1; i < width - 1; i++) {
                for (j = 1; j < height - 1; j++) {
                    t = (col[i][j] - LAND) / contourstep;
                    if (col[i][j] >= LAND
                            && ((col[i - 1][j] - LAND) / contourstep > t
                            || (col[i + 1][j] - LAND) / contourstep > t
                            || (col[i][j - 1] - LAND) / contourstep > t
                            || (col[i][j + 1] - LAND) / contourstep > t)) {
                        /* if point is at contour line and any neighbour is higher */
                        outx[k] = i;
                        outy[k++] = j;
                    }
                }
            }
        }
        if (coastContourLines > 0) {
            contourstep = (LAND - LOWEST) / 20;
            for (i = 1; i < width - 1; i++) {
                for (j = 1; j < height - 1; j++) {
                    t = (col[i][j] - LAND) / contourstep;
                    if (col[i][j] <= SEA && t >= -coastContourLines
                            && ((col[i - 1][j] - LAND) / contourstep > t
                            || (col[i + 1][j] - LAND) / contourstep > t
                            || (col[i][j - 1] - LAND) / contourstep > t
                            || (col[i][j + 1] - LAND) / contourstep > t)) {
                        /* if point is at contour line and any neighbour is higher */
                        outx[k] = i;
                        outy[k++] = j;
                    }
                }
            }
        }
        if (do_bw) /* if outline only, clear colours */ {
            for (i = 0; i < width; i++) {
                for (j = 0; j < height; j++) {
                    if (col[i][j] >= LOWEST) {
                        col[i][j] = (short) WHITE;
                    } else {
                        col[i][j] = (short) BLACK;
                    }
                }
            }
        }
        /* draw outline (in black if outline only) */
        contourstep = (HIGHEST - LAND) / (contourLines + 1);
        for (j = 0; j < k; j++) {
            if (do_bw) {
                t = BLACK;
            } else {
                t = col[outx[j]][outy[j]];
                if (t != OUTLINE1 && t != OUTLINE2) {
                    if (contourLines > 0 && t >= LAND) {
                        if (((t - LAND) / contourstep) % 2 == 1) {
                            t = OUTLINE1;
                        } else {
                            t = OUTLINE2;
                        }
                    } else if (t <= SEA) {
                        t = OUTLINE1;
                    }
                }
            }
            col[outx[j]][outy[j]] = (short) t;
        }
    }

    private void smoothshades() {
        int i, j;

        for (i = 0; i < width - 2; i++) {
            for (j = 0; j < height - 2; j++) {
                shades[i][j] = (short) ((4 * shades[i][j] + 2 * shades[i][j + 1]
                        + 2 * shades[i + 1][j] + shades[i + 1][j + 1] + 4) / 9);
            }
        }
    }

    private void planet0(double x, double y, double z, int i, int j) {
        double alt, y2, sun, temp, rain;
        int colour;

        alt = planet1(x, y, z);

        /* calculate temperature based on altitude and latitude */
 /* scale: -0.1 to 0.1 corresponds to -30 to +30 degrees Celsius */
 /* approximate amount of sunlight at latitude ranged from 0.1 to 1.1 */
        sun = Math.sqrt(1.0 - y * y);
        /* deep water colder */
        if (alt < 0) {
            temp = sun / 8.0 + alt * 0.3;
        } else {
            /* high altitudes colder */
            temp = sun / 8.0 - alt * 1.2;
        }

        if (temp < tempMin && alt > 0) {
            tempMin = temp;
        }
        if (temp > tempMax && alt > 0) {
            tempMax = temp;
        }
        if (temperature) {
            alt = temp - 0.05;
        }

        /* calculate rainfall based on temperature and latitude */
 /* rainfall approximately proportional to temperature but reduced
           near horse latitudes (+/- 30 degrees, y=0.5) and reduced for
           rain shadow */
        y2 = Math.abs(y) - 0.5;
        rain = temp * 0.65 + 0.1 - 0.011 / (y2 * y2 + 0.1);
        rain += 0.03 * rainShadow;
        if (rain < 0.0) {
            rain = 0.0;
        }

        if (rain < rainMin && alt > 0) {
            rainMin = rain;
        }
        if (rain > rainMax && alt > 0) {
            rainMax = rain;
        }

        if (rainfall) {
            alt = rain - 0.02;
        }

        if (nonLinear) {
            /* non-linear scaling to make flatter near sea level */
            alt = alt * alt * alt * 300;
        }
        /* store height for heightfield */
//  if (file_type == heightfield) heights[i][j] = 10000000*alt;

        y2 = y * y;
        y2 = y2 * y2;
        y2 = y2 * y2;

        /* calculate colour */
        if (makeBiomes) {
            /* make biome colours */
            int tt = Math.min(44, Math.max(0, (int) (rain * 300.0 - 9)));
            int rr = Math.min(44, Math.max(0, (int) (temp * 300.0 + 10)));
            char bio = biomes[tt].charAt(rr);
            if (alt <= 0.0) {
                colour = SEA + (int) ((SEA - LOWEST + 1) * (10 * alt));
                if (colour < LOWEST) {
                    colour = LOWEST;
                }
            } else {
                /* from LAND+2 to LAND+23 */
                colour = bio - 64 + LAND;
            }
        } else if (alt <= 0.0) {
            /* if below sea level then */
            if (latic > 0 && y2 + alt >= 1.0 - 0.02 * latic * latic) {
                colour = HIGHEST;
                /* icecap if close to poles */
            } else {
                colour = SEA + (int) ((SEA - LOWEST + 1) * (10 * alt));
                if (colour < LOWEST) {
                    colour = LOWEST;
                }
            }
        } else {
            if (latic > 0) {
                alt += 0.1 * latic * y2;
                /* altitude adjusted with latitude */
            }
            if (alt >= 0.1) /* if high then */ {
                colour = HIGHEST;
            } else {
                colour = LAND + (int) ((HIGHEST - LAND + 1) * (10 * alt));
                if (colour > HIGHEST) {
                    colour = HIGHEST;
                }
            }
        }

        /* store colour */
        col[i][j] = (short) colour;

        /* store (x,y,z) coordinates for grid drawing */
        if (vgrid != 0.0) {
            xxx[i][j] = x;
            zzz[i][j] = z;
        }
        if (hgrid != 0.0 || vgrid != 0.0) {
            yyy[i][j] = y;
        }

        /* store shading info */
        if (doshade > 0) {
            shades[i][j] = (short) shade;
        }

        return;
    }

    Vertex ssa = new Vertex(), ssb = new Vertex(), ssc = new Vertex(), ssd = new Vertex();

    /* a-d Tetrahedron vertices
       x,y,z goal point
       level levels to go
     */
    private double planet(Vertex a, Vertex b, Vertex c, Vertex d, double x, double y, double z, int level) {
        Vertex e = new Vertex();
        double lab, lac, lad, lbc, lbd, lcd, maxlength;
        double es1, es2, es3;
        double eax, eay, eaz, epx, epy, epz;
        double ecx, ecy, ecz, edx, edy, edz;
        double x1, y1, z1, x2, y2, z2, l1, tmp;

        if (level > 0) {

            /* make sure ab is longest edge */
            lab = dist2(a, b);
            lac = dist2(a, c);
            lad = dist2(a, d);
            lbc = dist2(b, c);
            lbd = dist2(b, d);
            lcd = dist2(c, d);

            maxlength = lab;
            if (lac > maxlength) {
                maxlength = lac;
            }
            if (lad > maxlength) {
                maxlength = lad;
            }
            if (lbc > maxlength) {
                maxlength = lbc;
            }
            if (lbd > maxlength) {
                maxlength = lbd;
            }
            if (lcd > maxlength) {
                maxlength = lcd;
            }

            if (lac == maxlength) {
                return (planet(a, c, b, d, x, y, z, level));
            }
            if (lad == maxlength) {
                return (planet(a, d, b, c, x, y, z, level));
            }
            if (lbc == maxlength) {
                return (planet(b, c, a, d, x, y, z, level));
            }
            if (lbd == maxlength) {
                return (planet(b, d, a, c, x, y, z, level));
            }
            if (lcd == maxlength) {
                return (planet(c, d, a, b, x, y, z, level));
            }

            if (level == 11) {
                /* save tetrahedron for caching */
                ssa = new Vertex(a);
                ssb = new Vertex(b);
                ssc = new Vertex(c);
                ssd = new Vertex(d);
            }

            /* ab is longest, so cut ab */
            e.s = rand2(a.s, b.s);
            es1 = rand2(e.s, e.s);
            es2 = 0.5 + 0.1 * rand2(es1, es1);
            /* find cut point */
            es3 = 1.0 - es2;

            if (a.s < b.s) {
                e.x = es2 * a.x + es3 * b.x;
                e.y = es2 * a.y + es3 * b.y;
                e.z = es2 * a.z + es3 * b.z;
            } else if (a.s > b.s) {
                e.x = es3 * a.x + es2 * b.x;
                e.y = es3 * a.y + es2 * b.y;
                e.z = es3 * a.z + es2 * b.z;
            } else {
                /* as==bs, very unlikely to ever happen */
                e.x = 0.5 * a.x + 0.5 * b.x;
                e.y = 0.5 * a.y + 0.5 * b.y;
                e.z = 0.5 * a.z + 0.5 * b.z;
            }

            /* new altitude is: */
            if (matchMap && lab > matchSize) {
                /* use map height */
                double l, xx, yy;
                l = Math.sqrt(e.x * e.x + e.y * e.y + e.z * e.z);
                yy = Math.asin(e.y / l) * 23 / PI + 11.5;
                xx = Math.atan2(e.x, e.z) * 23.5 / PI + 23.5;
                e.h = cl0[(int) (xx + 0.5)][(int) (yy + 0.5)] * 0.1 / 8.0;
            } else {
                if (lab > 1.0) {
                    lab = Math.pow(lab, 0.5);
                }
                /* decrease contribution for very long distances */
                e.h = 0.5 * (a.h + b.h) /* average of end points */
                        + e.s * dd1 * Math.pow(Math.abs(a.h - b.h), POWA)
                        /* plus contribution for altitude diff */
                        + es1 * dd2 * Math.pow(lab, POW);
                /* plus contribution for distance */
            }

            /* calculate approximate rain shadow for new point */
            if (e.h <= 0.0 || !(rainfall || makeBiomes)) {
                e.shadow = 0.0;
            } else {
                x1 = 0.5 * (a.x + b.x);
                x1 = a.h * (x1 - a.x) + b.h * (x1 - b.x);
                y1 = 0.5 * (a.y + b.y);
                y1 = a.h * (y1 - a.y) + b.h * (y1 - b.y);
                z1 = 0.5 * (a.z + b.z);
                z1 = a.h * (z1 - a.z) + b.h * (z1 - b.z);
                l1 = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
                if (l1 == 0.0) {
                    l1 = 1.0;
                }
                tmp = Math.sqrt(1.0 - y * y);
                if (tmp < 0.0001) {
                    tmp = 0.0001;
                }
                x2 = x * x1 + y * y1 + z * z1;
                z2 = -z / tmp * x1 + x / tmp * z1;
                if (lab > 0.04) {
                    e.shadow = (a.shadow + b.shadow - Math.cos(PI * shade_angle / 180.0) * z2 / l1) / 3.0;
                } else {
                    e.shadow = (a.shadow + b.shadow) / 2.0;
                }
            }

            /* find out in which new tetrahedron target point is */
            eax = a.x - e.x;
            eay = a.y - e.y;
            eaz = a.z - e.z;
            ecx = c.x - e.x;
            ecy = c.y - e.y;
            ecz = c.z - e.z;
            edx = d.x - e.x;
            edy = d.y - e.y;
            edz = d.z - e.z;
            epx = x - e.x;
            epy = y - e.y;
            epz = z - e.z;
            if ((eax * ecy * edz + eay * ecz * edx + eaz * ecx * edy
                    - eaz * ecy * edx - eay * ecx * edz - eax * ecz * edy)
                    * (epx * ecy * edz + epy * ecz * edx + epz * ecx * edy
                    - epz * ecy * edx - epy * ecx * edz - epx * ecz * edy) > 0.0) {
                /* point is inside acde */
                return (planet(c, d, a, e, x, y, z, level - 1));
            } else {
                /* point is inside bcde */
                return (planet(c, d, b, e, x, y, z, level - 1));
            }
        } else {
            /* level == 0 */
            if (doshade == 1 || doshade == 2) {
                /* bump map */
                x1 = 0.25 * (a.x + b.x + c.x + d.x);
                x1 = a.h * (x1 - a.x) + b.h * (x1 - b.x) + c.h * (x1 - c.x) + d.h * (x1 - d.x);
                y1 = 0.25 * (a.y + b.y + c.y + d.y);
                y1 = a.h * (y1 - a.y) + b.h * (y1 - b.y) + c.h * (y1 - c.y) + d.h * (y1 - d.y);
                z1 = 0.25 * (a.z + b.z + c.z + d.z);
                z1 = a.h * (z1 - a.z) + b.h * (z1 - b.z) + c.h * (z1 - c.z) + d.h * (z1 - d.z);
                l1 = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
                if (l1 == 0.0) {
                    l1 = 1.0;
                }
                tmp = Math.sqrt(1.0 - y * y);
                if (tmp < 0.0001) {
                    tmp = 0.0001;
                }
                x2 = x * x1 + y * y1 + z * z1;
                y2 = -x * y / tmp * x1 + tmp * y1 - z * y / tmp * z1;
                z2 = -z / tmp * x1 + x / tmp * z1;
                shade = (int) ((-Math.sin(PI * shade_angle / 180.0) * y2 - Math.cos(PI * shade_angle / 180.0) * z2)
                        / l1 * 48.0 + 128.0);
                if (shade < 10) {
                    shade = 10;
                }
                if (shade > 255) {
                    shade = 255;
                }
                if (doshade == 2 && (a.h + b.h + c.h + d.h) < 0.0) {
                    shade = 150;
                }
            } else if (doshade == 3) {
                /* daylight shading */
                double hh = a.h + b.h + c.h + d.h;
                if (hh <= 0.0) {
                    /* sea */
                    x1 = x;
                    y1 = y;
                    z1 = z;
                    /* (x1,y1,z1) = normal vector */
                } else {
                    /* add bumbmap effect */
                    x1 = 0.25 * (a.x + b.x + c.x + d.x);
                    x1 = (a.h * (x1 - a.x) + b.h * (x1 - b.x) + c.h * (x1 - c.x) + d.h * (x1 - d.x));
                    y1 = 0.25 * (a.y + b.y + c.y + d.y);
                    y1 = (a.h * (y1 - a.y) + b.h * (y1 - b.y) + c.h * (y1 - c.y) + d.h * (y1 - d.y));
                    z1 = 0.25 * (a.z + b.z + c.z + d.z);
                    z1 = (a.h * (z1 - a.z) + b.h * (z1 - b.z) + c.h * (z1 - c.z) + d.h * (z1 - d.z));
                    l1 = 5.0 * Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
                    x1 += x * l1;
                    y1 += y * l1;
                    z1 += z * l1;
                }
                l1 = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
                if (l1 == 0.0) {
                    l1 = 1.0;
                }
                x2 = Math.cos(PI * shade_angle / 180.0 - 0.5 * PI) * Math.cos(PI * shade_angle2 / 180.0);
                y2 = -Math.sin(PI * shade_angle2 / 180.0);
                z2 = -Math.sin(PI * shade_angle / 180.0 - 0.5 * PI) * Math.cos(PI * shade_angle2 / 180.0);
                shade = (int) ((x1 * x2 + y1 * y2 + z1 * z2) / l1 * 170.0 + 10);
                if (shade < 10) {
                    shade = 10;
                }
                if (shade > 255) {
                    shade = 255;
                }
            }
            rainShadow = 0.25 * (a.shadow + b.shadow + c.shadow + d.shadow);
            return 0.25 * (a.h + b.h + c.h + d.h);
        }
    }

    private double planet1(double x, double y, double z) {
//        Vertex a, b, c, d;

        double abx, aby, abz, acx, acy, acz, adx, ady, adz, apx, apy, apz;
        double bax, bay, baz, bcx, bcy, bcz, bdx, bdy, bdz, bpx, bpy, bpz;

        /* check if point is inside cached tetrahedron */
        abx = ssb.x - ssa.x;
        aby = ssb.y - ssa.y;
        abz = ssb.z - ssa.z;
        acx = ssc.x - ssa.x;
        acy = ssc.y - ssa.y;
        acz = ssc.z - ssa.z;
        adx = ssd.x - ssa.x;
        ady = ssd.y - ssa.y;
        adz = ssd.z - ssa.z;
        apx = x - ssa.x;
        apy = y - ssa.y;
        apz = z - ssa.z;

        if ((adx * aby * acz + ady * abz * acx + adz * abx * acy
                - adz * aby * acx - ady * abx * acz - adx * abz * acy)
                * (apx * aby * acz + apy * abz * acx + apz * abx * acy
                - apz * aby * acx - apy * abx * acz - apx * abz * acy) > 0.0) {
            /* p is on same side of abc as d */
            if ((acx * aby * adz + acy * abz * adx + acz * abx * ady
                    - acz * aby * adx - acy * abx * adz - acx * abz * ady)
                    * (apx * aby * adz + apy * abz * adx + apz * abx * ady
                    - apz * aby * adx - apy * abx * adz - apx * abz * ady) > 0.0) {
                /* p is on same side of abd as c */
                if ((abx * ady * acz + aby * adz * acx + abz * adx * acy
                        - abz * ady * acx - aby * adx * acz - abx * adz * acy)
                        * (apx * ady * acz + apy * adz * acx + apz * adx * acy
                        - apz * ady * acx - apy * adx * acz - apx * adz * acy) > 0.0) {
                    /* p is on same side of acd as b */
                    bax = -abx;
                    bay = -aby;
                    baz = -abz;
                    bcx = ssc.x - ssb.x;
                    bcy = ssc.y - ssb.y;
                    bcz = ssc.z - ssb.z;
                    bdx = ssd.x - ssb.x;
                    bdy = ssd.y - ssb.y;
                    bdz = ssd.z - ssb.z;
                    bpx = x - ssb.x;
                    bpy = y - ssb.y;
                    bpz = z - ssb.z;
                    if ((bax * bcy * bdz + bay * bcz * bdx + baz * bcx * bdy
                            - baz * bcy * bdx - bay * bcx * bdz - bax * bcz * bdy)
                            * (bpx * bcy * bdz + bpy * bcz * bdx + bpz * bcx * bdy
                            - bpz * bcy * bdx - bpy * bcx * bdz - bpx * bcz * bdy) > 0.0) {
                        /* p is on same side of bcd as a */
 /* Hence, p is inside cached tetrahedron */
 /* so we start from there */
                        return (planet(ssa, ssb, ssc, ssd, x, y, z, 11));
                    }
                }
            }
        }
        /* otherwise, we start from scratch */

        return (planet(tetra[0], tetra[1], tetra[2], tetra[3],
                /* vertices of tetrahedron */
                x, y, z, /* coordinates of point we want colour of */
                Depth));
        /* subdivision Depth */

    }

    public void setPlanetModel(PlanetModel model) {
        view = model.getProjection().getView();
        width = model.getWidth();
        height = model.getHeight();
        scale = model.getScale();
        rseed = model.getSeed();
        longi = model.getLongitude();
        lat = model.getLatitude();
        hgrid = model.getHgrid();
        vgrid = model.getVgrid();
        doshade = model.getShading().getShade();
        transparent = model.getTransparent();
        coastContourLines = model.getCoastalContourLines();
        contourLines = model.getContourLines();
        do_bw = model.getBlackAndWhite();
        do_outline = model.getOutline();
        latic = model.getLatitudeColor();
        temperature = model.getTemperature();
        makeBiomes = model.getBiome();
        M = model.getBase();
        shade_angle = model.getShadeLongitude();
        shade_angle2 = model.getShadeLatitude();
        this.planetModel = model;
    }

    public PlanetModel getPlanetModel() {
        return planetModel;
    }

    public BufferedImage toImage() {
//        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                double shade = 1.0;
                if (doshade > 0) {
                    shade = shades[i][j] / 150.;
                }
                int r = Math.min((int) (shade * rtable[col[i][j]]), 255);
                int g = Math.min((int) (shade * gtable[col[i][j]]), 255);
                int b = Math.min((int) (shade * btable[col[i][j]]), 255);
                int alpha = 255;
                // If tranparent is set, make background transparent
                if (transparent && col[i][j] == 2) {
                    alpha = 0;
                }
//                int rgb = ((r & 0x0ff) << 16) | ((g & 0x0ff) << 8) | (b & 0x0ff);
                int rgb = (alpha << 24) | (r << 16) | (g << 8) | b;

                img.setRGB(i, j, rgb);
            }
        }

        return img;
    }
}
