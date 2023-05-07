import javafx.collections.ObservableList;
import java.util.Comparator;
import java.util.Optional;

class Library {

  private final ObservableList<Book> books;
  private final ObservableList<Member> members;
  private final ObservableList<LoanRecord> records;

  public Library(ObservableList<Book> booksIn, ObservableList<Member> membersIn, ObservableList<LoanRecord> recordsIn) {
    this.books = booksIn;
    this.members = membersIn;
    this.records = recordsIn;
  }

  public Integer nextMemberID() {
    Optional<Member> currentMax = members.stream()
            .max(Comparator.comparing(Member::getMemberId));
    return currentMax.map(member -> member.getMemberId() + 1)
            .orElse(0);
  }

  public void addBook(Book newBook) {
    books.add(newBook);
  }
  
  public Book searchBookByISBN(int ISBN) {
	Optional<Book> bk = findBook(ISBN);
    return bk.orElse(null);
  }

  public void registerMember(Member newMember) {
    members.add(newMember);
  }
  
  public boolean isMemberRegistered(int memberId) {
    Optional<Member> m = findMember(memberId);
    return m.isPresent();
  }
  
  public int numOfBooksCheckedOutByMember(int memberId) {
    Optional<Member> m = findMember(memberId);
    return m.map(Member::getNumCheckedOut).orElse(-1);
  }
  
  public LoanRecord checkOutBook(int memberId, long ISBN) {
    Optional<Member> memberOpt = findMember(memberId);
    Optional<Book> bookOpt = findBook(ISBN);
    if (memberOpt.isPresent() && bookOpt.isPresent()) {
      Member member = memberOpt.get();
      Book book = bookOpt.get();
      member.incrBookCount();
      book.checkOut();
      LoanRecord record = new LoanRecord(book, member);
      records.add(record);
      return record;
    } else {
      return null;
    }
  }

  private Optional<Member> findMember(int memberId) {
    return members.stream()
            .filter(m -> m.getMemberId() == memberId)
            .findFirst();
  }

  private Optional<Book> findBook(long ISBN) {
    return books.stream()
            .filter(b -> b.getIsbn() == ISBN)
            .findFirst();
  }

  private Optional<LoanRecord> findRecord(Member m, Book b) {
    return records.stream()
            .filter(r -> r.getBook() == b)
            .filter(r -> r.getMember() == m)
            .findFirst();
  }
  
  public void checkInBook(Member member, Book book) {
    Optional<LoanRecord> rec = findRecord(member, book);
    if (rec.isPresent()) {
      book.checkIn();
      member.decrBookCount();
      records.remove(rec.get());
    }
  }
}