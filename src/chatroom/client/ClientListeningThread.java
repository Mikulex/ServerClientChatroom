package chatroom.client;

import chatroom.model.message.*;
import chatroom.serializer.Serializer;

import java.io.IOException;
import java.io.InputStream;

/**
 * Deserialize data from Stream and puts it in a Message Object,
 * displaying them afterwards
 */
public class ClientListeningThread extends Thread {
    private Client client;
    private InputStream in;
    private Serializer serializer;

    public ClientListeningThread(InputStream in, Client client) throws IOException {
        this.in = in;
        serializer = new Serializer();
        this.client = client;
    }


    @Override
    public void run() {
        while (client.isRunning()) {
            try {
                //Read byte from stream to decide on which type of message is incoming
                byte type = (byte)in.read();

                //Socket is closed if the Stream returns -1
                if(type == (byte)-1){
                    throw new IOException("Cannot reach server");
                }
                //deserialize message from stream and put it into an message
                Message m = serializer.deserialize(in, type);

                handleMessage(m);
            } catch (IOException ex) {
                System.err.println("Connection Lost: " + ex.toString() + "\nShutting down client!");
                client.stop();
            }
        }
        System.err.println("Shutting down receiving handler!");
    }

    /**
     * Prints the message incoming from the server and does further actions if needed
     * @param message The message which should be handled
     */
    private void handleMessage(Message message) {
        switch (client.getMessageTypeDictionary().getType(message.getType())) {
            case PUBLICTEXTMSG:
                PublicTextMessage publicTextMessage = ((PublicTextMessage) message);
                String publicString = publicTextMessage.getSender() + ": " + publicTextMessage.getMessage();
                System.out.println(publicString);
                break;
            case PUBLICSERVERMSG:
                PublicServerMessage publicServerMessage = ((PublicServerMessage) message);
                System.out.println("*** " + publicServerMessage.getMessage() + " ***");
                break;
            case TARGETSERVERMSG:
                TargetedServerMessage targetedServerMessage = ((TargetedServerMessage) message);
                System.out.println("Server: " + targetedServerMessage.getMessage());
                break;
            case TARGETTEXTMSG:
                TargetedTextMessage targetedTextMessage = ((TargetedTextMessage) message);
                String targetedString = targetedTextMessage.getSender() + " (whispered): " + targetedTextMessage.getMessage();
                System.out.println(targetedString);
            case LOGINRESPONSEMSG:
                switch (((LoginResponseMessage)message).getResponse()){
                    case SUCCESS:
                        client.setLoggedIn(true);
                        System.out.println("*** You are logged in! ***");
                        break;
                    case CREATED_ACCOUNT:
                        client.setLoggedIn(true);
                        System.out.println("*** This name was not given. Created a new Account! ***");
                        break;
                    case ALREADY_LOGGED_IN:
                        System.out.println("*** Someone is already using your Account!!!! ***");
                        System.out.println("*** Your Account might be in danger. Contact an admin! ***");
                        client.stop();
                        break;
                    case WRONG_PASSWORD:
                        System.out.println("*** Wrong password! Please try again! ***");
                        client.getClientSender().authenticate();
                        break;
                } break;
        }

    }

}
