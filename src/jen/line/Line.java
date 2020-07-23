package jen.line;

import javafx.scene.paint.Color;

public class Line {
    public final XY a;
    public final XY b;
    public final int width;
    public final Color color;

    public Line(XY a, XY b, int width, Color color) {
        this.a = a;
        this.b = b;
        this.width = width;
        this.color = color;
    }
}
