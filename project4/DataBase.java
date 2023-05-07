import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataBase {

    private final String fileSep = FileSystems.getDefault().getSeparator();
    private Path dataFolder;
    private final ObservableList<Book> books;
    private final ObservableList<Member> members;
    private final ObservableList<LoanRecord> records;
    public DataBase(String path) throws IOException {
        setPath(path);
        this.books = FXCollections.observableArrayList(loadBooks());
        this.members = FXCollections.observableArrayList(loadMembers());
        this.records = FXCollections.observableArrayList(loadRecords());
    }

    public void setPath(String path) throws IOException {
        if (checkPath(path)) {
            this.dataFolder = Paths.get(path);
        } else {
            throw new IOException("Unable to verify that data folder is writable");
        }
    }

    private boolean checkPath(String path) {
        Path filePath = Paths.get(path);
        return Files.exists(filePath) &&
                Files.isDirectory(filePath) &&
                Files.isWritable(filePath);
    }

    public ObservableList<Book> getBooks() {
        return books;
    }

    public ObservableList<Member> getMembers() {
        return members;
    }

    public ObservableList<LoanRecord> getRecords() {
        return records;
    }

    public synchronized void saveMember(Member input) {
        StringJoiner joiner = new StringJoiner(",", "", "\n");
        joiner.add(String.valueOf(input.getMemberId()));
        joiner.add(input.getMemberName());
        writeCsvLine(CsvType.MEMBER, joiner.toString());
    }

    public synchronized void saveBook(Book input) {
        StringJoiner joiner = new StringJoiner(",", "", "\n");
        joiner.add(input.getTitle());
        joiner.add(input.getAuthorName());
        joiner.add(String.valueOf(input.getIsbn()));
        joiner.add(String.valueOf(input.getQuantity()));
        joiner.add(String.valueOf(input.getAvailableQty()));
        writeCsvLine(CsvType.BOOK, joiner.toString());
    }

    public synchronized void saveRecord(LoanRecord rec) {
        StringJoiner joiner = new StringJoiner(",", "", "\n");
        joiner.add(String.valueOf(rec.getBook().getIsbn()));
        joiner.add(String.valueOf(rec.getMember().getMemberId()));
        writeCsvLine(CsvType.RECORD, joiner.toString());
    }

    public void shutDown() {
        try {
            String prefix = dataFolder + fileSep;
            Path booksPath = Paths.get(prefix + CsvType.BOOK.getFileName());
            Path membersPath = Paths.get(prefix + CsvType.MEMBER.getFileName());
            Path recsPath = Paths.get(prefix + CsvType.RECORD.getFileName());
            Utils.recreateFile(booksPath);
            Utils.recreateFile(membersPath);
            Utils.recreateFile(recsPath);
            books.forEach(this::saveBook);
            members.forEach(this::saveMember);
            records.forEach(this::saveRecord);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeCsvLine(CsvType input, String line) {
        String fullName = dataFolder + fileSep + input.getFileName();
        Path csvPath = Paths.get(fullName);
        try {
            Files.write(csvPath, line.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String[]> readCSV(CsvType input) {
        String fullName = dataFolder + fileSep + input.getFileName();
        Path csvPath = Paths.get(fullName);
        try(Stream<String> lines = Files.lines(csvPath)) {
            return lines.map(line -> line.split(","))   // split each line on comma
                    .collect(Collectors.toList());         // collect the split lines into a list
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads the saved member data, including their list
     * of currently borrowed/'checked out' books
     * from the members.csv & records.csv files
     *
     * @return  a List of Member objects
     */
    public List<Member> loadMembers() {
        List<Member> membersList = new ArrayList<>();
        List<String[]> splitLines = readCSV(CsvType.MEMBER);
        for (String[] s : splitLines) {
            String name = s[1];
            int id = Integer.parseInt(s[0]);
            membersList.add(new Member(name, id));
        }
        return membersList;
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

    public List<LoanRecord> loadRecords() {
        List<LoanRecord> recsList = new ArrayList<>();
        List<String[]> splitLines = readCSV(CsvType.RECORD);
        for (String[] s : splitLines) {
            long isbn = Long.parseLong(s[0]);
            int memId = Integer.parseInt(s[1]);
            Optional<Book> b = findBook(isbn);
            Optional<Member> m = findMember(memId);
            if (b.isPresent() && m.isPresent()) {
                LoanRecord r = new LoanRecord(b.get(), m.get());
                recsList.add(r);
            }
        }
        return recsList;
    }

    public List<Book> loadBooks() {
        List<Book> booksList = new ArrayList<>();
        List<String[]> splitLines = readCSV(CsvType.BOOK);
        for (String[] s : splitLines) {
            String title = s[0];
            String author = s[1];
            long isbn = Long.parseLong(s[2]);
            int qty = Integer.parseInt(s[3]);
            int avail = Integer.parseInt(s[4]);
            Book newBook = new Book(title, author, isbn, qty, avail);
            booksList.add(newBook);
        }
        return booksList;
    }
}
