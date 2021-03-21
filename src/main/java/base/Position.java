package base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    private int x;
    private int y;

    @JsonCreator
    public static Position createPosition(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        Position position = new Position();
        position.x = x - 1;
        position.y = y - 1;
        return position;
    }

}
