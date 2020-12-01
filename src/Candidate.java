public class Candidate {
    private Point point;
    private Link involvedLink;
    private double emissionProb;

    public double getEmissionProb() {
        return emissionProb;
    }

    public void setEmissionProb(double emissionProb) {
        this.emissionProb = emissionProb;
    }



    public Candidate (Point point, Link involvedLink){
        this.point = point;
        this.involvedLink = involvedLink;
    }

    public Candidate (Point point, Link involvedLink, double emissionProb){
        this.point = point;
        this.involvedLink = involvedLink;
        this.emissionProb = emissionProb;
    }

    public Point getPoint() {
        return point;
    }

    public Link getInvolvedLink(){
        return involvedLink;
    }

    public String toString() {
        return "Involved link: " + involvedLink + " Point: " + point;
    }
}
