package ui;

import dao.FlightDAO;
import dao.FlightInstanceDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.Customer;
import models.Flight;
import models.FlightInstance;


public class CustomerMenu {
    public static void show(Customer customer, Scanner scanner){
        boolean running = true;

        while(running){
            System.out.println("Welcome to the customer menu!");
            System.out.println();
            System.out.println("1. Search for flights between airports A and B.");
            System.out.println("2. Book Flight");
            System.out.println("3. View my Reservations");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Ask Customer Service Question");
            System.out.println("6. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    searchFlights(scanner);
                    break;
                case "2":
                    //bookFlights(scanner);
                    break;
                case "3":
                    //viewReservation(scanner);
                    break;
                case "4":
                    //cancelReservation(scanner);
                    break;
                case "5":
                    //askCustomerService;
                    break;
                case "6":
                    running = false;
                    System.out.println("Logging out");
                    break;
                default:
                    System.out.println("Invalid option...");                
            }
        }
    }

    private static void searchFlights(Scanner scanner){
        boolean running = true;
        while (running) {
            System.out.println("Welcome to the flight search menu!");
            System.out.println();
            System.out.println("1. Search One way flights.");
            System.out.println("2. Search Round trip flights.");
            System.out.println("3. Exit.");


            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    handleSearchOneWayFlights(scanner);
                    break;
                case "2":
                    // handleSearchRoundTripFlights(scanner);
                    break;
                default:
                    System.out.println("Invalid option...");                
            }
        }

        System.out.println("Please enter the departure airport: ");
        String depAirport = scanner.nextLine();
        System.out.println("Please enter the destination airport");
        String destAirport = scanner.nextLine();
        

        FlightDAO dao = new FlightDAO();
        List<Flight> flights = new ArrayList<>();
        flights = dao.searchFlights(depAirport, destAirport);

        if(flights.isEmpty()){
            System.out.println("Sorry, no flights available.");
        }
        else{
            for (Flight f : flights) {
                System.out.println(
                    f.getAirlineId() + " " + f.getFlightNumber() + " " +
                    f.getDepartureAirport() + " -> " + f.getArrivalAirport() +
                    " " + f.getDepartureTime() + " to " + f.getArrivalTime());
            }
        }
    }

    private static void handleSearchOneWayFlights(Scanner scanner){
        System.out.println("Please enter the departure airport: ");
        String depAirport = scanner.nextLine();
        System.out.println("Please enter the destination airport");
        String destAirport = scanner.nextLine();
        System.out.println("Please enter the departure date (YYYY-MM-DD)");
        String date = scanner.nextLine();
        System.out.println("Is your schedule flexible? (y/n)");
        String flexible = scanner.nextLine();
        
        FlightInstanceDAO dao = new FlightInstanceDAO();
        List<FlightInstance> flights = new ArrayList<>();

        if(flexible.equalsIgnoreCase("y")){
            flights = dao.searchFlights(depAirport, destAirport, date, true);       
        }
        else
            flights = dao.searchFlights(depAirport, destAirport, date, false);

        if(flights.isEmpty()){
            System.out.println("Sorry, no flights available.");
        }
        else{
            System.out.println("====================== \n Here are the available flights: \n (\"====================== \n");
            for (FlightInstance f : flights) {
                System.out.println(
                    f.getAirlineId() + " " + f.getFlightNumber() + " " +
                    f.getDepartureAirport() + " -> " + f.getArrivalAirport() +
                    " " + f.getDepartureDateTime() + " to " + f.getArrivalDateTime());
            }
        }
    }

    private static void handleSearchRoundTripFLights(Scanner scanner){
        System.out.println("Please enter the departure airport: ");
        String depAirport = scanner.nextLine();
        System.out.println("Please enter the destination airport");
        String destAirport = scanner.nextLine();
        System.out.println("Please enter the departure date (YYYY-MM-DD)");
        String depDate = scanner.nextLine();
        System.out.println("Please enter the return date (YYYY-MM-DD)");
        String retDate = scanner.nextLine();
        System.out.println("Is your schedule flexible? (y/n)");
        String flexible = scanner.nextLine();



    }

    private static void bookFlights(Customer customer, Scanner scanner){
        System.out.println("Enter airline id: ");
        String airlineId = scanner.nextLine();
        System.out.println("Enter flight number: ");
        int flightNum = scanner.nextInt();
        System.out.println("Enter");
    }
}
