import java.time.LocalDate;

public class BorrowRecord {
    private String recordId;
    private String bookId;
    private String memberId;
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private boolean isReturned;

    public BorrowRecord(String recordId, String bookId, String memberId, LocalDate borrowDate) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.isReturned = false;
    }

    // Getters and Setters
    public String getRecordId() { return recordId; }
    public String getBookId() { return bookId; }
    public String getMemberId() { return memberId; }
    public LocalDate getBorrowDate() { return borrowDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() { return isReturned; }
    public void setReturned(boolean returned) { isReturned = returned; }

    @Override
    public String toString() {
        return "BorrowRecord{" +
                "recordId='" + recordId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", memberId='" + memberId + '\'' +
                ", borrowDate=" + borrowDate +
                ", returnDate=" + returnDate +
                ", isReturned=" + isReturned +
                '}';
    }
}