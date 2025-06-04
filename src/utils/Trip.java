package utils;

import java.sql.Date;
import java.sql.Time;

public class Trip {
    private int id;
    private int driverId;
    private String departure;
    private String destination;
    private Date date;
    private Time time;
    private int seatsAvailable;
    private double price;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    public String getDeparture() { return departure; }
    public void setDeparture(String departure) { this.departure = departure; }
    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Time getTime() { return time; }
    public void setTime(Time time) { this.time = time; }
    public int getSeatsAvailable() { return seatsAvailable; }
    public void setSeatsAvailable(int seatsAvailable) { this.seatsAvailable = seatsAvailable; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}