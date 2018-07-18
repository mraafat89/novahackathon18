package virginia.com.smartroute;
import java.util.ArrayList;
import java.util.List;
public class SmartRoute {
    private List<SmartSegment> smartSegments;

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getCal() {
        return cal;
    }

    public void setCal(double cal) {
        this.cal = cal;
    }

    public double getRisk() {
        return risk;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    private double cost;
    private double time;
    private double cal;
    private double risk;
    private double distance;

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    private double score;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SmartRoute(SmartSegment smartSegment) {
        this.smartSegments = new ArrayList<>();
        smartSegments.add(smartSegment);
        cost = smartSegment.getCost();
        time = smartSegment.getTime();
        cal = smartSegment.getCal();
        risk = smartSegment.getRisk();
        distance = smartSegment.getDistance();
    }
    public void addSegment(SmartSegment smartSegment) {
        smartSegments.add(smartSegment);
        cost += smartSegment.getCost();
        time += smartSegment.getTime();
        cal += smartSegment.getCal();
        risk += smartSegment.getRisk();
        distance += smartSegment.getDistance();
    }
    public void addCost(double addition) {
        cost+= addition;
    }
    public void setRisk(double myRisk) {
        risk = myRisk;
    }

}
