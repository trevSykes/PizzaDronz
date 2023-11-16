package uk.ac.ed.inf;

/**
 * Object class that represents a move the drone makes. Used in the JSON serialization of the flightpath.
 */
public class DroneMove {
    public final String orderNo;
    public final double fromLongitude;
    public final double fromLatitude;
    public final double angle;
    public final double toLongitude;
    public final double toLatitude;

    public DroneMove(String orderNo, double fromLng, double fromLat, double angle,
                     double toLng, double toLat){
        this.orderNo = orderNo;
        this.fromLongitude = fromLng;
        this.fromLatitude = fromLat;
        this.angle = angle;
        this.toLongitude = toLng;
        this.toLatitude = toLat;
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

}
