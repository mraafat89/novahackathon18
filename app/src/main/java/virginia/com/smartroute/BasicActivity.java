package virginia.com.smartroute;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.lyft.networking.ApiConfig;

public class BasicActivity extends Activity {
    private Button buttonSearch;
    private EditText editTextOrigin;
    private EditText editTextDstn;
    private List<SmartRoute> smartRoutes;
    static int counter = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        ApiConfig apiConfig = new ApiConfig.Builder()
                .setClientId("...")
                .setClientToken("...")
                .build();
        smartRoutes = new ArrayList<>();
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(new LatLng(38.958731 ,-77.356947), new LatLng(38.8539,-77.04924), "driving", "true", false);

                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);
            }
        });

        editTextOrigin = (EditText) findViewById(R.id.editTextOrigin);
        editTextDstn = (EditText) findViewById(R.id.editTextDestn);

        // Call metrofare function using start,end station
        // output data is stored within string, double variables in metrofare script
        //MetroFareXML.main("E10","J03");
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            BasicActivity.ParserTask parserTask = new BasicActivity.ParserTask();
            parserTask.execute(result);
        }
    }


    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<SmartSegment>> {
        // Parsing the data in non-ui thread
        @Override
        protected List<SmartSegment> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<SmartSegment> route = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                route = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return route;
        }

        @Override
        protected void onPostExecute(List<SmartSegment> segments) {
            TollXML tollXML = new TollXML();
            if(counter==0) {
                for (int i = 0; i < segments.size(); i++) {
                    if (i == 0 || i == 2) { // static tolls location
                        ;//TODO cost =  tollXML.CalculateTollsCost("3110", "3130");
                        segments.get(i).addCost(0.5);
                    }
                    smartRoutes.add(new SmartRoute(segments.get(i)));
                    //add parking cost
                    smartRoutes.get(i).addCost(21);
                }
                // Route Fastest Public Only
                ///
                String url = getDirectionsUrl(new LatLng(38.958731 ,-77.356947), new LatLng(38.853895,-77.049237), "transit", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // Route 2 Fastest Low Cost
             if (counter ==1) {
                 SmartRoute smartRoute = new SmartRoute(segments.get(0));
                 smartRoutes.add(smartRoute);
                // Route 2 origin to parking
                 String url = getDirectionsUrl(new LatLng(38.958731 ,-77.356947), new LatLng(38.959571,-77.357274), "walking", "false", true);
                 BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                 downloadTask.execute(url);
                 counter++;
                 return;
            }
            // Route 2 Parking to Parking
            if (counter ==2) {
                SmartRoute smartRoute = new SmartRoute(segments.get(0));
                smartRoutes.add(smartRoute);
                String url = getDirectionsUrl(new LatLng(38.959571 ,-77.357274), new LatLng(38.84401,-77.052395), "driving", "false", false);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // Route 2 parking to cycling
            if (counter ==3) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                String url = getDirectionsUrl(new LatLng(38.84401 ,-77.052395), new LatLng(38.842832,-77.050174), "walking", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // Route 2 cycling to cycling
            if (counter ==4) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                String url = getDirectionsUrl(new LatLng(38.842832 ,-77.050174), new LatLng(38.853303,-77.049611), "bicycling", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // Route 2 cycling to destn
            if (counter ==5) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                String url = getDirectionsUrl(new LatLng(38.853303 ,-77.049611), new LatLng(38.853895,-77.049237), "walking", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // Route 3 Most Calories Public
            // walk to bike
            if (counter ==6) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                // add parking
                smartRoutes.get(smartRoutes.size() - 1).addCost(7);
                String url = getDirectionsUrl(new LatLng(38.958731 ,-77.356947), new LatLng(38.957225,-77.358128), "walking", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            //bike to bike
            if (counter ==7) {
                SmartRoute smartRoute = new SmartRoute(segments.get(0));
                smartRoutes.add(smartRoute);
                // bike to bike
                String url = getDirectionsUrl(new LatLng(38.957225 ,-77.358128), new LatLng(38.948259,-77.338089), "bicycling", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // bike to bike thrw transit
            if (counter ==8) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                // bike to bike
                String url = getDirectionsUrl(new LatLng(38.948259 ,-77.338089), new LatLng(38.894618,-77.072233), "transit", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // bike to bike thrw transit
            if (counter ==9) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                // bike to bike
                String url = getDirectionsUrl(new LatLng(38.894618 ,-77.072233), new LatLng(38.853303,-77.049611), "bicycling", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // walk to destn
            if (counter ==10) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                // bike to bike
                String url = getDirectionsUrl(new LatLng(38.853303 ,-77.049611), new LatLng(38.853895,-77.049237), "walking", "false", true);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // Route 3 walk to park
            if (counter ==11) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                // walk to bike
                String url = getDirectionsUrl(new LatLng(38.958731 ,-77.356947), new LatLng(38.959571,-77.357274), "walking", "false", false);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // drive
            if (counter ==12) {
                SmartRoute smartRoute = new SmartRoute(segments.get(0));
                smartRoutes.add(smartRoute);
                // drive
                String url = getDirectionsUrl(new LatLng(38.959571 ,-77.357274), new LatLng(38.897688,-77.070637), "driving", "false", false);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // walk to bike
            if (counter ==13) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                // walk to bike
                String url = getDirectionsUrl(new LatLng(38.897688 ,-77.070637), new LatLng(38.894618, -77.072233), "walking", "false", false);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // bike
            if (counter ==14) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                //  bike
                String url = getDirectionsUrl(new LatLng(38.894618 ,-77.072233), new LatLng(38.853303, -77.049611), "bicycling", "false", false);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // walk
            if (counter ==15) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                //  walk
                String url = getDirectionsUrl(new LatLng(38.853303 , -77.049611), new LatLng(38.853895, -77.049237), "walking", "false", false);
                BasicActivity.DownloadTask downloadTask = new BasicActivity.DownloadTask();
                downloadTask.execute(url);
                counter++;
                return;
            }
            // end of route 3
            if (counter ==16) {
                smartRoutes.get(smartRoutes.size() - 1).addSegment(segments.get(0));
                smartRoutes.get(smartRoutes.size() - 1).addCost(18);
                counter++;

            }
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest, String mode, String alternatives, boolean tolls) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&mode=" + mode + "&alternatives=" + alternatives + "&key=AIzaSyD8Kt_dOhuiNVICRzh2Xtjy3HK0KSRX0Tc" ;
        if (tolls == true)
            parameters += "&avoid=tolls";
        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}
//ArrayList points = null;
//            PolylineOptions lineOptions = null;
//            MarkerOptions markerOptions = new MarkerOptions();
//
//            for (int i = 0; i < result.size(); i++) {
//                points = new ArrayList();
//                lineOptions = new PolylineOptions();
//
//                List<HashMap<String, String>> path = result.get(i);
//
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//
//                lineOptions.addAll(points);
//                lineOptions.width(12);
//                lineOptions.color(Color.RED);
//                lineOptions.geodesic(true);
//
//            }

// Drawing polyline in the Google Map for the i-th route
//mMap.addPolyline(lineOptions);