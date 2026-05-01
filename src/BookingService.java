
import dao.FlightInstanceDAO;
import dao.ReservationDAO;
import dao.TicketDAO;

public class BookingService {
    private final FlightInstanceDAO flightInstanceDAO;
    private final ReservationDAO reservationDAO;
    private final TicketDAO ticketDAO;

    public BookingService() {
        this.flightInstanceDAO = new FlightInstanceDAO();
        this.reservationDAO = new ReservationDAO();
        this.ticketDAO = new TicketDAO();
    }
}
