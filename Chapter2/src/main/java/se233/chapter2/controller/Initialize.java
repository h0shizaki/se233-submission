package se233.chapter2.controller;

import javafx.scene.control.Alert;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;
import java.util.ArrayList;

public class Initialize {
    public static void initializeApp()  {
        checkForAPIKey();
        Currency c = new Currency("USD");
        ArrayList<CurrencyEntity> c_list = FetchData.fetch_range(c.getShortCode(),14);
        c.setHistorical(c_list);
        c.setCurrent(c_list.get(c_list.size()-1));
        ArrayList<Currency> currencyList = new ArrayList<>() ;
        currencyList.add(c);
        Launcher.setCurrencyList(currencyList);

        ArrayList<String> currencyCode = FetchData.fetchCurrencyList();
        Launcher.setCurrencyCodeList(currencyCode);

    }

    public static void checkForAPIKey(){
        if(System.getenv("JAVA_CURRENCY_API_KEY") == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error: Missing api key ");
            alert.setHeaderText(null);
            alert.setContentText("Missing an api key please add the api to the environment variable");
            alert.showAndWait();
            System.exit(0);
        }
    }
}
