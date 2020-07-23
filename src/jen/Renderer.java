package jen;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import jen.line.Line;
import jen.line.LineData;
import jen.line.XY;

import java.util.List;

import static jen.Renderer.Mode.BRESENHAM;

public class Renderer {

    private final LineData data;
    private final GraphicsContext ctx;
    private final PixelWriter px;
    private final int width;
    private final int height;
    public Mode mode = BRESENHAM;
    private boolean drawingLine = false;
    private boolean prevPixelDrawn = false;

    public Renderer(LineData data, Canvas canvas) {
        this.data = data;
        ctx = canvas.getGraphicsContext2D();
        px = ctx.getPixelWriter();
        width = (int) canvas.getWidth();
        height = (int) canvas.getHeight();
    }

    public void update() {
        clear();
        List<Line> lines = data.getLines();
        for (Line line : lines) {
            draw(line.a.x, line.a.y, line.b.x, line.b.y, line.width, line.color);
        }
    }

    private void draw(int x0, int y0, int x1, int y1, int width, Color color) {
        switch (mode) {
            case BRESENHAM: bresenham(x0, y0, x1, y1, width, color);
                break;
            case SIMPLE: simple(x0, y0, x1, y1, width, color);
        }
        drawingLine = false;
        prevPixelDrawn = false;
    }

    public void clear() {
        ctx.clearRect(0, 0, width, height);
    }

    private void simple(double x0, double y0, double x1, double y1, int width, Color color) {
        Coordinates c = collapse((int) x0, (int) y0, (int) x1, (int) y1);
        x0 = c.x0; x1 = c.x1; y0 = c.y0; y1 = c.y1;

        double dx = x1 - x0;
        double m = (y1 - y0) / dx;

        while (x0 < x1) {
            plot((int) x0, (int) y0, width, color, c.flags);
            y0 += m;
            x0++;
        }
    }

    private void bresenham(int x0, int y0, int x1, int y1, int width, Color color) {
        Coordinates c = collapse(x0, y0, x1, y1);
        x0 = c.x0; x1 = c.x1; y0 = c.y0; y1 = c.y1;

        int dx = x1 - x0;
        int dy = y1 - y0;

        int i = 2 * dy;
        int E = i - dx;
        int k = 2 * (dy - dx);

        while (x0 < x1) {
            plot(x0, y0, width, color, c.flags);
            if (E < 0) {
                E += i;
            } else {
                y0++;
                E += k;
            }
            x0++;
        }
    }

    private void plot(int x, int y, int width, Color color, boolean[] flags) {
        if (width <= 0) return;
        if (width == 1) {
            XY xy = expand(x, y, flags);
            if (xy.x < this.width && xy.y < this.height && xy.x >= 0 && xy.y >= 0) {
                px.setColor(xy.x, xy.y, color);
            }
        } else {
            width = width / 2;
            for (int i = y - width; i <= y + width; ++i) {
                XY xy = expand(x, i, flags);
                if (xy.x < this.width && xy.y < this.height && xy.x >= 0 && xy.y >= 0) {
                    px.setColor(xy.x, xy.y, color);
                }
            }
        }
    }

    private Coordinates collapse(int x0, int y0, int x1, int y1) {
        boolean sFlag = false;
        if (Math.abs(x1 - x0) < Math.abs(y1 - y0)) {
            sFlag = true;
            int tmp;
            tmp = x0;
            x0 = y0;
            y0 = tmp;
            tmp = x1;
            x1 = y1;
            y1 = tmp;
        }
        boolean xFlag = false;
        if (x1 < x0) {
            xFlag = true;
            x0 = width - x0;
            x1 = width - x1;
        }
        boolean yFlag = false;
        if (y1 < y0) {
            yFlag = true;
            y0 = height - y0;
            y1 = height - y1;
        }

        return new Coordinates(x0, y0, x1, y1, xFlag, yFlag, sFlag);
    }

    private XY expand(int x, int y, boolean... flags) {
        if (flags[0]) x = width - x;
        if (flags[1]) y = height - y;
        if (flags[2]) return new XY(y, x);
        return new XY(x, y);
    }

    private class Coordinates {
        private final int x0;
        private final int y0;
        private final int x1;
        private final int y1;
        private final boolean[] flags = new boolean[3];
        private Coordinates(int x0, int y0, int x1, int y1, boolean xFlag, boolean yFlag, boolean sFlag) {
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            flags[0] = xFlag;
            flags[1] = yFlag;
            flags[2] = sFlag;
        }
    }

    public enum Mode {
        BRESENHAM, SIMPLE
    }
}
