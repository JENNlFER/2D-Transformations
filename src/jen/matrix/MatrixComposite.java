package jen.matrix;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import jen.util.Format;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MatrixComposite {

    private final Map<String, Transformation> transMap = new LinkedHashMap<>();
    private final GridPane display;
    private Matrix matrix;

    public MatrixComposite(GridPane display) {
        this.display = display;
        matrix = new Translation();
        transMap.put("identity-matrix", (Transformation) matrix);
    }

    public void add(Transformation trans, String id) {
        transMap.put(id, trans);
    }

    public void delete(String id) {
        transMap.remove(id);
    }

    public void clear() {
        matrix = new Translation();
        transMap.clear();
        transMap.put("identity-matrix", (Transformation) matrix);
    }

    public void deactivate() {
        transMap.forEach((s, t) -> t.setEnabled(false));
        update();
    }

    public void update() {
        Iterator<Map.Entry<String,Transformation>> itr = transMap.entrySet().iterator();
        matrix = itr.next().getValue();
        while(itr.hasNext()) {
            Transformation m = itr.next().getValue();
            matrix = matrix.mult(m);
        }

        ObservableList<Node> cells = display.getChildren();
        for (int i = 0; i < 9; ++i) {
            ((Label)cells.get(i)).setText(Format.numToStr(matrix.get(i)));
        }
    }

    public Matrix getMatrix() {
        return matrix.mult(new Matrix());
    }

    public List<Transformation> getTransformations() {
        List<Transformation> list = new ArrayList<>(transMap.values());
        list.remove(0);
        return list;
    }
}
