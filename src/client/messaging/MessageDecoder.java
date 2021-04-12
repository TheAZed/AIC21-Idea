package client.messaging;

import client.messaging.messages.ExplorationUpdateMessage;
import client.messaging.messages.IDDisambiguationMessage;
import client.messaging.messages.QueenIdAssignmentMessage;

import java.util.ArrayList;
import java.util.List;

public final class MessageDecoder {
    private final CellEncoder cellEncoder;

    public MessageDecoder(CellEncoder cellEncoder) {
        this.cellEncoder = cellEncoder;
    }

    public List<Message> decodeChatText(String messageText) {
        StringBuilder binaryMessage = new StringBuilder();
        for (int i = 0; i < messageText.length(); i++) {
            binaryMessage.append(Integer.toBinaryString(messageText.charAt(i)));
        }
        return decodeBinaryMessages(binaryMessage);
    }

    private List<Message> decodeBinaryMessages(StringBuilder binaryMessage) {
        List<Message> messages = new ArrayList<>();
        while (true) {
            if (binaryMessage.toString().equals("0".repeat(binaryMessage.length()))) {
                return messages;
            }
            messages.add(extractBinaryMessage(binaryMessage));
        }
    }

    private Message extractBinaryMessage(StringBuilder binaryMessage) {
        int typeLength = MessageType.BIT_LENGTH;
        int typeValue = Integer.parseInt(binaryMessage.substring(0, typeLength), 2);
        System.out.println("message: " + binaryMessage.toString() + " len: " + typeLength + " val: " + typeValue);
        MessageType type = MessageType.getMessageType(typeValue);
        binaryMessage.delete(0, typeLength);
        switch (type) {
            case ID_DISAMBIGUATION:
                return IDDisambiguationMessage.extractFromData(binaryMessage);
            case QUEEN_ID_ASSIGNMENT:
                return QueenIdAssignmentMessage.extractFromData(binaryMessage);
            case EXPLORATION_UPDATE:
                return ExplorationUpdateMessage.extractFromData(binaryMessage, cellEncoder);
        }
        return null;
    }
}
