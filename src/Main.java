import java.util.Scanner;
import java.util.Random;

// SQLite / JDBC
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    // FUNZIONE LUHN
    static int calcChecksumLuhn(String first15) {
        int sum = 0;

        for (int i = 0; i < first15.length(); i++) {
            int digit = first15.charAt(i) - '0';

            // raddoppia posizioni dispari (umane) => indici pari (Java)
            if (i % 2 == 0) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
        }

        return (10 - (sum % 10)) % 10;
    }

    // Crea tabella se non esiste
    static void createTable(String url) {
        String sql = "CREATE TABLE IF NOT EXISTS card (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "number TEXT, " +
                "pin TEXT, " +
                "balance INTEGER DEFAULT 0" +
                ");";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Inserisce una carta nel DB
    static void insertCard(String url, String number, String pin) {
        String insertSQL = "INSERT INTO card(number, pin) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Controlla login: esiste una riga con number+pin?
    static boolean checkLogin(String url, String number, String pin) {
        String selectSQL = "SELECT 1 FROM card WHERE number = ? AND pin = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setString(1, number);
            pstmt.setString(2, pin);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // true se ha trovato almeno una riga
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Legge il balance dal DB (default 0)
    static int getBalance(String url, String number) {
        String selectSQL = "SELECT balance FROM card WHERE number = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {

            pstmt.setString(1, number);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("balance");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // se non trovato (non dovrebbe), restituisce 0
    }

    public static void main(String[] args) {

        // 1) Leggo -fileName
        String fileName = "card.s3db";
        if (args.length > 1 && args[0].equals("-fileName")) {
            fileName = args[1];
        }
        String url = "jdbc:sqlite:" + fileName;

        // 2) Creo DB + tabella (se non esiste)
        createTable(url);

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        while (true) {

            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");

            int choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    System.out.println("Your card has been created");
                    System.out.println("Your card number:");

                    // 9 cifre account
                    int accountNumber = random.nextInt(1000000000);
                    String accountPart = String.format("%09d", accountNumber);

                    // prime 15 cifre
                    String first15 = "400000" + accountPart;

                    // calcolo checksum
                    int checksum = calcChecksumLuhn(first15);
                    String fullCard = first15 + checksum;

                    System.out.println(fullCard);

                    System.out.println("Your card PIN:");
                    String pin = String.format("%04d", random.nextInt(10000));
                    System.out.println(pin);

                    // 3) Salvo nel DB (non più in HashMap)
                    insertCard(url, fullCard, pin);
                    break;

                case 2:
                    System.out.println("Enter your card number:");
                    scanner.nextLine(); // pulizia buffer
                    String enteredCard = scanner.nextLine();

                    System.out.println("Enter your PIN:");
                    String enteredPin = scanner.nextLine();

                    // 4) Login controllato sul DB
                    if (checkLogin(url, enteredCard, enteredPin)) {

                        System.out.println("You have successfully logged in!");

                        boolean loggedIn = true;

                        while (loggedIn) {

                            System.out.println("1. Balance");
                            System.out.println("2. Log out");
                            System.out.println("0. Exit");

                            int accountChoice = scanner.nextInt();

                            switch (accountChoice) {

                                case 1:
                                    // 5) Balance letto dal DB
                                    int balance = getBalance(url, enteredCard);
                                    System.out.println("Balance: " + balance);
                                    break;

                                case 2:
                                    System.out.println("You have successfully logged out!");
                                    loggedIn = false;
                                    break;

                                case 0:
                                    System.out.println("Bye!");
                                    return;
                            }
                        }

                    } else {
                        System.out.println("Wrong card number or PIN!");
                    }

                    break;

                case 0:
                    System.out.println("Bye!");
                    return;

                default:
                    System.out.println("Wrong Input");
            }
        }
    }
}