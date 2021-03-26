package com.exstas.kalaha.kalaha_move_chain;

import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

import static com.exstas.kalaha.constants.Player.PLAYER_A;
import static com.exstas.kalaha.constants.Player.PLAYER_B;

@Slf4j
@Component
public class TryToResolveWinnerHandler extends AbstractKalahaMoveChain {

    @Override
    public Kalaha processMove(Kalaha kalaha, int stonesToSow, int pitIdToSow) {
        Pit housePlayerA = kalaha.getPitHouseForPlayer(PLAYER_A);
        Pit housePlayerB = kalaha.getPitHouseForPlayer(PLAYER_B);

        Assert.notNull(housePlayerA, "Can't find pit house for player=" + PLAYER_A);
        Assert.notNull(housePlayerB, "Can't find pit house for player=" + PLAYER_B);

        int stonesInHouses = housePlayerA.getStones() + housePlayerB.getStones();
        if(stonesInHouses == kalaha.getAmountOfStonesInGame()) {
            if(housePlayerA.getStones() > housePlayerB.getStones()) {
                kalaha.setWinner(PLAYER_A);
            } else{
                kalaha.setWinner(PLAYER_B);
            }
        }

        List<Pit> pits = kalaha.getPits();
        int sumOfStonesInPitForPlayerA = pits.stream().filter(pit -> PLAYER_A.getPlayerPitIds().contains(pit.getId())).mapToInt(Pit::getStones).sum();
        int sumOfStonesInPitForPlayerB = pits.stream().filter(pit -> PLAYER_B.getPlayerPitIds().contains(pit.getId())).mapToInt(Pit::getStones).sum();

        if(sumOfStonesInPitForPlayerA == 0 || sumOfStonesInPitForPlayerB == 0) {
            kalaha.setWinner(sumOfStonesInPitForPlayerA == 0 ? PLAYER_A : PLAYER_B);
        }

        return kalaha;
    }
}
