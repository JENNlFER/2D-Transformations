package jen.line;

import javafx.scene.paint.Color;
import jen.matrix.Matrix;
import jen.matrix.MatrixComposite;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LineData {

    private final List<XY> vertices = new ArrayList<>();
    private final List<Edge> edges = new LinkedList<>();
    private final List<XY> transformedVertices = new LinkedList<>();
    private final MatrixComposite composite;

    public LineData(MatrixComposite composite) {
        this.composite = composite;
    }

    public void update() {
        transformedVertices.clear();
        Matrix matrix = composite.getMatrix();
        for (XY vertex : vertices) {
            Matrix coords = new Matrix(vertex.x, vertex.y, 1, 0, 0, 0, 0, 0, 0).mult(matrix);
            transformedVertices.add(new XY((int) coords.get(0), (int) coords.get(1)));
        }
    }

    public void apply() {
        vertices.clear();
        vertices.addAll(transformedVertices);
    }

    public void clear() {
        vertices.clear();
        transformedVertices.clear();
        edges.clear();
    }

    public void addLine(Line line) {
        addLine(line.a.x, line.a.y, line.b.x, line.b.y, line.width, line.color);
    }

    public void addLine(int x0, int y0, int x1, int y1, int width, Color color) {
        int indexV1;
        XY v1 = new XY(x0, y0);
        if (!vertices.contains(v1)) {
            vertices.add(v1);
            indexV1 = vertices.size() - 1;
        } else {
            indexV1 = vertices.indexOf(v1);
        }

        int indexV2;
        XY v2 = new XY(x1, y1);
        if (!vertices.contains(v2)) {
            vertices.add(v2);
            indexV2 = vertices.size() - 1;
        } else {
            indexV2 = vertices.indexOf(v2);
        }

        edges.add(new Edge(indexV1, indexV2, width, color));
    }

    public List<Line> getLines() {
        List<Line> lines = new ArrayList<>();
        for (Edge edge : edges) {
            XY a = transformedVertices.get(edge.end1);
            XY b = transformedVertices.get(edge.end2);
            lines.add(new Line(a, b, edge.width, edge.color));
        }
        return lines;
    }
}
