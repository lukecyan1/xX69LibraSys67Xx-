package Package67;

import javax.swing.JOptionPane;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

// Stores info about one book
class Book {
    String bookId;
    String title;
    String author;
    String category;
    boolean isBorrowed; // true = taken, false = available

    public Book(String bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isBorrowed = false; // available at start
    }

    public boolean isAvailable() {
        return !isBorrowed;
    }

    public void borrowCopy() {
        isBorrowed = true;
    }

    public void returnCopy() {
        isBorrowed = false;
    }

    public String getBookInfo() {
        String status = isAvailable() ? "Available" : "Not Available";
        return "[" + bookId + "] " + title
            + "\n    Author   : " + author
            + "\n    Category : " + category
            + "\n    Status   : " + status;
    }
}

// Stores one borrowing transaction, loan fixed at 14 days
class BorrowRecord {
    String recordId;
    String borrowerName;
    String bookId;
    String bookTitle;
    String borrowDate;
    String dueDate;
    String returnDate;
    double fineAmount;
    boolean isReturned;

    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    static final int LOAN_DAYS = 14;

    public BorrowRecord(String recordId, String borrowerName, String bookId, String bookTitle, String borrowDate) {
        this.recordId = recordId;
        this.borrowerName = borrowerName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.fineAmount = 0;
        this.isReturned = false;
        this.returnDate = "-";
        // due date = borrow date + 14 days
        LocalDate borrow = LocalDate.parse(borrowDate, fmt);
        this.dueDate = borrow.plusDays(LOAN_DAYS).format(fmt);
    }

    // Call when book is returned - also calculates fine if late
    public void markReturned(String returnDate) {
        this.isReturned = true;
        this.returnDate = returnDate;
        LocalDate due = LocalDate.parse(dueDate, fmt);
        LocalDate returned = LocalDate.parse(returnDate, fmt);
        long daysLate = ChronoUnit.DAYS.between(due, returned);
        if (daysLate > 0) {
            this.fineAmount = daysLate * 0.50; // RM0.50 per day late
        }
    }

    public String getRecordInfo() {
        String status = isReturned ? "Returned on " + returnDate : "Active (Due: " + dueDate + ")";
        String info = "[" + recordId + "] " + bookTitle
            + "\n    Borrower  : " + borrowerName
            + "\n    Borrowed  : " + borrowDate
            + "\n    Due Date  : " + dueDate
            + "\n    Status    : " + status;
        if (fineAmount > 0) {
            info = info + "\n    Fine      : RM " + String.format("%.2f", fineAmount);
        }
        return info;
    }

    // Save record as one line: R001|Ali|B002|...|false
    public String toFileLine() {
        return recordId + "|" + borrowerName + "|" + bookId + "|" + bookTitle + "|"
            + borrowDate + "|" + dueDate + "|" + returnDate + "|" + fineAmount + "|" + isReturned;
    }

    // Rebuild a BorrowRecord from one saved line
    public static BorrowRecord fromFileLine(String line) {
        String[] p = line.split("\\|");
        BorrowRecord r = new BorrowRecord(p[0], p[1], p[2], p[3], p[4]);
        r.dueDate    = p[5];
        r.returnDate = p[6];
        r.fineAmount = Double.parseDouble(p[7]);
        r.isReturned = Boolean.parseBoolean(p[8]);
        return r;
    }
}

// Stores all books and records, handles all logic
class LibraData {
    ArrayList<Book> bookList = new ArrayList<>();
    ArrayList<BorrowRecord> recordList = new ArrayList<>();
    int recordCounter = 1;
    static final int MAX_BORROW = 3;       // max books per person
    static final String FILE_NAME = "borrow_records.txt";

    public LibraData() {
        bookList.add(new Book("B001", "Introduction to Java Programming", "Y. Daniel Liang", "Programming"));
        bookList.add(new Book("B002", "Data Structures and Algorithms", "Michael T. Goodrich", "Programming"));
        bookList.add(new Book("B003", "Computer Networks", "Andrew Tanenbaum", "Networking"));
        bookList.add(new Book("B004", "Operating System Concepts", "Silberschatz", "Systems"));
        bookList.add(new Book("B005", "Database System Concepts", "Abraham Silberschatz", "Database"));
        bookList.add(new Book("B006", "Calculus Early Transcendentals", "James Stewart", "Mathematics"));
        bookList.add(new Book("B007", "Physics for Scientists", "Serway & Jewett", "Science"));
        bookList.add(new Book("B008", "Artificial Intelligence", "Stuart Russell", "Programming"));
        loadFromFile();
    }

    // Find book by ID, returns null if not found
    public Book findBook(String id) {
        for (Book b : bookList) {
            if (b.bookId.equalsIgnoreCase(id.trim())) return b;
        }
        return null;
    }

    // Count how many books this person is currently borrowing
    public int countActiveBorrows(String name) {
        int count = 0;
        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.borrowerName.equalsIgnoreCase(name)) count++;
        }
        return count;
    }

    // Search books by keyword - checks ID, title, author, category
    public String searchBooks(String keyword) {
        String kw = keyword.toLowerCase();
        String result = "";
        int count = 0;
        for (Book b : bookList) {
            if (b.bookId.toLowerCase().contains(kw) || b.title.toLowerCase().contains(kw)
                || b.author.toLowerCase().contains(kw) || b.category.toLowerCase().contains(kw)) {
                result = result + b.getBookInfo() + "\n\n";
                count++;
            }
        }
        return count == 0 ? "No books found for \"" + keyword + "\"." : "Found " + count + " result(s):\n\n" + result;
    }

    // Returns all books as formatted text
    public String getAllBooks() {
        String result = "=== Book Catalogue ===\n\n";
        for (Book b : bookList) result = result + b.getBookInfo() + "\n\n";
        return result;
    }

    // Borrow a book - always 14 days, max 3 books per person
    public String borrowBook(String borrowerName, String bookId, String today) {
        Book book = findBook(bookId);
        if (book == null)
            return "ERROR: Book ID \"" + bookId + "\" not found.";
        if (!book.isAvailable())
            return "ERROR: \"" + book.title + "\" is not available.";
        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.bookId.equalsIgnoreCase(bookId) && r.borrowerName.equalsIgnoreCase(borrowerName))
                return "ERROR: " + borrowerName + " already borrowed this book.";
        }
        if (countActiveBorrows(borrowerName) >= MAX_BORROW)
            return "ERROR: " + borrowerName + " already has " + MAX_BORROW + " books. Please return one first.";

        String recordId = "R" + String.format("%03d", recordCounter++);
        BorrowRecord rec = new BorrowRecord(recordId, borrowerName, bookId, book.title, today);
        recordList.add(rec);
        book.borrowCopy();
        saveToFile();
        return "Borrow successful!\n\n" + rec.getRecordInfo();
    }

    // Return a book using name + book ID
    public String returnBook(String borrowerName, String bookId, String today) {
        BorrowRecord rec = null;
        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.borrowerName.equalsIgnoreCase(borrowerName) && r.bookId.equalsIgnoreCase(bookId)) {
                rec = r;
                break;
            }
        }
        if (rec == null)
            return "ERROR: No active borrow found for \"" + borrowerName + "\" with Book ID \"" + bookId + "\".";

        rec.markReturned(today);
        Book book = findBook(rec.bookId);
        if (book != null) book.returnCopy();
        saveToFile();

        String result = "Return successful!\n\n" + rec.getRecordInfo();
        if (rec.fineAmount > 0)
            result = result + "\n\nOverdue fine: RM " + String.format("%.2f", rec.fineAmount) + ". Please pay at the counter.";
        return result;
    }

    // Get all borrow records for a person
    public String getBorrowHistory(String name) {
        String result = "";
        int count = 0;
        for (BorrowRecord r : recordList) {
            if (r.borrowerName.toLowerCase().contains(name.toLowerCase())) {
                result = result + r.getRecordInfo() + "\n\n";
                count++;
            }
        }
        return count == 0 ? "No records found for \"" + name + "\"."
            : "Borrow History for \"" + name + "\" (" + count + " record(s)):\n\n" + result;
    }

    // Save all records to file - first line is counter, then one record per line
    public void saveToFile() {
        try {
            FileWriter fw = new FileWriter(FILE_NAME);
            fw.write(recordCounter + "\n");
            for (BorrowRecord r : recordList) fw.write(r.toFileLine() + "\n");
            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Save error: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Load records from file when program starts
    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return; // no file yet, first time running
        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
            String firstLine = br.readLine().trim();
            if (firstLine.startsWith("COUNTER:")) firstLine = firstLine.replace("COUNTER:", "");
            recordCounter = Integer.parseInt(firstLine);
            String line = br.readLine();
            while (line != null) {
                if (!line.trim().isEmpty()) {
                    BorrowRecord r = BorrowRecord.fromFileLine(line.trim());
                    recordList.add(r);
                    if (!r.isReturned) {
                        Book b = findBook(r.bookId);
                        if (b != null) b.isBorrowed = true; // mark book as still borrowed
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Load error: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}

// Main class - login and menu, JOptionPane only
public class TEstmambo {

    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    static LibraData db = new LibraData();
    static int totalBorrowed = 0;
    static int totalReturned = 0;

    public static void main(String[] args) {
        if (!showLoginScreen()) {
            JOptionPane.showMessageDialog(null, "Login failed. Program will close.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null,
            "Welcome, Admin!\n"
            + "------------------------------\n"
            + "xX69LibraSys67Xx\n"
            + "Library Management System\n\n"
            + "Team Trio Ensem",
            "Welcome", JOptionPane.INFORMATION_MESSAGE);

        String[] options = { "Search Book", "Borrow Book", "Return Book", "Borrow History", "Exit" };
        boolean running = true;

        while (running) {
            String msg = "Please choose an option:\n"
                + "------------------------------\n"
                + "Borrowed this session : " + totalBorrowed + "\n"
                + "Returned this session : " + totalReturned;

            int choice = JOptionPane.showOptionDialog(null, msg, "xX69LibraSys67Xx - Main Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            if (choice == 0) showSearchBook();
            else if (choice == 1) showBorrowBook();
            else if (choice == 2) showReturnBook();
            else if (choice == 3) showBorrowHistory();
            else if (choice == 4) running = !showExitScreen();
        }
    }

    // Login - 3 attempts allowed
    static boolean showLoginScreen() {
        for (int attempts = 3; attempts > 0; attempts--) {
            String user = JOptionPane.showInputDialog(null,
                "xX69LibraSys67Xx - Admin Login\n"
                + "------------------------------\n"
                + "This system made by Team Trio Ensem\n"
                + "Enter Username:", "Login", JOptionPane.QUESTION_MESSAGE);
            if (user == null) return false;

            String password = JOptionPane.showInputDialog(null, "Enter Password now :", "Login", JOptionPane.QUESTION_MESSAGE);
            if (password == null) return false;

            if (user.trim().equalsIgnoreCase("HJD") && password.equals("xx67xx")) return true;

            if (attempts > 1)
                JOptionPane.showMessageDialog(null, "Wrong credentials. " + (attempts - 1) + " attempt(s) left.", "Login Failed", JOptionPane.WARNING_MESSAGE);
        }
        return false;
    }

    // Exit - confirm then show session summary
    static boolean showExitScreen() {
        int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return false;

        JOptionPane.showMessageDialog(null,
            "=6=7= Session Summary =6=7=\n\n"
            + "Total Borrowed : " + totalBorrowed + "\n"
            + "Total Returned : " + totalReturned + "\n\n"
            + "Thank you for using xX69LibraSys67Xx!\n"
            + "Goodbye Mambo! 67 :)",
            "Goodbye", JOptionPane.INFORMATION_MESSAGE);
        return true;
    }

    static void showSearchBook() {
        String keyword = getInput("Enter keyword to search (title / author / category / ID):");
        if (keyword == null) { cancelled(); return; }
        JOptionPane.showMessageDialog(null, db.searchBooks(keyword), "Search Results", JOptionPane.PLAIN_MESSAGE);
    }

    static void showBorrowBook() {
        String name = getInput("Enter borrower name:");
        if (name == null) { cancelled(); return; }

        // Show book catalogue in scrollable popup so OK button stays visible
        javax.swing.JTextArea area = new javax.swing.JTextArea(db.getAllBooks());
        area.setEditable(false);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(500, 400));
        JOptionPane.showMessageDialog(null, scroll, "Book Catalogue", JOptionPane.PLAIN_MESSAGE);

        String bookId = getInput("Enter Book ID to borrow:");
        if (bookId == null) { cancelled(); return; }

        String result = db.borrowBook(name, bookId, today());
        if (result.startsWith("Borrow successful")) {
            totalBorrowed++;
            JOptionPane.showMessageDialog(null, result, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, result, "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void showReturnBook() {
        String name = getInput("Enter your name:");
        if (name == null) { cancelled(); return; }

        String bookId = getInput("Enter Book ID to return (example: B001):");
        if (bookId == null) { cancelled(); return; }

        String result = db.returnBook(name, bookId, today());
        if (result.startsWith("Return successful")) {
            totalReturned++;
            JOptionPane.showMessageDialog(null, result, "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, result, "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    static void showBorrowHistory() {
        String name = getInput("Enter borrower name:");
        if (name == null) { cancelled(); return; }
        JOptionPane.showMessageDialog(null, db.getBorrowHistory(name), "Borrow History", JOptionPane.PLAIN_MESSAGE);
    }

    // Keeps asking until user types something, returns null if Cancel pressed
    static String getInput(String prompt) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, prompt, "Input", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;
            if (!input.trim().isEmpty()) return input;
            JOptionPane.showMessageDialog(null, "Please enter something. Cannot be empty.", "Empty Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    static void cancelled() {
        JOptionPane.showMessageDialog(null, "Action cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
    }

    static String today() {
        return LocalDate.now().format(fmt);
    }
}