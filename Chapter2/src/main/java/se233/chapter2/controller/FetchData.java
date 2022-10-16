package se233.chapter2.controller;

import javafx.scene.control.Alert;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import se233.chapter2.model.CurrencyEntity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

public class FetchData {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd") ;

    public static ArrayList<CurrencyEntity> fetch_range(String src , int n ) {
        ArrayList<CurrencyEntity> currencyRateList = new ArrayList<>() ;
        String dateMid ;
        String dateStart ;
        String dateEnd ;

        try{
            if(n > 16) {
                //If the date is out of range
                System.out.println("Date of required information is out of range");
                System.exit(0);
            }else if (n <= 8){
                // if fetched date is in range 8 do only 1 query
                dateEnd = LocalDate.now().format(FORMATTER) ;
                dateStart = LocalDate.now().minusDays(n).format(FORMATTER) ;

                String req_url = createURL(src,dateStart,dateEnd);

                //Read json
                decodeJson(currencyRateList,src,req_url);

            }else{
                //range more than 8 days do 2 queries to the api
                dateEnd = LocalDate.now().format(FORMATTER) ;
                dateMid = LocalDate.now().minusDays(8).format(FORMATTER) ;
                dateStart = LocalDate.now().minusDays(n).format(FORMATTER);

                String req_url_1 = createURL(src,dateStart,dateMid);
                String req_url_2 = createURL(src,dateMid,dateEnd);
                decodeJson(currencyRateList,src,req_url_1);
                decodeJson(currencyRateList,src,req_url_2);

            }
        }catch (MalformedURLException err) {
            System.out.println("Encountered a Malformed Url exception");
        }catch (IOException err){
            System.out.println("Encounter an IO exception");
        }
        //Sort the currency data by date
        currencyRateList.sort(new Comparator<CurrencyEntity>() {
            @Override
            public int compare(CurrencyEntity o1, CurrencyEntity o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        if(currencyRateList.size() == 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error: Cannot fetch the api or the data is empty.");
            alert.setHeaderText(null);
            alert.setContentText("Failed to fetch data");
            alert.showAndWait();
            System.exit(0);
        }

        return currencyRateList ;
    }


    public static void decodeJson(ArrayList<CurrencyEntity> currencyRateList , String src, String req_url) throws MalformedURLException, IOException{
        String responseJson = null ;

        //Read json
        responseJson = IOUtils.toString(new URL(req_url) , Charset.defaultCharset()) ;
        JSONObject res = new JSONObject(responseJson).getJSONObject(String.format("%s_THB",src));

        Iterator keysToCopyIterator = res.keys() ;

        //Add to the storage
        while(keysToCopyIterator.hasNext()) {
            String key = (String)keysToCopyIterator.next();
            Double rate = Double.parseDouble(res.get(key).toString());
            currencyRateList.add(new CurrencyEntity(rate , key));
        }

    }
    public static String createURL(String src ,String dateStart , String dateEnd ) {
        //get api key from env variable
        String apiKey = System.getenv("JAVA_CURRENCY_API_KEY");

        return String.format("https://free.currconv.com/api/v7/convert?q=%s_THB&compact=ultra&date=%s&endDate=%s&apiKey=%s",
                src,
                dateStart,
                dateEnd,
                apiKey
        );
    }

    public static ArrayList<String> fetchCurrencyList() {
        //Do the request to the api to get list of currency code
        ArrayList<String> currencyCode = new ArrayList<>();
        String url = "https://free.currconv.com/api/v7/currencies?apiKey=" + System.getenv("JAVA_CURRENCY_API_KEY");
        String responseJson = null ;
        //Read json data
        try{
            responseJson = IOUtils.toString(new URL(url) , Charset.defaultCharset()) ;

            JSONObject res = new JSONObject(responseJson).getJSONObject("results");
            Iterator keysToCopyIterator = res.keys() ;

            //Parsing the json object to the storage variable
            while(keysToCopyIterator.hasNext()) {
                String key = (String)keysToCopyIterator.next() ;
                currencyCode.add(key);
            }

        }catch (MalformedURLException err) {
            System.out.println("Encountered a Malformed Url exception");
        }catch (IOException err){
            System.out.println("Encounter an IO exception");
        }
        return currencyCode ;
    }

}
