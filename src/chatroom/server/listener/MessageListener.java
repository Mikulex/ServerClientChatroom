package chatroom.server.listener;

import chatroom.model.Message;
import chatroom.model.TargetedTextMessage;
import chatroom.serializer.Serializer;
import chatroom.server.Server;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MessageListener extends Thread {
    private final ArrayBlockingQueue<Message> messageQueue;
    private Server server;
    private final Serializer serializer;

    public MessageListener(Server server) {
        messageQueue = new ArrayBlockingQueue<>(100);  //Synchronized queue containing messages from clients
        this.server = server;
        serializer = new Serializer();
    }
    
    @Override
    public void run(){
        while(server.isRunning()){
                try {
                    Message m = messageQueue.poll(1, TimeUnit.SECONDS);
                    if (m == null){
                        continue;
                    }

                    //Byte sent by client decides which type of message is sent
                    switch(server.getMessageTypeDictionary().getType(m.getType())){
                        case PUBLICSERVERMSG: case PUBLICTEXTMSG:
                            sendToAll(m);
                            break;
                        case TARGETTEXTMSG:
                            sendToTarget(m, ((TargetedTextMessage)m).getReceiver());
                            break;
                    }
                } catch (InterruptedException e) {
                    System.err.println("*** MessageListener interrupted by server! ***");
                }
        }
    }

    public void sendToTarget(Message m, int id){
        //TODO: optimization
        for(UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()){
            if(u.getUserConnectionInfo().getId() == id){
                serializer.serialize(u.getUserConnectionInfo().getOut(), m.getType(), m);
                break;
            }
        }
    }
    public void sendToTarget(Message m, String loginName){
        for(UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()){
            if(u.getUserConnectionInfo().getLoginName().equals(loginName)){
                serializer.serialize(u.getUserConnectionInfo().getOut(), m.getType(), m);
                break;
            }
        }
    }
    public void sendToAll(Message m){
        for(UserListeningThread u : server.getNetworkListener().getUserListeningThreadList()){
            if(u.getUserConnectionInfo().isLoggedIn()){
                serializer.serialize(u.getUserConnectionInfo().getOut(), m.getType(), m);
            }
        }
    }


    public ArrayBlockingQueue<Message> getMessageQueue() {
        return messageQueue;
    }
}
