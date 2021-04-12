package client.messaging.messages;

import client.messaging.CellEncoder;
import client.messaging.EncodingUtils;
import client.messaging.Message;
import client.messaging.MessageType;
import client.model.Cell;
import client.model.enums.Direction;
import client.utils.Point;

import java.util.List;

public class ExplorationUpdateMessage extends Message {
    public static final MessageType type = MessageType.EXPLORATION_UPDATE;
    private final Point center;
    private final Direction direction;
    private final List<Cell> cellList;

    public static ExplorationUpdateMessage extractFromData(StringBuilder data, CellEncoder cellEncoder) {

        int centerX = EncodingUtils.extractInteger(data, 6);
        int centerY = EncodingUtils.extractInteger(data, 6);
        Point center = new Point(centerX, centerY);
        int dirNum = EncodingUtils.extractInteger(data, 2);
        Direction direction = Direction.CENTER;
        switch (dirNum) {
            case 0:
                direction = Direction.RIGHT;
                break;
            case 1:
                direction = Direction.UP;
                break;
            case 2:
                direction = Direction.LEFT;
                break;
            case 3:
                direction = Direction.DOWN;
        }
        List<Cell> cellList = cellEncoder.extractCellList(data, center, direction);
        return new ExplorationUpdateMessage(center, direction, cellList);
    }

    public ExplorationUpdateMessage(Point center, Direction direction, List<Cell> cellList) {
        this.center = center;
        this.direction = direction;
        this.cellList = cellList;
    }

    public Point getCenter() {
        return center;
    }

    public Direction getDirection() {
        return direction;
    }

    public List<Cell> getCellList() {
        return cellList;
    }

    @Override
    public MessageType getType() {
        return type;
    }

    @Override
    public String getEncodedString() {
        return null;
    }

    public String getEncodedString(CellEncoder cellEncoder) {
        String type = getEncodedTypeStr();
        String centerX = EncodingUtils.encodeInteger(center.x, 6);
        String centerY = EncodingUtils.encodeInteger(center.y, 6);
        String directionStr = "";
        switch (direction) {
            case RIGHT:
                directionStr = "00";
                break;
            case UP:
                directionStr = "01";
                break;
            case LEFT:
                directionStr = "10";
                break;
            case DOWN:
                directionStr = "11";
                break;
            default:
                //  Should not happen
                return "";
        }
        String cellsEncoded = cellEncoder.encodeCellList(center, direction, cellList);
        return type + centerX + centerY + directionStr + cellsEncoded;
    }
}
