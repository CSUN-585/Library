import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import java.io.IOException;

public class AddMemberPopup extends Dialog<String[]> {

    @FXML private DialogPane popupPane;
    @FXML private TextField firstName, lastName, memberId;

    public AddMemberPopup(int nextId) {
        String title = "Register a new member:";
        this.setTitle(title);
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/gui/fxml/newMemberPopup.fxml"));
        loader.setController(this);
        try {
            loader.load();
            memberId.setText(String.valueOf(nextId));
            Button okBtn = (Button) popupPane.lookupButton(ButtonType.OK);
            bindOkButton(okBtn);
            this.setDialogPane(popupPane);
            this.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return new String[] {firstName.getText(), lastName.getText(), memberId.getText()};
                }
                return null;
            });
        } catch (IOException ex) {
            System.out.println("Error when attempting to register new member, please try again.");
        }
    }

    private void bindOkButton(Button btn) {
        btn.disableProperty().bind(Bindings.createBooleanBinding(() ->
            (firstName.getText().isEmpty() || lastName.getText().isEmpty()),
                firstName.textProperty(), lastName.textProperty()));
    }
}
