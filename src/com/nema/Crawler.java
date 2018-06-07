package com.nema;

import org.apache.commons.io.IOUtils;
import org.json.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class Crawler {
    protected List<String> linksToVisit = new ArrayList<>();
    protected Set<String> queued = new HashSet<>();
    protected Set<String> visited = new HashSet<>();
    protected static long MAX_PAGES_TO_CRAWL = 10000;
    protected static final String REMOTE_JSON = "https://raw.githubusercontent.com/OnAssignment/compass-interview/master/data.json";
    protected long numRequests;
    protected long numFailed;
    protected long numSuccess;
    /*
    Assumes that a successful connection to the page has a response status code 200.  If the response is
    anything else, the status code is printed in the console.
     */
    public boolean crawlPage(String strUrl)
    {
        BufferedReader br = null;
        HttpsURLConnection connection = null;
        try
        {
            URL url = new URL(strUrl);
            connection = (HttpsURLConnection)url.openConnection();
            numRequests++;
            if(connection.getResponseCode() == 200)
            {
                System.out.println("Successfully connected to " + url);
            }
            else{
                System.out.println("Failed connecting to " + url +"\n\tStatus Code:"
                        +connection.getResponseCode());
                return false;
            }

            if(!connection.getContentType().contains("text/html"))
            {
                System.out.println("\tNot able to parse content type");
                return false;
            }
            br =
                    new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String input;
            while ((input = br.readLine()) != null){
                sb.append(input.toLowerCase());
            }
            br.close();
            connection.disconnect();
            String baseUri = url.getProtocol()+"://"+url.getHost();
            Document htmlDocument = Jsoup.parse(sb.toString(), baseUri);
            Elements linksOnPage = htmlDocument.select("a[href]");
            String absUrl;
            for(Element link : linksOnPage)
            {
                absUrl = link.absUrl("href");
                if(!visited.contains(absUrl)  && !queued.contains(absUrl)){
                    linksToVisit.add(absUrl);
                    queued.add(absUrl);
                }

            }
            return true;
        }
        catch(IOException ioe)
        {
            System.out.println("The HTTP request threw an exception: "+strUrl);
            return false;
        }
        finally{
            if(connection!=null)
                connection.disconnect();
            if (br != null)
                try {
                        br.close();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
        }
    }

    /*
    Crawls the links included in the remote URL (a json page), and all of their links provided
    they haven't already been included.  Will not return to pages already checked or placed
    on the queue to be visited in the future.
     */
    public void crawl(){
        boolean successful;
        while(visited.size()< MAX_PAGES_TO_CRAWL && !linksToVisit.isEmpty()) {
                String nextURL = linksToVisit.remove(0);
                if(!visited.contains(nextURL)){
                    successful = crawlPage(nextURL);
                    if(successful)
                        numSuccess++;
                    else
                        numFailed++;
                    visited.add(nextURL);

                }
        }



        if(visited.size()> MAX_PAGES_TO_CRAWL)
        {
            System.out.println("Exceeded maximum number of pages to crawl");
        }
        System.out.println(
                "**********************************************\n"+
                "STATISTICS\n"+
                "**********************************************\n"+
                "total number of http requests performed: "+numRequests+"\n" +
                "total number of successful requests: "+ numSuccess +"\n" +
                "total number of failed requests: "+ numFailed +"\n");

    }
    public Crawler(){
        URL url = null;
        try {
            url = new URL(REMOTE_JSON);
            JSONObject json = new JSONObject(IOUtils.toString(url, Charset.forName("UTF-8")));
            JSONArray arr = json.getJSONArray("links");
            for(int i=0; i<arr.length(); i++){
                linksToVisit.add(arr.getString(i));
                queued.add(arr.getString(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    The user has the option to specify the maximum number of connections made; otherwise a default
    of 10,000 is used.
     */
    public static void main(String[] args) {
        if(args.length>0)
            MAX_PAGES_TO_CRAWL = Long.parseLong(args[0]);
        Crawler c = new Crawler();
        c.crawl();
    }
}
