package client.messaging;

public enum MessageType {
    ENEMY_SPOTTED,
    ID_DISAMBIGUATION,
    QUEEN_ROLE_ASSIGNMENT,
    QUEEN_ID_ASSIGNMENT,
    EXPLORATION_UPDATE,
    RESOURCE_EMPTIED,
    ENEMY_BASE_SPOTTED,
    NAVIGATOR_INSTRUCTION,
    LONG_MESSAGE_START,
    LONG_MESSAGE_CONTINUE,
    LONG_MESSAGE_END,
    ;

    public static final int BIT_LENGTH;
    static {
        BIT_LENGTH = Integer.toBinaryString(values().length).length();
        int val = 0;
        for (MessageType type : values()) {
            type.value = val;
            val++;
        }
    }
    public static MessageType getMessageType(int value) {
        return values()[value];
    }

    private int value;

    public int getValue() {
        return value;
    }
}
