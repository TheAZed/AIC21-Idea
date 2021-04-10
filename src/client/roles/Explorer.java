package client.roles;

import client.World;
import client.model.Answer;

public class Explorer implements Role {
    private World currentWorld;
    @Override
    public Answer getAnswer(World newWorld) {
        return null;
    }
}
