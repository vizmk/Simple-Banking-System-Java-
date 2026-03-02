import  java.util.Scanner;
import  java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        //dichiarazione variabili globali
        int balance=0;
        //creazione map
        Map<String, String> accounts = new HashMap<>();
        Random random=new Random();
        int index;

        do {
            System.out.println("1. Create an account");
            System.out.println("2. Log into account");
            System.out.println("0. Exit");



            index = scanner.nextInt();
            switch (index) {
                case 1:
                    System.out.println("Your card has been created");
                    System.out.println("Your card number:");     //creazione numero carta
                    int accountNumber = random.nextInt(1000000000);
                    String card= String.format("%09d", accountNumber);
                    int checksum=random.nextInt(10);
                    System.out.println("400000"+card+checksum);



                    System.out.println("Your card PIN:");    //creazione pin
                    int pinRandom= random.nextInt(10000);
                    String pinAccount=String.format("%04d",pinRandom);
                    System.out.println(pinAccount);
                    String fullCard = "400000" + card + checksum;
                    accounts.put(fullCard, pinAccount);
                    break;
                case 2:
                    System.out.println("Enter your card number:");
                    scanner.nextLine();
                    String enterCard= scanner.nextLine();
                    System.out.println("Enter your PIN:");
                    String enterPin=scanner.nextLine();
                    //controllo valori
                    if(accounts.containsKey(enterCard)&&(accounts.get(enterCard).equals(enterPin))){
                        System.out.println("You have successfully logged in!");
                        System.out.println("1. Balance");
                        System.out.println("2. Log out");
                        System.out.println("0. Exit");
                        int slt =scanner.nextInt();
                        switch(slt){
                            case 1:
                                System.out.println(balance);
                                break;
                            case 2:
                                System.out.println("You have successfully logged out!");
                                break;
                            case 3:
                                return;


                        }
                    }else{
                        System.out.println("Wrong card number or PIN!");

                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Wrong Input");
            }
        } while (index != 0);
        System.out.println("Bye!");
    }
}