package virginia.com.smartroute;
import java.util.ArrayList;
import java.util.List;
public class SmartRoute {
    private List<SmartSegment> smartSegments;
    private double cost;
    private double time;
    private double cal;
    private double risk;
    private double distance;

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

}
