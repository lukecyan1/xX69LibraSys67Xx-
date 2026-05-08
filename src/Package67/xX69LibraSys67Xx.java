package Package67;

import javax.swing.*;
import java.awt.Image;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class xX69LibraSys67Xx {

    static final String BASE_PATH = "src/Package67/";
    static final String BOOK_FILE = BASE_PATH + "books.txt";
    static final String RECORD_FILE = BASE_PATH + "records.txt";
    static final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    static int borrowedCount = 0;
    static int returnedCount = 0;

    static ImageIcon icon;

    public static void main(String[] args) {

        // load and resize image
        try {
            ImageIcon raw = new ImageIcon(BASE_PATH + "library.png");
            Image scaled = raw.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        } catch (Exception e) {
            icon = null;
        }

        if (!login()) return;
        menu();
    }

    // login
    static boolean login() {

        String user = "";
        while (user.trim().isEmpty()) {
            user = JOptionPane.showInputDialog(null, "Username:");
            if (user == null) return false;
            if (user.trim().isEmpty())
                JOptionPane.showMessageDialog(null, "username cannot be empty");
        }

        String pass = "";
        while (pass.trim().isEmpty()) {
            pass = JOptionPane.showInputDialog(null, "Password:");
            if (pass == null) return false;
            if (pass.trim().isEmpty())
                JOptionPane.showMessageDialog(null, "password cannot be empty");
        }

        if (user.trim().equals("admin") && pass.trim().equals("library123")) {
            JOptionPane.showMessageDialog(null, "Login ok", "Login", JOptionPane.INFORMATION_MESSAGE, icon);
            return true;
        }

        JOptionPane.showMessageDialog(null, "wrong user/pass");
        return false;
    }

    // main menu
    static void menu() {

        String[] opt = {"Search Book", "Borrow Book", "Return Book", "Borrowing History", "Exit"};
        int c;

        do {
            c = JOptionPane.showOptionDialog(null, "Library System", "menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    icon, opt, opt[0]);

            if (c == 0) searchBook();
            if (c == 1) borrowBook();
            if (c == 2) returnBook();
            if (c == 3) viewHistory();

        } while (c != 4 && c != JOptionPane.CLOSED_OPTION);

        exitSummary();
    }

    // search book
    static void searchBook() {

        String key = "";
        while (key.trim().isEmpty()) {
            key = JOptionPane.showInputDialog(null, "Enter keyword:");
            if (key == null) return;
            if (key.trim().isEmpty())
                JOptionPane.showMessageDialog(null, "keyword cannot be empty");
        }

        String res = "";

        try (BufferedReader br = new BufferedReader(new FileReader(BOOK_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.toLowerCase().contains(key.toLowerCase())) {
                    res += line + "\n";
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "file error");
            return;
        }

        if (res.equals("")) res = "no book found";

        JOptionPane.showMessageDialog(null, res, "Search Result", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    // borrow book
    static void borrowBook() {

        String name = "";
        while (name.trim().isEmpty()) {
            name = JOptionPane.showInputDialog(null, "Enter your name:");
            if (name == null) return;
            if (name.trim().isEmpty())
                JOptionPane.showMessageDialog(null, "name cannot be empty");
        }

        String bookId = "";
        while (bookId.trim().isEmpty()) {
            bookId = JOptionPane.showInputDialog(null, "Enter book ID:");
            if (bookId == null) return;
            if (bookId.trim().isEmpty())
                JOptionPane.showMessageDialog(null, "book id cannot be empty");
        }

        // 7 or 14 days only
        String[] dayOpt = {"7 days", "14 days"};
        int dayChoice = -1;
        while (dayChoice == -1 || dayChoice == JOptionPane.CLOSED_OPTION) {
            dayChoice = JOptionPane.showOptionDialog(null, "Select loan period:", "Borrow Book",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    icon, dayOpt, dayOpt[0]);
            if (dayChoice == JOptionPane.CLOSED_OPTION)
                JOptionPane.showMessageDialog(null, "please select loan period");
        }

        int days = (dayChoice == 0) ? 7 : 14;

        ArrayList<String> books = readFile(BOOK_FILE);
        ArrayList<String> records = readFile(RECORD_FILE);

        // duplicate check
        for (String r : records) {
            String[] p = r.split("\\|");
            if (p.length > 8 && p[1].equalsIgnoreCase(name) && p[2].equalsIgnoreCase(bookId) && p[8].equals("false")) {
                JOptionPane.showMessageDialog(null, "already borrowed this book");
                return;
            }
        }

        String title = "";
        boolean found = false;

        for (int i = 0; i < books.size(); i++) {
            String[] p = books.get(i).split("\\|");
            if (p[0].equalsIgnoreCase(bookId)) {
                int avail = Integer.parseInt(p[5]);
                if (avail <= 0) {
                    JOptionPane.showMessageDialog(null, "no copy left");
                    return;
                }
                avail--;
                p[5] = String.valueOf(avail);
                title = p[1];
                found = true;
                books.set(i, String.join("|", p));
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(null, "book not found");
            return;
        }

        writeFile(BOOK_FILE, books);

        String recordId = "R" + System.currentTimeMillis();
        String dueDate = LocalDate.now().plusDays(days).format(fmt);
        String record = recordId + "|" + name + "|" + bookId + "|" + title + "|"
                + today() + "|" + dueDate + "|-|-|false";

        appendFile(RECORD_FILE, record);
        borrowedCount++;

        String receipt = "Borrow successful!\n"
                + "Record ID: " + recordId + "\n"
                + "Name: " + name + "\n"
                + "Book: " + title + "\n"
                + "Borrow Date: " + today() + "\n"
                + "Due Date: " + dueDate + "\n"
                + "Late fine: RM0.50 per day";

        JOptionPane.showMessageDialog(null, receipt, "Receipt", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    // return book
    static void returnBook() {

        String name = "";
        while (name.trim().isEmpty()) {
            name = JOptionPane.showInputDialog(null, "Enter your name:");
            if (name == null) return;
            if (name.trim().isEmpty())
                JOptionPane.showMessageDialog(null, "name cannot be empty");
        }

        ArrayList<String> rec = readFile(RECORD_FILE);

        // find active borrows
        ArrayList<String> active = new ArrayList<>();
        String list = "";
        int count = 1;

        for (String r : rec) {
            String[] p = r.split("\\|");
            if (p.length > 8 && p[1].equalsIgnoreCase(name) && p[8].equals("false")) {
                active.add(r);
                list += count + ". " + p[3] + " (due: " + p[5] + ")\n";
                count++;
            }
        }

        if (active.isEmpty()) {
            JOptionPane.showMessageDialog(null, "no active borrowings found for " + name);
            return;
        }

        // pick which book to return
        int chosen = -1;
        while (chosen < 1 || chosen > active.size()) {
            String choice = JOptionPane.showInputDialog(null, "Your borrowed books:\n\n" + list + "\nEnter number to return:");
            if (choice == null) return;
            try {
                chosen = Integer.parseInt(choice.trim());
                if (chosen < 1 || chosen > active.size())
                    JOptionPane.showMessageDialog(null, "enter a number between 1 and " + active.size());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "invalid input, enter a number");
            }
        }

        String[] p = active.get(chosen - 1).split("\\|");

        LocalDate due = LocalDate.parse(p[5], fmt);
        LocalDate now = LocalDate.now();
        long late = ChronoUnit.DAYS.between(due, now);
        double fine = Math.max(0, late * 0.5);

        for (int i = 0; i < rec.size(); i++) {
            if (rec.get(i).startsWith(p[0])) {
                String[] up = rec.get(i).split("\\|");
                up[6] = today();
                up[7] = String.valueOf(fine);
                up[8] = "true";
                rec.set(i, String.join("|", up));
                break;
            }
        }

        restoreBook(p[2]);
        returnedCount++;
        writeFile(RECORD_FILE, rec);

        if (fine > 0) {
            JOptionPane.showMessageDialog(null, "Book returned.\nFine: RM" + fine,
                    "Return", JOptionPane.INFORMATION_MESSAGE, icon);
        } else {
            JOptionPane.showMessageDialog(null, "Book returned successfully.",
                    "Return", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }

    // borrowing history - active only
    static void viewHistory() {

        String name = "";
        while (name.trim().isEmpty()) {
            name = JOptionPane.showInputDialog(null, "Enter your name:");
            if (name == null) return;
            if (name.trim().isEmpty())
                JOptionPane.showMessageDialog(null, "name cannot be empty");
        }

        ArrayList<String> rec = readFile(RECORD_FILE);
        String out = "";

        for (String r : rec) {
            String[] p = r.split("\\|");
            // only show books not yet returned
            if (p.length > 8 && p[1].equalsIgnoreCase(name) && p[8].equals("false")) {
                out += "Record ID: " + p[0] + "\n"
                     + "Book: " + p[3] + "\n"
                     + "Borrow Date: " + p[4] + "\n"
                     + "Due Date: " + p[5] + "\n\n";
            }
        }

        if (out.equals("")) out = "no active borrowings found";

        JOptionPane.showMessageDialog(null, out, "Borrowing History", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    // exit with confirmation and summary
    static void exitSummary() {

        int confirm = JOptionPane.showConfirmDialog(null, "Exit the system?", "Exit",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            menu();
            return;
        }

        JOptionPane.showMessageDialog(null,
                "Session ended\nBorrowed: " + borrowedCount + "\nReturned: " + returnedCount,
                "Summary", JOptionPane.INFORMATION_MESSAGE, icon);
    }

    static void restoreBook(String bookId) {

        ArrayList<String> books = readFile(BOOK_FILE);

        for (int i = 0; i < books.size(); i++) {
            String[] p = books.get(i).split("\\|");
            if (p[0].equalsIgnoreCase(bookId)) {
                int a = Integer.parseInt(p[5]);
                p[5] = String.valueOf(a + 1);
                books.set(i, String.join("|", p));
            }
        }

        writeFile(BOOK_FILE, books);
    }

    static ArrayList<String> readFile(String file) {

        ArrayList<String> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "file error");
        }

        return list;
    }

    static void writeFile(String file, ArrayList<String> data) {

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            for (String s : data) pw.println(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "write error");
        }
    }

    static void appendFile(String file, String line) {

        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
            pw.println(line);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "append error");
        }
    }

    static String today() {
        return LocalDate.now().format(fmt);
    }
}