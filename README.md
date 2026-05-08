# xX69LibraSys67Xx

A simple Java library management system built with `JOptionPane` GUI dialogs. Made for CBS coursework by **Trio Ensem**.

---

## Group Members

| Name | Student ID |
|---|---|
| Muhammad Jad Fahmi Bin Mohd Fadzli | CBS25070712 |
| Khairin Darwisy Bin Khairul Anuar | CBS25070550 |
| Adam Hasif Bin Ahmad Sadini | CBS25070541 |

---

## Features

- **Search Book** — Search for any book by keyword (title, author, category)
- **Borrow Book** — Borrow a book with a 7 or 14 day loan period, generates a receipt
- **Return Book** — Return by name, pick from a list of active borrows, calculates late fine at RM0.50/day
- **Borrowing History** — Shows all books currently borrowed (not yet returned) with borrow and due dates
- **Input Validation** — All inputs are validated with re-prompting loops
- **Exit** — Confirmation dialog before exit, shows session summary of total borrowed and returned
- **Image in Dialogs** — Library image displayed in all JOptionPane dialogs

---

## Requirements

- Java JDK 8 or above
- NetBeans IDE (recommended) or any Java IDE

---

## File Structure

```
ProjectRoot/
├── src/
│   └── Package67/
│       ├── xX69LibraSys67Xx.java
│       ├── books.txt
│       ├── records.txt
│       └── library.png
```

---

## Setup

1. Clone or download the project
2. Open in NetBeans (or your preferred IDE)
3. Make sure `books.txt`, `records.txt`, and `library.png` are inside `src/Package67/`
4. Run `xX69LibraSys67Xx.java`

---

## books.txt Format

Each line represents one book in this format:

```
BookID|Title|Author|Category|Year|Copies
```

Example:

```
B001|Introduction to Java|John Smith|Programming|2020|3
B002|Data Structures|Jane Doe|Programming|2019|2
B003|Database Systems|Ali Hassan|Computer Science|2021|1
```

> `records.txt` should exist as an empty file before first run.

---

## Login

| Username | Password |
|---|---|
| admin | library123 |

---

## Late Fine

Books returned after the due date are charged **RM0.50 per day** overdue.
