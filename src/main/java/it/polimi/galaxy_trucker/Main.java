package it.polimi.galaxy_trucker;

import it.polimi.galaxy_trucker.client.ClientMain;
import it.polimi.galaxy_trucker.server.ServerMain;

import java.time.Duration;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main {
    public static void main(String[] args) {
        String mode = args.length > 0 ? args[0].toLowerCase() : "";

        if (mode.isEmpty()) {
            System.out.println("Welcome to \u001B[95mGalaxy Trucker\u001B[0m!");
            System.out.println("Enter mode [client/server]: ");
            Scanner scanner = new Scanner(System.in);
            mode = scanner.nextLine().toLowerCase();
        }

        String[] args2 = new String[0];
        if(args.length > 1) args2 = Arrays.copyOfRange(args, 1, args.length);

        switch (mode) {
            case "client" -> ClientMain.main(args2);
            case "server" -> ServerMain.main(args2);
            default -> {
                System.err.println("Invalid mode. Use 'client' or 'server'");
                System.exit(1);
            }
        }
    }
}