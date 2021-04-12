package client.messaging.messages;

import client.messaging.EncodingUtils;
import client.messaging.Message;
import client.messaging.MessageType;


public class QueenIdAssignmentMessage extends Message {
    public static final MessageType type = MessageType.QUEEN_ID_ASSIGNMENT;
    private final int idRangeStart;
    private final int idRangeEnd;

    public static QueenIdAssignmentMessage extractFromData(StringBuilder binaryData) {
        int rangeStart = EncodingUtils.extractInteger(binaryData, 8);
        int rangeEnd = EncodingUtils.extractInteger(binaryData, 8);
        return new QueenIdAssignmentMessage(rangeStart, rangeEnd);
    }

    public QueenIdAssignmentMessage(int idRangeStart, int idRangeEnd) {
        this.idRangeStart = idRangeStart;
        this.idRangeEnd = idRangeEnd;
    }

    public int getIdRangeStart() {
        return idRangeStart;
    }

    public int getIdRangeEnd() {
        return idRangeEnd;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public String getEncodedString() {
        String type = getEncodedTypeStr();
        String startStr = EncodingUtils.encodeInteger(idRangeStart, 8);
        String endStr = EncodingUtils.encodeInteger(idRangeEnd, 8);
        return type + startStr + endStr;
    }
}
