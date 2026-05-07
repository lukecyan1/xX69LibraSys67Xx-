/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Library;

/**
 *
 * @author cyanl
 */
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
public class Teset_ffrist {

    public static void main(String[] args) {

        if (Login.login()) {
            Menu.showMenu();
        } else {
            System.exit(0);
        }

    }
}
    

 class Login {

    public static boolean login() {

        String username = JOptionPane.showInputDialog("Enter Username:");
        String password = JOptionPane.showInputDialog("Enter Password:");

        if (username.equals("admin") && password.equals("1234") || username.equals("user") && password.equals("1234")) {
            JOptionPane.showMessageDialog(null, "            \"Welcome, Admin!\\n\"\r\n" + //
                                "            + \"─────────────────────────────\\n\"\r\n" + //
                                "            + \"xX69LibraSys67Xx  –  Library Management System\\n\\n\"\r\n" + //
                                "            + \"Team Trio Ensem\",\r\n" + //
                                "            \"xX69LibraSys67Xx\",");
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Wrong Username or Password!");
            return false;
        }
    }
}



 class Menu {

    public static void showMenu() {

        String[] options = {"Search Book", "Borrow Book", "Return Book", "History", "Exit"};
        int choice;

        do {
            choice = JOptionPane.showOptionDialog(
                null,
                "Choose an option:",
                "Main Menu",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]
            );

            switch (choice) {
                case 0:
                    BookManager.searchBook();
                    break;

                case 1:
                    BookManager.borrowBook();
                    break;

                case 2:
                    BookManager.returnBook();
                    break;

                case 3:
                    BookManager.showHistory();
                    break;

                case 4:
                    JOptionPane.showMessageDialog(null, "Exiting system...");
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice!");
            }

        } while (choice != 4);
    }
}


 class BookManager {

    public static void searchBook() {
        String book = JOptionPane.showInputDialog("Enter book name to search:");
        JOptionPane.showMessageDialog(null, "Searching for: " + book);
    }

    public static void borrowBook() {
        String book = JOptionPane.showInputDialog("Enter book to borrow:");
        JOptionPane.showMessageDialog(null, book + " borrowed successfully!");
    }

    public static void returnBook() {
        String book = JOptionPane.showInputDialog("Enter book to return:");
        JOptionPane.showMessageDialog(null, book + " returned successfully!");
    }

    public static void showHistory() {
        JOptionPane.showMessageDialog(null, "Showing history (not implemented yet)");
    }
}

class ListBook {
    public static void main(String[] args) {
        String[] books = {"Book 1", "Book 2", "Book 3"};
        
    }
}
