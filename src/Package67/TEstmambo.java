package Package67;

import javax.swing.JOptionPane;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

// ============================================================
// Book class
// This class stores all the information about one book.
// It also keeps track of how many copies are available.
// ============================================================
class Book {

    // --- Fields (book information) ---
    String bookId;          // unique ID like "B001"
    String title;           // book title
    String author;          // author name
    String category;        // category like "Programming", "Science"
    int totalCopies;        // total copies the library owns
    int availableCopies;    // copies not currently borrowed

    // Constructor - called when we create a new Book object
    public Book(String bookId, String title, String author, String category, int totalCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies; // at start, all copies are available
    }

    // Returns true if there is at least 1 copy available
    public boolean isAvailable() {
        return availableCopies > 0;
    }

    // Called when someone borrows this book - reduce available count by 1
    public void borrowCopy() {
        if (availableCopies > 0) availableCopies--;
    }

    // Called when someone returns this book - increase available count by 1
    public void returnCopy() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    // Returns a nicely formatted string with all book info
    public String getBookInfo() {
        // Build status text depending on availability
        String status;
        if (isAvailable()) {
            status = "Available (" + availableCopies + "/" + totalCopies + " copies left)";
        } else {
            status = "Not Available";
        }

        // Return all info as one formatted string
        return "[" + bookId + "] " + title
            + "\n    Author   : " + author
            + "\n    Category : " + category
            + "\n    Status   : " + status;
    }
}

// ============================================================
// BorrowRecord class
// This class stores one borrowing transaction.
// Every time someone borrows a book, one BorrowRecord is created.
// Loan period is fixed at 14 days.
// ============================================================
class BorrowRecord {

    // --- Fields ---
    String recordId;        // unique ID like "R001"
    String borrowerName;    // name of the person borrowing
    String bookId;          // which book was borrowed
    String bookTitle;       // book title (saved here for easy display)
    String borrowDate;      // date the book was borrowed (dd-MM-yyyy)
    String dueDate;         // date the book must be returned by
    String returnDate;      // date the book was actually returned
    double fineAmount;      // fine in RM (RM0.50 per day overdue)
    boolean isReturned;     // true if book has been returned, false if still borrowed

    // Date formatter - we use "dd-MM-yyyy" format throughout the system
    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Loan period is fixed at 14 days - no need to pass it in
    static final int LOAN_DAYS = 14;

    // Constructor - creates a new borrow record
    // No loanDays parameter needed, always uses 14 days
    public BorrowRecord(String recordId, String borrowerName, String bookId, String bookTitle, String borrowDate) {
        this.recordId = recordId;
        this.borrowerName = borrowerName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.fineAmount = 0;        // no fine at the start
        this.isReturned = false;    // book not returned yet

        // Calculate the due date by adding 14 days to the borrow date
        // Example: borrowed on 07-05-2026 -> due on 21-05-2026
        LocalDate borrow = LocalDate.parse(borrowDate, fmt);
        LocalDate due = borrow.plusDays(LOAN_DAYS);
        this.dueDate = due.format(fmt);
    }

    // Call this method when the borrower returns the book
    // It marks the record as returned and calculates any fine
    public void markReturned(String returnDate) {
        this.isReturned = true;
        this.returnDate = returnDate;

        // Calculate how many days late the book was returned
        // ChronoUnit.DAYS.between() gives the difference in days
        LocalDate due = LocalDate.parse(dueDate, fmt);
        LocalDate returned = LocalDate.parse(returnDate, fmt);
        long daysLate = ChronoUnit.DAYS.between(due, returned);

        // Only charge fine if returned after due date
        if (daysLate > 0) {
            this.fineAmount = daysLate * 0.50; // RM0.50 per overdue day
        }
    }

    // Returns a formatted string showing all the record details
    public String getRecordInfo() {
        // Status shows different text depending on whether returned or not
        String status;
        if (isReturned) {
            status = "Returned on " + returnDate;
        } else {
            status = "Active (Due: " + dueDate + ")";
        }

        // Build the main info string
        String info = "[" + recordId + "] " + bookTitle
            + "\n    Borrower  : " + borrowerName
            + "\n    Borrowed  : " + borrowDate
            + "\n    Due Date  : " + dueDate
            + "\n    Status    : " + status;

        // Only add the fine line if there is actually a fine
        if (fineAmount > 0) {
            info = info + "\n    Fine      : RM " + String.format("%.2f", fineAmount);
        }

        return info;
    }
}

// ============================================================
// LibraData class
// This is the "brain" of the system.
// It stores all books and borrow records,
// and handles all the logic like borrowing and returning.
// ============================================================
class LibraData {

    // Lists to store all books and all borrow records
    ArrayList<Book> bookList = new ArrayList<>();
    ArrayList<BorrowRecord> recordList = new ArrayList<>();

    // Counter to auto-generate record IDs: R001, R002, R003...
    int recordCounter = 1;

    // Constructor - preload the library with 8 books
    public LibraData() {
        bookList.add(new Book("B001", "Introduction to Java Programming", "Y. Daniel Liang", "Programming", 3));
        bookList.add(new Book("B002", "Data Structures and Algorithms", "Michael T. Goodrich", "Programming", 2));
        bookList.add(new Book("B003", "Computer Networks", "Andrew Tanenbaum", "Networking", 2));
        bookList.add(new Book("B004", "Operating System Concepts", "Silberschatz", "Systems", 1));
        bookList.add(new Book("B005", "Database System Concepts", "Abraham Silberschatz", "Database", 3));
        bookList.add(new Book("B006", "Calculus Early Transcendentals", "James Stewart", "Mathematics", 2));
        bookList.add(new Book("B007", "Physics for Scientists", "Serway & Jewett", "Science", 2));
        bookList.add(new Book("B008", "Artificial Intelligence", "Stuart Russell", "Programming", 1));
    }

    // Find a book by its ID - returns the Book object, or null if not found
    public Book findBook(String id) {
        for (Book b : bookList) {
            if (b.bookId.equalsIgnoreCase(id.trim())) {
                return b;
            }
        }
        return null; // book not found
    }

    // Find a borrow record by its ID - returns the BorrowRecord, or null if not found
    public BorrowRecord findRecord(String id) {
        for (BorrowRecord r : recordList) {
            if (r.recordId.equalsIgnoreCase(id.trim())) {
                return r;
            }
        }
        return null; // record not found
    }

    // Search books by keyword
    // Checks against book ID, title, author, and category
    public String searchBooks(String keyword) {
        String kw = keyword.toLowerCase(); // convert to lowercase for case-insensitive matching
        String result = "";
        int count = 0;

        // Loop through all books and check if keyword matches any field
        for (Book b : bookList) {
            if (b.bookId.toLowerCase().contains(kw)
                || b.title.toLowerCase().contains(kw)
                || b.author.toLowerCase().contains(kw)
                || b.category.toLowerCase().contains(kw)) {
                result = result + b.getBookInfo() + "\n\n";
                count++;
            }
        }

        // If nothing matched, return a "not found" message
        if (count == 0) {
            return "No books found for \"" + keyword + "\".";
        }

        return "Found " + count + " result(s):\n\n" + result;
    }

    // Returns a list of ALL books in the library
    public String getAllBooks() {
        String result = "=== Book Catalogue ===\n\n";
        for (Book b : bookList) {
            result = result + b.getBookInfo() + "\n\n";
        }
        return result;
    }

    // Borrow a book - loan period is always 14 days
    // Steps: check book exists -> check has copies -> check not already borrowed -> create record
    public String borrowBook(String borrowerName, String bookId, String today) {
        // Step 1: Check if the book ID exists
        Book book = findBook(bookId);
        if (book == null) {
            return "ERROR: Book ID \"" + bookId + "\" not found.";
        }

        // Step 2: Check if there are available copies
        if (!book.isAvailable()) {
            return "ERROR: \"" + book.title + "\" has no available copies.";
        }

        // Step 3: Check if this person already has an active borrow for the same book
        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.bookId.equalsIgnoreCase(bookId) && r.borrowerName.equalsIgnoreCase(borrowerName)) {
                return "ERROR: " + borrowerName + " already borrowed this book and has not returned it.";
            }
        }

        // Step 4: All checks passed - create a new borrow record (14 days fixed)
        String recordId = "R" + String.format("%03d", recordCounter);
        recordCounter++; // increment for next record

        BorrowRecord rec = new BorrowRecord(recordId, borrowerName, bookId, book.title, today);
        recordList.add(rec);  // save the record
        book.borrowCopy();    // reduce available copies by 1

        return "Borrow successful!\n\n" + rec.getRecordInfo();
    }

    // Return a book
    // Steps: find the record -> mark returned -> restore the book copy
    public String returnBook(String recordId, String today) {
        // Step 1: Find the record
        BorrowRecord rec = findRecord(recordId);
        if (rec == null) {
            return "ERROR: Record ID \"" + recordId + "\" not found.";
        }

        // Step 2: Make sure it hasn't already been returned
        if (rec.isReturned) {
            return "ERROR: This book was already returned.";
        }

        // Step 3: Mark as returned (this also calculates any fine)
        rec.markReturned(today);

        // Step 4: Give the copy back to the book
        Book book = findBook(rec.bookId);
        if (book != null) {
            book.returnCopy();
        }

        // Build the result message
        String result = "Return successful!\n\n" + rec.getRecordInfo();

        // Show fine warning if book was returned late
        if (rec.fineAmount > 0) {
            result = result + "\n\nThis book is overdue. Please pay RM " + String.format("%.2f", rec.fineAmount) + " at the counter.";
        }

        return result;
    }

    // Get all borrow records that are still active (book not returned yet)
    public String getActiveBorrows() {
        String result = "";
        int count = 0;

        for (BorrowRecord r : recordList) {
            if (!r.isReturned) { // only include records where book is still out
                result = result + r.getRecordInfo() + "\n\n";
                count++;
            }
        }

        if (count == 0) {
            return "No active borrows at the moment.";
        }

        return "Active Borrows (" + count + "):\n\n" + result;
    }

    // Get all borrow records for a specific person
    // Uses partial name match, so "Ali" will also match "Aliana"
    public String getBorrowHistory(String name) {
        String result = "";
        int count = 0;

        for (BorrowRecord r : recordList) {
            if (r.borrowerName.toLowerCase().contains(name.toLowerCase())) {
                result = result + r.getRecordInfo() + "\n\n";
                count++;
            }
        }

        if (count == 0) {
            return "No records found for \"" + name + "\".";
        }

        return "Borrow History for \"" + name + "\" (" + count + " record(s)):\n\n" + result;
    }
}

// ============================================================
// TEstmambo - Main class
// This handles the login screen and the main menu.
// All the show*() methods display JOptionPane dialogs only.
// ============================================================
public class TEstmambo {

    // Date formatter used throughout the main class
    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // Our database object - holds all books and records
    static LibraData db = new LibraData();

    // Session counters - reset every time the program starts
    static int totalBorrowed = 0;
    static int totalReturned = 0;

    // Entry point of the program
    public static void main(String[] args) {
        // Show login screen first - if login fails, stop the program
        if (!showLoginScreen()) {
            JOptionPane.showMessageDialog(null, "Login failed. Program will close.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Show welcome message after successful login
        JOptionPane.showMessageDialog(null,
            "Welcome, Admin!\n"
            + "------------------------------\n"
            + "xX69LibraSys67Xx\n"
            + "Library Management System\n\n"
            + "Team Trio Ensem",
            "Welcome", JOptionPane.INFORMATION_MESSAGE);

        // Main menu options
        String[] options = { "Search Book", "Borrow Book", "Return Book", "Active Borrows", "Borrow History", "Exit" };

        boolean running = true;

        // Keep showing the menu until user chooses Exit
        while (running) {
            // Menu message shows session stats
            String msg = "Please choose an option:\n"
                + "------------------------------\n"
                + "Borrowed this session : " + totalBorrowed + "\n"
                + "Returned this session : " + totalReturned;

            // Show the option dialog and get the user's choice
            int choice = JOptionPane.showOptionDialog(null, msg,
                "xX69LibraSys67Xx - Main Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]);

            // Call the matching method based on what user chose
            if (choice == 0) {
                showSearchBook();
            } else if (choice == 1) {
                showBorrowBook();
            } else if (choice == 2) {
                showReturnBook();
            } else if (choice == 3) {
                showActiveBorrows();
            } else if (choice == 4) {
                showBorrowHistory();
            } else if (choice == 5) {
                // showExitScreen() returns true if user confirmed exit
                running = !showExitScreen();
            }
        }
    }

    // --- Login Screen ---
    // Shows username and password fields, allows 3 attempts
    // Returns true if login is successful, false otherwise
    static boolean showLoginScreen() {
        for (int attempts = 3; attempts > 0; attempts--) {
            // Ask for username
            String user = JOptionPane.showInputDialog(null,
                "xX69LibraSys67Xx - Admin Login\n"
                + "------------------------------\n"
                + "This system made by Team Trio Ensem\n"
                + "Enter Username:",
                "Login", JOptionPane.QUESTION_MESSAGE);

            // If user pressed Cancel, stop login
            if (user == null) return false;

            // Ask for password using plain input dialog - no extra imports needed
            String password = JOptionPane.showInputDialog(null,
                "Enter Password now :",
                "Login", JOptionPane.QUESTION_MESSAGE);

            // If user pressed Cancel, stop login
            if (password == null) return false;

            // Check if username and password are correct
            if (user.trim().equalsIgnoreCase("HJD") && password.equals("xx67xx")) {
                return true; // login successful!
            }

            // Wrong credentials - show how many attempts are left
            if (attempts > 1) {
                JOptionPane.showMessageDialog(null,
                    "Wrong credentials. " + (attempts - 1) + " attempt(s) left.",
                    "Login Failed", JOptionPane.WARNING_MESSAGE);
            }
        }
        return false; // all 3 attempts used, login failed
    }

    // --- Exit Screen ---
    // Asks user to confirm, then shows a session summary
    // Returns true if user confirmed exit, false if they cancelled
    static boolean showExitScreen() {
        int answer = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to exit?",
            "Exit", JOptionPane.YES_NO_OPTION);

        // If user said No, go back to menu
        if (answer != JOptionPane.YES_OPTION) return false;

        // Show session summary before closing
        JOptionPane.showMessageDialog(null,
            "=6=7= Session Summary =6=7=\n\n"
            + "Total Borrowed : " + totalBorrowed + "\n"
            + "Total Returned : " + totalReturned + "\n\n"
            + "Thank you for using xX69LibraSys67Xx!\n"
            + "Goodbye Mambo! 67 :)",
            "Goodbye", JOptionPane.INFORMATION_MESSAGE);

        return true; // confirmed exit
    }

    // --- Search Book ---
    // Asks user for a keyword, then shows matching books
    static void showSearchBook() {
        // Ask for keyword input
        String keyword = getInput("Enter keyword to search (title / author / category / ID):");

        // If user cancelled, show cancelled message and stop
        if (keyword == null) {
            cancelled();
            return;
        }

        // Search and show results
        JOptionPane.showMessageDialog(null, db.searchBooks(keyword), "Search Results", JOptionPane.PLAIN_MESSAGE);
    }

    // --- Borrow Book ---
    // Collects borrower name, shows catalogue, asks for book ID
    // Loan period is fixed at 14 days - no need to ask
    static void showBorrowBook() {
        // Step 1: Ask for borrower name
        String name = getInput("Enter borrower name:");
        if (name == null) {
            cancelled();
            return;
        }

        // Step 2: Show full catalogue so user can see all books and IDs
        JOptionPane.showMessageDialog(null, db.getAllBooks(), "Book Catalogue", JOptionPane.PLAIN_MESSAGE);

        // Step 3: Ask which book to borrow
        String bookId = getInput("Enter Book ID to borrow:");
        if (bookId == null) {
            cancelled();
            return;
        }

        // Step 4: Process the borrow - loan is always 14 days
        String result = db.borrowBook(name, bookId, today());

        // Show success or error message
        if (result.startsWith("Borrow successful")) {
            totalBorrowed++; // update session counter
            JOptionPane.showMessageDialog(null, result, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, result, "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Return Book ---
    // Asks for a record ID and processes the return
    static void showReturnBook() {
        // Ask for the record ID (e.g. R001)
        String recordId = getInput("Enter Record ID to return (example: R001):");
        if (recordId == null) {
            cancelled();
            return;
        }

        // Process the return
        String result = db.returnBook(recordId, today());

        // Show success or error message
        if (result.startsWith("Return successful")) {
            totalReturned++; // update session counter
            JOptionPane.showMessageDialog(null, result, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, result, "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Active Borrows ---
    // Shows all books that are currently still borrowed
    static void showActiveBorrows() {
        JOptionPane.showMessageDialog(null, db.getActiveBorrows(), "Active Borrows", JOptionPane.PLAIN_MESSAGE);
    }

    // --- Borrow History ---
    // Asks for a name and shows all records for that person
    static void showBorrowHistory() {
        String name = getInput("Enter borrower name:");
        if (name == null) {
            cancelled();
            return;
        }

        JOptionPane.showMessageDialog(null, db.getBorrowHistory(name), "Borrow History", JOptionPane.PLAIN_MESSAGE);
    }

    // --- Helper: getInput ---
    // Shows an input dialog and keeps asking until user types something
    // Returns null if user presses Cancel
    static String getInput(String prompt) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, prompt, "Input", JOptionPane.QUESTION_MESSAGE);

            // User pressed Cancel - return null to signal cancellation
            if (input == null) {
                return null;
            }

            // Valid input - return it
            if (!input.trim().isEmpty()) {
                return input;
            }

            // Empty input - ask again
            JOptionPane.showMessageDialog(null, "Please enter something. Cannot be empty.", "Empty Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- Helper: cancelled ---
    // Shows a simple "Action cancelled" popup
    static void cancelled() {
        JOptionPane.showMessageDialog(null, "Action cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- Helper: today ---
    // Returns today's date as a formatted string, e.g. "07-05-2026"
    static String today() {
        return LocalDate.now().format(fmt);
    }
}