package it.unipi.dii.inginf.lsmdb.unimusic.frontend.gui;

import it.unipi.dii.inginf.lsmdb.unimusic.frontend.MiddlewareConnector;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class sideBarController implements Initializable {
    private MiddlewareConnector connector;

    @FXML private Button favourites;
    @FXML private Button addPlaylist;
    @FXML private Button personalProfile;
    @FXML private Button logout;
    @FXML private Button home;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connector = MiddlewareConnector.getInstance();

        home.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    App.setRoot("homepage");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        personalProfile.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    App.setRoot("personalProfile");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                connector.logout();
                try {
                    App.setRoot("welcome");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
