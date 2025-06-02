package utils;

public class Review {
    private int id;
    private int driverId;
    private int passengerId;
    private int rating;
    private String comment;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getDriverId() { return driverId; }
    public void setDriverId(int driverId) { this.driverId = driverId; }
    public int getPassengerId() { return passengerId; }
    public void setPassengerId(int passengerId) { this.passengerId = passengerId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}