package jen.util;

import javafx.scene.paint.Color;
import jen.line.Line;
import jen.line.XY;
import jen.matrix.Rotation;
import jen.matrix.Scale;
import jen.matrix.Transformation;
import jen.matrix.Translation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static boolean write(File file, boolean background, List<Line> lines, List<Transformation> transformations) {
        try {
            file.createNewFile();
        }
        catch (IOException e) {
            return false;
        }

        try {
            FileWriter out = new FileWriter(file);
            out.write("BGCL " + (background ? "BLACK" : "WHITE") + "\n");

            for (Line line : lines) {
                out.write("LINE " + Format.out(line.a.x) + " " + Format.out(line.a.y) + " " +
                        Format.out(line.b.x) + " " + Format.out(line.b.y) + " " + Format.out(line.width) +
                        " " + Format.color(line.color.getRed()) +
                        " " + Format.color(line.color.getGreen()) +
                        " " + Format.color(line.color.getBlue()) + "\n");
            }

            for (Transformation trans : transformations) {
                if (trans instanceof Translation) {
                    Translation t = (Translation) trans;
                    out.write("TRAN " + Format.out(trans.getTx()) + " " + Format.out(trans.getTy()));
                } else if (trans instanceof Rotation) {
                    Rotation r = (Rotation) trans;
                    out.write("ROTN " + Format.out(Math.toDegrees(r.getTx())));
                    if (r.isComplex()) {
                        out.write(" " + Format.out(r.getCx()) + " " + Format.out(r.getCy()));
                    } else {
                        out.write(" " + Format.out(0) + " " + Format.out(0));
                    }
                } else if (trans instanceof Scale) {
                    Scale s = (Scale) trans;
                    out.write("SCAL " + Format.out(s.getTx()) + " " + Format.out(s.getTy()));
                    if (s.isComplex()) {
                        out.write(" " + Format.out(s.getCx()) + " " + Format.out(s.getCy()));
                    } else {
                        out.write(" " + Format.out(0) + " " + Format.out(0));
                    }
                }
                out.write("\n");
            }
            out.close();
        }
        catch (IOException e) {
            return false;
        }
        return true;
    }

    public static File2D read(File file) {
        int badFormat = 0;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;
            boolean black = true;
            List<Line> lineList = new ArrayList<>();
            List<Transformation> transList = new ArrayList<>();
            while ((line = in.readLine()) != null) {
                String[] params = line.split(" ");
                if (params.length < 2) {
                    badFormat++;
                    continue;
                }
                switch (params[0]) {
                    case "BGCL":
                        switch (params[1]) {
                            case "WHITE":
                                black = false;
                                break;
                            case "BLACK":
                            default:
                                black = true;
                        }
                        break;
                    case "LINE":
                        int x0;
                        int y0;
                        int x1;
                        int y1;
                        int width = 1;
                        int r = 0;
                        int g = 0;
                        int b = 0;
                        try {
                            if (params.length >= 5) {
                                x0 = (int) Format.strToNum(params[1]);
                                y0 = (int) Format.strToNum(params[2]);
                                x1 = (int) Format.strToNum(params[3]);
                                y1 = (int) Format.strToNum(params[4]);
                            }
                            else {
                                badFormat++;
                                continue;
                            }
                            if (params.length >= 6) {
                                width = (int) Format.strToNum(params[5]);
                            }
                            if (params.length == 9) {
                                r = Math.min(255, Math.max(0, (int) Format.strToNum(params[6])));
                                g = Math.min(255, Math.max(0, (int) Format.strToNum(params[7])));
                                b = Math.min(255, Math.max(0, (int) Format.strToNum(params[8])));
                            }
                        } catch (NumberFormatException | NullPointerException e) {
                            badFormat++;
                            continue;
                        }
                        lineList.add(new Line(new XY(x0, y0), new XY(x1, y1), width, Color.rgb(r, g, b)));
                        break;
                    case "TRAN":
                        double x = 0;
                        double y = 0;
                        try {
                            if (params.length == 3) {
                                x = Format.strToNum(params[1]);
                                y = Format.strToNum(params[2]);
                            } else {
                                badFormat++;
                                continue;
                            }
                        } catch (NumberFormatException | NullPointerException e) {
                            badFormat++;
                            continue;
                        }
                        transList.add(new Translation(x, y));
                        break;
                    case "ROTN":
                        double rad = 0;
                        double Cx = 0;
                        double Cy = 0;
                        try {
                            if (params.length == 4) {
                                rad = Math.toRadians(Format.strToNum(params[1]));
                                Cx = Format.strToNum(params[2]);
                                Cy = Format.strToNum(params[3]);
                            } else {
                                badFormat++;
                                continue;
                            }
                        } catch (NumberFormatException | NullPointerException e) {
                            badFormat++;
                            continue;
                        }
                        transList.add(new Rotation(rad, Cx, Cy));
                        break;
                    case "SCAL":
                        x = 1;
                        y = 1;
                        Cx = 0;
                        Cy = 0;
                        try {
                            if (params.length == 5) {
                                x = Format.strToNum(params[1]);
                                y = Format.strToNum(params[2]);
                                Cx = Format.strToNum(params[3]);
                                Cy = Format.strToNum(params[4]);
                            } else {
                                badFormat++;
                                continue;
                            }
                        } catch (NumberFormatException | NullPointerException e) {
                            badFormat++;
                            continue;
                        }
                        transList.add(new Scale(x, y, Cx, Cy));
                        break;
                    default:
                        badFormat++;
                }
            }
            return new File2D(black, lineList, transList, badFormat);
        }
        catch (FileNotFoundException e) { }
        catch (IOException e) { }
        return null;
    }

    public static class File2D {
        public final boolean black;
        public final List<Line> lines;
        public final List<Transformation> transformations;
        public final int errors;

        File2D(boolean black, List<Line> lines, List<Transformation> transformations, int errors) {
            this.black = black;
            this.lines = lines;
            this.transformations = transformations;
            this.errors = errors;
        }
    }
}
