package server;

import com.sun.istack.internal.Nullable;
import server.User.User;
import server.User.UserManager;
import server.exceptions.DuplicateUsernameException;
import server.exceptions.UserNotFoundException;
import server.messages.Message;
import server.messages.MessageType;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatService {

    private static final Logger logger = Logger.getLogger(ChatService.class.getName());
    private final UserManager manager;

    private User serverUser = new User("Server");

    public ChatService() {
        manager = new UserManager();
    }


    public void connectUser(User user, ServerThread thread) throws IOException {
        info(user.getName() + " is connecting to the server.");
        try {
            manager.connectUser(user, thread);

/*            Message msg = new Message(serverUser,MessageType.PRIVATE, "Welcome to the chat!");
            thread.printOnOutputStream(msg);*/

            // NOTIFY OTHER USERS
            updateUsers(user, "connected");


        } catch (DuplicateUsernameException e) {
            Message msg = new Message(serverUser, MessageType.PRIVATE, "Username is already been used.");
            thread.printOnOutputStream(msg);
            info(user.getName() + " failed to connect to server.");
        }
    }

    public void sendPrivateMSG(Message msg) throws IOException {
        ServerThread thread = manager.getServerThreadByString(msg.getReceiverString());
        thread.printOnOutputStream(msg);

    }

    public void sendBroadcastMSG(Message msg) throws IOException {
        Collection<ServerThread> threads = manager.getServerThreads();
        msg.setActiveUsers(manager.getUserStrings()); //SEND UPDATE ABOUT USERS

        if (!threads.isEmpty()) {
            for (ServerThread thread : manager.getServerThreads()) { // each client has own server thread
                thread.printOnOutputStream(msg);
            }
            info(msg.getSender() + " is broadcasting: " + msg);
        }
    }

    private void updateUsers(User user, String info) throws IOException {
        // NOTIFY OTHER USERS
        Message msg1 = new Message(MessageType.BROADCAST, user.getName() + " is " + info);
        msg1.setSender(serverUser);
        sendBroadcastMSG(msg1);

    }


    public void disconnectUser(User user, ServerThread thread) {
        try {
            info(user.getName() + " is leaving the chat.");
            manager.disconnectUser(user);

            // NOTIFY OTHER USERS
            updateUsers(user, "disconnected");

            thread.stopThread();

        } catch (UserNotFoundException | IOException e) {
            error(e.getMessage());
        }

    }

    private static void info(String msg, @Nullable Object... params) {
        logger.log(Level.INFO, msg, params);
    }

    private static void error(String msg, @Nullable Object... params) {
        logger.log(Level.WARNING, msg, params);
    }
}