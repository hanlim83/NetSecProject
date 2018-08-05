package Model;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.gax.paging.Page;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Inbox {


    private ObservableList<Inbox> thisInbox = FXCollections.observableArrayList();

    private String name;
    private String detail;
    private Button button;

    private Storage storage;
    private OAuth2Login login = new OAuth2Login();
    private Credential credential;

    public Inbox(String filename, String date) {

        this.name = filename;
        this.detail = date;
        this.button = new Button("Download");

        button.setOnAction(e -> {

            System.out.println("hello world");

        });

    }

    public Inbox() {


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public ObservableList assign(String bucketname) throws Exception {
        getStorage();
        Page<Blob> blobs = storage.list(bucketname);

        for (Blob blob : blobs.iterateAll()) {

            String namef = blob.getName();
            System.out.println(namef);
            String detailf = convertTime(blob.getCreateTime());
            System.out.println(detailf);
            thisInbox.add(new Inbox(namef, detailf));
        }
        return thisInbox;

    }

    private String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat(" dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    private void getStorage() throws Exception {
        credential = login.login();
        if (credential.getExpiresInSeconds() < 250) {
            credential = login.login();
            storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        }
        if (storage == null) {
            storage = StorageOptions.newBuilder().setCredentials(GoogleCredentials.create(new AccessToken(credential.getAccessToken(), null))).build().getService();
        }
    }

}
