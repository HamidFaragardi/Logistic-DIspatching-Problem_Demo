package base;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonCreator
    public static InputParameters createInputParameters(
            @JsonProperty("usersPosition") Position[] usersPosition,
            @JsonProperty("driversPosition") Position[] driversPosition,
            @JsonProperty("restaurantsPosition") Position[] restaurantsPosition,
            @JsonProperty("userExpectations") int[] userExpectations,
            @JsonProperty("userFoodPreparation") int[] userFoodPreparation,
            @JsonProperty("userRestaurantIndex") int[] userRestaurantIndex

    ) {
        InputParameters inputParameters = new InputParameters();
        inputParameters.usersPosition = usersPosition;
        inputParameters.driversPosition = driversPosition;
        inputParameters.restaurantsPosition = restaurantsPosition;
        inputParameters.userExpectations = new int[userExpectations.length];
        inputParameters.userFoodPreparation = new int[userFoodPreparation.length];
        inputParameters.userRestaurantIndex = new int[userRestaurantIndex.length];

        for (int i = 0; i < userExpectations.length; i++) {
            inputParameters.userExpectations[i] = userExpectations[i] - 1;
        }
        for (int i = 0; i < userFoodPreparation.length; i++) {
            inputParameters.userFoodPreparation[i] = userFoodPreparation[i] - 1;
        }
        for (int i = 0; i < userRestaurantIndex.length; i++) {
            inputParameters.userRestaurantIndex[i] = userRestaurantIndex[i] - 1;
        }
        return inputParameters;
    }
}
