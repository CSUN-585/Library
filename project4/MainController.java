import gui.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import java.util.Optional;

public class MainController {

    private Library library;
    private DataBase db;
    private FilteredList<Book> filteredBooks;
    private FilteredList<LoanRecord> filteredRecords;
    @FXML
    private Menu appMenu, helpMenu;
    @FXML
    private MenuBar appMenuBar;
    @FXML
    private ChoiceBox<String> searchByBox;
    @FXML
    private TextField searchTextField;
    @FXML
    private TableView<Book> searchTable;
    @FXML
    private TableView<LoanRecord> recordsTable;
    @FXML
    private TableView<Member> memberTable;
    @FXML
    private TableColumn<Member, Number> mbrIdCol, mbrCountCol;

    @FXML
    private TableColumn<Member, String> mbrNameCol;
    @FXML
    private TableColumn<LoanRecord, Number> bkISBNCol, memIDCol;
    @FXML
    private TableColumn<LoanRecord, String> bkTitleCol, bkAuthCol, memNameCol;
    @FXML
    private TableColumn<LoanRecord, LoanRecord> checkInCol;
    @FXML
    private TableColumn<Book, String> titleCol, authorCol;
    @FXML
    private TableColumn<Book, Number> isbnCol, inStockCol, loanedCol;
    @FXML
    private TableColumn<Book, Book> checkoutCol;

    public MainController(DataBase input) {
        this.db = input;
        this.library = new Library(db.getBooks(), db.getMembers(), db.getRecords());
    }

    @FXML
    private void addNewBook(ActionEvent e) {
        AddBookPopup popup = new AddBookPopup();
        Optional<String[]> result = popup.showAndWait();
        if (result.isPresent()) {
            String title = result.get()[0];
            String author = result.get()[1];
            long isbn = Long.parseLong(result.get()[2]);
            int qty = Integer.parseInt(result.get()[3]);
            Book newBook = new Book(title, author, isbn, qty);
            library.addBook(newBook);
            db.saveBook(newBook);
        }
    }

    @FXML
    private void addNewMember(ActionEvent e) {
        int nextID = library.nextMemberID();
        AddMemberPopup popup = new AddMemberPopup(nextID);
        Optional<String[]> result = popup.showAndWait();
        if (result.isPresent()) {
            String first = result.get()[0];
            String last = result.get()[1];
            int newId = Integer.parseInt(result.get()[2]);
            Member newMember = new Member(first, last, newId);
            library.registerMember(newMember);
            db.saveMember(newMember);
        }
    }

    @FXML
    private void checkInBook(Member member, Book book) {
        Alert alert = new SkinnedAlert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Check in book?");
        alert.setContentText("Check in the following book: \n" +
                book.getTitle() + ", by:n" +
                book.getAuthorName() + "\n" +
                "ISBN: " + book.getIsbn() + "\n\n" +
                "for member: " + member.getMemberName() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            library.checkInBook(member, book);
        }
    }

    @FXML
    private void checkOutBook(Book book) {
        CheckOutPopup popup = new CheckOutPopup(db.getMembers(), book);
        Optional<Pair<Long, Integer>> result = popup.showAndWait();
        if (result.isPresent()) {
            long isbn = result.get().getKey();
            int memberId = result.get().getValue();
            LoanRecord record = library.checkOutBook(memberId, isbn);
            db.saveRecord(record);
        }
    }

    @FXML
    private void displayAboutBox() {
        Alert alert = new SkinnedAlert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Comp 585 - Project 4, Library App");
        alert.setContentText("Jocelyn Mallon,\n" +
                "Miranda Medina,\n" +
                "Hemanth Illuri &\n" +
                "Mackayla Rodriguez");
        alert.showAndWait();
    }

    private void initMembersTable() {
        memberTable.setItems(db.getMembers());
        mbrNameCol.setCellValueFactory(cellData -> cellData.getValue().memberName());
        mbrIdCol.setCellValueFactory(cellData -> cellData.getValue().memberId());
        mbrCountCol.setCellValueFactory(cellData -> Bindings.createLongBinding(() ->
                db.getRecords().stream()
                        .filter(r -> r.getMember().getMemberId() == cellData.getValue().getMemberId())
                        .count(),
                filteredRecords));
    }

    private void initRecordsTable() {
        this.filteredRecords = new FilteredList<>(db.getRecords());
        recordsTable.setItems(filteredRecords);
        bkISBNCol.setCellValueFactory(cellData -> cellData.getValue().bookISBN());
        bkTitleCol.setCellValueFactory(cellData -> cellData.getValue().bookTitle());
        bkAuthCol.setCellValueFactory(cellData -> cellData.getValue().bookAuthor());
        memNameCol.setCellValueFactory(cellData -> cellData.getValue().memberName());
        memIDCol.setCellValueFactory(cellData -> cellData.getValue().memberId());
        checkInCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        // this handles the 'check in' button in the right most table column
        checkInCol.setCellFactory(cell -> new TableCell<>() {
            private final Button checkInBtn = new Button();
            @Override
            protected void updateItem(LoanRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(checkInBtn);
                SVGPath svg = new SVGPath();
                svg.setContent(Glyphs.ADD());
                svg.setFill(Color.web("#dadada"));
                checkInBtn.setContentDisplay(ContentDisplay.RIGHT);
                checkInBtn.setGraphicTextGap(12);
                checkInBtn.setGraphic(svg);
                checkInBtn.prefWidthProperty().bind(cell.widthProperty());
                checkInBtn.setOnAction(event -> checkInBook(item.getMember(), item.getBook()));
                checkInBtn.setTooltip(new Tooltip("Check-in this book"));
            }
        });
    }

    /**
     * Handles all the necessary initialization steps for our books/search table view.
     */
    private void initSearchTable() {
        this.filteredBooks = new FilteredList<>(db.getBooks());
        searchTable.setItems(filteredBooks);
        isbnCol.setCellValueFactory(cellData -> cellData.getValue().isbn());
        titleCol.setCellValueFactory(cellData -> cellData.getValue().title());
        authorCol.setCellValueFactory(cellData -> cellData.getValue().authorName());
        inStockCol.setCellValueFactory(cellData -> cellData.getValue().availableQty());
        loanedCol.setCellValueFactory(cellData -> cellData.getValue().quantity());
        checkoutCol.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        // this handles the 'checkout' button in the right most table column
        checkoutCol.setCellFactory(cell -> new TableCell<>() {
            private final Button checkOutBtn = new Button();
            @Override
            protected void updateItem(Book book, boolean empty) {
                super.updateItem(book, empty);
                if (empty || book == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(checkOutBtn);
                SVGPath svg = new SVGPath();
                svg.setContent(Glyphs.ADD());
                svg.setFill(Color.web("#dadada"));
                checkOutBtn.setContentDisplay(ContentDisplay.RIGHT);
                checkOutBtn.setGraphicTextGap(12);
                checkOutBtn.setGraphic(svg);
                checkOutBtn.prefWidthProperty().bind(cell.widthProperty());
                checkOutBtn.setOnAction(event -> checkOutBook(book));
                checkOutBtn.setTooltip(new Tooltip("Check-out this book"));
                checkOutBtn.disableProperty().bind(Bindings.not(book.inStock()));
            }
        });
    }

    private void initSearchHandlers() {
        ObservableList<String> searchChoices = FXCollections.observableArrayList(CsvType.BOOK.getColumns());
        searchByBox.setItems(searchChoices);
        searchByBox.setValue(searchChoices.get(0));
        searchTextField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (Utils.notNullOrEmpty(newVal)) {
                switch (searchByBox.getValue()) {
                    case "ISBN" -> filteredBooks.setPredicate(book ->
                            book.getIsbn() == Long.parseLong(newVal));
                    case "Title" -> filteredBooks.setPredicate(book ->
                            book.getTitle().contains(newVal));
                    case "Author" -> filteredBooks.setPredicate(book ->
                            book.getAuthorName().contains(newVal));
                    case "Qty" -> filteredBooks.setPredicate(book ->
                            book.getQuantity() >= Integer.parseInt(newVal));
                    case "Avail" -> filteredBooks.setPredicate(book ->
                            book.getAvailableQty() >= Integer.parseInt(newVal));

                }
            } else {
                filteredBooks.setPredicate(book -> true);
            }
        });
    }

    public void exitHandler(WindowEvent windowEvent) {
        db.shutDown();
    }

    public void initialize() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            appMenuBar.useSystemMenuBarProperty().set(true);
        }
        initSearchTable();
        initRecordsTable();
        initMembersTable();
        initSearchHandlers();
    }
}
