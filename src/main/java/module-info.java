module com.example.filemenager {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.reactivex.rxjava3;
    requires org.pdfsam.rxjavafx;


    opens com.example.filemenager to javafx.fxml;
    exports com.example.filemenager;
}