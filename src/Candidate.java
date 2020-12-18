public class Candidate {
    private Point point;
    private Link involvedLink;
    private double emissionProb;
    private double acc_prob;// accumulated probability (이전 최대 edge와 해당 node의 ep*tp를 곱함)
    private int prev_index;

    public int getPrev_index() {
        return prev_index;
    }

    public void setPrev_index(int prev_index) {
        this.prev_index = prev_index;
    }

    public Candidate (){
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

    public double getAcc_prob() {
        return acc_prob;
    }

    public void setAcc_prob(double acc_prob) {
        this.acc_prob = acc_prob;
    }



    public double getEmissionProb() {
        return emissionProb;
    }

    public void setEmissionProb(double emissionProb) {
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
