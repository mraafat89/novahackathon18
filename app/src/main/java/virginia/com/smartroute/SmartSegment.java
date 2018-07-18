package virginia.com.smartroute;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class SmartSegment {
    private JSONArray jSteps;

    public double getRisk() {
        return risk;
    }

    public void setRisk(double risk) {
        this.risk = risk;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
    public void addCost(double cost) {
        this.cost += cost;
    }

    public double getCal() {
        return cal;
    }

    public void setCal(double cal) {
        this.cal = cal;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    private double risk;
    private double cost;
    private double cal;
    private double time;
    private double distance;
    private List<Double> times;
    private List<Double> distances;
    private List pathList;
    private List<String> modes;

    public SmartSegment(JSONObject jLeg) {
        times = new ArrayList<>();
        modes = new ArrayList<>();
        distances = new ArrayList<>();
        try {
            jSteps = ((JSONObject) jLeg).getJSONArray("steps");
            time = Double.parseDouble(String.valueOf(((JSONObject)jLeg.get("duration")).get("value")));
            distance = Double.parseDouble(String.valueOf(((JSONObject)jLeg.get("distance")).get("value"))) * 0.000621371;
            pathList = new ArrayList<HashMap<String, String>>();
            // get the mode of the first step, it will be the same for the whole segment
           // mode = ((JSONObject) jSteps.get(0)).get("travel_mode").toString();
            for (int k = 0; k < jSteps.length(); k++) {
                modes.add(((JSONObject) jSteps.get(k)).get("travel_mode").toString());
                distances.add(Double.parseDouble(((JSONObject)(((JSONObject) jSteps.get(k)).get("distance"))).get("value").toString())* 0.000621371);
                times.add(Double.parseDouble(((JSONObject) (((JSONObject) jSteps.get(k)).get("duration"))).get("value").toString()));
                String polyline = "";
                polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                List list = decodePoly(polyline);
                for (int l = 0; l < list.size(); l++) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    hm.put("lat", Double.toString(((LatLng) list.get(l)).latitude));
                    hm.put("lng", Double.toString(((LatLng) list.get(l)).longitude));
                    pathList.add(hm);
                }
            }
            cost = 0;
            cal = 0;
            calcCal();
            calcCost();
        }catch (JSONException e) {
            //some exception handler code.
        }
    }


    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
    private void calcCost(){
        boolean cycleFlag = false;
        for(int i=0; i<modes.size(); i++) {
            if (new String(modes.get(i)).equals(new String("DRIVING"))) {
                cost += (0.786090909 * distances.get(i));
                /// 1 m = 0.000621371 mile
                /// cost per mile = $ 0.786090909
            } else if (new String(modes.get(i)).equals(new String("BICYCLING"))) {
                if(cycleFlag == false) {
                    cost += 2;
                    cycleFlag = true;
                }
            } else {
                cost += 0;
            }
        }
    }
    private void calcCal(){
        for(int i=0; i<modes.size(); i++) {
            if (new String(modes.get(i)).equals(new String("DRIVING")) || new String(modes.get(i)).equals(new String("TRANSIT"))) {
                // driving or transit burns 85.4166 cal per hour
                cal += (85.4166 * times.get(i) / 60 / 60);
            } else if (new String(modes.get(i)).equals(new String("BICYCLING"))) {
                cal += (54.1666 * 1000 * distances.get(i));
                // per mile biking is 54.1666 kcal/mile
            } else {
                cal += 100 * distances.get(i);
                // walking is 100 cal / mile
            }
        }
    }
}
