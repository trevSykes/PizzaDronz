package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.List;

public class Node implements Comparable<Node>{
    private Node previous;
    private double angleFromPrev;
    private List<Node> successors;
    private LngLat pos;
    private double f;
    private double g;
    private double h;
    private boolean inCentral;

    public Node(Node previous, double angleFromPrev, LngLat pos, boolean inCentral){
        this.previous = previous;
        this.angleFromPrev = angleFromPrev;
        this.pos = pos;
        this.inCentral = inCentral;
    }

    public DroneMove convertToMove(String orderID,int tick){
        return new DroneMove(orderID,
                this.previous.pos.lng(),
                this.previous.pos.lat(),
                this.angleFromPrev,
                this.pos.lng(),
                this.pos.lat(),
                tick);
    }

    @Override
    public int compareTo(Node o) {
        if (this.f() < o.f()) {
            return -1;
        } else if (this.f() > o.f()) {
            return 1;
        } else {
            return 0;
        }
    }

    public void setF(double val) {
        this.f = val;
    }

    public void setG(double val){
        this.g = val;
    }

    public void setH(double val) {
        this.h = val;
    }

    public void setSuccessors(List<Node> successors) {
        this.successors = successors;
    }

    public double g() {
        return g;
    }

    public double f() {
        return f;
    }

    public double h() {
        return h;
    }

    public Node previous() { return previous;}

    public List<Node> successors() {
        return successors;
    }

    public boolean isInCentral() { return inCentral;}

    public LngLat pos(){
        return pos;
    }
}