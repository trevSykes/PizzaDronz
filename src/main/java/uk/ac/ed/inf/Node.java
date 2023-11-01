package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.List;

public class Node implements Comparable<Node>{
    public Node previous;
    public double angleFromPrev;
    public LngLat pos;
    public double f;
    public double g;
    public boolean inCentral;

    public Node(Node previous, double angleFromPrev, LngLat pos){
        this.previous = previous;
        this.angleFromPrev = angleFromPrev;
        this.pos = pos;
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
        return Double.compare(this.f, o.f);
    }

//    public void setF(double val) {
//        this.f = val;
//    }
//
//    public void setG(double val){
//        this.g = val;
//    }
//
//    public void setH(double val) {
//        this.h = val;
//    }
//
//    public void setSuccessors(List<Node> successors) {
//        this.successors = successors;
//    }
//
//    public double g() {
//        return g;
//    }
//
//    public double f() {
//        return f;
//    }
//
//    public double h() {
//        return h;
//    }
//
//    public Node previous() { return previous;}
//
//    public List<Node> successors() {
//        return successors;
//    }
//
//    public boolean isInCentral() { return inCentral;}
//
//    public LngLat pos(){
//        return pos;
//    }
}