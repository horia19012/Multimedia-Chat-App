public class Message {
    private String senderUsername;
    private String receiverUsername;
    private String messageText;
    private String timestamp; // String representation of date and time

    private int senderIndex;

    public int getSenderIndex() {
        return senderIndex;
    }

    public void setSenderIndex(int senderIndex) {
        this.senderIndex = senderIndex;
    }

    public Message(String senderUsername, String receiverUsername, String messageText, String dateTime,int senderIndex) {
        this.senderUsername = senderUsername;
        this.receiverUsername = receiverUsername;
        this.messageText = messageText;
        this.timestamp = dateTime;
        this.senderIndex=senderIndex;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getDateTime() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderUsername='" + senderUsername + '\'' +
                ", receiverUsername='" + receiverUsername + '\'' +
                ", messageText='" + messageText + '\'' +
                ", dateTime='" + timestamp + '\'' +
                '}';
    }
}
