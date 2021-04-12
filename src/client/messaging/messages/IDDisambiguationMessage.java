package client.messaging.messages;

import client.messaging.EncodingUtils;
import client.messaging.Message;
import client.messaging.MessageType;
import client.model.enums.AntType;

public class IDDisambiguationMessage extends Message {
    public static final MessageType type = MessageType.ID_DISAMBIGUATION;
    private final AntType antType;
    private final int idAssignTurnDiff;
    private final String uuidString;

    public static IDDisambiguationMessage extractFromData(StringBuilder binaryData) {
        int antTypeInt = EncodingUtils.extractInteger(binaryData, 1);
        AntType antType = AntType.KARGAR;
        if (antTypeInt == 1) {
            antType = AntType.SARBAAZ;
        }
        int idAssignTurnDiff = EncodingUtils.extractInteger(binaryData, 8);

        String uuidString = EncodingUtils.decodeUuidString(binaryData.substring(0, 128));
        binaryData.delete(0, 128);
        return new IDDisambiguationMessage(antType, idAssignTurnDiff, uuidString);
    }

    public IDDisambiguationMessage(AntType antType, int idAssignTurnDiff, String uuidString) {
        this.antType = antType;
        this.uuidString = uuidString;
        this.idAssignTurnDiff = idAssignTurnDiff;
    }

    public AntType getAntType() {
        return antType;
    }

    public int getIdAssignTurnDiff() {
        return idAssignTurnDiff;
    }

    public String getUuidString() {
        return uuidString;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public String getEncodedString() {
        String type = getEncodedTypeStr();
        String turnDiffStr = EncodingUtils.encodeInteger(idAssignTurnDiff, 8);
        String uuidEncoded = EncodingUtils.encodeUuidString(uuidString);
        return type + turnDiffStr + uuidEncoded;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof IDDisambiguationMessage) {
            IDDisambiguationMessage other = (IDDisambiguationMessage) obj;
            return other.antType == this.antType &&
                    other.idAssignTurnDiff == this.idAssignTurnDiff &&
                    other.uuidString.equals(this.uuidString);
        }
        return false;
    }
}
