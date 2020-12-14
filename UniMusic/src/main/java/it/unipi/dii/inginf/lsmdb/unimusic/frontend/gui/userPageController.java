package it.unipi.dii.inginf.lsmdb.unimusic.frontend.gui;

import it.unipi.dii.inginf.lsmdb.unimusic.frontend.MiddlewareConnector;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.entities.Playlist;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.entities.Song;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.entities.User;
import it.unipi.dii.inginf.lsmdb.unimusic.middleware.exception.ActionNotCompletedException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class userPageController implements Initializable {
    private static MiddlewareConnector connector = MiddlewareConnector.getInstance();
    private static User userToDisplay;
    private static final ListToShow defaultList = ListToShow.PLAYLISTS;
    private static ListToShow listToShow = defaultList;
    private static final int previewImageHeight = 100;

    @FXML private AnchorPane parentPane;

    @FXML private TextField followsYou;
    @FXML private TextField userCompleteName;

    @FXML private Button followButton;

    @FXML private Button playlistsButton;
    @FXML private Button followingButton;
    @FXML private Button followedButton;

    @FXML private VBox listPane;

    public static void getUserPage(User user) throws IOException {
        try {
            userToDisplay = connector.getUser(user);
        } catch (ActionNotCompletedException e) {
            throw new IOException(e.getMessage());
        }
        App.setRoot("userPage");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        displayUserInformation();
        initializeButtons();
        displayListToShow();
    }

    //---------------------------------------------------------------------------------------------------------

    private void displayUserInformation() {
        if(userToDisplay.getUsername().equals(connector.getLoggedUser().getUsername()))
            parentPane.getChildren().remove(followsYou);
        else {
            if(connector.isFollowedBy(userToDisplay)) {
                followsYou.setVisible(true);
            }
        }

        userCompleteName.setText(userToDisplay.getFirstName() + " " + userToDisplay.getLastName());
    }

    private void initializeButtons() {
        if(userToDisplay.getUsername().equals(connector.getLoggedUser().getUsername()))
            parentPane.getChildren().remove(followButton);
        else {
            if(connector.follows(userToDisplay)) {
                followButton.setText("Unfollow");
                followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold");
            } else {
                followButton.setText("Follow");
                followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold");
            }
        }

        followButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    if(connector.follows(userToDisplay)) {
                        connector.unfollow(userToDisplay);
                        followButton.setText("Follow");
                        followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold");
                    } else {
                        connector.follow(userToDisplay);
                        followButton.setText("Unfollow");
                        followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold");
                    }
                } catch (ActionNotCompletedException e) {
                    e.printStackTrace();
                }
            }
        });

        playlistsButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ListToShow oldList = listToShow;
                listToShow = ListToShow.PLAYLISTS;
                try {
                    getUserPage(userToDisplay);
                } catch (IOException e) {
                    listToShow = oldList;
                    e.printStackTrace();
                }
            }
        });

        followingButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ListToShow oldList = listToShow;
                listToShow = ListToShow.FOLLOWING;
                try {
                    getUserPage(userToDisplay);
                } catch (IOException e) {
                    listToShow = oldList;
                    e.printStackTrace();
                }
            }
        });

        followedButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ListToShow oldList = listToShow;
                listToShow = ListToShow.FOLLOWED;
                try {
                    getUserPage(userToDisplay);
                } catch (IOException e) {
                    listToShow = oldList;
                    e.printStackTrace();
                }
            }
        });
    }

    private void displayListToShow() {

        switch (listToShow) {
            case PLAYLISTS:
                playlistsButton.setTextFill(Color.WHITE);
                displayPlaylistList();
                break;
            case FOLLOWING:
                followingButton.setTextFill(Color.WHITE);
                displayFollowingList();
                break;
            case FOLLOWED:
                followedButton.setTextFill(Color.WHITE);
                displayFollowedList();
        }

        listToShow = defaultList;
    }

    private void displayPlaylistList() {
        List<Playlist> playlistList = connector.getUserPlaylists(userToDisplay);
        if(playlistList.size() == 0)
            displayEmpty(listPane);
        else {
            for(Playlist playlist: playlistList) {
                listPane.getChildren().add(createPlaylistPreview(playlist));
            }
        }
    }

    private void displayFollowingList() {

    }

    private void displayFollowedList() {

    }

    //------------------------------------------------------------------------------------------------------------

    private void displayEmpty(Pane pane) {
        pane.getChildren().clear();

        Text emptyText = new Text("<EMPTY>");
        emptyText.setStyle("-fx-font-size: 28");
        emptyText.getStyleClass().add("text-id");

        pane.getChildren().add(emptyText);
    }

    private AnchorPane createPlaylistPreview(Playlist playlist) {
        AnchorPane playlistPreview = new AnchorPane();
        Separator horizontalSeparator = getHorizontalSeparator();
        Button playlistInformations = new Button(); playlistInformations.setStyle("-fx-background-color: transparent");
        playlistInformations.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                System.out.println("OPEN PLAYLIST <" + playlist.getID() + "> " + playlist.getName());
            }
        });

        Image playlistImage;
        HBox playlistGraphic = new HBox(30);
        System.out.println(playlist.getUrlImage());
        try {
            if(playlist.getUrlImage() == null || playlist.getUrlImage().equals(""))
                throw new Exception();

            playlistImage = new Image(
                    playlist.getUrlImage(),
                    0,
                    previewImageHeight,
                    true,
                    true,
                    true
            );

            if(playlistImage.isError()) {
                throw new Exception();
            }

        }catch(Exception ex){
            playlistImage = new Image(
                    "file:src/main/resources/it/unipi/dii/inginf/lsmdb/unimusic/frontend/gui/img/empty.jpg",
                    0,
                    previewImageHeight,
                    true,
                    true,
                    true
            );
        }

        ImageView playlistImageView = new ImageView(playlistImage);
        Text name = new Text(playlist.getName()); name.setFill(Color.WHITE); name.setStyle("-fx-font-weight: bold; -fx-font-size: 18px");
        playlistGraphic.getChildren().addAll(playlistImageView, name);
        playlistInformations.setGraphic(playlistGraphic);
        playlistPreview.getChildren().addAll(horizontalSeparator, playlistInformations);

        // if the user showed is not the logged user, then add buttons to follow/unfollow playlists
        if ( !connector.getLoggedUser().getUsername().equals(userToDisplay.getUsername())) {
            Button followButton = new Button();
            AnchorPane.setTopAnchor(followButton, 53.0); AnchorPane.setRightAnchor(followButton, 100.0);

            if ( connector.isFollowingPlaylist(playlist)) {
                followButton.setText("Unfollow");
                followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold; -fx-text-fill: white");
            } else {
                followButton.setText("Follow");
                followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white");
            }

            followButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        if ( connector.isFollowingPlaylist(playlist)) {
                            connector.unfollowPlaylist(playlist);
                            followButton.setText("Follow");
                            followButton.setStyle("-fx-background-color: green; -fx-font-weight: bold; -fx-text-fill: white");
                        } else {
                            connector.followPlaylist(playlist);
                            followButton.setText("Unfollow");
                            followButton.setStyle("-fx-background-color: red; -fx-font-weight: bold; -fx-text-fill: white");
                        }
                    } catch (ActionNotCompletedException e) {
                        e.printStackTrace();
                    }
                }
            });

            playlistPreview.getChildren().add(followButton);
        }

        return playlistPreview;
    }

    private Separator getHorizontalSeparator() {
        Separator horizontalSeparator = new Separator();
        AnchorPane.setTopAnchor(horizontalSeparator, 0.0);
        AnchorPane.setLeftAnchor(horizontalSeparator, 0.0);
        AnchorPane.setRightAnchor(horizontalSeparator, 0.0);
        return horizontalSeparator;
    }

    private enum ListToShow {
        PLAYLISTS,
        FOLLOWING,
        FOLLOWED
    }
}