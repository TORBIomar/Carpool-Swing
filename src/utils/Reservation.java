package utils;

import java.sql.Timestamp;

public class Reservation {
    private int id;
    private int passengerId;
    private int tripId;
    private int seatsReserved;
    private Timestamp reservedAt;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }

    public int getTripId() { return tripId; }
    public void setTripId(int tripId) { this.tripId = tripId; }

    public int getSeatsReserved() { return seatsReserved; }
    public void setSeatsReserved(int seatsReserved) { this.seatsReserved = seatsReserved; }

    public Timestamp getReservedAt() { return reservedAt; }
    public void setReservedAt(Timestamp reservedAt) { this.reservedAt = reservedAt; }
}