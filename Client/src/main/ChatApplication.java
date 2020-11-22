package main;

import com.sun.istack.internal.Nullable;
import gui.PrivatChatController;
import gui.PublicChatController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatApplication extends Application {
    private final Logger logger = Logger.getLogger(ChatApplication.class.getName());

    private static Stage publicStage, privateStage;
    private static Scene publicScene;
    private static BorderPane publicBorderPane;
    private static ChatApplication chatApplication;
    public static ChatClient chatClient = null;
    private static PublicChatController publicChatController;
    private static URL loginFXML, publicFXML, privateFXML;
    public static final String title = "Socket-based Chat";
    public static FXMLLoader fxmlLoader;
    public static String correspondent = null;

    /*  -------------------------------- CONSTRUCTOR -------------------------------- */
    public ChatApplication() throws MalformedURLException {
        publicChatController = new PublicChatController();
        if (chatApplication == null) chatApplication = this;

        ClassLoader classLoader = getClass().getClassLoader();
        loginFXML= classLoader.getResource("gui/LoginForm.fxml");
        publicFXML= classLoader.getResource("gui/PublicChat.fxml");
        privateFXML= classLoader.getResource("gui/PrivateChat.fxml");
    }

    /*  -------------------------------- START -------------------------------- */
    @Override
    public void start(Stage primaryStage) {

        //Init stage
        publicStage = primaryStage;
        publicStage.setTitle(title);
        try {
            showLogin("Welcome to "+title);
        } catch (Exception e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }

    /*  -------------------------------- CONNECTING -------------------------------- */
    public static void connectToServer(String userName, String serverName, int portNumber) throws IOException {
        chatClient = new ChatClient(userName, serverName, portNumber);
        if (chatClient.start()) {
            chatClient.connectUser(userName);
            launchPublicChat();
        }
    }

    /*  -------------------------------- LOGIN GUI -------------------------------- */
    public static void showLogin(String title) {
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(loginFXML);
        try {
            publicBorderPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publicScene = new Scene(publicBorderPane);
        publicStage.setScene(publicScene);

        publicStage.setOnShowing(event -> {
            publicStage.setResizable(false);
            publicStage.setFullScreen(false);
        });
        publicStage.setTitle(title);
        publicStage.show();
    }

    /*  -------------------------------- PUBLIC GUI -------------------------------- */
    public static void launchPublicChat() {
        publicChatController.setChatClient(chatClient);

        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(publicFXML);
        try {
            publicBorderPane = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        publicScene = new Scene(publicBorderPane);
        publicStage.setScene(publicScene);

        publicStage.setOnCloseRequest(event -> {
            try {
                publicChatController.closePublicChat(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        publicStage.setTitle("Group Chat");
        publicStage.show();
    }

    /*  -------------------------------- PRIVATE GUI -------------------------------- */
    public static void launchPrivateChat(String user) {
        privateStage = new Stage();
        correspondent = user;
        PrivatChatController privatChatController = new PrivatChatController();
        fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(privateFXML);
        try {
            BorderPane privateBorderPane = fxmlLoader.load();
            Scene privateScene = new Scene(privateBorderPane);
            privateStage.setScene(privateScene);

            privateStage.setOnCloseRequest(event -> {
                try {
                    boolean b = askCloseCurrentChat(event);

                    if (b) {
                        privatChatController.closePrivateChat();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            privateStage.setTitle("Private Chat wit " + user);
            privateStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void resetPrivateChat() {
        if (privateStage != null) {
            privateStage.close();
        }
        correspondent = null;
        chatClient.resetPrivateChat();

    }


    /*  -------------------------------- ALERTS -------------------------------- */
    public static boolean askOpenNewChat(String newUser) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, newUser + " send you a message, do you want to answer ?", ButtonType.YES, ButtonType.NO);
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        return ButtonType.YES.equals(result);
    }

    public static boolean askCloseCurrentChat(WindowEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to close this private chat with " + correspondent + "?", ButtonType.YES, ButtonType.NO);
        ButtonType result = alert.showAndWait().orElse(ButtonType.NO);
        if (ButtonType.NO.equals(result)) {
            event.consume();
        }
        return ButtonType.YES.equals(result);
    }

    /*  -------------------------------- GETTERS -------------------------------- */
    public static ChatApplication getApplication() {
        return chatApplication;
    }

    public static ChatClient getChatClient() {
        return chatClient;
    }

    /*  -------------------------------- LOGGER -------------------------------- */
    private void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}
