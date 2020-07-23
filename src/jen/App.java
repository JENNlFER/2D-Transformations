package jen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {

    private static final int width = 1119;
    private static final int height = 751;
    public static Stage STAGE;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(loadFXML("interface")));
        primaryStage.setHeight(height);
        primaryStage.setWidth(width);
        primaryStage.setMinWidth(width);
        primaryStage.setMinHeight(height);
        primaryStage.setMaxWidth(width);
        primaryStage.setMaxHeight(height);
        primaryStage.setTitle("2D Transformations - Jennifer Teissler");
        primaryStage.show();
        STAGE = primaryStage;
    }

    public static <T extends Pane> T loadFXML(String fxml){
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        try {
            return fxmlLoader.load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return (T) new AnchorPane();
    }
}