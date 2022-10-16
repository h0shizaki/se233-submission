package se233.chapter3.controller;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se233.chapter3.Launcher;
import se233.chapter3.model.FileFreq;
import se233.chapter3.model.PDFdocument;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;


public class MainViewController {
    private Map<String , String> fileMap ;
    Logger logger = LoggerFactory.getLogger(MainViewController.class);
    LinkedHashMap<String,ArrayList<FileFreq>> uniqueSets;
    @FXML
    private ListView<String> inputListView ;
    @FXML
    private ProgressBar progressBar ;
    @FXML
    private ListView listView ;

    @FXML
    private Button startButton ;

    @FXML
    private MenuItem closeMenu ;

    @FXML
    public void initialize() {
        // init Hashmap to map filePath with fileName
        fileMap = new HashMap<>() ;

        inputListView.setOnDragOver( e -> {
            Dragboard db = e.getDragboard();
            final boolean isAccept = db.getFiles().get(0).getName().toLowerCase().endsWith(".pdf");

            if(db.hasFiles() && isAccept) {
                e.acceptTransferModes(TransferMode.COPY);
            }else{
                e.consume();
            }
        });

        inputListView.setOnDragDropped( event -> {
            Dragboard db = event.getDragboard();
            boolean success = false ;
            if(db.hasFiles()) {
                success = true ;
                String filePath ;
                int total_files = db.getFiles().size() ;

                for(int i = 0 ; i < total_files ; i++){
                    File file = db.getFiles().get(i);
                    filePath = file.getAbsolutePath();
                    //add map path and map
                    fileMap.put(file.getName(),filePath);
                    inputListView.getItems().add(file.getName()) ;
                }

            }
            event.setDropCompleted(success);
            event.consume();
        } );

        listView.setOnMouseClicked( event -> {
            try{
                ArrayList<FileFreq> listOfLinks = uniqueSets.get(listView.getSelectionModel().getSelectedItem());
                ListView<FileFreq> popupListView = new ListView<>() ;
                LinkedHashMap<FileFreq,String> lookupTable = new LinkedHashMap<>() ;

                for(int i = 0 ; i < listOfLinks.size() ; i++) {
                    lookupTable.put(listOfLinks.get(i), listOfLinks.get(i).getPath());
                    popupListView.getItems().add(listOfLinks.get(i));
                }
                popupListView.setPrefHeight(popupListView.getItems().size()*28);
                popupListView.setOnMouseClicked( innerEvent -> {
                    Launcher.hs.showDocument("file:///"+lookupTable.get(popupListView.getSelectionModel().getSelectedItem()));
                    popupListView.getScene().getWindow().hide();
                });

                Popup popup = new Popup();
                popup.getContent().add(popupListView);
                popup.show(Launcher.stage);

                popupListView.addEventHandler(KeyEvent.KEY_PRESSED , innerEvent -> {
                    if(KeyCode.ESCAPE == innerEvent.getCode()) {
                        popup.hide();
                    }
                } );
            }catch (Exception e){
//                e.printStackTrace();
            }
        });


        startButton.setOnAction( event -> {
            //Clear old data in list
            listView.getItems().clear();

            Parent bgRoot = Launcher.stage.getScene().getRoot();
            Task<Void> processTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    ProgressIndicator pi = new ProgressIndicator() ;
                    VBox box = new VBox(pi) ;
                    box.setAlignment(Pos.CENTER);
                    Launcher.stage.getScene().setRoot(box);

                    ExecutorService executor = Executors.newFixedThreadPool(1) ;
                    final ExecutorCompletionService<Map<String,FileFreq>> completionService = new ExecutorCompletionService<>(executor) ;

                    // Convert file name to file path by using fileMap
                    ArrayList<String> inputListViewName = new ArrayList<>() ;
                    for(int i = 0 ; i < inputListView.getItems().size() ;i++){
                        logger.info("Open file: {}",inputListView.getItems().get(i));
                        inputListViewName.add(fileMap.get(inputListView.getItems().get(i)));
                    }
                    List<String> inputListViewItems = inputListViewName ;


                    int total_files = inputListViewItems.size() ;
                    Map<String, FileFreq>[] wordMap = new Map[total_files] ;

                    for(int i = 0 ; i < total_files ; i++){
                        try{
                            String filePath = inputListViewItems.get(i) ;
                            PDFdocument p = new PDFdocument(filePath);
                            completionService.submit(new WordMapPageTask(p));
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                    for (int i = 0 ; i < total_files ; i++){
                        try{
                            Future<Map<String,FileFreq>> future = completionService.take() ;
                            wordMap[i] = future.get();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    try{
                        WordMapMergeTask merger = new WordMapMergeTask(wordMap);
                        Future<LinkedHashMap<String, ArrayList<FileFreq>>> future = executor.submit(merger) ;
                        uniqueSets = future.get() ;

                        //Show the duplicate word frequency count.
                        //By create new set new key and value to the uniqueSets map.
                        LinkedHashMap<String, ArrayList<FileFreq>> uniqueSetCount = new LinkedHashMap<>() ;

                        //Loop all element in frequency list
                        //Then create new key
                        uniqueSets.entrySet().stream()
                                .forEach( arr -> {
                                    String key = arr.getKey() ;

                                    //Cursed way to create new key
                                    for(int i = 0 ; i < arr.getValue().size() ; i++){
                                        if(i == 0) key += "(" ;
                                        key += arr.getValue().get(i).getFreq();
                                        if(i != arr.getValue().size()-1) {
                                            key += ",";
                                        }else {
                                            key += ")";
                                        }
                                    }

                                    //Add new key to temporary map
                                    uniqueSetCount.put(key,arr.getValue());

                                });

                        //Apply temporary map to uniqueSet.
                        uniqueSets = uniqueSetCount;

                        //Add set of map key to the list view
                        listView.getItems().addAll( uniqueSets.keySet() ) ;

                    }catch (ExecutionException | InterruptedException e){
                        e.printStackTrace();
                    }finally {
                        executor.shutdown();
                    }

                    return null ;
                }
            };

            processTask.setOnSucceeded( e -> {
                Launcher.stage.getScene().setRoot(bgRoot);
            });

            Thread thread = new Thread(processTask) ;
            thread.setDaemon(true);
            thread.start();
        });

        //Create close menu controller
        closeMenu.setOnAction(event -> System.exit(0));
    }
}
