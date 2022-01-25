package com.example.filemenager;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.pdfsam.rxjavafx.observables.JavaFxObservable;

import java.io.File;



public class HelloController {

    @FXML private Button buttonBack;
    @FXML private Button buttonGo;
    @FXML private TextField pathField;
    @FXML private VBox filesVBox;
    @FXML private ScrollPane scrollPane;
    @FXML private Button home;

    String currenthPath;
    File directory;
    Observer<String> finder;

    @FXML
    public void initialize() {
        currenthPath = "D:\\studia\\Semestr 5";
        directory = new File(currenthPath);
        pathField.setText(currenthPath);

        finder = new Observer<String>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onNext(@NonNull String path) {
                pathField.setText(path);
                clear();

                directory = new File(path);

                // dal folderow
                Observable
                        .just(directory)
                        .flatMap(dir ->
                                Observable.fromArray(dir.listFiles()))
                        .filter(File::isDirectory)
                        .forEach(this::addDirectory);

                // dla plikow
                Observable
                        .just(directory)
                        .flatMap(file ->
                                Observable.fromArray(file.listFiles()))
                        .filter(File::isFile)
                        .forEach(this::addFile);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("There was an error!");

                alert.showAndWait();
            }

            @Override
            public void onComplete() {

            }

            private void addDirectory(File dir) {
                Button button = new Button(dir.getName());
                button.setOnMouseClicked(mouseEvent -> mouseClickedButton(button, dir.getPath()));
                filesVBox.getChildren().add(button);
            }

            private void addFile(File file) {
                Label label = new Label(file.getName());
                label.setStyle("-fx-background-color: #E6D104");
                filesVBox.getChildren().add(label);
            }
        };

        Observable<ActionEvent> homeEvent = JavaFxObservable.actionEventsOf(home);
        homeEvent
                .map(i -> System.getProperty("user.home"))
                .subscribe(finder);

        Observable<ActionEvent> buttonBackEvent = JavaFxObservable.actionEventsOf(buttonBack);
        buttonBackEvent
                .map(i -> directory.getParent())
                .subscribe(finder);

        Observable<ActionEvent> buttonGoEvent = JavaFxObservable.actionEventsOf(buttonGo);
        buttonGoEvent
                .map(i -> pathField.getText())
                .subscribe(finder);

        buttonGo.fire();

//        Observable.interval(1, 1, TimeUnit.SECONDS)
//                .observeOn(JavaFxScheduler.platform())
//                .map(i -> i.toString())
//                .forEach(home::setText);

    }

    private void mouseClickedButton(Node button, String path) {
        Observable<ActionEvent> buttonEvent = JavaFxObservable.actionEventsOf(button);
        buttonEvent
                .map(i -> path)
                .subscribe(finder);
    }

    private void clear() {
        filesVBox.getChildren().clear();
    }

}