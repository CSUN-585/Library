import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private final String DATADIR = "/Users/jocelyn/Documents/School/CSUN/Spring 2023/COMP585/Projects/Library/project4/data/";

    @Override
    public void start(final Stage primaryStage) throws IOException {
        DataBase db = new DataBase(DATADIR);
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/gui/fxml/main.fxml"));
        MainController mc = new MainController(db);
        loader.setController(mc);
        Parent root = loader.load();
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(mc::exitHandler);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
