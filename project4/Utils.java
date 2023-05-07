import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;

import javafx.scene.control.Alert.AlertType;

public class Utils {

    public static boolean isNullOrEmpty(String input) {
        return (input == null || input.isEmpty());
    }

    public static boolean notNullOrEmpty(String input) {
        return !isNullOrEmpty(input);
    }

    public static void deleteFile(Path path) throws IOException {
        Files.deleteIfExists(path);
    }

    public static void recreateFile(Path path) throws IOException {
        deleteFile(path);
        Files.createFile(path);
    }


    // get the stage information from the main thread
    public static Window getStage(Event e) {
        // account for button/action events
        if (e.getEventType().getName().contains("ACTION")) {
            Node source = (Node) e.getSource();
            return source.getScene().getWindow();
        } else {
            // account for window events
            try {
                return (Window) e.getSource();
            } catch (ClassCastException ex) {
                return (Window) e.getTarget();
            }
        }
    }

    public static Alert showAlert(Event e, String text, String type) {
        Alert alert = showAlert(text, type);
        alert.initOwner(Utils.getStage(e));
        return alert;
    }

    public static Alert showAlert(String text, Exception ex) {
        Alert alert = Utils.showAlert(text, "e");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String exceptionText = sw.toString();
        Label label = new Label("The exception stacktrace was:");
        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);
        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        return alert;
    }

    public static Alert showAlert(String text, String type) {
        Alert alert;
        if (type.toLowerCase().contains("e")) {
            alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error:");
        } else if (type.toLowerCase().contains("w")) {
            alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning:");
            alert.getButtonTypes().add(ButtonType.CANCEL);
        } else if (type.toLowerCase().contains("c")) {
            alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Please select how to proceed:");
        } else {
            alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Notice:");
        }
        alert.setHeaderText(null);
        alert.setContentText(text);
        return alert;
    }
}
