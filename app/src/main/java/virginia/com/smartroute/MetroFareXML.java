package virginia.com.smartroute;

// // This sample uses the Apache HTTP client from HTTP Components (http://hc.apache.org/httpcomponents-client-ga/)
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPostHC4;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.client.methods.HttpGetHC4;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.StringEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MetroFareXML {

    public String startStat;
    public String endStat;
    public Double compMi;
    public Double railTime;
    public Double peakFare;
    public Double offPeakFare;
    public Double seniorFare;







        public Double mainFare(final String start, final String end) {

            new Thread(new Runnable() {
                public void run() {
                    HttpClient httpclient = HttpClients.createDefault();

                    try {

                        URIBuilder builder = new URIBuilder("https://api.wmata.com/Rail.svc/json/jSrcStationToDstStationInfo");

                        builder.setParameter("FromStationCode", start);
                        builder.setParameter("ToStationCode", end);

                        URI uri = builder.build();
                        //HttpPostHC4 request = new HttpPostHC4(uri);
                        HttpGetHC4 request = new HttpGetHC4(uri);
                        request.setHeader("api_key", "ae7fb4a5bd5c45e18aad6936edad5965");
                        //builder.setParameter("api_key", "ae7fb4a5bd5c45e18aad6936edad5965");
                        //request.setHeader("api_key", "{}");

                        // Request body
                        //StringEntity reqEntity = new StringEntity("{body}");
                        //request.setEntity(reqEntity);
                        //request.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

                        HttpResponse response = httpclient.execute(request);
                        HttpEntity entity = response.getEntity();


                        if (entity != null) {
                            //System.out.println(EntityUtils.toString(entity));

                            String in = EntityUtils.toString(entity);
                            JSONObject reader = new JSONObject(in);

                            JSONObject st = reader.getJSONArray("StationToStationInfos").getJSONObject(0);
                            String strt = st.getString("SourceStation");
                            String ed = st.getString("DestinationStation");
                            Double cm = st.getDouble("CompositeMiles");
                            Double rt = st.getDouble("RailTime");
                            JSONObject rf = st.getJSONObject("RailFare");
                            Double pt = rf.getDouble("PeakTime");
                            Double op = rf.getDouble("OffPeakTime");
                            Double sd = rf.getDouble("SeniorDisabled");
                            System.out.println(pt.toString());

                            SetVars(strt,ed,cm,rt,pt,op,sd);

                            //JSONObject main  = reader.getJSONObject("main");
                            //temperature = main.getString("temp");

                        }
                    } catch (Exception e) {
                        //System.out.println(e.getMessage());
                        System.out.println("Issue");
                    }
                }
            }).start();
            return peakFare;
        }

        public void SetVars(String s, String e, Double m, Double t, Double c, Double o, Double d) {
            startStat = s;
            endStat = e;
            compMi = m;
            railTime = t;
            peakFare = c;
            offPeakFare = o;
            seniorFare = d;

        }




}
