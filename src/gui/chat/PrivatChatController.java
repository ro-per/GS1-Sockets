package gui.chat;

import client.ChatApplication;
import client.ChatClient;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;

public class PrivatChatController {

    private String correspondent;
    /* ----------------------------- @FXML ----------------------------- */
    @FXML
    private TextField msgField;
    @FXML
    private Label chatTitle;
    @FXML
    private ListView chatPanePrivate;
    @FXML
    private Button send_button;


    public void initialize() {

        chatPanePrivate.setItems(ChatApplication.chatClient.getPrivateMessages());

        this.correspondent=ChatApplication.correspondent;

        String loggedInAs = "Logged in as (" + ChatApplication.chatClient.getUser().toString() + ")";
        String chattingTo = "your talking to (" + correspondent + ")";
        chatTitle.setText(loggedInAs + " | " + chattingTo);

    }

    /* ----------------------------- CONSTRUCTOR ----------------------------- */
    public PrivatChatController() {
    }

    public PrivatChatController(String correspondent) {
        this.correspondent = correspondent;
    }

    /* ----------------------------- SEND PRIVATE ----------------------------- */
    public void sendPrivateAction() throws IOException {
        String text = msgField.getText();
        if (!text.isEmpty()) {
            ChatApplication.chatClient.sendPrivateMsg(text, correspondent);
            msgField.clear();
        } else {
            flashTextField(this.msgField);
        }
    }

    /* ----------------------------- KEY PRESSED ----------------------------- */
    public void keyPressed(KeyEvent ke) throws IOException {
        if (ke.getCode().equals(KeyCode.ENTER)) sendPrivateAction();
    }

    /* ----------------------------- FIELD PRESSED ----------------------------- */
    public void messageFieldClicked() {
        msgField.setEffect(null);
    }

    /* ----------------------------- VISUAL EFFECTS ----------------------------- */
    public void flashTextField(TextField t) {
        Lighting errorLighting = new Lighting();
        t.setEffect(errorLighting);
    }

    /* ----------------------------- EXIT ----------------------------- */
    public void closePrivateChat() throws IOException {

        //TODO close private window
    }

}
