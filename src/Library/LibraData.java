/*
 * LibraData.java
 * stores all books and borrow records
 * acts like a simple database for the system
 *
 * Team Trio Ensem
 * CBS25070712 - Muhammad Jad Fahmi
 * CBS25070550 - Khairin Darwisy
 * CBS25070541 - Adam Hasif
 */

import java.util.ArrayList;

public class LibraData {

    ArrayList<Book>         bookList   = new ArrayList<>();
    ArrayList<BorrowRecord> recordList = new ArrayList<>();

    public LibraData() {
        bookList.add(new Book("B001", "Introduction to Java Programming", "Y. Daniel Liang",       "Programming",  3));
        bookList.add(new Book("B002", "Data Structures and Algorithms",   "Michael T. Goodrich",   "Programming",  2));
        bookList.add(new Book("B003", "Computer Networks",                "Andrew Tanenbaum",      "Networking",   2));
        bookList.add(new Book("B004", "Operating System Concepts",        "Silberschatz",          "Systems",      1));
        bookList.add(new Book("B005", "Database System Concepts",         "Abraham Silberschatz",  "Database",     3));
        bookList.add(new Book("B006", "Calculus Early Transcendentals",   "James Stewart",         "Mathematics",  2));
        bookList.add(new Book("B007", "Physics for Scientists",           "Serway & Jewett",       "Science",      2));
        bookList.add(new Book("B008", "Artificial Intelligence",          "Stuart Russell",        "Programming",  1));
    }

    public Book findBookById(String bookId) {
        for (Book b : bookList) {
            // TODO: return b if b.bookId matches bookId (case insensitive, trimmed)
        }
        return null;
    }

    public String searchBook(String keyword) {
        String kw = keyword.trim().toLowerCase();
        StringBuilder sb = new StringBuilder();

        for (Book b : bookList) {
            // TODO: check if kw is contained in bookId, title, author or category (all lowercase)
            // if match found, append b.getBookInfo() + "\n\n" to sb
        }

        if (sb.length() == 0) {
            return "No book found for \"" + keyword + "\".\nTip: Try a shorter keyword (e.g. \"java\", \"net\", \"B001\").";
        }
        return sb.toString().trim();
    }

    public String getAllBooks() {
        StringBuilder sb = new StringBuilder();
        // TODO: loop bookList, append each book.getBookInfo() + "\n\n"
        return sb.toString().trim();
    }

    public String getAllBooksTable() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-6s %-42s %-22s %-14s %s%n", "ID", "Title", "Author", "Category", "Avail"));
        sb.append("-".repeat(100)).append("\n");

        for (Book b : bookList) {
            // TODO: build avail string - "X/Y" if available, "NONE" if not
            String avail = "";

            sb.append(String.format("%-6s %-42s %-22s %-14s %s%n",
                b.bookId,
                truncate(b.title, 41),
                truncate(b.author, 21),
                truncate(b.category, 13),
                avail));
        }
        return sb.toString();
    }

    // -------------------------------------------------------
    // Borrow
    // -------------------------------------------------------

    public String borrowBook(String borrowerName, String bookId, int loanDays, String todayDate) {

        // TODO: normalise bookId - remove spaces and uppercase it

        Book book = findBookById(bookId);
        if (book == null) {
            return "ERROR: Book ID \"" + bookId + "\" not found.\nTip: IDs look like B001, B002, etc.";
        }

        // TODO: check if book is available, if not return the error message:
        // "ERROR: \"title\" is currently not available.\nAll X copy/copies are out on loan."

        // TODO: check for duplicate borrow - loop recordList
        // if any record has isReturned==false AND same bookId AND same borrowerName (case insensitive)
        // return "ERROR: Already borrowing \"title\".\nPlease return it first (Record ID: recordId)."

        book.borrowCopy();

        String recordId = "R" + String.format("%03d", recordList.size() + 1);
        BorrowRecord record = new BorrowRecord(recordId, borrowerName, bookId, book.title, todayDate, loanDays);
        recordList.add(record);

        // TODO: return the receipt string, copy this exactly:
        // "=== Borrowing Receipt ===\n"
        // + "Record ID  : " + recordId + "\n"
        // + "Borrower   : " + borrowerName + "\n"
        // + "Book       : " + book.title + "\n"
        // + "Borrow Date: " + todayDate + "\n"
        // + "Due Date   : " + record.dueDate + "\n"
        // + "Loan Period: " + loanDays + " days\n"
        // + "=========================\n"
        // + "IMPORTANT: Save your Record ID (" + recordId + ")\n"
        // + "You will need it to return this book.\n"
        // + "Fine: RM0.50 per day if late."
        return "";
    }

    // -------------------------------------------------------
    // Return
    // -------------------------------------------------------

    public String returnBook(String recordId, String returnDate) {
        BorrowRecord record = findRecordById(recordId);

        if (record == null) {
            return "ERROR: Record ID \"" + recordId + "\" not found.\nTip: Use 'View Active Borrows' to look up your Record ID.";
        }
        if (record.isReturned) {
            return "ERROR: Record \"" + recordId + "\" (" + record.bookTitle + ") was already returned on " + record.returnDate + ".";
        }

        record.markReturned(returnDate);

        Book book = findBookById(record.bookId);
        // TODO: call book.returnCopy() but check book != null first

        // TODO: build and return the return summary, copy this exactly:
        // String msg = "=== Return Summary ===\n"
        //            + "Record ID : " + record.recordId + "\n"
        //            + "Book      : " + record.bookTitle + "\n"
        //            + "Borrower  : " + record.borrowerName + "\n"
        //            + "Returned  : " + returnDate + "\n";
        // then if record.fineAmount > 0, append:
        //     "Fine      : RM" + String.format("%.2f", record.fineAmount) + "\n"
        //     + "======================\nPlease collect fine payment."
        // else append:
        //     "Fine      : None\n======================\nReturned on time!"
        return "";
    }

    // -------------------------------------------------------
    // History & active borrows
    // -------------------------------------------------------

    public String getBorrowHistory(String keyword) {
        String kw = keyword.trim().toLowerCase();
        StringBuilder sb = new StringBuilder();

        // TODO: loop recordList, include records where borrowerName.toLowerCase() contains kw
        // append record.getRecordInfo() + "\n\n" for each match

        if (sb.length() == 0) {
            return "No borrowing history found matching \"" + keyword + "\".\nTip: Try a partial name (e.g. \"ali\", \"ahmad\").";
        }
        return sb.toString().trim();
    }

    public String getActiveRecords() {
        StringBuilder sb = new StringBuilder();

        // TODO: loop recordList, only include records where isReturned == false
        // append record.getRecordInfo() + "\n\n"

        return sb.length() == 0 ? "No active borrows at the moment." : sb.toString().trim();
    }

    // -------------------------------------------------------
    // helpers - done, dont touch
    // -------------------------------------------------------

    private BorrowRecord findRecordById(String recordId) {
        for (BorrowRecord r : recordList) {
            if (r.recordId.equalsIgnoreCase(recordId.trim())) return r;
        }
        return null;
    }

    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }
}