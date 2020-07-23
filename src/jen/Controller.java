package jen;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import jen.matrix.MatrixComposite;
import jen.matrix.Rotation;
import jen.matrix.Scale;
import jen.matrix.Transformation;
import jen.matrix.Translation;
import jen.line.LineData;
import jen.util.FileHandler;
import jen.util.Format;
import jen.util.Popup;
import jen.util.Rainbow;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import static javafx.scene.control.Alert.AlertType.*;

public class Controller {

    private static int id = 0;
    private MatrixComposite composite;
    private Renderer render;
    private LineData lines;

    @FXML private GridPane Matrix;
    @FXML private VBox Transformations;
    @FXML private Canvas Canvas;
    @FXML private AnchorPane Background;
    @FXML private RadioMenuItem White;
    @FXML private RadioMenuItem Black;

    @FXML
    private void initialize() {
        composite = new MatrixComposite(Matrix);
        lines = new LineData(composite);
        render = new Renderer(lines, Canvas);
        composite.update();
    }

    @FXML
    private void Import() {
        FileChooser fc = new FileChooser();
        File file;
        FileHandler.File2D data;
        if ((file = fc.showOpenDialog(App.STAGE)) != null &&(data = FileHandler.read(file)) != null) {
            if (data.errors != 0) Popup.show(WARNING, "There were some issues importing the file",
                    data.errors + " line(s) failed to import.");
            if (data.black) Black(); else White();

            Transformations.getChildren().clear();
            composite.clear();
            lines.clear();
            render.clear();

            data.lines.forEach(line -> lines.addLine(line));
            data.transformations.forEach(trans -> {
                ObservableList<Node> options = null;
                if (trans instanceof Translation) {
                    options = bind(App.loadFXML("translate"), trans);
                    find(options, TextField.class, "Tx").setText(Format.numToStr(trans.getTx()));
                    find(options, TextField.class, "Ty").setText(Format.numToStr(trans.getTy()));
                } else if (trans instanceof Rotation) {
                    options = bind(App.loadFXML("rotate"), trans);
                    find(options, TextField.class, "radians").setText(Format.numToStr(trans.getTx()));
                    find(options, TextField.class, "degrees").setText(Format.numToStr(Math.toDegrees(trans.getTx())));
                } else if (trans instanceof Scale) {
                    options = bind(App.loadFXML("scale"), trans);
                    find(options, TextField.class, "Tx").setText(Format.numToStr(trans.getTx()));
                    find(options, TextField.class, "Ty").setText(Format.numToStr(trans.getTy()));
                }
                trans.setEnabled(false);
                find(options, CheckBox.class, "active").setSelected(false);
                if (trans.isComplex()) {
                    find(options, CheckBox.class, "complex");
                    TextField Cx = find(options, TextField.class, "Cx");
                    Cx.setText(Format.numToStr(trans.getCx()));
                    Cx.setDisable(false);
                    TextField Cy = find(options, TextField.class, "Cy");
                    Cy.setText(Format.numToStr(trans.getCy()));
                    Cy.setDisable(false);
                }
            });
            update();
        }
    }

    @FXML
    private void Export() {
        FileChooser fc = new FileChooser();
        File file = fc.showSaveDialog(App.STAGE);
        if (file != null &&!FileHandler.write(file, Black.isSelected(), lines.getLines(), composite.getTransformations())) {
            Popup.show(ERROR, "Export unsuccessful", "File was not able to be created or already exists.");
        }
    }

    @FXML
    private void Translation() {
        bind(App.loadFXML("translate"), new Translation());
    }

    @FXML
    private void Rotation() {
        bind(App.loadFXML("rotate"), new Rotation());
    }

    @FXML
    private void Scale() {
        bind(App.loadFXML("scale"), new Scale());
    }

    private ObservableList<Node> bind(Pane pane, Transformation transformation) {
        pane.setId(String.valueOf(id++));
        Transformations.getChildren().add(pane);
        composite.add(transformation, pane.getId());
        ObservableList<Node> children = options(pane);
        children.filtered(c -> c.getId() != null).iterator().forEachRemaining(n -> {
            switch (n.getId()) {
                case "Tx": n.setOnKeyReleased(e -> {
                    transformation.setTx(Format.strToNum(((TextField)n).getText()));
                    update();
                }); break;
                case "Ty": n.setOnKeyReleased(e -> {
                    transformation.setTy(Format.strToNum(((TextField)n).getText()));
                    update();
                }); break;
                case "Cx": n.setOnKeyReleased(e -> {
                    transformation.setCx(Format.strToNum(((TextField)n).getText()));
                    update();
                }); break;
                case "Cy": n.setOnKeyReleased(e -> {
                    transformation.setCy(Format.strToNum(((TextField)n).getText()));
                    update();
                }); break;
                case "complex": ((CheckBox)n).setOnAction(e -> {
                    boolean complex = ((CheckBox) n).isSelected();
                    TextField Cx = find(children, TextField.class,"Cx");
                    TextField Cy = find(children, TextField.class,"Cy");
                    Cx.setDisable(!complex);
                    Cy.setDisable(!complex);
                    transformation.setComplex(complex);
                    if (complex) {
                        transformation.setCx(Format.strToNum(Cx.getText()));
                        transformation.setCy(Format.strToNum(Cy.getText()));
                    }
                    update();
                }); break;
                case "active": ((CheckBox)n).setOnAction(e -> {
                    transformation.setEnabled(((CheckBox) n).isSelected());
                    update();
                }); break;
                case "degrees": n.setOnKeyReleased(e -> {
                    double radians = Math.toRadians(Format.strToNum(((TextField)n).getText()));
                    transformation.setTx(radians);
                    find(children, TextField.class, "radians").setText(Format.numToStr(radians));
                    update();
                }); break;
                case "radians": n.setOnKeyReleased(e -> {
                    double radians = Format.strToNum(((TextField)n).getText());
                    transformation.setTx(radians);
                    find(children, TextField.class, "degrees").setText(Format.numToStr(Math.toDegrees(radians)));
                    update();
                }); break;
                case "delete": ((Button)n).setOnAction(e -> {
                    composite.delete(pane.getId());
                    Transformations.getChildren().remove(pane);
                    update();
                });
            }
        });
        return children;
    }

    @FXML
    private void Delete() {
        Optional<ButtonType> result = Popup.show(CONFIRMATION, "Delete all Transformations?");
        if (result.get() == ButtonType.OK){
            Transformations.getChildren().clear();
            composite.clear();
            composite.update();
            lines.update();
            render.update();
        }
    }

    @FXML
    private void Apply() {
        Optional<ButtonType> result = Popup.show(CONFIRMATION, "Apply all Transformations?");
        if (result.get() == ButtonType.OK){
            lines.apply();
            composite.deactivate();
            lines.update();
            render.update();
            Transformations.getChildren().forEach(anchor ->
                    find(options((Pane) anchor), CheckBox.class, "active").setSelected(false));
        }
    }

    @FXML
    private void Random() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        for (int i = 0; i < 100; ++i) {
            lines.addLine(r.nextInt(900), r.nextInt(700),
                    r.nextInt(900), r.nextInt(700), r.nextInt(10),
                    Color.rgb(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
        }
        lines.update();
        render.update();
    }

    @FXML
    private void Spectrum() {
        Rainbow color = new Rainbow(5);
        for (double theta = 0; theta < Math.PI * 2; theta += 0.0208) {
            int x = (int) (300 * Math.cos(theta) + 450);
            int y = (int) (300 * Math.sin(theta) + 350);
            lines.addLine(450, 350, x, y, 8, color.next());
        }
        lines.update();
        render.update();
    }

    @FXML
    private void Bresenham() {
        render.mode = Renderer.Mode.BRESENHAM;
    }

    @FXML
    private void Simple() {
        render.mode = Renderer.Mode.SIMPLE;
    }

    @FXML
    private void White() {
        Background.setStyle("-fx-background-color: white");
        Black.setSelected(false);
        White.setSelected(true);
    }

    @FXML
    private void Black() {
        Background.setStyle("-fx-background-color: black");
        Black.setSelected(true);
        White.setSelected(false);
    }

    @FXML
    private void Clear() {
        lines.clear();
        render.clear();
    }

    private ObservableList<Node> options(Pane pane) {
        return ((AnchorPane)((TitledPane)pane.getChildren().get(0)).getContent()).getChildren();
    }

    private <T extends Node> T find(ObservableList<Node> nodes, Class<T> type, String name) {
        return type.cast(nodes.filtered(f -> f.getId() != null && f.getId().equals(name)).get(0));
    }

    private void update() {
        composite.update();
        lines.update();
        render.update();
    }
}
