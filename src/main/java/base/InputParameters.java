package base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InputParameters {
    private Position[] usersPosition;           // Index: 0:k-1
    private Position[] driversPosition;         // Index: 0:j-1
    private Position[] restaurantsPosition;     // Index: 0:i-1
    private int[] userExpectations;         // Index: 0:k-1
    private int[] userFoodPreparation;      // Index: 0:k-1
    private int[] userRestaurantIndex;      // Index: 0:k-1, values: 0:i-1
}
