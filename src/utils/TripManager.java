package utils;

import java.sql.Date;
import java.util.List;
import java.sql.Time;

public class TripManager {
    private static TripManager instance;
    private TripDAO tripDAO;

    private TripManager() {
        tripDAO = new TripDAO();
    }

    public static TripManager getInstance() {
        if (instance == null) {
            instance = new TripManager();
        }
        return instance;
    }

    public List<Trip> searchTrips(String departure, String destination) {
        return tripDAO.searchTrips(departure, destination);
    }

    public Trip getTripById(int id) {
        return tripDAO.getTripById(id);
    }

    public void createTrip(int driverId, String departure, String destination, Date dateTime, Time sqlTime, int seats, double price) {
        Trip trip = new Trip();
        trip.setDriverId(driverId);
        trip.setDeparture(departure);
        trip.setDestination(destination);
        trip.setDate(new Date(dateTime.getTime()));
        trip.setTime(new java.sql.Time(dateTime.getTime()));
        trip.setSeatsAvailable(seats);
        trip.setPrice(price);
        tripDAO.createTrip(trip);
    }

    public void updateTrip(Trip trip) {
        tripDAO.updateTrip(trip);
    }

    public void deleteTrip(int id) {
        tripDAO.deleteTrip(id);
    }
}