module pull {
	requires java.rmi;
    requires javafx.controls;
    requires javafx.fxml;
    exports pull;
    exports peertopeer;
    exports peertopeer.ui;
    opens peertopeer.ui to javafx.fxml;
}