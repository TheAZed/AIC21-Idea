package client.messaging;

public abstract class Message {

    public abstract MessageType getType();

    public abstract String getEncodedString();

    protected String getEncodedTypeStr() {
        return EncodingUtils.encodeInteger(getType().getValue(), MessageType.BIT_LENGTH);
    }
}
