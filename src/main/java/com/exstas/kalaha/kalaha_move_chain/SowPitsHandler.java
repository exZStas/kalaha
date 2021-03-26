package com.exstas.kalaha.kalaha_move_chain;

import com.exstas.kalaha.PitService;
import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.exstas.kalaha.constants.PitConstants.*;

@Slf4j
@Component
public class SowPitsHandler extends AbstractKalahaMoveChain {

    @Autowired
    private PitService pitService;

    @Override
    public Kalaha processMove(Kalaha kalaha, int stonesToSow, int pitIdToSow) {
        int nextPitIdToSow = pitIdToSow + 1;
        //if exceed amount of pits -> set pitId = PIT_1_PLAYER_A
        if(nextPitIdToSow > AMOUNT_OF_PITS_AND_HOUSES) {
            nextPitIdToSow = PIT_1_PLAYER_A;
        }

        //check on house. If active player is A and they trying to sow to house of player B or vice versa
        //then skip this pit and move to the next one
        boolean isEligibleToSow = pitService.isEligibleToSowPit(kalaha.getActivePlayer(), pitIdToSow);
        if(!isEligibleToSow) {
            return this.processMove(kalaha, stonesToSow, nextPitIdToSow);
        }

        //if we have only 1 stone
        if(stonesToSow == 1) {
            return nextChain.processMove(kalaha, stonesToSow, pitIdToSow);
        }

        Pit pitToSow = pitService.getPitById(kalaha, pitIdToSow);
        pitToSow.sow();
        stonesToSow -= 1;

        return this.processMove(kalaha, stonesToSow, nextPitIdToSow);
    }
}
