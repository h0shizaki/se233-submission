module se233.chapter5 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.slf4j;
    requires org.apache.logging.log4j;


    opens se233.chapter5 to javafx.fxml;
    exports se233.chapter5;
}