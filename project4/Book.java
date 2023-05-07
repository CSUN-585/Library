import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

public class Book {

    private SimpleStringProperty title;
    private SimpleStringProperty authorName;
    private SimpleLongProperty isbn;
    private SimpleIntegerProperty quantity;
    private SimpleIntegerProperty available;

    public Book(String title, String author, long isbn, int qty, int avail) {
        this.title = new SimpleStringProperty(title);
        this.authorName = new SimpleStringProperty(author);
        this.isbn = new SimpleLongProperty(isbn);
        this.quantity = new SimpleIntegerProperty(qty);
        this.available = new SimpleIntegerProperty(avail);
    }
    public Book(String title, String author, long isbn, int qty) {
        // if this constructor is called, we're actually adding a new
        // book, so just set the quantity and avail to the same qty val
        this(title, author, isbn, qty, qty);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty title() {
        return title;
    }

    public String getAuthorName() {
        return authorName.get();
    }

    public SimpleStringProperty authorName() {
        return authorName;
    }

    public long getIsbn() {
        return isbn.get();
    }

    public SimpleLongProperty isbn() {
        return isbn;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public SimpleIntegerProperty quantity() {
        return quantity;
    }

    public void checkOut() {
        int avail = getAvailableQty();
        if (avail > 0) {
            this.available.set(avail - 1);
        }
    }

    public void checkIn() {
        int avail = getAvailableQty();
        if (avail < getQuantity()) {
            this.available.set(avail + 1);
        }
    }

    public int getAvailableQty() {
        return available.get();
    }

    public SimpleIntegerProperty availableQty() {
        return available;
    }

    public BooleanBinding inStock() {
        return Bindings.createBooleanBinding(() -> getAvailableQty() > 0, available);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(title.get());
        builder.append(", by: ");
        builder.append(authorName.get());
        builder.append(", ISBN: ");
        builder.append(isbn.get());
        builder.append(", Qty: ");
        builder.append(quantity.get());
        return builder.toString();
    }
}
