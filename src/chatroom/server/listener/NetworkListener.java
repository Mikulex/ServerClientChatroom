package chatroom.server.listener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import chatroom.model.User;
import chatroom.model.UserStorage;
import chatroom.server.Server;

public class NetworkListener extends Thread {

    private Server server;
    private ArrayList<UserListeningThread> userListeningThreadList;
    private UserStorage userStorage;
    private int idCounter;

    public NetworkListener(Server server) {
        this.userListeningThreadList = new ArrayList<>();
        this.server = server;
        this.userStorage = new UserStorage();
        idCounter = 0;
    }

    @Override
    public void run() {
        while (server.isRunning()) {
            try {
                User user = new User(server.getListener().accept(), idCounter);
                ++idCounter;
                //TODO: Thread pools (see executer)
                //Create new Thread for the MessageListener for each new client
                UserListeningThread u = new UserListeningThread(user, server);
                getUserListeningThreadList().add(u);
                u.start();
            } catch (IOException ex) {
                System.out.println("*** Connection Error! " + ex.toString() + ") ***");
                //.accept() will throw if server is closing
            }

        }
        shutdown();
    }

    //TODO: Proper Logout of Client
//    public void removeClient(){
//
//    }

    public ArrayList<UserListeningThread> getUserListeningThreadList() {
        return userListeningThreadList;
    }

    public void shutdown(){
        System.out.println("*** Shutting down network listener ***");
        System.out.println("- Closing sockets of Client in List");
        for(UserListeningThread u : userListeningThreadList){
            try {
                u.getUser().getIn().close();
                u.getUser().getOut().close();
                u.getUser().getSocket().close();
            } catch (IOException e) {
                //No need to handle since we close the server anyways
            }

        }
    }
}
