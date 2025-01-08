module push {
	requires java.rmi;
    requires javafx.controls;
    requires javafx.fxml;
    exports push;
    exports peertopeer;
    exports peertopeer.ui;
    opens peertopeer.ui to javafx.fxml;
}