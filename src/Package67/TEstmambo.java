package Package67;

import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

class Book {
    String bookId;                  // e.g. "B001"
    String title;                   // book title
    String author;                  // author name
    String category;                // e.g. "Programming"
    boolean isBorrowed;             // true = taken, false = available

    public Book(String bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isBorrowed = false;    // available at start
    }

    public boolean isAvailable() {
        return !isBorrowed;         // true if not borrowed
    }

    public void borrowCopy() {
        isBorrowed = true;          // mark as taken
    }

    public void returnCopy() {
        isBorrowed = false;         // mark as available again
    }

    public String getBookInfo() {
        String status = isAvailable() ? "Available" : "Not Available"; // ternary = short if/else
        return "[" + bookId + "] " + title
            + "\n    Author   : " + author
            + "\n    Category : " + category
            + "\n    Status   : " + status;
    }
}

class BorrowRecord {
    String recordId;                // e.g. "R001"
    String borrowerName;            // person who borrowed
    String bookId;                  // which book
    String bookTitle;               // book title saved here for easy display
    String borrowDate;              // date borrowed e.g. "09-05-2026"
    String dueDate;                 // must return by this date
    String returnDate;              // actual return date, "-" if not yet
    double fineAmount;              // RM0.50 per day overdue
    boolean isReturned;             // false = still out, true = returned

    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy"); // date format
    public BorrowRecord(String recordId, String borrowerName, String bookId, String bookTitle, String borrowDate, int loanDays) {
        this.recordId = recordId;
        this.borrowerName = borrowerName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.borrowDate = borrowDate;
        this.fineAmount = 0;        // no fine at start
        this.isReturned = false;    // not returned yet
        this.returnDate = "-";      // "-" means not returned yet
        LocalDate borrow = LocalDate.parse(borrowDate, fmt);                   // convert string to date
        this.dueDate = borrow.plusDays(loanDays).format(fmt);                  // add chosen days to get due date
    }

    public void markReturned(String returnDate) {
        this.isReturned = true;
        this.returnDate = returnDate;
        LocalDate due = LocalDate.parse(dueDate, fmt);                         // parse due date
        LocalDate returned = LocalDate.parse(returnDate, fmt);                 // parse return date
        long daysLate = ChronoUnit.DAYS.between(due, returned);                // calculate days late
        if (daysLate > 0) {
            this.fineAmount = daysLate * 0.50;                                 // RM0.50 per late day
        }
    }

    public String getRecordInfo() {
        String status = isReturned ? "Returned on " + returnDate : "Active (Due: " + dueDate + ")"; // ternary
        String info = "[" + recordId + "] " + bookTitle
            + "\n    Borrower  : " + borrowerName
            + "\n    Borrowed  : " + borrowDate
            + "\n    Due Date  : " + dueDate
            + "\n    Status    : " + status;
        if (fineAmount > 0) {
            info = info + "\n    Fine      : RM " + String.format("%.2f", fineAmount); // only show if got fine
        }
        return info;
    }

    public String toFileLine() {                                               // save record as one line in file
        return recordId + "|" + borrowerName + "|" + bookId + "|" + bookTitle + "|"
            + borrowDate + "|" + dueDate + "|" + returnDate + "|" + fineAmount + "|" + isReturned;
    }

    public static BorrowRecord fromFileLine(String line) {                     // rebuild record from saved line
        String[] p = line.split("\\|");                                        // split by "|"
        BorrowRecord r = new BorrowRecord(p[0], p[1], p[2], p[3], p[4], 0);   // loanDays=0, dueDate overwritten below
        r.dueDate    = p[5];
        r.returnDate = p[6];
        r.fineAmount = Double.parseDouble(p[7]);                               // String -> double
        r.isReturned = Boolean.parseBoolean(p[8]);                             // String -> boolean
        return r;
    }
}

class LibraData {
    ArrayList<Book> bookList = new ArrayList<>();                               // list of all books
    ArrayList<BorrowRecord> recordList = new ArrayList<>();                     // list of all borrow records
    int recordCounter = 1;                                                     // used to make R001, R002...
    static final int MAX_BORROW = 3;                                           // max books per person
    static final String FILE_NAME = "borrow_records.txt";                      // file to save records

    public LibraData() {
        bookList.add(new Book("B001", "Introduction to Java Programming", "Y. Daniel Liang", "Programming"));
        bookList.add(new Book("B002", "Data Structures and Algorithms", "Michael T. Goodrich", "Programming"));
        bookList.add(new Book("B003", "Computer Networks", "Andrew Tanenbaum", "Networking"));
        bookList.add(new Book("B004", "Operating System Concepts", "Silberschatz", "Systems"));
        bookList.add(new Book("B005", "Database System Concepts", "Abraham Silberschatz", "Database"));
        bookList.add(new Book("B006", "Calculus Early Transcendentals", "James Stewart", "Mathematics"));
        bookList.add(new Book("B007", "Physics for Scientists", "Serway & Jewett", "Science"));
        bookList.add(new Book("B008", "Artificial Intelligence", "Stuart Russell", "Programming"));
        loadFromFile();                                                         // load saved records on startup
    }

    public Book findBook(String id) {
        for (Book b : bookList) {
            if (b.bookId.equalsIgnoreCase(id.trim())) return b;                // return book if ID matches
        }
        return null;                                                           // not found
    }

    public int countActiveBorrows(String name) {
        int count = 0;
        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.borrowerName.equalsIgnoreCase(name)) count++; // count unreturned records
        }
        return count;
    }

    public String searchBooks(String keyword) {
        String kw = keyword.toLowerCase();                                     // make lowercase for comparison
        String result = "";
        int count = 0;
        for (Book b : bookList) {
            if (b.bookId.toLowerCase().contains(kw) || b.title.toLowerCase().contains(kw)
                || b.author.toLowerCase().contains(kw) || b.category.toLowerCase().contains(kw)) {
                result = result + b.getBookInfo() + "\n\n";                    // add matching book to result
                count++;
            }
        }
        return count == 0 ? "No books found for \"" + keyword + "\"." : "Found " + count + " result(s):\n\n" + result;
    }

    public String getAllBooks() {
        String result = "=== Book Catalogue ===\n\n";
        for (Book b : bookList) result = result + b.getBookInfo() + "\n\n";    // add each book to result
        return result;
    }

    public String borrowBook(String borrowerName, String bookId, int loanDays, String today) {
        Book book = findBook(bookId);
        if (book == null)
            return "ERROR: Book ID \"" + bookId + "\" not found.";             // book does not exist
        if (!book.isAvailable())
            return "ERROR: \"" + book.title + "\" is not available.";          // no copy available
        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.bookId.equalsIgnoreCase(bookId) && r.borrowerName.equalsIgnoreCase(borrowerName))
                return "ERROR: " + borrowerName + " already borrowed this book."; // duplicate borrow check
        }
        if (countActiveBorrows(borrowerName) >= MAX_BORROW)
            return "ERROR: " + borrowerName + " already has " + MAX_BORROW + " books. Please return one first."; // limit check

        String recordId = "R" + String.format("%03d", recordCounter++);        // generate ID e.g. R001
        BorrowRecord rec = new BorrowRecord(recordId, borrowerName, bookId, book.title, today, loanDays);
        recordList.add(rec);                                                   // add to list
        book.borrowCopy();                                                     // mark book as taken
        saveToFile();                                                          // save immediately
        return "Borrow successful!\n\n" + rec.getRecordInfo();
    }

    public String returnBook(String borrowerName, String bookId, String today) {
        BorrowRecord rec = null;
        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.borrowerName.equalsIgnoreCase(borrowerName) && r.bookId.equalsIgnoreCase(bookId)) {
                rec = r;                                                        // found the active record
                break;
            }
        }
        if (rec == null)
            return "ERROR: No active borrow found for \"" + borrowerName + "\" with Book ID \"" + bookId + "\".";

        rec.markReturned(today);                                               // mark returned + calculate fine
        Book book = findBook(rec.bookId);
        if (book != null) book.returnCopy();                                   // make book available again
        saveToFile();

        String result = "Return successful!\n\n" + rec.getRecordInfo();
        if (rec.fineAmount > 0)
            result = result + "\n\nOverdue fine: RM " + String.format("%.2f", rec.fineAmount) + ". Please pay at the counter.";
        return result;
    }

    public String getBorrowHistory(String name) {
        String result = "";
        int count = 0;
        for (BorrowRecord r : recordList) {
            if (r.borrowerName.toLowerCase().contains(name.toLowerCase())) {
                result = result + r.getRecordInfo() + "\n\n";                  // add matching record
                count++;
            }
        }
        return count == 0 ? "No records found for \"" + name + "\"."
            : "Borrow History for \"" + name + "\" (" + count + " record(s)):\n\n" + result;
    }

    public void saveToFile() {
        try {
            FileWriter fw = new FileWriter(FILE_NAME);
            fw.write(recordCounter + "\n");                                    // first line = counter
            for (BorrowRecord r : recordList) fw.write(r.toFileLine() + "\n"); // one record per line
            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Save error: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE, icon);
        }
    }

    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;                                            // no file yet, skip
        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
            String firstLine = br.readLine().trim();
            if (firstLine.startsWith("COUNTER:")) firstLine = firstLine.replace("COUNTER:", ""); // handle old format
            recordCounter = Integer.parseInt(firstLine);                       // restore counter
            String line = br.readLine();
            while (line != null) {
                if (!line.trim().isEmpty()) {
                    BorrowRecord r = BorrowRecord.fromFileLine(line.trim());
                    recordList.add(r);                                         // add to list
                    if (!r.isReturned) {
                        Book b = findBook(r.bookId);
                        if (b != null) b.isBorrowed = true;                    // mark book as still borrowed
                    }
                }
                line = br.readLine();
            }
            br.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Load error: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE, icon);
        }
    }
}

public class TEstmambo {

    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");  // date format
    static LibraData db = new LibraData();                                     // our database object
    static int totalBorrowed = 0;                                              // session counter
    static int totalReturned = 0;                                              // session counter
    static ImageIcon icon = null;                                              // image icon for dialogs

    public static void main(String[] args) {
        // Load image and resize to 64x64 so it fits nicely in the dialog
        // Put library.png inside src/Package67/ folder
        try {
            ImageIcon raw = new ImageIcon("src/Package67/library.png");        // load the image
            Image scaled = raw.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH); // resize to 64x64
            icon = new ImageIcon(scaled);                                      // wrap back as ImageIcon
        } catch (Exception e) {
            icon = null;                                                       // no image, just skip
        }

        if (!showLoginScreen()) {                                              // stop if login fails
            JOptionPane.showMessageDialog(null, "Login failed. Program will close.", "Access Denied", JOptionPane.WARNING_MESSAGE, icon);
            return;
        }

        JOptionPane.showMessageDialog(null,
            "Welcome, TEam Trio Ensem!\n"
            + "------------------------------\n"
            + "xX69LibraSys67Xx\n"
            + "Library Management System\n\n"
            + "Team Trio Ensem",
            "Welcome", JOptionPane.INFORMATION_MESSAGE, icon);

        String[] options = { "Search Book", "Borrow Book", "Return Book", "Borrow History", "Exit" };
        boolean running = true;

        while (running) {                                                      // keep showing menu until Exit
            String msg = "Please choose an option:\n"
                + "------------------------------\n"
                + "Borrowed this session : " + totalBorrowed + "\n"
                + "Returned this session : " + totalReturned;

            int choice = JOptionPane.showOptionDialog(null, msg, "xX69LibraSys67Xx - Main Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, icon, options, options[0]);

            if (choice == 0) showSearchBook();
            else if (choice == 1) showBorrowBook();
            else if (choice == 2) showReturnBook();
            else if (choice == 3) showBorrowHistory();
            else if (choice == 4) running = !showExitScreen();
        }
    }

    static boolean showLoginScreen() {
        for (int attempts = 3; attempts > 0; attempts--) {                    // max 3 tries
            String user = JOptionPane.showInputDialog(null,
                "xX69LibraSys67Xx - Admin Login\n"
                + "------------------------------\n"
                + "This system made by Team Trio Ensem\n"
                + "Enter Username:", "Login", JOptionPane.QUESTION_MESSAGE);
            if (user == null) return false;                                    // Cancel pressed

            String password = JOptionPane.showInputDialog(null, "Enter Password now :", "Login", JOptionPane.QUESTION_MESSAGE);
            if (password == null) return false;                                // Cancel pressed

            if (user.trim().equalsIgnoreCase("admin") && password.equals("xx67xx")) return true; // login ok

            if (attempts > 1)
                JOptionPane.showMessageDialog(null, "Wrong credentials. " + (attempts - 1) + " attempt(s) left.", "Login Failed", JOptionPane.WARNING_MESSAGE, icon);
        }
        return false;                                                          // all 3 attempts failed
    }

    static boolean showExitScreen() {
        int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return false;                    // user said No

        JOptionPane.showMessageDialog(null,
            "=6=7= Session Summary =6=7=\n\n"
            + "Total Borrowed : " + totalBorrowed + "\n"
            + "Total Returned : " + totalReturned + "\n\n"
            + "Thank you for using xX69LibraSys67Xx!\n"
            + "Goodbye Mambo! 67 :)",
            "Goodbye", JOptionPane.INFORMATION_MESSAGE, icon);
        return true;                                                           // confirmed exit
    }

    static void showSearchBook() {
        String keyword = getInput("Enter keyword to search (title / author / category / ID):");
        if (keyword == null) { cancelled(); return; }
        JOptionPane.showMessageDialog(null, db.searchBooks(keyword), "Search Results", JOptionPane.PLAIN_MESSAGE, icon);
    }

    static void showBorrowBook() {
        String name = getInput("Enter borrower name:");
        if (name == null) { cancelled(); return; }

        javax.swing.JTextArea area = new javax.swing.JTextArea(db.getAllBooks()); // scrollable catalogue
        area.setEditable(false);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(500, 400));             // set popup size
        JOptionPane.showMessageDialog(null, scroll, "Book Catalogue", JOptionPane.PLAIN_MESSAGE, icon);

        String bookId = getInput("Enter Book ID to borrow:");
        if (bookId == null) { cancelled(); return; }

        // Ask loan period - 7 or 14 days only
        String[] loanOptions = { "7 days", "14 days" };
        int loanChoice = JOptionPane.showOptionDialog(null, "Select loan period:",
            "Loan Period", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            icon, loanOptions, loanOptions[0]);
        if (loanChoice < 0) { cancelled(); return; }                           // dialog closed without picking
        int loanDays = (loanChoice == 0) ? 7 : 14;                            // 0 = 7 days, 1 = 14 days

        String result = db.borrowBook(name, bookId, loanDays, today());
        if (result.startsWith("Borrow successful")) {
            totalBorrowed++;                                                   // update session counter
            JOptionPane.showMessageDialog(null, result, "Success", JOptionPane.INFORMATION_MESSAGE, icon);
        } else {
            JOptionPane.showMessageDialog(null, result, "Failed", JOptionPane.ERROR_MESSAGE, icon);
        }
    }

    static void showReturnBook() {
        String name = getInput("Enter your name:");
        if (name == null) { cancelled(); return; }

        String bookId = getInput("Enter Book ID to return (example: B001):");
        if (bookId == null) { cancelled(); return; }

        String result = db.returnBook(name, bookId, today());
        if (result.startsWith("Return successful")) {
            totalReturned++;                                                   // update session counter
            JOptionPane.showMessageDialog(null, result, "Success", JOptionPane.INFORMATION_MESSAGE, icon);
        } else {
            JOptionPane.showMessageDialog(null, result, "Failed", JOptionPane.ERROR_MESSAGE, icon);
        }
    }

    static void showBorrowHistory() {
        String name = getInput("Enter borrower name:");
        if (name == null) { cancelled(); return; }
        JOptionPane.showMessageDialog(null, db.getBorrowHistory(name), "Borrow History", JOptionPane.PLAIN_MESSAGE, icon);
    }

    static String getInput(String prompt) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, prompt, "Input", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;                                    // Cancel pressed
            if (!input.trim().isEmpty()) return input;                         // valid input
            JOptionPane.showMessageDialog(null, "Please enter something. Cannot be empty.", "Empty Input", JOptionPane.WARNING_MESSAGE, icon);
        }
    }

    static void cancelled() {
        JOptionPane.showMessageDialog(null, "Action cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    static String today() {
        return LocalDate.now().format(fmt);                                    // return today as "dd-MM-yyyy"
    }
}