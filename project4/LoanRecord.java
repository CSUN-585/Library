import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.LongBinding;
import javafx.beans.binding.StringBinding;

public class LoanRecord {

    private Book book;
    private Member member;

    public LoanRecord(Book bk, Member mem) {
        this.book = bk;
        this.member = mem;
    }

    public Book getBook() {
        return this.book;
    }

    public Member getMember() {
        return this.member;
    }

    public LongBinding bookISBN() {
        return Bindings.createLongBinding(() -> book.getIsbn(), book.isbn());
    }

    public StringBinding bookTitle() {
        return Bindings.createStringBinding(() -> book.getTitle(), book.title());
    }

    public StringBinding bookAuthor() {
        return Bindings.createStringBinding(() -> book.getAuthorName(), book.authorName());
    }

    public StringBinding memberName() {
        return Bindings.createStringBinding(() -> member.getMemberName(), member.memberName());
    }

    public IntegerBinding memberId() {
        return Bindings.createIntegerBinding(() -> member.getMemberId(),member.memberId());
    }
}
