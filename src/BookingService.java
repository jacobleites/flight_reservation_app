
import dao.FlightInstanceDAO;
import dao.ReservationDAO;
import dao.TicketDAO;
import models.Customer;
import models.FlightInstance;

public class BookingService {
    private final FlightInstanceDAO flightInstanceDAO;
    private final ReservationDAO reservationDAO;
    private final TicketDAO ticketDAO;

    public BookingService() {
        this.flightInstanceDAO = new FlightInstanceDAO();
        this.reservationDAO = new ReservationDAO();
        this.ticketDAO = new TicketDAO();
    }
    public static void bookFlight(Customer customer, FlightInstance flight){
        
    }
}
