package chatroom.model;

import java.util.HashMap;

public class MessageTypeDictionary {

    private HashMap<Byte, MessageType> messageTypeHashMap;
    private HashMap<MessageType,Byte> reverseHashMap;

    public enum MessageType {
        SERVERMSG, PUBLICTEXTMSG, TARGETTEXTMSG, LOGINMSG, LOGOUTMSG
    }

    public MessageTypeDictionary() {
        messageTypeHashMap = new HashMap<>();
        messageTypeHashMap.put((byte) 0, MessageType.SERVERMSG);
        messageTypeHashMap.put((byte) 1, MessageType.PUBLICTEXTMSG);
        messageTypeHashMap.put((byte) 2, MessageType.TARGETTEXTMSG);
        messageTypeHashMap.put((byte) 3, MessageType.LOGINMSG);
        messageTypeHashMap.put((byte) 4, MessageType.LOGOUTMSG);

        for(byte key : messageTypeHashMap.keySet()){
            reverseHashMap.put(messageTypeHashMap.get(key), key);
        }
    }

    public MessageType getType(byte type) {
        return messageTypeHashMap.get(type);
    }

    public byte getByte(MessageType messageType){
        return reverseHashMap.get(messageType);
    }



}
