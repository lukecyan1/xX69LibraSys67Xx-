import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

// done - book class, stores book info
class Book {

    String bookId;
    String title;
    String author;
    String category;
    int totalCopies;
    int availableCopies;

    public Book(String bookId, String title, String author, String category, int totalCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public boolean isAvailable() {
        return availableCopies > 0;
    }

    public void borrowCopy() {
        if (availableCopies > 0) availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    public String getBookInfo() {
        String status = isAvailable()
            ? "Available (" + availableCopies + "/" + totalCopies + " copies left)"
            : "Not Available";

        return "[" + bookId + "] " + title
            + "\n    Author   : " + author
            + "\n    Category : " + category
            + "\n    Status   : " + status;
    }
}

// TODO - BorrowRecord class, belum touch lagi ni
// fields: recordId, borrowerName, bookId, bookTitle, borrowDate, dueDate, returnDate, fineAmount, isReturned
// constructor kena calculate dueDate - just add loanDays to borrowDate using LocalDate
// markReturned() - set isReturned to true, then calculate fine (RM0.50 per day overdue, guna ChronoUnit.DAYS)
// getRecordInfo() - return formatted string of the record, only show fine line if fineAmount > 0
class BorrowRecord {

    // TODO - letak fields sini, jangan lupa DateTimeFormatter fmt

    // TODO constructor - kira dueDate guna LocalDate.parse then plusDays
    public BorrowRecord(String recordId, String borrowerName, String bookId, String bookTitle, String borrowDate, int loanDays) {
        // TODO
    }

    // TODO - set returned, then figure out how many days late, darab with 0.50
    public void markReturned(String returnDate) {
        // TODO
    }

    // TODO
    public String getRecordInfo() {
        return ""; // TODO belum buat
    }
}

// done-ish - preloaded the books, methods belum  siap
class LibraData {

    ArrayList<Book> bookList = new ArrayList<>();
    ArrayList<BorrowRecord> recordList = new ArrayList<>();

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

    public Book findBook(String id) {
        for (Book b : bookList) {
            if (b.bookId.equalsIgnoreCase(id.trim())) {
                return b;
            }
        }
        return null;
    }

    public BorrowRecord findRecord(String id) {
        for (BorrowRecord r : recordList) {
            if (r.recordId.equalsIgnoreCase(id.trim())) {
                return r;
            }
        }
        return null;
    }

    // TODO - searchBooks(keyword), check against id/title/author/category, kalau takde return "No books found..."

    // TODO - getAllBooksTable(), build formatted table string guna String.format,  align properly

    // TODO - borrowBook(), check book exists, ada copies, borrower tak pinjam buku sama, then add new record

    // TODO - returnBook(), find record, mark returned, add copy back, tunjuk fine if any

    // TODO - getActiveBorrows(), return all records where isReturned == false

    // TODO - getBorrowHistory(name), search by borrower name, partial match fine too

    // TODO - shorten(text, max), private helper to trim strings that are too long for the table
}

// main class - login and menu done
public class LibraSys {

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
                null, options, options[0]);

            if (choice == 0) {
                // TODO - showSearchBook()
            } else if (choice == 1) {
                // TODO - showBorrowBook()
            } else if (choice == 2) {
                // TODO - showReturnBook()
            } else if (choice == 3) {
                // TODO - showActiveBorrows()
            } else if (choice == 4) {
                // TODO - showBorrowHistory()
            } else if (choice == 5) {
                running = !showExitScreen();
            }
        }
    }

    static boolean showLoginScreen() {
        for (int attempts = 3; attempts > 0; attempts--) {
            String user = JOptionPane.showInputDialog(null,
                "xX69LibraSys67Xx - Admin Login\n"
                + "------------------------------\n"
                + "Enter Username:",
                "Login", JOptionPane.QUESTION_MESSAGE);

            if (user == null) return false;

            JPasswordField pw = new JPasswordField(20);
            JPanel panel = new JPanel(new BorderLayout(0, 6));
            panel.add(new JLabel("Enter Password:"), BorderLayout.NORTH);
            panel.add(pw, BorderLayout.CENTER);

            int confirmed = JOptionPane.showConfirmDialog(null, panel, "Login", JOptionPane.OK_CANCEL_OPTION);
            if (confirmed != JOptionPane.OK_OPTION) return false;

            String password = new String(pw.getPassword());

            if (user.trim().equalsIgnoreCase("admin") && password.equals("library123")) {
                return true;
            }

            if (attempts > 1) {
                JOptionPane.showMessageDialog(null,
                    "Wrong credentials. " + (attempts - 1) + " attempt(s) left.",
                    "Login Failed", JOptionPane.WARNING_MESSAGE);
            }
        }
        return false;
    }

    static boolean showExitScreen() {
        int answer = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to exit?",
            "Exit", JOptionPane.YES_NO_OPTION);

        if (answer != JOptionPane.YES_OPTION) return false;

        JOptionPane.showMessageDialog(null,
            "=== Session Summary ===\n\n"
            + "Total Borrowed : " + totalBorrowed + "\n"
            + "Total Returned : " + totalReturned + "\n\n"
            + "Thank you for using xX69LibraSys67Xx!\n"
            + "Goodbye! :)",
            "Goodbye", JOptionPane.INFORMATION_MESSAGE);

        return true;
    }

    // TODO - showSearchBook(), tanya keyword then show results in scrollable dialog
    // TODO - showBorrowBook(), ask name, tunjuk catalogue, ask book ID, pick loan period, then borrow
    // TODO - showReturnBook(), tanya record ID, process return, show fine if overdue
    // TODO - showBorrowHistory(), ask name then show their history
    // TODO - showScrollable(), wrap JTextArea in JScrollPane then show as dialog, reusable helper
    // TODO - getInput(), keep looping until user types something, return null if cancelled
    // TODO - cancelled(), small helper je - show "Action cancelled" dialog
    // TODO - today(), return today's date as formatted string guna fmt
}