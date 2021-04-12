package client.roles;

import client.World;
import client.model.Answer;
import client.model.enums.Direction;
import client.routing.RoutingEngine;
import client.routing.RoutingState;
import client.utils.MapMemory;

public class Explorer implements Roles {
    private World currentWorld;
    private final MapMemory mapMemory;
    private final RoutingEngine routingEngine;

    public Explorer(World initWorld) {
        this.currentWorld = initWorld;
        this.mapMemory = new MapMemory(initWorld.getMapWidth(), initWorld.getMapHeight());
        this.routingEngine = new RoutingEngine(mapMemory);
        routingEngine.setState(RoutingState.EXPLORING);
    }

    @Override
    public Answer getAnswer(World newWorld) {
        currentWorld = newWorld;
        mapMemory.updateData(newWorld);
        if (mapMemory.isLastTimeExplored()) {
            routingEngine.dropCurrentRoute();
        }
        Direction nextDirection = routingEngine.getNextMove(newWorld);
        return new Answer(nextDirection);
    }
}
