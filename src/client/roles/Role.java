package client.roles;

import client.World;
import client.model.Answer;

public interface Role {
    Answer getAnswer(World currentWorld);
}
