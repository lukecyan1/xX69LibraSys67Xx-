/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Library;

/**
 *
 * @author cyanl
 */
import javax.swing.JOptionPane;
public class Teset_ffrist {

    public static void main(String[] args) {

        int choice;

        do {
            String input = JOptionPane.showInputDialog(
                "===== MAIN MENU =====\n" +
                "1. Search Book\n" +
                "2. Borrow Book\n" +
                "3. Return Book\n" +
                "4.History" +
                "5. Exit\n\n" +
                "Enter your choice:"
            );

            // Handle cancel button
            if (input == null) {
                break;
            }

            choice = Integer.parseInt(input);

            switch (choice) {
                case 1:
                    JOptionPane.showMessageDialog(null, "Search Book selected");
                    break;

                case 2:
                    JOptionPane.showMessageDialog(null, "Borrow Book selected");
                    break;

                case 3:
                    JOptionPane.showMessageDialog(null, "Return Book selected");
                    break;

                case 4:
                    JOptionPane.showMessageDialog(null, "History");
                    break;
                    
                case 5:
                    JOptionPane.showMessageDialog(null, "Mampus dari sini");
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice!  67 ");
            }

        } while (choice != 4);
    }
}

