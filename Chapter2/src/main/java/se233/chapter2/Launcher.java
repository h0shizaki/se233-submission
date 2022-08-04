package se233.chapter2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import se233.chapter2.controller.Initialize;
import se233.chapter2.controller.RefreshTask;
import se233.chapter2.model.Currency;
import se233.chapter2.view.CurrencyParentPaneCallable;
import se233.chapter2.view.TopPane;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Launcher extends Application {
    private static Stage primaryStage;
    private static Scene mainScene;
    private static FlowPane mainPane;
    private static TopPane topPane;
    private static CurrencyParentPaneCallable currencyParentPaneCallable;
    private static ArrayList<Currency> currencyList;

    private static ArrayList<String> currencyCodeList;

    public static void initMainPane()  {
        mainPane = new FlowPane();
        topPane = new TopPane();
        currencyParentPaneCallable = new CurrencyParentPaneCallable(currencyList);
        mainPane.getChildren().add(topPane);
        mainPane.getChildren().add(currencyParentPaneCallable);
    }

    @Override
    public void start(Stage primaryStage) throws ExecutionException, InterruptedException {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Currency Watcher");
        this.primaryStage.setResizable(false);
        Initialize.initializeApp();
        initMainPane();
        mainScene = new Scene(mainPane);
        this.primaryStage.setScene(mainScene);
        this.primaryStage.show();
        RefreshTask r = new RefreshTask();
        Thread th = new Thread(r);
        th.setDaemon(true);
        th.start();
    }

    public static void refreshPane() throws ExecutionException, InterruptedException {
        topPane.refreshPane();
        currencyParentPaneCallable.refreshPane(currencyList);
        primaryStage.sizeToScene();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static ArrayList<Currency> getCurrencyList() {
        return currencyList;
    }
    public static void setCurrencyList(ArrayList<Currency> currencyList) {
        Launcher.currencyList = currencyList;
    }
    public static ArrayList<String> getCurrencyCodeList() {
        return currencyCodeList;
    }
    public static void setCurrencyCodeList(ArrayList<String> currencyCodeList) {
        Launcher.currencyCodeList = currencyCodeList;
    }
}
