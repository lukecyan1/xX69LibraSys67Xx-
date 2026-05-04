/*
 * BorrowRecord.java
 * one record = one borrowing transaction
 *
 * Team Trio Ensem
 * CBS25070712 - Muhammad Jad Fahmi
 * CBS25070550 - Khairin Darwisy
 * CBS25070541 - Adam Hasif
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BorrowRecord {

    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    String  recordId;
    String  borrowerName;
    String  bookId;
    String  bookTitle;
    String  borrowDate;
    String  dueDate;
    String  returnDate;
    double  fineAmount;
    boolean isReturned;

    public BorrowRecord(String recordId, String borrowerName,
                        String bookId, String bookTitle,
                        String borrowDate, int loanDays) {

        this.recordId     = recordId;
        this.borrowerName = borrowerName;
        this.bookId       = bookId;
        this.bookTitle    = bookTitle;
        this.borrowDate   = borrowDate;
        this.isReturned   = false;
        this.fineAmount   = 0.0;
        this.returnDate   = null;

        // TODO: calculate dueDate
        // parse borrowDate using FMT then add loanDays using plusDays()
        // then format back to string and store in this.dueDate
    }

    public void markReturned(String returnDate) {
        this.isReturned = true;
        this.returnDate = returnDate;

        LocalDate due = LocalDate.parse(dueDate, FMT);
        LocalDate ret = LocalDate.parse(returnDate, FMT);

        // TODO: calculate overdue days using ChronoUnit.DAYS.between(due, ret)
        // if overdue > 0 then fineAmount = overdueDays * 0.50
        // otherwise fineAmount stays 0.0
    }

    public String getRecordInfo() {
        // TODO: build status string
        // if returned -> "Returned  (returnDate)"
        // if not -> "Borrowing"
        String status = "";

        // TODO: build and return the full info string
        // copy this format exactly (spacing matters):
        //
        // "[" + recordId + "] " + bookTitle
        // + "\n    Borrower  : " + borrowerName
        // + "\n    Borrow    : " + borrowDate
        // + "\n    Due       : " + dueDate
        // + "\n    Status    : " + status
        // then only if fineAmount > 0, also append:
        // + "\n    Fine      : RM" + String.format("%.2f", fineAmount)
        return "";
    }
}