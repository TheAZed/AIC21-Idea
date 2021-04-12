package client;


import client.messaging.CellEncoder;
import client.messaging.EncodingUtils;
import client.messaging.Message;
import client.messaging.MessageDecoder;
import client.messaging.messages.ExplorationUpdateMessage;
import client.messaging.messages.IDDisambiguationMessage;
import client.model.Answer;
import client.model.Cell;
import client.model.enums.AntType;
import client.model.enums.Direction;
import client.roles.Explorer;
import client.roles.Roles;
import client.routing.RoutingEngine;
import client.routing.RoutingState;
import client.utils.MapMemory;
import client.utils.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * You must put your code in this class {@link AI}.
 * This class has {@link #turn}, to do orders while game is running;
 */

public class AI {
    /**
     * this method is for participants' code
     *
     * @param world is your data for the game (read the documentation on {@link client.World})
     * the return value is a {@link client.model.Answer} which consists of Direction for your
     * next destination in map (the necessary parameter), the Message (not necessary) for your
     * chat message and the value (if there is any message) for your message value.
     */
    private Roles role;
    static int turn = 0;
    private int id = -1;
    private Role currentRole;
    private boolean isElder = false;
    private AntType selfType;
    private MapMemory mapMemory;
    private RoutingEngine routingEngine;
    private CellEncoder cellEncoder;
    private MessageDecoder messageDecoder;
    private Message lastSentMessage;
    private int viewRange;

    // Unknown vars
    private int minId = 0;
    private int queenId = -1;
    private int explorerId = -1;
    private int turnsWaiting = 0;

    // Queen vars

    // Explorer vars
    private Direction lastDirection = Direction.RIGHT;

    public Answer turn(World world) {
        // Enter your AI code here
        if (turn < 1) {
            // Check if elder
            if (world.getChatBox().getAllChats().size() <= 0) {
                isElder = true;
            }
            this.selfType = world.getAntType();
            this.mapMemory = new MapMemory(world.getMapWidth(), world.getMapHeight());
            this.routingEngine = new RoutingEngine(mapMemory);
            this.cellEncoder = new CellEncoder(mapMemory, world.getAnt().getViewDistance());
            this.messageDecoder = new MessageDecoder(cellEncoder);
            this.viewRange = world.getAnt().getViewDistance();
            this.currentRole = Role.UNKNOWN;
//            this.role = new Explorer(world);
        }
        Answer answer = new Answer(Direction.CENTER);
        switch (currentRole) {
            case UNKNOWN:
                answer = unknown(world);
                break;
            case QUEEN:
                answer = queen(world);
                break;
            case EXPLORER:
                answer = explorer(world);
                break;
        }
//        Answer answer = role.getAnswer(world);
        AI.turn++;
        System.out.println("turn passed!");
        return answer;
    }

    private void updateMapWithExplorationMessage(ExplorationUpdateMessage message) {
        List<Point> points = mapMemory.getBorderPointsForDirection(message.getCenter(), message.getDirection(), viewRange);
        List<Cell> newCells = message.getCellList();
        for (int i = 0; i < points.size(); i++) {
            mapMemory.updateCell(points.get(i), newCells.get(i));
        }
    }

    private Answer unknown(World world) {
        if (isElder) {
            List<Message> messages = new ArrayList<>();
            world.getChatBox().getAllChatsOfTurn(turn).forEach(chat -> {
                messages.addAll(messageDecoder.decodeChatText(chat.getText()));
            });
            for (Message m : messages) {
                if (m instanceof ExplorationUpdateMessage) {
                    this.updateMapWithExplorationMessage((ExplorationUpdateMessage) m);
                }
                if (m instanceof IDDisambiguationMessage) {
                    IDDisambiguationMessage message = (IDDisambiguationMessage) m;
                    if (message.equals(lastSentMessage)) {
                        this.id = this.minId;
                        break;
                    }
                    if (message.getIdAssignTurnDiff() == turnsWaiting) {
                        if (this.queenId < 0 && message.getAntType() == AntType.KARGAR) {
                            this.queenId = minId;
                        } else if (this.explorerId < 0 && message.getAntType() == AntType.KARGAR) {
                            this.explorerId = minId;
                        }
                        this.minId++;
                    }
                }
            }
            if (this.id < 0) {
                this.turnsWaiting++;
                String uuidString = UUID.randomUUID().toString().replace("-", "");
                IDDisambiguationMessage message = new IDDisambiguationMessage(selfType, turnsWaiting, uuidString);
                String binaryData = message.getEncodedString();
                System.out.println("binary Str: " + binaryData);
                String messageText = EncodingUtils.encodeBinaryToText(binaryData, 32);
                return new Answer(Direction.CENTER, messageText, 50);
            } else {
                if (this.queenId < 0) {
                    this.queenId = id;
                    this.currentRole = Role.QUEEN;
                } else if (this.explorerId < 0) {
                    this.explorerId = id;
                    this.currentRole = Role.EXPLORER;
                } else {
                    this.currentRole = Role.WORKER;
                }
            }
        } else {
            // TODO: 12.04.21 Fill later
        }
        return new Answer(Direction.CENTER);
    }

    private Answer queen(World world) {
        return new Answer(Direction.CENTER);
    }

    private Answer explorer(World world) {
        routingEngine.setState(RoutingState.EXPLORING);
        mapMemory.updateData(world);
        if (mapMemory.isLastTimeExplored()) {
            routingEngine.dropCurrentRoute();
        }
        Direction nextDirection = routingEngine.getNextMove(world);
        if (lastDirection != Direction.CENTER) {
            Point position = new Point(world.getAnt().getCurrentX(), world.getAnt().getCurrentY());
            // TODO: 12.04.21 Should add checks if we actually moved
            List<Point> borderPoints = mapMemory.getBorderPointsForDirection(position, lastDirection, viewRange);
            List<Cell> cells = new ArrayList<>();
            for (Point point : borderPoints) {
                cells.add(mapMemory.getCell(point));
            }
            ExplorationUpdateMessage message = new ExplorationUpdateMessage(position, lastDirection, cells);
            String binaryData = message.getEncodedString(cellEncoder);
            String messageText = EncodingUtils.encodeBinaryToText(binaryData, 32);
            Answer finalAnswer = new Answer(nextDirection, messageText, 100);
            lastDirection = nextDirection;
            return finalAnswer;
        }
        Answer finalAnswer = new Answer(nextDirection);
        lastDirection = nextDirection;
        return finalAnswer;
    }
}