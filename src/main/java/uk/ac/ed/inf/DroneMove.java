package uk.ac.ed.inf;

public class DroneMove {
    private final String orderNo;
    private final double fromLongitude;
    private final double fromLatitude;
    private final double angle;
    private final double toLongitude;
    private final double toLatitude;
    private final int ticksSinceStartOfCalculation;

    public DroneMove(String orderNo, double fromLng, double fromLat, double angle,
                     double toLng, double toLat, int ticks){
        this.orderNo = orderNo;
        this.fromLongitude = fromLng;
        this.fromLatitude = fromLat;
        this.angle = angle;
        this.toLongitude = toLng;
        this.toLatitude = toLat;
        this.ticksSinceStartOfCalculation = ticks;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public double getFromLongitude(){
        return fromLongitude;
    }

    public double getFromLatitude() {
        return fromLatitude;
    }

    public double getAngle() {
        return angle;
    }

    public double getToLongitude() {
        return toLongitude;
    }

    public double getToLatitude() {
        return toLatitude;
    }

    public int getTicksSinceStartOfCalculation() {
        return ticksSinceStartOfCalculation;
    }
}
