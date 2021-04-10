package client;


import client.model.Answer;
import client.model.Ant;
import client.model.enums.Direction;
import client.roles.Explorer;
import client.roles.Role;

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
    static int turn = 0;
    static Role currentRole;

    public Answer turn(World world) {
        // Enter your AI code here
        if (turn <= 1) {
            currentRole = new Explorer(world);
        }
        Answer answer = currentRole.getAnswer(world);
        AI.turn++;
        System.out.println("turn passed!"); // this is a sample code you can easily delete this line
        // You can generate an answer using one of these two ways:
//        Answer answer_1 = new Answer(Direction.DOWN);
//        Answer answer_2 = new Answer(Direction.UP, "new message", 10);
        return answer;
    }
}