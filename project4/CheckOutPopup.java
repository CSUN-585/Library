import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.io.IOException;

public class CheckOutPopup extends Dialog<Pair<Long, Integer>> {

    @FXML private DialogPane popupPane;
    @FXML private TextField bookTitle, bookISBN;
    @FXML private ComboBox<Member> membersBox;

    public CheckOutPopup(ObservableList<Member> members, Book book) {
        String title = "Check out a Book:";
        this.setTitle(title);
        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/gui/fxml/CheckOutPopup.fxml"));
        loader.setController(this);
        try {
            loader.load();
            Button okBtn = (Button) popupPane.lookupButton(ButtonType.OK);
            bindOkButton(okBtn);
            membersBox.setItems(members);
            bookTitle.setText(book.getTitle());
            bookISBN.setText(String.valueOf(book.getIsbn()));
            this.setDialogPane(popupPane);
            this.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    int memId = membersBox.getSelectionModel().getSelectedItem().getMemberId();
                    return new Pair<>(book.getIsbn(), memId);
                }
                return null;
            });
        } catch (IOException ex) {
            System.out.println("Error when attempting to check out book, please try again.");
        }
    }

    private void bindOkButton(Button btn) {
        btn.disableProperty().bind(Bindings.createBooleanBinding(() ->
            (bookTitle.getText().isEmpty() || bookISBN.getText().isEmpty()),
                bookISBN.textProperty(), bookTitle.textProperty()));
    }
}
