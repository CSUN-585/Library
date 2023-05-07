public enum CsvType {
    BOOK("inventory.csv", new String[]{"Title", "Author", "ISBN", "Qty", "Avail"}),
    MEMBER("members.csv", new String[]{"MemberID", "Name"}),
    RECORD("records.csv", new String[]{"ISBN", "MemberID"});

    private String fileName;
    private String[] columns;
    CsvType(final String text, final String[] cols) {
        this.fileName = text;
        this.columns = cols;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String[] getColumns() {
        return this.columns;
    }
}
