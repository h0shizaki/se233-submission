package se233.chapter2.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import se233.chapter2.controller.AllEventHandler;
import se233.chapter2.controller.DrawGraphTask;
import se233.chapter2.model.Currency;

import java.util.concurrent.*;


public class CurrencyPane extends BorderPane implements Callable<CurrencyPane> {
    private Currency currency ;
    private Button watch ;
    private Button delete ;
    private Button unWatch ;

    @Override
    public CurrencyPane call() {
//        System.out.println("Test callable Border Pane");
        return this ;
    }
    public CurrencyPane(Currency currency) {

        this.delete = new Button("Delete");
        this.watch = new Button("Watch");
        this.unWatch = new Button("Unwatch");

        //Set delete button onClick
        delete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onDelete(currency.getShortCode()) ;
            }
        });
        //Set watch button onClick
        watch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AllEventHandler.onWatch(currency.getShortCode());
            }
        });

        //Set unWatch button onClick
        unWatch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                AllEventHandler.onUnWatch(currency.getShortCode());
            }
        });

        this.setPadding(new Insets(0));
        this.setPrefSize(640,300);
        this.setStyle("-fx-border-color: black");
        try{
            this.refreshPane(currency) ;
        }catch (ExecutionException err){
            System.out.println("Encountered an execution exception.");
        }catch (InterruptedException err){
            System.out.println("Encountered an interrupted exception.");
        }

    }

    public void refreshPane(Currency currency) throws ExecutionException , InterruptedException{
        this.currency = currency ;
        Pane currencyInfo = genInfoPane() ;
        Pane topArea = genTopArea() ;

        FutureTask futureTask = new FutureTask<VBox>(new DrawGraphTask(currency));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(futureTask);
        VBox currencyGraph = (VBox) futureTask.get() ;

        this.setTop(topArea) ;
        this.setLeft(currencyInfo) ;
        this.setCenter(currencyGraph);
    }

    private Pane genInfoPane() {
        VBox currencyInfoPane = new VBox(10);
        currencyInfoPane.setPadding(new Insets(5,25,5,25));
        currencyInfoPane.setAlignment(Pos.CENTER);
        Label exchangeString = new Label("") ;
        Label watchString = new Label("") ;
        exchangeString.setStyle("-fx-font-size: 20");
        watchString.setStyle("-fx-font-size: 14");

        if(this.currency != null){
            exchangeString.setText(String.format("%s: %.4f",this
                    .currency.getShortCode(),this.currency.getCurrent().getRate())
            );

            if(this.currency.getWatch() == true) {
                watchString.setText(String.format("(Watch @%.4f)",this.currency.getWatchRate()));
            }
        }
        currencyInfoPane.getChildren().addAll(exchangeString,watchString);
        return currencyInfoPane;
    }

    private HBox genTopArea() {
        HBox topArea = new HBox(10);
        topArea.setPadding(new Insets(5));
        topArea.getChildren().addAll(watch,unWatch,delete);
        ((HBox) topArea).setAlignment(Pos.CENTER_RIGHT);
        return  topArea;
    }
}
