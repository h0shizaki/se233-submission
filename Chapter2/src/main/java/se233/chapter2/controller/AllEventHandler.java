package se233.chapter2.controller;

import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.chapter2.Launcher;
import se233.chapter2.model.Currency;
import se233.chapter2.model.CurrencyEntity;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class AllEventHandler {
    public static Logger logger = LoggerFactory.getLogger(AllEventHandler.class);

    public static void onRefresh() {
        try{
            Launcher.refreshPane();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void onAdd() {
        ArrayList<String> currencyCodeList = Launcher.getCurrencyCodeList();
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Currency");
            dialog.setContentText("Currency code:");
            dialog.setHeaderText(null);
            dialog.setGraphic(null);

            String inputCurrency  ;

            //Declare flag variable
            boolean flag = false ;
            // while-loop to prompt the user to input the currency code until the input is correct
            while (!flag) {
                Optional<String> code = dialog.showAndWait();
                if (code.isPresent()) {
                    //Read input from user
                    inputCurrency = code.get().toUpperCase();
                    // check the currency code is exist in the arraylist of the currency code list or not
                    flag = currencyCodeList.contains(inputCurrency);
                    Currency c = new Currency(inputCurrency);
                    //Do query if the input are contained in the arraylist of currencyCode
                    if(flag) {
                        ArrayList<Currency> currencyList = Launcher.getCurrencyList();
                        for(int i = 0 ; i < currencyList.size(); i++) {
                            if(currencyList.get(i).getShortCode().equals(inputCurrency)) return;
                        }
                        ArrayList<CurrencyEntity> c_list = FetchData.fetch_range(c.getShortCode(), 14);
                        c.setHistorical(c_list);
                        c.setCurrent(c_list.get(c_list.size() - 1));
                        currencyList.add(c);
                        Launcher.setCurrencyList(currencyList);
                        Launcher.refreshPane();
//                        System.out.println("Add currency {}");
                        logger.info("Add currency {}",code.get());
                    }else{
                        //If the response is empty that means the currency code is invalid
                        //Show the alert to re-enter again
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error: Invalid currency code ");
                        alert.setHeaderText(null);
                        alert.setContentText("Please re-enter the currency ");
                        alert.showAndWait();
                    }
                }else {
                    //If user hit cancel button stop the loop.
                    break;
                }
            }

        }catch(InterruptedException e){
            e.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }

    }

    public static void onDelete(String code) {
        try {
            ArrayList<Currency> currencyList = Launcher.getCurrencyList() ;
            int index = -1 ;
            for(int i = 0 ; i < currencyList.size() ; i++){
                if(currencyList.get(i).getShortCode().equals(code)){
                    index = i ;
                    break ;
                }
            }

            if(index != -1 ){
                Currency removedCurrency = currencyList.remove(index);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();
                logger.info("Delete currency {}",removedCurrency.getShortCode());
            }

        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }

    }

    public static void onWatch(String code){
        try {
            ArrayList<Currency> currencyList = Launcher.getCurrencyList();
            int index = -1;
            for (int i = 0; i < currencyList.size(); i++) {
                if (currencyList.get(i).getShortCode().equals(code)) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Add watch");
                dialog.setContentText("Rate:");
                dialog.setHeaderText(null);
                dialog.setGraphic(null);
                Optional<String> retrievedRate = dialog.showAndWait() ;

                if(retrievedRate.isPresent()){
                    double rate = Double.parseDouble(retrievedRate.get());
                    currencyList.get(index).setWatch(true);
                    currencyList.get(index).setWatchRate(rate);
                    Launcher.setCurrencyList(currencyList);
                    Launcher.refreshPane();
                }
            }
        }catch (InterruptedException e) {
            e.printStackTrace();
        }catch (ExecutionException e){
            e.printStackTrace();
        }
    }

    public static void onUnWatch(String code) {
        //Search for the currency which user wants to unWatch
        ArrayList<Currency> currencyList = Launcher.getCurrencyList();
        int index = -1;
        for (int i = 0; i < currencyList.size(); i++) {
            if (currencyList.get(i).getShortCode().equals(code)) {
                index = i;
                break;
            }
        }

        if(index != -1){
            try {
                currencyList.get(index).setWatch(false);
                currencyList.get(index).setWatchRate(0.0);
                Launcher.setCurrencyList(currencyList);
                Launcher.refreshPane();
            }catch (InterruptedException e) {
                e.printStackTrace();
            }catch (ExecutionException e){
                e.printStackTrace();
            }
        }

    }
}

