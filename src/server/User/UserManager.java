package server.User;

import server.ServerThread;
import server.exceptions.DuplicateUsernameException;
import server.exceptions.UserNotFoundException;

import java.util.*;
import java.util.logging.Logger;

public class UserManager {

    private static final Logger logger = Logger.getLogger(UserManager.class.getName());
    private Map<User, ServerThread> userServerThreadMap;

    public UserManager() {
        userServerThreadMap = new HashMap<>();
    }

    public void connectUser(User user, ServerThread thread) throws DuplicateUsernameException {
        boolean duplicate = false;
        for (User u : userServerThreadMap.keySet()) {
            if (u.equals(user)) {
                duplicate = true;
            }
        }
        if (duplicate || user.compareWithString("Server")) {


            throw new DuplicateUsernameException(user.getName());
        } else {

            userServerThreadMap.put(user, thread);

        }
    }

    public void disconnectUser(User user) throws UserNotFoundException {
        if (userServerThreadMap.containsKey(user)) {
            userServerThreadMap.remove(user);
        } else throw new UserNotFoundException(user.getName());
    }

    public ServerThread getServerThread(User user) {

        return userServerThreadMap.get(user);

    }

    public Set<String> getUserStrings() {
        Set<String> userStrings = new HashSet<>();
        for (User u: userServerThreadMap.keySet()){
            userStrings.add(u.getName());
        }
        return userStrings;
    }

    public ServerThread getServerThreadByString(String userString) {
        User u = null;
        for (User user : userServerThreadMap.keySet()) {
            if (user.compareWithString(userString)) {
                u = user;
            }
        }

        return userServerThreadMap.get(u);
    }

    public Collection<ServerThread> getServerThreads() {
        return userServerThreadMap.values();
    }

    public Set<ServerThread> getServerThreadByUsers(Set<User> userSet) {
        Set<ServerThread> threads = new HashSet<>();
        for (User user : userServerThreadMap.keySet()) {
            if (userSet.contains(user)) {
                threads.add(userServerThreadMap.get(user));
            }
        }
        return threads;
    }
}
