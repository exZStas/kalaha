package com.exstas.kalaha;

import com.exstas.kalaha.constants.Player;
import com.exstas.kalaha.kalaha_move_chain.ProcessOneStoneHandler;
import com.exstas.kalaha.kalaha_move_chain.SowPitsHandler;
import com.exstas.kalaha.kalaha_move_chain.TryToResolveWinnerHandler;
import com.exstas.kalaha.kalaha_move_chain.ValidateKalahaMoveHandler;
import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.exstas.kalaha.constants.PitConstants.*;
import static com.exstas.kalaha.constants.Player.*;

@Service
@Slf4j
public class KalahaService {

    //ideally should be replaced with redis/database
    private static final ConcurrentHashMap<String, Kalaha> kalahaGamesCache = new ConcurrentHashMap<>();

    @Autowired
    private KalahaBuilder kalahaBuilder;

    @Autowired
    private PitService pitService;

    @Autowired
    private ValidateKalahaMoveHandler validateKalahaMoveHandler;

    @Autowired
    private SowPitsHandler sowPitsHandler;

    @Autowired
    private ProcessOneStoneHandler processOneStoneHandler;

    @Autowired
    private TryToResolveWinnerHandler tryToResolveWinnerHandler;

    public Kalaha createKalaha(Player startingPlayer, int stones) {
        Assert.isTrue(stones > 0, "Can't create kalaha with zero or less stones");

        Kalaha kalaha = kalahaBuilder.
                withId(UUID.randomUUID().toString()).
                withStones(stones).
                withStartingPlayer(startingPlayer).
                withDefaultPits().
                build();
        kalahaGamesCache.put(kalaha.getId(), kalaha);

        return kalaha;
    }

    public Kalaha getKalahaById(String kalahaId) {
        Assert.hasLength(kalahaId, "Kalaha id can't be null or trim empty");

        Kalaha kalaha = kalahaGamesCache.get(kalahaId);
        Assert.notNull(kalaha, String.format("Kalaha with id=%s can't be found", kalahaId));

        return kalaha;
    }

    public Kalaha makeMove(String kalahaId, int requestedPitId) {
        Assert.notNull(kalahaId, "Kalaha id can't be null");
        Assert.isTrue(requestedPitId > 0 && requestedPitId < 14, "Can't sow because requested pit doesn't exist");

        Kalaha kalaha = getKalahaById(kalahaId);

        validateKalahaMoveHandler.setNextChain(sowPitsHandler);
        sowPitsHandler.setNextChain(processOneStoneHandler);
        processOneStoneHandler.setNextChain(tryToResolveWinnerHandler);

        Pit requestedPit = pitService.getPitById(kalaha, requestedPitId);
        int stonesToSow = requestedPit.seizeStones();
        Kalaha kalahaAfterMove = validateKalahaMoveHandler.processMove(kalaha, stonesToSow, requestedPitId);

/*        Player activePlayer = kalaha.getActivePlayer();

        //check player can't request house pit
        if(requestedPitId == HOUSE_PLAYER_A || requestedPitId == HOUSE_PLAYER_B) {
            return kalaha;
        }

        //check player can request only pits which belongs to they
        boolean isPitBelongsToPlayer = isPitBelongsToPlayer(activePlayer, requestedPitId);
        if(!isPitBelongsToPlayer) {
            return kalaha;
        }

        Pit requestedPit = getPitById(kalaha, requestedPitId);

        if(Objects.isNull(requestedPit)) {
            //return special object with error code and status?
            log.warn("Couldn't find requested pit. Pit id={}, kalaha id={}", requestedPitId, kalahaId);
            return kalaha;
        }

        if(requestedPit.getStones() == 0) {
            log.info("Requested pit is empty. Nothing to do. Pit id={}, kalaha id={}", requestedPitId, kalahaId);
            return kalaha;
        }

        int stonesToSow = requestedPit.seizeStones();
        int pitIdToSow = requestedPitId + 1;
        Kalaha kalahaAfterMove = sowPits(kalaha, stonesToSow, pitIdToSow);
        Player winner = tryToResolveWinner(kalahaAfterMove);
        kalahaAfterMove.setWinner(winner);*/

        return kalahaAfterMove;
    }

    public Kalaha sowPits(Kalaha kalaha, int stones, int pitIdToSow) {
        Player activePlayer = kalaha.getActivePlayer();
        Player nextActivePlayer = activePlayer == PLAYER_A ? PLAYER_B : PLAYER_A;
        //no stones -> next player should make a move
        if(stones == 0) {
            kalaha.setActivePlayer(nextActivePlayer);
            return kalaha;
        }

        int nextPitIdToSow = pitIdToSow + 1;
        //if exceed amount of pits -> set pitId = PIT_1_PLAYER_A
        if(nextPitIdToSow > AMOUNT_OF_PITS_AND_HOUSES) {
            nextPitIdToSow = PIT_1_PLAYER_A;
        }
        //check on house. If active player is A and they trying to sow to house of player B or vice versa
        //then skip this pit and move to the next one
        boolean isEligibleToSow = isEligibleToSowPit(activePlayer, pitIdToSow);
        if(!isEligibleToSow) {
            return sowPits(kalaha, stones, nextPitIdToSow);
        }

        boolean isPitBelongsToActivePlayer = isPitBelongsToPlayer(activePlayer, pitIdToSow);
        Pit pitToSow = getPitById(kalaha, pitIdToSow);
        //if we have only 1 stone
        if(stones == 1) {
            if(pitIdToSow == HOUSE_PLAYER_A || pitIdToSow == HOUSE_PLAYER_B) {
                pitToSow.sow();
                return kalaha;
            } else if(isPitBelongsToActivePlayer && pitToSow.isEmpty()) {
                //get stones from opposite pit along with stone from current pit and put it to active player house
                Pit oppositePit = getOppositePitForPit(kalaha, pitToSow);
                int seizedStones = oppositePit.seizeStones() + stones;

                Pit pitHouse = kalaha.getPitHouseForPlayer(activePlayer);
                int stonesOfPitHouse = pitHouse.seizeStones() + seizedStones;

                pitHouse.setStones(stonesOfPitHouse);
                stones -= 1;
                return sowPits(kalaha, stones, nextPitIdToSow);
            }

        }

        pitToSow.sow();
        stones -= 1;
        return sowPits(kalaha, stones, nextPitIdToSow);
    }

    /**
     * Return PLAYER_A or PLAYER_B if winner can be decided. If no winner method return null
     * @param kalaha
     * @return @Nullable Player
     */
    public Player tryToResolveWinner(Kalaha kalaha) {
        Pit housePlayerA = kalaha.getPitHouseForPlayer(PLAYER_A);
        Pit housePlayerB = kalaha.getPitHouseForPlayer(PLAYER_B);

        Assert.notNull(housePlayerA, "Can't find pit house for player=" + PLAYER_A);
        Assert.notNull(housePlayerB, "Can't find pit house for player=" + PLAYER_B);

        int stonesInHouses = housePlayerA.getStones() + housePlayerB.getStones();
        if(stonesInHouses == kalaha.getAmountOfStonesInGame()) {
            if(housePlayerA.getStones() > housePlayerB.getStones()) {
                return PLAYER_A;
            } else{
                return PLAYER_B;
            }
        }

        List<Pit> pits = kalaha.getPits();
        int sumOfStonesInPitForPlayerA = pits.stream().filter(pit -> PLAYER_A.getPlayerPitIds().contains(pit.getId())).mapToInt(Pit::getStones).sum();
        int sumOfStonesInPitForPlayerB = pits.stream().filter(pit -> PLAYER_B.getPlayerPitIds().contains(pit.getId())).mapToInt(Pit::getStones).sum();

        if(sumOfStonesInPitForPlayerA == 0 || sumOfStonesInPitForPlayerB == 0) {
            return sumOfStonesInPitForPlayerA == 0 ? PLAYER_A : PLAYER_B;
        }

        return null;
    }

    public boolean isEligibleToSowPit(Player activePlayer, int pitIdToSow) {
        //edge cases
        if(pitIdToSow == HOUSE_PLAYER_A && activePlayer == PLAYER_B ||
                pitIdToSow == HOUSE_PLAYER_B && activePlayer == PLAYER_A) {
            return false;
        }

        return true;
    }


    public boolean isPitBelongsToPlayer(Player player, int pitId) {
        if(player == PLAYER_A && (pitId >= PIT_1_PLAYER_A && pitId <= HOUSE_PLAYER_A)) {
            return true;
        } else {
            return player == PLAYER_B && (pitId >= PIT_1_PLAYER_B && pitId <= HOUSE_PLAYER_B);
        }
    }

    public Pit getPitById(Kalaha kalaha, int id) {
        return kalaha.getPits().stream().
                filter(pit -> pit.getId() == id).
                findFirst().
                orElse(null);
    }

    public Pit getOppositePitForPit(Kalaha kalaha, Pit pit) {
        int oppositePitId = AMOUNT_OF_PITS_AND_HOUSES - pit.getId();
        return getPitById(kalaha, oppositePitId);
    }




//idea: pit iterator
//when pits of some player are empty decide winner

}
