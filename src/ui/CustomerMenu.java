package ui;

import dao.FlightInstanceDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.Customer;
import models.FlightInstance;
import src.BookingService;

public class CustomerMenu {
    public static void show(Customer customer, Scanner scanner){
        boolean running = true;

        while(running){
            System.out.println("Welcome to the customer menu!");
            System.out.println();
            System.out.println("1. Search for flights");
            System.out.println("2. Book Flight");
            System.out.println("3. View my Reservations");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Ask Customer Service Question");
            System.out.println("6. Exit");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    searchFlights(customer,scanner);
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

    private static void searchFlights(Customer customer, Scanner scanner){
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
                    handleSearchOneWayFlights(customer,scanner);
                    break;
                case "2":
                    handleSearchRoundTripFlights(customer, scanner);
                    break;
                default:
                    System.out.println("Invalid option...");                
            }
        }
    }

    private static void handleSearchOneWayFlights(Customer customer, Scanner scanner){
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
        else{ // TODO: display cost of the flights, this may require updating the searchFlight method if not done so already.
            System.out.println("================================ \n Here are the available flights: \n================================ \n");
            for (FlightInstance f : flights) {
                System.out.println(
                    f.getAirlineId() + " " + f.getFlightNumber() + " " +
                    f.getDepartureAirport() + " -> " + f.getArrivalAirport() +
                    " " + f.getDepartureDateTime() + " to " + f.getArrivalDateTime());
            }
        }

        System.out.println("\n================================\n");
        System.out.println("Would you like to book one of these flights? (y/n)");
        String choice = scanner.nextLine();
        if(choice.equalsIgnoreCase("y")){
            bookFlights(customer, scanner, false);
        }
    }

    private static void handleSearchRoundTripFlights(Customer customer, Scanner scanner){
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

        FlightInstanceDAO dao = new FlightInstanceDAO();
        List<FlightInstance> depFlights = new ArrayList<>();
        List<FlightInstance> retFlights = new ArrayList<>();

        if(flexible.equalsIgnoreCase("y")){
            depFlights = dao.searchFlights(depAirport, destAirport, depDate, true);
            retFlights = dao.searchFlights(destAirport, depAirport, retDate, true);
        }
        else{
            depFlights = dao.searchFlights(depAirport, destAirport, depDate, false);
            retFlights = dao.searchFlights(destAirport, depAirport, retDate, false);
        }
        // TODO: display the cost of the flight as well, this may require updating the searchFlight method if not done already
        System.out.println("================================\n Here are the available departures\n================================");
        for (FlightInstance f : depFlights) {
                System.out.println(
                    f.getAirlineId() + " " + f.getFlightNumber() + " " +
                    f.getDepartureAirport() + " -> " + f.getArrivalAirport() +
                    " " + f.getDepartureDateTime() + " to " + f.getArrivalDateTime());
            }
        System.out.println("================================\n Here are the available return flights\n================================");
        for (FlightInstance f : retFlights) {
                System.out.println(
                    f.getAirlineId() + " " + f.getFlightNumber() + " " +
                    f.getDepartureAirport() + " -> " + f.getArrivalAirport() +
                    " " + f.getDepartureDateTime() + " to " + f.getArrivalDateTime());
        } // could rewrite this loop as a function but I am too lazy, copy and paste is easy enough
        System.out.println("Would you like to a pair of these flights? (y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")){
            bookFlights(customer, scanner, true);
        }
    }

    private static void bookFlights(Customer customer, Scanner scanner, boolean isRoundTrip){
        if(isRoundTrip){
            System.out.println("Please enter the airline id for the departure flight: ");
            String depAirlineId = scanner.nextLine();
            System.out.println("Please enter the flight number for the departure flight: ");
            int depFlightNum = scanner.nextInt();
            System.out.println("Please enter the airline id for the return flight: ");
            String retAirlineId = scanner.nextLine();
            System.out.println("Please enter the flight number for the return flight: ");
            int retFlightNum = scanner.nextInt();
        }
        else{
            System.out.println("Please enter the airline id for the departure flight: ");
            String depAirlineId = scanner.nextLine();
            System.out.println("Please enter the flight number for the departure flight: ");
            int depFlightNum = scanner.nextInt();
            // bookFlight(customer)
        }
    }
}
