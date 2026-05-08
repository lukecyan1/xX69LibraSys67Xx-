package Package67;

import javax.swing.*;
import java.awt.Image;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

class Book {

    String bookId;
    String title;
    String author;
    String category;
    boolean isBorrowed;

    public Book(String bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.isBorrowed = false;
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

        LocalDate borrow = LocalDate.parse(borrowDate, fmt);
        this.dueDate = borrow.plusDays(LOAN_DAYS).format(fmt);
    }

    public void markReturned(String returnDate) {
        this.isReturned = true;
        this.returnDate = returnDate;

        LocalDate due = LocalDate.parse(dueDate, fmt);
        LocalDate returned = LocalDate.parse(returnDate, fmt);
        long daysLate = ChronoUnit.DAYS.between(due, returned);

        if (daysLate > 0) {
            this.fineAmount = daysLate * 0.50;
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
            info += "\n    Fine      : RM " + String.format("%.2f", fineAmount);
        }

        return info;
    }

    public String toFileLine() {
        return recordId + "|" + borrowerName + "|" + bookId + "|" + bookTitle + "|"
            + borrowDate + "|" + dueDate + "|" + returnDate + "|" + fineAmount + "|" + isReturned;
    }

    public static BorrowRecord fromFileLine(String line) {
        String[] parts = line.split("\\|");
        BorrowRecord r = new BorrowRecord(parts[0], parts[1], parts[2], parts[3], parts[4]);
        r.dueDate    = parts[5];
        r.returnDate = parts[6];
        r.fineAmount = Double.parseDouble(parts[7]);
        r.isReturned = Boolean.parseBoolean(parts[8]);
        return r;
    }
}

class LibraData {

    ArrayList<Book> bookList = new ArrayList<>();
    ArrayList<BorrowRecord> recordList = new ArrayList<>();
    int recordCounter = 1;

    static final int MAX_BORROW = 3;
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

    public Book findBook(String id) {
        for (Book b : bookList) {
            if (b.bookId.equalsIgnoreCase(id.trim()))
                return b;
        }
        return null;
    }

    public BorrowRecord findRecord(String id) {
        for (BorrowRecord r : recordList) {
            if (r.recordId.equalsIgnoreCase(id.trim()))
                return r;
        }
        return null;
    }

    public int countActiveBorrows(String borrowerName) {
        int count = 0;
        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.borrowerName.equalsIgnoreCase(borrowerName))
                count++;
        }
        return count;
    }

    public String searchBooks(String keyword) {
        String kw = keyword.toLowerCase();
        String result = "";
        int count = 0;

        for (Book b : bookList) {
            if (b.bookId.toLowerCase().contains(kw)
                    || b.title.toLowerCase().contains(kw)
                    || b.author.toLowerCase().contains(kw)
                    || b.category.toLowerCase().contains(kw)) {
                result += b.getBookInfo() + "\n\n";
                count++;
            }
        }

        if (count == 0)
            return "No books found for \"" + keyword + "\".";

        return "Found " + count + " result(s):\n\n" + result;
    }

    public String getAllBooks() {
        String result = "=== Book Catalogue ===\n\n";
        for (Book b : bookList)
            result += b.getBookInfo() + "\n\n";
        return result;
    }

    public String borrowBook(String borrowerName, String bookId, String today) {
        Book book = findBook(bookId);
        if (book == null)
            return "ERROR: Book ID \"" + bookId + "\" not found.";

        if (!book.isAvailable())
            return "ERROR: \"" + book.title + "\" is not available.";

        for (BorrowRecord r : recordList) {
            if (!r.isReturned && r.bookId.equalsIgnoreCase(bookId) && r.borrowerName.equalsIgnoreCase(borrowerName))
                return "ERROR: " + borrowerName + " already borrowed this book and has not returned it.";
        }

        if (countActiveBorrows(borrowerName) >= MAX_BORROW)
            return "ERROR: " + borrowerName + " has reached the maximum of " + MAX_BORROW + " borrowed books.\nPlease return a book first.";

        String recordId = "R" + String.format("%03d", recordCounter++);
        BorrowRecord rec = new BorrowRecord(recordId, borrowerName, bookId, book.title, today);
        recordList.add(rec);
        book.borrowCopy();
        saveToFile();

        return "Borrow successful!\n\n" + rec.getRecordInfo();
    }

    public String returnBook(String borrowerName, String bookId, String today) {
        BorrowRecord rec = null;

        for (BorrowRecord r : recordList) {
            if (!r.isReturned
                    && r.borrowerName.equalsIgnoreCase(borrowerName)
                    && r.bookId.equalsIgnoreCase(bookId)) {
                rec = r;
                break;
            }
        }

        if (rec == null)
            return "ERROR: No active borrow found for \"" + borrowerName + "\" with Book ID \"" + bookId + "\".";

        rec.markReturned(today);

        Book book = findBook(rec.bookId);
        if (book != null)
            book.returnCopy();

        saveToFile();

        String result = "Return successful!\n\n" + rec.getRecordInfo();

        if (rec.fineAmount > 0)
            result += "\n\nThis book is overdue. Please pay RM " + String.format("%.2f", rec.fineAmount) + " at the counter.";

        return result;
    }

    public String getActiveBorrows() {
        String result = "";
        int count = 0;

        for (BorrowRecord r : recordList) {
            if (!r.isReturned) {
                result += r.getRecordInfo() + "\n\n";
                count++;
            }
        }

        if (count == 0)
            return "No active borrows at the moment.";

        return "Active Borrows (" + count + "):\n\n" + result;
    }

    public String getBorrowHistory(String name) {
        String result = "";
        int count = 0;

        for (BorrowRecord r : recordList) {
            if (r.borrowerName.toLowerCase().contains(name.toLowerCase())) {
                result += r.getRecordInfo() + "\n\n";
                count++;
            }
        }

        if (count == 0)
            return "No records found for \"" + name + "\".";

        return "Borrow History for \"" + name + "\" (" + count + " record(s)):\n\n" + result;
    }

    public void saveToFile() {
        try {
            FileWriter fw = new FileWriter(FILE_NAME);
            fw.write(recordCounter + "\n");
            for (BorrowRecord r : recordList)
                fw.write(r.toFileLine() + "\n");
            fw.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Save error: " + e.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void loadFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));

            String firstLine = br.readLine().trim();
            if (firstLine.startsWith("COUNTER:"))
                firstLine = firstLine.replace("COUNTER:", "");
            recordCounter = Integer.parseInt(firstLine);

            String line = br.readLine();
            while (line != null) {
                if (!line.trim().isEmpty()) {
                    BorrowRecord r = BorrowRecord.fromFileLine(line.trim());
                    recordList.add(r);
                    if (!r.isReturned) {
                        Book b = findBook(r.bookId);
                        if (b != null) b.isBorrowed = true;
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

public class TEstmambo {

    static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    static LibraData db = new LibraData();
    static int totalBorrowed = 0;
    static int totalReturned = 0;
    static ImageIcon icon;

    public static void main(String[] args) {

        try {
            ImageIcon raw = new ImageIcon("src/Package67/library.png");
            Image scaled = raw.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        } catch (Exception e) {
            icon = null;
        }

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
            "Welcome", JOptionPane.INFORMATION_MESSAGE, icon);

        String[] options = { "Search Book", "Borrow Book", "Return Book", "Active Borrows", "Borrow History", "Exit" };
        boolean running = true;

        while (running) {
            String msg = "Please choose an option:\n"
                + "------------------------------\n"
                + "Borrowed this session : " + totalBorrowed + "\n"
                + "Returned this session : " + totalReturned;

            int choice = JOptionPane.showOptionDialog(null, msg,
                "xX69LibraSys67Xx - Main Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                icon, options, options[0]);

            if (choice == 0) showSearchBook();
            else if (choice == 1) showBorrowBook();
            else if (choice == 2) showReturnBook();
            else if (choice == 3) showActiveBorrows();
            else if (choice == 4) showBorrowHistory();
            else if (choice == 5) running = !showExitScreen();
        }
    }

    static boolean showLoginScreen() {
        for (int attempts = 3; attempts > 0; attempts--) {
            String user = JOptionPane.showInputDialog(null,
                "xX69LibraSys67Xx - Admin Login\n"
                + "------------------------------\n"
                + "This system made by Team Trio Ensem\n"
                + "Enter Username:",
                "Login", JOptionPane.QUESTION_MESSAGE);

            if (user == null) return false;

            String password = JOptionPane.showInputDialog(null,
                "Enter Password now :",
                "Login", JOptionPane.QUESTION_MESSAGE);

            if (password == null) return false;

            if (user.trim().equalsIgnoreCase("HJD") && password.equals("xx67xx"))
                return true;

            if (attempts > 1)
                JOptionPane.showMessageDialog(null,
                    "Wrong credentials. " + (attempts - 1) + " attempt(s) left.",
                    "Login Failed", JOptionPane.WARNING_MESSAGE);
        }
        return false;
    }

    static boolean showExitScreen() {
        int answer = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
        if (answer != JOptionPane.YES_OPTION) return false;

        JOptionPane.showMessageDialog(null,
            "=6=7= Session Summary =6=7=\n\n"
            + "Total Borrowed : " + totalBorrowed + "\n"
            + "Total Returned : " + totalReturned + "\n\n"
            + "Thank you for using xX69LibraSys67Xx!\n"
            + "Goodbye Mambo! 67 :)",
            "Goodbye", JOptionPane.INFORMATION_MESSAGE, icon);

        return true;
    }

    static void showSearchBook() {
        String keyword = getInput("Enter keyword to search (title / author / category / ID):");
        if (keyword == null) { cancelled(); return; }
        JOptionPane.showMessageDialog(null, db.searchBooks(keyword), "Search Results", JOptionPane.PLAIN_MESSAGE, icon);
    }

    static void showBorrowBook() {
        String name = getInput("Enter borrower name:");
        if (name == null) { cancelled(); return; }

        JOptionPane.showMessageDialog(null, db.getAllBooks(), "Book Catalogue", JOptionPane.PLAIN_MESSAGE, icon);

        String bookId = getInput("Enter Book ID to borrow:");
        if (bookId == null) { cancelled(); return; }

        String result = db.borrowBook(name, bookId, today());

        if (result.startsWith("Borrow successful")) {
            totalBorrowed++;
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
            totalReturned++;
            JOptionPane.showMessageDialog(null, result, "Success", JOptionPane.INFORMATION_MESSAGE, icon);
        } else {
            JOptionPane.showMessageDialog(null, result, "Failed", JOptionPane.ERROR_MESSAGE, icon);
        }
    }

    static void showActiveBorrows() {
        JOptionPane.showMessageDialog(null, db.getActiveBorrows(), "Active Borrows", JOptionPane.PLAIN_MESSAGE, icon);
    }

    static void showBorrowHistory() {
        String name = getInput("Enter borrower name:");
        if (name == null) { cancelled(); return; }
        JOptionPane.showMessageDialog(null, db.getBorrowHistory(name), "Borrow History", JOptionPane.PLAIN_MESSAGE, icon);
    }

    static String getInput(String prompt) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, prompt, "Input", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;
            if (!input.trim().isEmpty()) return input;
            JOptionPane.showMessageDialog(null, "Please enter something. Cannot be empty.", "Empty Input", JOptionPane.WARNING_MESSAGE);
        }
    }

    static void cancelled() {
        JOptionPane.showMessageDialog(null, "Action cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    static String today() {
        return LocalDate.now().format(fmt);
    }
}