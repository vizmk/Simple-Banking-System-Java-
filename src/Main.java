import java.util.Scanner;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

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

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        Map<String, String> accounts = new HashMap<>();

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

                    accounts.put(fullCard, pin);
                    break;

                case 2:
                    System.out.println("Enter your card number:");
                    scanner.nextLine(); // pulizia buffer
                    String enteredCard = scanner.nextLine();

                    System.out.println("Enter your PIN:");
                    String enteredPin = scanner.nextLine();

                    if (accounts.containsKey(enteredCard) &&
                            accounts.get(enteredCard).equals(enteredPin)) {

                        System.out.println("You have successfully logged in!");

                        boolean loggedIn = true;

                        while (loggedIn) {

                            System.out.println("1. Balance");
                            System.out.println("2. Log out");
                            System.out.println("0. Exit");

                            int accountChoice = scanner.nextInt();

                            switch (accountChoice) {

                                case 1:
                                    System.out.println("Balance: 0");
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