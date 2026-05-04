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
                "1. Add Data\n" +
                "2. View Data\n" +
                "3. Delete Data\n" +
                "4. Exit\n\n" +
                "Enter your choice:"
            );

            // Handle cancel button
            if (input == null) {
                break;
            }

            choice = Integer.parseInt(input);

            switch (choice) {
                case 1:
                    JOptionPane.showMessageDialog(null, "Add Data selected");
                    break;

                case 2:
                    JOptionPane.showMessageDialog(null, "View Data selected");
                    break;

                case 3:
                    JOptionPane.showMessageDialog(null, "Delete Data selected");
                    break;

                case 4:
                    JOptionPane.showMessageDialog(null, "Exiting...");
                    break;

                default:
                    JOptionPane.showMessageDialog(null, "Invalid choice!");
            }

        } while (choice != 4);
    }
}

