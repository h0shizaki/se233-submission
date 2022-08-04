package se233.chapter2.view;


import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import se233.chapter2.model.Currency;
import java.util.ArrayList;
import java.util.concurrent.*;


public class CurrencyParentPaneCallable extends FlowPane {
    public CurrencyParentPaneCallable(ArrayList<Currency> currencyList) {
        this.setPadding(new Insets(0));
        refreshPane(currencyList) ;
    }

    public void refreshPane(ArrayList<Currency> currencyList) {
        this.getChildren().clear();

        ExecutorService executor = Executors.newSingleThreadExecutor() ;
        for (Currency currency : currencyList) {

            try{
                FutureTask<CurrencyPane> futureTask =  new FutureTask<>( new CurrencyPane(currency) );
                executor.execute(futureTask);
                this.getChildren().add(futureTask.get());
            }catch (ExecutionException e) {
                e.printStackTrace();
            }catch (InterruptedException e){
                e.printStackTrace();
            }

        }


    }
}