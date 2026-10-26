import java.time.LocalDate;
import java.util.*;

public class Main {
    private static final String BOOKS_FILE = "books.txt";
    private static final String MEMBERS_FILE = "members.txt";
    private static final String BORROWS_FILE = "borrowings.txt";

    private static Repository<Book> bookRepo;
    private static Repository<Member> memberRepo;
    private static Repository<BorrowRecord> borrowRepo;

    private static List<Book> books = new ArrayList<>();
    private static List<Member> members = new ArrayList<>();
    private static List<BorrowRecord> borrowRecords = new ArrayList<>();

    private static Member activeUser;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        initializeRepositories();
        loadData();
        setupUserProfile();
        runMainMenu();
    }

    private static void initializeRepositories() {
        bookRepo = new FileRepository<>(BOOKS_FILE,
                b -> b.getId() + ";" + b.getTitle() + ";" + b.getAuthor() + ";" + b.getPublicationYear() + ";" + b.getPublisherId() + ";" + b.isBorrowed(),
                line -> {
                    String[] p = line.split(";");
                    if (p.length < 6) return null;
                    Book b = new Book(p[0], p[1], p[2], Integer.parseInt(p[3]), p[4]);
                    b.setBorrowed(Boolean.parseBoolean(p[5]));
                    return b;
                });

        memberRepo = new FileRepository<>(MEMBERS_FILE,
                m -> m.getClass().getSimpleName() + ";" + m.getId() + ";" + m.getName() + ";" + m.getEmail(),
                line -> {
                    String[] p = line.split(";");
                    if (p.length < 4) return null;
                    String type = p[0];
                    switch (type) {
                        case "StudentMember": return new StudentMember(p[1], p[2], p[3]);
                        case "PublisherMember": return new PublisherMember(p[1], p[2], p[3]);
                        case "LibrarianMember": return new LibrarianMember(p[1], p[2], p[3]);
                        default: return null;
                    }
                });

        borrowRepo = new FileRepository<>(BORROWS_FILE,
                r -> r.getRecordId() + ";" + r.getBookId() + ";" + r.getMemberId() + ";" + r.getBorrowDate() + ";" + r.isReturned(),
                line -> {
                    String[] p = line.split(";");
                    if (p.length < 5) return null;
                    BorrowRecord r = new BorrowRecord(p[0], p[1], p[2], LocalDate.parse(p[3]));
                    r.setReturned(Boolean.parseBoolean(p[4]));
                    return r;
                });
    }

    private static void loadData() {
        books = bookRepo.loadAll();
        members = memberRepo.loadAll();
        borrowRecords = borrowRepo.loadAll();
    }

    private static void saveData() {
        bookRepo.saveAll(books);
        memberRepo.saveAll(members);
        borrowRepo.saveAll(borrowRecords);
    }

    private static void setupUserProfile() {
        System.out.println("=============================================");
        System.out.println(" Welcome to Library Management System (LMS) ");
        System.out.println("=============================================");
        System.out.println("Select Active User Role:");
        System.out.println("1. Student");
        System.out.println("2. Publisher");
        System.out.println("3. Librarian");
        int choice = getIntInput("Choice: ");

        System.out.print("Enter your Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter your Email: ");
        String email = scanner.nextLine();
        String id = "USR-" + System.currentTimeMillis() % 10000;

        switch (choice) {
            case 1:
                activeUser = new StudentMember(id, name, email);
                break;
            case 2:
                activeUser = new PublisherMember(id, name, email);
                break;
            case 3:
            default:
                activeUser = new LibrarianMember(id, name, email);
                break;
        }

        if (members.stream().noneMatch(m -> m.getId().equals(activeUser.getId()))) {
            members.add(activeUser);
            saveData();
        }

        System.out.println("\nActive User Profile Created: " + activeUser.getName() + " (" + activeUser.getClass().getSimpleName() + ")\n");
    }

    private static void runMainMenu() {
        boolean exit = false;
        while (!exit) {
            System.out.println("\n--- MAIN MENU [" + activeUser.getClass().getSimpleName() + "] ---");
            System.out.println("1. Book & Publisher Menu");
            System.out.println("2. Member & Borrowings Menu");
            System.out.println("3. Stream Grouping Reports Menu");
            System.out.println("4. Switch Role / Re-initialize Profile");
            System.out.println("5. Exit System");
            int choice = getIntInput("Select option: ");

            switch (choice) {
                case 1: bookAndPublisherMenu(); break;
                case 2: memberAndBorrowingsMenu(); break;
                case 3: streamReportsMenu(); break;
                case 4: setupUserProfile(); break;
                case 5:
                    saveData();
                    System.out.println("Data saved successfully. Goodbye!");
                    exit = true;
                    break;
                default: System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void bookAndPublisherMenu() {
        System.out.println("\n--- Book & Publisher Operations ---");
        System.out.println("1. View All Books");
        System.out.println("2. Add Single Book");
        System.out.println("3. Add Multiple Books for Publisher");

        int choice = getIntInput("Select option: ");
        switch (choice) {
            case 1:
                if (books.isEmpty()) System.out.println("No books in library.");
                else books.forEach(System::println);
                break;
            case 2:
                addBookFlow(activeUser instanceof PublisherMember ? activeUser.getId() : "PUB-GENERIC");
                break;
            case 3:
                System.out.print("Enter Publisher ID: ");
                String pubId = scanner.nextLine();
                int count = getIntInput("How many books to add? ");
                for (int i = 0; i < count; i++) {
                    System.out.println("Adding book " + (i + 1) + " of " + count);
                    addBookFlow(pubId);
                }
                break;
            default: System.out.println("Invalid choice.");
        }
    }

    private static void addBookFlow(String pubId) {
        String id = "BK-" + (books.size() + 101);
        System.out.print("Title: ");
        String title = scanner.nextLine();
        System.out.print("Author: ");
        String author = scanner.nextLine();
        int year = getIntInput("Publication Year: ");

        Book b = new Book(id, title, author, year, pubId);
        books.add(b);
        saveData();
        System.out.println("Book added successfully: " + b);
    }

    private static void memberAndBorrowingsMenu() {
        System.out.println("\n--- Member & Borrowings Menu ---");
        System.out.println("1. Register New Member");
        System.out.println("2. Borrow Book");
        System.out.println("3. Return Book");
        System.out.println("4. View Active Borrowings");

        int choice = getIntInput("Select option: ");
        switch (choice) {
            case 1:
                registerMemberFlow();
                break;
            case 2:
                borrowBookFlow();
                break;
            case 3:
                returnBookFlow();
                break;
            case 4:
                borrowRecords.stream()
                        .filter(r -> !r.isReturned())
                        .forEach(System::println);
                break;
            default: System.out.println("Invalid option.");
        }
    }

    private static void registerMemberFlow() {
        System.out.println("Select Member Type: 1. Student 2. Publisher 3. Librarian");
        int type = getIntInput("Type: ");
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        String id = "MEM-" + (members.size() + 1001);

        Member m;
        if (type == 1) m = new StudentMember(id, name, email);
        else if (type == 2) m = new PublisherMember(id, name, email);
        else m = new LibrarianMember(id, name, email);

        members.add(m);
        saveData();
        System.out.println("Member registered successfully: " + m);
    }

    private static void borrowBookFlow() {
        System.out.print("Enter Book ID to borrow: ");
        String bookId = scanner.nextLine();

        Optional<Book> bookOpt = books.stream().filter(b -> b.getId().equalsIgnoreCase(bookId) && !b.isBorrowed()).findFirst();
        if (bookOpt.isEmpty()) {
            System.out.println("Book unavailable or not found.");
            return;
        }

        Book book = bookOpt.get();
        book.setBorrowed(true);

        String recordId = "BR-" + (borrowRecords.size() + 1);
        BorrowRecord record = new BorrowRecord(recordId, book.getId(), activeUser.getId(), LocalDate.now());
        borrowRecords.add(record);
        saveData();
        System.out.println("Book borrowed successfully!");
    }

    private static void returnBookFlow() {
        System.out.print("Enter Borrow Record ID: ");
        String recordId = scanner.nextLine();

        Optional<BorrowRecord> recOpt = borrowRecords.stream()
                .filter(r -> r.getRecordId().equalsIgnoreCase(recordId) && !r.isReturned())
                .findFirst();

        if (recOpt.isEmpty()) {
            System.out.println("Active record not found.");
            return;
        }

        BorrowRecord rec = recOpt.get();
        rec.setReturned(true);

        books.stream()
                .filter(b -> b.getId().equalsIgnoreCase(rec.getBookId()))
                .findFirst()
                .ifPresent(b -> b.setBorrowed(false));

        int daysOverdue = getIntInput("Enter overdue days (0 if on time): ");
        double fee = activeUser.calculateLateFees(daysOverdue);

        saveData();
        System.out.println("Book returned. Late Fee Assessed: $" + String.format("%.2f", fee));
    }

    private static void streamReportsMenu() {
        System.out.println("\n--- Stream Grouping Reports ---");
        System.out.println("1. Total Active Borrowed Books Count");
        System.out.println("2. Books Grouped by Starting Title Letter");
        System.out.println("3. Books Grouped by Publication Era");

        int choice = getIntInput("Select option: ");
        switch (choice) {
            case 1:
                long count = ReportEngine.countTotalBorrowedBooks(borrowRecords);
                System.out.println("Total Borrowed Books: " + count);
                break;
            case 2:
                System.out.println("Books Grouped by First Letter:");
                ReportEngine.groupBooksByStartingLetter(books)
                        .forEach((letter, cnt) -> System.out.println("Letter '" + letter + "': " + cnt + " book(s)"));
                break;
            case 3:
                System.out.println("Books Grouped by Publication Era:");
                ReportEngine.groupBooksByPublicationEra(books)
                        .forEach((era, list) -> {
                            System.out.println("\n" + era + " (" + list.size() + "):");
                            list.forEach(b -> System.out.println("  - " + b.getTitle() + " (" + b.getPublicationYear() + ")"));
                        });
                break;
            default: System.out.println("Invalid option.");
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer input. Please enter a number.");
            }
        }
    }
}
