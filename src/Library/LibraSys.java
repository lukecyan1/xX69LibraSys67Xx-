/*
 * LibraSys.java
 * main class - entry point for xX69LibraSys67Xx
 * all dialogs are handled here using JOptionPane
 *
 * Team Trio Ensem
 * CBS25070712 - Muhammad Jad Fahmi
 * CBS25070550 - Khairin Darwisy
 * CBS25070541 - Adam Hasif
 */

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LibraSys {

    static final String ADMIN_USER = "admin";
    static final String ADMIN_PASS = "library123";

    static LibraData data          = new LibraData();
    static int       totalBorrowed = 0;
    static int       totalReturned = 0;

    static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static void main(String[] args) {

        // TODO: call handleLogin(), if returns false show warning "Access denied. Exiting." then stop
        
        JOptionPane.showMessageDialog(null,
            "Welcome, Admin!\n"
            + "─────────────────────────────\n"
            + "xX69LibraSys67Xx  –  Library Management System\n\n"
            + "Team Trio Ensem",
            "xX69LibraSys67Xx",
            JOptionPane.INFORMATION_MESSAGE);

        boolean running = true;
        while (running) {
            int choice = showMainMenu();
            switch (choice) {
                case 0: handleSearch();        break;
                case 1: handleBorrow();        break;
                case 2: handleReturn();        break;
                case 3: handleActiveBorrows(); break;
                case 4: handleHistory();       break;
                case 5: running = !handleExit(); break;
                default: break;
            }
        }
    }

    // -------------------------------------------------------
    // Login
    // -------------------------------------------------------
    static boolean handleLogin() {
        int attempts = 3;

        while (attempts > 0) {
            String user = JOptionPane.showInputDialog(null,
                "xX69LibraSys67Xx  –  Admin Login\n"
                + "─────────────────────────────\n"
                + "Username:",
                "Login",
                JOptionPane.QUESTION_MESSAGE);

            if (user == null) return false;

            JPasswordField pwField = new JPasswordField(20);
            JPanel p = new JPanel(new BorderLayout(0, 6));
            p.add(new JLabel("Password:"), BorderLayout.NORTH);
            p.add(pwField, BorderLayout.CENTER);

            int ok = JOptionPane.showConfirmDialog(null, p, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (ok != JOptionPane.OK_OPTION) return false;

            String pass = new String(pwField.getPassword());

            // TODO: check if user matches ADMIN_USER (case insensitive, trimmed)
            // AND pass matches ADMIN_PASS (exact)
            // if both match return true

            attempts--;
            if (attempts > 0) {
                JOptionPane.showMessageDialog(null,
                    "Incorrect username or password.\n" + attempts + " attempt(s) remaining.",
                    "Login Failed", JOptionPane.WARNING_MESSAGE);
            }
        }

        return false;
    }

    // -------------------------------------------------------
    // Main menu
    // -------------------------------------------------------
    static int showMainMenu() {
        String[] options = {
            "Search Book",
            "Borrow Book",
            "Return Book",
            "Active Borrows",
            "Borrow History",
            "Exit"
        };

        // TODO: replace menuMsg with the full version, copy this exactly:
        // "Select an option:\n"
        // + "────────────────────────────\n"
        // + "  Borrowed this session : " + totalBorrowed + "\n"
        // + "  Returned this session : " + totalReturned
        String menuMsg = "Select an option:";

        int choice = JOptionPane.showOptionDialog(null,
            menuMsg,
            "xX69LibraSys67Xx  –  Main Menu",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]);

        return (choice == JOptionPane.CLOSED_OPTION) ? 99 : choice;
    }

    // -------------------------------------------------------
    // Search
    // -------------------------------------------------------
    static void handleSearch() {
        String keyword = promptNonEmpty(
            "Enter title, author, category, or Book ID to search:\n(Partial keywords work)",
            "Search Book");

        if (keyword == null) { showCancelled(); return; }

        String result = data.searchBook(keyword.trim());
        // TODO: show result using showScrollable(), title = "Search results for \"keyword\""
    }

    // -------------------------------------------------------
    // Borrow
    // -------------------------------------------------------
    static void handleBorrow() {
        String borrowerName = promptNonEmpty("Enter borrower name:", "Borrow Book");
        if (borrowerName == null) { showCancelled(); return; }

        showBookList();

        String bookId = null;
        while (true) {
            bookId = promptNonEmpty("Enter Book ID (e.g. B001):", "Borrow Book");
            if (bookId == null) { showCancelled(); return; }

            bookId = bookId.replaceAll("\\s+", "").toUpperCase();

            if (!bookId.matches("B\\d+")) {
                int retry = JOptionPane.showConfirmDialog(null,
                    "\"" + bookId + "\" doesn't look like a valid Book ID.\n"
                    + "Book IDs look like: B001, B002, B003 ...\n\nTry again?",
                    "Invalid Format",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if (retry == JOptionPane.YES_OPTION) continue;
            }
            break;
        }

        String[] loanOptions = {"7 Days", "14 Days"};
        int loanChoice = JOptionPane.showOptionDialog(null,
            "Select loan period:", "Loan Period",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
            null, loanOptions, loanOptions[0]);

        if (loanChoice == JOptionPane.CLOSED_OPTION) { showCancelled(); return; }

        int loanDays = (loanChoice == 0) ? 7 : 14;

        String receipt = data.borrowBook(borrowerName.trim(), bookId, loanDays, getTodayDate());

        // TODO: if receipt starts with "ERROR" show error dialog
        // else increment totalBorrowed and show info dialog with receipt
    }

    // -------------------------------------------------------
    // Return
    // -------------------------------------------------------
    static void handleReturn() {
        String recordId = promptNonEmpty(
            "Enter Record ID to return (e.g. R001).\nTip: Use 'Active Borrows' to look up your Record ID.",
            "Return Book");

        if (recordId == null) { showCancelled(); return; }

        String result = data.returnBook(recordId.trim().toUpperCase(), getTodayDate());

        if (result.startsWith("ERROR")) {
            JOptionPane.showMessageDialog(null, result, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            // TODO: increment totalReturned and show success dialog
        }
    }

    // -------------------------------------------------------
    // Active borrows
    // -------------------------------------------------------
    static void handleActiveBorrows() {
        // TODO: get active records from data and show using showScrollable()
        // title = "Currently Active Borrows"
    }

    // -------------------------------------------------------
    // History
    // -------------------------------------------------------
    static void handleHistory() {
        String keyword = promptNonEmpty(
            "Enter borrower name to search history:\n(Partial names ok)",
            "Borrowing History");

        if (keyword == null) { showCancelled(); return; }

        // TODO: call data.getBorrowHistory() and show with showScrollable()
        // title = "Borrowing History - \"keyword\""
    }

    // -------------------------------------------------------
    // Exit
    // -------------------------------------------------------
    static boolean handleExit() {
        int confirm = JOptionPane.showConfirmDialog(null,
            "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            // TODO: show the session summary dialog, copy this exactly:
            // JOptionPane.showMessageDialog(null,
            //     "=== Session Summary ===\n\n"
            //     + "Total Borrowed : " + totalBorrowed + "\n"
            //     + "Total Returned : " + totalReturned + "\n\n"
            //     + "Thank you for using xX69LibraSys67Xx!\nGoodbye :)",
            //     "Session Summary",
            //     JOptionPane.INFORMATION_MESSAGE);
            return true;
        }
        return false;
    }

    // -------------------------------------------------------
    // UI helpers - done, dont change
    // -------------------------------------------------------

    static void showScrollable(String title, String content) {
        JTextArea area = new JTextArea(content);
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setMargin(new Insets(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(620, 300));

        JOptionPane.showMessageDialog(null, scroll, title, JOptionPane.INFORMATION_MESSAGE);
    }

    static void showBookList() {
        JTextArea area = new JTextArea(data.getAllBooksTable());
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        area.setMargin(new Insets(6, 8, 6, 8));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(700, 220));

        JOptionPane.showMessageDialog(null, scroll, "Book Catalogue", JOptionPane.INFORMATION_MESSAGE);
    }

    static String getTodayDate() {
        return LocalDate.now().format(FMT);
    }

    static String promptNonEmpty(String message, String title) {
        while (true) {
            String input = JOptionPane.showInputDialog(null, message, title, JOptionPane.QUESTION_MESSAGE);
            if (input == null) return null;
            if (!input.trim().isEmpty()) return input;
            JOptionPane.showMessageDialog(null,
                "Input cannot be empty. Please try again.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    static void showCancelled() {
        JOptionPane.showMessageDialog(null,
            "Action cancelled. Returning to main menu.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
    }
}