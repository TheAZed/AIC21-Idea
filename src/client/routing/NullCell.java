package client.routing;

import client.model.Cell;
import client.model.Resource;
import client.model.enums.CellType;
import client.model.enums.ResourceType;

public class NullCell extends Cell {
    public NullCell(int x, int y) {
        super(null, x, y, new Resource(ResourceType.NONE, 0));
    }
}
