package client.roles;

import client.World;
import client.model.Answer;

public interface Role {
    public Answer getAnswer(World currentWorld);
}
