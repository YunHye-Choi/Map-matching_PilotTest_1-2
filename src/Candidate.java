import com.sun.istack.internal.localization.NullLocalizable;

public class Candidate {
    private Point point;
    private Link involvedLink;
    private int tp;
    private int ep;

    public Candidate (Point point, Link involvedLink){
        this.point = point;
        this.involvedLink = involvedLink;
        this.tp= 0;
        this.ep=0;
    }

    public Point getPoint() {
        return point;
    }

    public Link getInvolvedLink(){
        return involvedLink;
    }
    //유림 혹시 몰라 push

    public void setTp(int tp) {
        this.tp = tp;
    }

    public void setEp(int ep) {
        this.ep = ep;
    }

    public int getTp() {
        return tp;
    }

    public int getEp() {
        return ep;
    }
}
