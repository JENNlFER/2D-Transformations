package jen.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class Popup {

    public static Optional<ButtonType> show(Alert.AlertType type, String header) {
       return show(type, header, "");
    }

    public static Optional<ButtonType> show(Alert.AlertType type, String header, String content) {
        Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(type.name());
        alert.setHeaderText(header);
        alert.setContentText(content);
        return alert.showAndWait();
    }
}
