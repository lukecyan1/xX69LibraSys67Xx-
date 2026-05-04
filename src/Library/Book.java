/*
 * Book.java
 * stores info about one book
 *
 * Team Trio Ensem
 * CBS25070712 - Muhammad Jad Fahmi
 * CBS25070550 - Khairin Darwisy
 * CBS25070541 - Adam Hasif
 */

public class Book {

    String bookId;
    String title;
    String author;
    String category;
    int totalCopies;
    int availableCopies;

    public Book(String bookId, String title, String author, String category, int totalCopies) {
        this.bookId      = bookId;
        this.title       = title;
        this.author      = author;
        this.category    = category;
        this.totalCopies = totalCopies;
        // TODO: availableCopies should start equal to totalCopies
    }

    public boolean isAvailable() {
        // TODO: return true if availableCopies > 0
        return false;
    }

    public void borrowCopy() {
        // TODO: minus 1 from availableCopies, dont let it go below 0
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) {
            // TODO: add 1 back to availableCopies
        }
    }

    public String getShortInfo() {
        // TODO: return one line string for the catalogue table
        // format exactly:  bookId + " | " + title + " | " + author + " | " + category + " | " + avail
        // avail = availableCopies + "/" + totalCopies  if available, otherwise "NONE"
        return "";
    }

    public String getBookInfo() {
        String status;
        if (isAvailable()) {
            // TODO: set status to "Available (X/Y)" where X=available Y=total
            status = "";
        } else {
            status = "Not Available";
        }

        return "[" + bookId + "] " + title
            + "\n    Author   : " + author
            + "\n    Category : " + category
            + "\n    Status   : " + status;
    }
}