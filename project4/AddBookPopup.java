import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import java.io.IOException;

public class AddBookPopup extends Dialog<String[]> {

    @FXML private DialogPane popupPane;
    @FXML private TextField initialQty, bookTitle, bookAuthor, bookISBN;

    public AddBookPopup() {
        String title = "Add a new Book:";
        this.setTitle(title);
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/gui/fxml/newBookPopup.fxml"));
        loader.setController(this);
        try {
            loader.load();
            Button okBtn = (Button) popupPane.lookupButton(ButtonType.OK);
            bindOkButton(okBtn);
            this.setDialogPane(popupPane);
            this.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return new String[] {
                            bookTitle.getText(),
                            bookAuthor.getText(),
                            bookISBN.getText(),
                            initialQty.getText()
                    };
                }
                return null;
            });
        } catch (IOException ex) {
            System.out.println("Error when attempting to add new book, please try again.");
        }
    }

    private void bindOkButton(Button btn) {
        btn.disableProperty().bind(Bindings.createBooleanBinding(() ->
            (bookTitle.getText().isEmpty() || bookAuthor.getText().isEmpty() ||
                    bookISBN.getText().isEmpty() || initialQty.getText().isEmpty()),
                bookISBN.textProperty(), bookTitle.textProperty(),
                bookAuthor.textProperty(), initialQty.textProperty()));
    }
}
