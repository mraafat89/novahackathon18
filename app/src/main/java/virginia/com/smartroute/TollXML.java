package virginia.com.smartroute;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TollXML extends FragmentActivity {

    public List<String> tollList = new ArrayList<>();
    public List<String> startList = new ArrayList<>();
    public List<String> endList = new ArrayList<>();

    public void CalculateTollsCost(String enter, String exit) {
        // Download current toll data
        mainTollTrip();

        // read XML
        mainXmlRead();

        //System.out.println("End : ");

    }

    public void mainTollTrip() {
        String url = "https://smarterroads.org/dataset/download/29?token=E3EGM8dAEfbmumsBtS6Jnzcu69kkbbQzKdRPu50IP3Ie4NQe9sq8e9wQpnMp8jxg&file=TollingTripPricing-I66/TollingTripPricing_current.xml";
        try {
            //downloadUsingNIO(url, "current_tolls.xml");

            //downloadUsingStream(url,"SD card\\Android\\data");
            downloadUsingStream(url, "current_tolls.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadUsingStream(String urlStr, String file) throws IOException{

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            android.os.StrictMode.ThreadPolicy policy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
            android.os.StrictMode.setThreadPolicy(policy);
        }

        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        //FileOutputStream fis = new FileOutputStream(file);
        FileOutputStream fis = openFileOutput(file, Context.MODE_PRIVATE);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
        //System.out.println("Hello");

    }

    private static void downloadUsingNIO(String urlStr, String file) throws IOException {

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            android.os.StrictMode.ThreadPolicy policy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
            android.os.StrictMode.setThreadPolicy(policy);
        }

        URL url = new URL(urlStr);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();
    }





    public void mainXmlRead() {

        try {
            String filePath = "current_tolls.xml";
            //String filePath = "SD card\\Android\\data";
            File fXmlFile = new File(getFilesDir(), filePath);

            /*System.out.println("Hello");
            //filename is filepath string
            java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(fXmlFile));
            String line;
            StringBuilder sb = new StringBuilder();

            while((line=br.readLine())!= null){
                sb.append(line.trim());
            }
            System.out.println(sb);*/

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();



            //System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("opt");

            //System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                //System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    //System.out.println("Toll : " + eElement.getAttribute("ZoneTollRate"));
                    //System.out.println("Start : " + eElement.getAttribute("StartZoneID"));
                    //System.out.println("End : " + eElement.getAttribute("EndZoneID"));

                    String toll = eElement.getAttribute("ZoneTollRate");
                    String start = eElement.getAttribute("StartZoneID");
                    String end = eElement.getAttribute("EndZoneID");

                    tollList.add(toll);
                    startList.add(start);
                    endList.add(end);


                }
            }

            for (String toll : tollList) {
                System.out.println(toll);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        /*String filePath = "current_tolls.xml";
        //String filePath = "SD card\\Android\\data";
        File xmlFile = new File(getFilesDir(), filePath);
        //File xmlFile = new File(filePath);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
            NodeList nodeList = doc.getElementsByTagName("opt");
            //now XML is loaded as Document in memory, lets convert it to Object List
            List<String> tollList = new ArrayList<>();
            List<String> startList = new ArrayList<>();
            List<String> endList = new ArrayList<>();

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String toll = getTagValue("ZoneTollRate",element);
                    String start = getTagValue("StartZoneID",element);
                    String end = getTagValue("EndZoneID",element);
                    tollList.add(toll);
                    startList.add(start);
                    endList.add(end);
                }

            }

            //lets print Employee list information
            for (String toll : tollList) {
                System.out.println(toll);
            }
        } catch (SAXException | ParserConfigurationException | IOException e1) {
            e1.printStackTrace();
        }*/

    }

    private static String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }
}
