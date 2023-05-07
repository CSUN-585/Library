import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Member {

    private SimpleIntegerProperty memberId;
    private SimpleStringProperty memberName;
    private SimpleIntegerProperty numBooks;

    public Member(String first, String last, int id) {
        this(first + " " + last, id);
    }

    public Member(String name, int id) {
        this.memberName = new SimpleStringProperty(name);
        this.memberId = new SimpleIntegerProperty(id);
        this.numBooks = new SimpleIntegerProperty(0);
    }

    public void setNumBooks(Integer in) {
        this.numBooks.set(in);
    }

    public void incrBookCount() {
        numBooks.set(numBooks.get() + 1);
    }

    public void decrBookCount() {
        numBooks.set(numBooks.get() - 1);
    }

    public int getMemberId() {
        return memberId.get();
    }

    public SimpleIntegerProperty memberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName.get();
    }

    public SimpleStringProperty memberName() {
        return memberName;
    }

    public Integer getNumCheckedOut() {
        return this.numBooks.get();
    }

    public SimpleIntegerProperty getNumBooks() {
        return this.numBooks;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(memberId.get());
        builder.append(": ");
        builder.append(memberName.get());
        return builder.toString();
    }
}
