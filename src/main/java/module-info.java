module se233.chapter1.introtorpg {
    requires javafx.controls;
    requires javafx.fxml;


    opens se233.chapter1.introtorpg to javafx.fxml;
    exports se233.chapter1.introtorpg;
}