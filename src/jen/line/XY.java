package jen.line;

public class XY {
    public final int x;
    public final int y;

    public XY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof XY) && (x == ((XY)o).x) && (y == ((XY)o).y);
    }
}
