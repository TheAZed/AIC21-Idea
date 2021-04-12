package client.messaging;

import client.model.Cell;
import client.model.Resource;
import client.model.enums.CellType;
import client.model.enums.Direction;
import client.model.enums.ResourceType;
import client.routing.NullCell;
import client.utils.MapMemory;
import client.utils.Point;

import java.util.*;
import java.util.stream.Collectors;

public class CellEncoder {
    private final MapMemory mapMemory;
    private final int viewRange;
    private final Map<Integer, Integer> resourceValueMap = new HashMap<>() {{
        put(0, 10);
        put(1, 20);
        put(2, 50);
        put(3, 100);
    }};
    private final List<Integer> resourceValuesList;
    private final int resourceValueEncodedLen;

    public CellEncoder(MapMemory mapMemory, int viewRange) {
        this.mapMemory = mapMemory;
        this.viewRange = viewRange;
        this.resourceValuesList = resourceValueMap.values().stream().sorted().collect(Collectors.toList());
        this.resourceValueEncodedLen = EncodingUtils.getIntegerBitCount(resourceValuesList.size());
    }

    public List<Cell> extractCellList(StringBuilder data, Point center, Direction direction) {
        int centerX = center.x, centerY = center.y;
        List<Cell> cells = new ArrayList<>();
        List<Point> points = mapMemory.getBorderPointsForDirection(center, direction, viewRange);
        for (Point point :
                points) {
            cells.add(extractCell(data, point.x, point.y));
        }
        return cells;
    }

    private Cell extractCell(StringBuilder data, int x, int y) {
        String cellType = data.substring(0, 2);
        data.delete(0, 2);
        switch (cellType) {
            case "00":  // Empty
                return new Cell(CellType.EMPTY, x, y, new Resource(ResourceType.NONE, 0));
            case "01":  // Resource
                Resource resource = extractResource(data);
                return new Cell(CellType.EMPTY, x, y, resource);
            case "10":  // Wall
                return new Cell(CellType.WALL, x, y,  new Resource(ResourceType.NONE, 0));
            case "11":  // Base
                return new Cell(CellType.BASE, x, y, new Resource(ResourceType.NONE, 0));
            default:
                return new NullCell(x, y);
        }
    }

    private Resource extractResource(StringBuilder data) {
        String resourceType = data.substring(0, 1);
        data.delete(0, 1);
        int resourceValueCode = EncodingUtils.extractInteger(data, 2);
        int resourceValue = resourceValueMap.get(resourceValueCode);
        if (resourceType.equals("0")) {  // Grass
            return new Resource(ResourceType.GRASS, resourceValue);
        } else {  // Bread
            return new Resource(ResourceType.BREAD, resourceValue);
        }
    }

    public String encodeCellList(Point center, Direction direction, List<Cell> cells) {
        String centerX = EncodingUtils.encodeInteger(center.x, 6);
        String centerY = EncodingUtils.encodeInteger(center.y, 6);
        StringBuilder data = new StringBuilder(centerX + centerY);
        for (Cell cell :
                cells) {
            data.append(encodeCell(cell));
        }
        return data.toString();
    }

    private String encodeCell(Cell cell) {
        switch (cell.getType()) {
            case EMPTY:
                if (cell.getResource().getType() == ResourceType.NONE) {
                    return "00";
                } else {
                    String encodedResource = encodeResource(cell.getResource());
                    return "01" + encodedResource;
                }
            case WALL:
                return "10";
            case BASE:
                return "11";
            default:
                return "00";
        }
    }

    private String encodeResource(Resource resource) {
        String resourceStr = "";
        if (resource.getType() == ResourceType.GRASS) {
            resourceStr += "0";
        } else {
            resourceStr += "1";
        }
        for (int i = 0; i < resourceValuesList.size() - 1; i++) {
            if (resource.getValue() <= resourceValuesList.get(i)) {
                resourceStr += EncodingUtils.encodeInteger(i, resourceValueEncodedLen);
                return resourceStr;
            }
        }
        resourceStr += EncodingUtils.encodeInteger(resourceValuesList.size(), resourceValueEncodedLen);
        return resourceStr;
    }

}
