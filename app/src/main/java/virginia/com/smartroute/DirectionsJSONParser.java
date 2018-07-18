package virginia.com.smartroute;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by anupamchugh on 27/11/15.
 */

public class DirectionsJSONParser {
    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    public List<SmartSegment> parse(JSONObject jObject){

        List<SmartSegment> smartSegments = new ArrayList<SmartSegment>() ;
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");


            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++) {
                /** Fill Create a smart route object*/
                smartSegments.add(new SmartSegment((JSONObject) ((JSONObject) jRoutes.get(i)).getJSONArray("legs").get(0)));
               /* jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String, String>>();

                *//** Traversing all legs *//*
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");


                    *//** Traversing all steps *//*
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List list = decodePoly(polyline);

                        *//** Traversing all points *//*
                        for(int l=0;l <list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return smartSegments;
    }

}