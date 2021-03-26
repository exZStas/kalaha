package com.exstas.kalaha;

import com.exstas.kalaha.constants.Player;
import com.exstas.kalaha.factory.KalahaFactory;
import com.exstas.kalaha.kalaha_move_chain.ProcessOneStoneHandler;
import com.exstas.kalaha.kalaha_move_chain.SowPitsHandler;
import com.exstas.kalaha.kalaha_move_chain.TryToResolveWinnerHandler;
import com.exstas.kalaha.kalaha_move_chain.ValidateKalahaMoveHandler;
import com.exstas.kalaha.model.Kalaha;
import com.exstas.kalaha.model.Pit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static com.exstas.kalaha.constants.PitConstants.*;
import static com.exstas.kalaha.constants.PitConstants.PIT_6_PLAYER_B;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class TestKalahaService {

    @InjectMocks
    @Autowired
    private KalahaService kalahaService;

    @Autowired
    private KalahaBuilder kalahaBuilder;
    @Autowired
    private PitService pitService;

    @Autowired
    private KalahaFactory kalahaFactory;

    @Autowired
    private ValidateKalahaMoveHandler validateKalahaMoveHandler;
    @Autowired
    private SowPitsHandler sowPitsHandler;
    @Autowired
    private ProcessOneStoneHandler processOneStoneHandler;
    @Autowired
    private TryToResolveWinnerHandler tryToResolveWinnerHandler;

    private Kalaha kalaha_1;
    private String kalahaId_1 = "kalaha_1";

    @BeforeEach
    public void setUp() {
        kalaha_1 = kalahaService.createKalaha(Player.PLAYER_A, 4);
    }

    @Test
    //Player A makes move and stones are distributed correctly, next move belongs to player B
    public void testMakeMove_Player_A() {
        //given
        int stones = 4;
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, stones));
        pits.add(new Pit(PIT_2_PLAYER_A, stones));
        pits.add(new Pit(PIT_3_PLAYER_A, stones));
        pits.add(new Pit(PIT_4_PLAYER_A, stones));
        pits.add(new Pit(PIT_5_PLAYER_A, stones));
        pits.add(new Pit(PIT_6_PLAYER_A, stones));
        pits.add(new Pit(HOUSE_PLAYER_A, 0));

        pits.add(new Pit(PIT_1_PLAYER_B, stones));
        pits.add(new Pit(PIT_2_PLAYER_B, stones));
        pits.add(new Pit(PIT_3_PLAYER_B, stones));
        pits.add(new Pit(PIT_4_PLAYER_B, stones));
        pits.add(new Pit(PIT_5_PLAYER_B, stones));
        pits.add(new Pit(PIT_6_PLAYER_B, stones));
        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        kalaha_1.setPits(pits);

        String expectedPits = "[1:0, 2:5, 3:5, 4:5, 5:5, 6:4, 7:0, 8:4, 9:4, 10:4, 11:4, 12:4, 13:4, 14:0]";
        Player expectedPlayer = Player.PLAYER_B;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_1_PLAYER_A);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
    }

    @Test
    //Player B makes move and stones are distributed correctly, next move belongs to player A
    public void testMakeMove_Player_B() {
        //given
        int stones = 4;
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, stones));
        pits.add(new Pit(PIT_2_PLAYER_A, stones));
        pits.add(new Pit(PIT_3_PLAYER_A, stones));
        pits.add(new Pit(PIT_4_PLAYER_A, stones));
        pits.add(new Pit(PIT_5_PLAYER_A, stones));
        pits.add(new Pit(PIT_6_PLAYER_A, stones));
        pits.add(new Pit(HOUSE_PLAYER_A, 0));

        pits.add(new Pit(PIT_1_PLAYER_B, stones));
        pits.add(new Pit(PIT_2_PLAYER_B, stones));
        pits.add(new Pit(PIT_3_PLAYER_B, stones));
        pits.add(new Pit(PIT_4_PLAYER_B, stones));
        pits.add(new Pit(PIT_5_PLAYER_B, stones));
        pits.add(new Pit(PIT_6_PLAYER_B, stones));
        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_B);

        String expectedPits = "[1:4, 2:4, 3:4, 4:4, 5:4, 6:4, 7:0, 8:0, 9:5, 10:5, 11:5, 12:5, 13:4, 14:0]";
        Player expectedPlayer = Player.PLAYER_A;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_1_PLAYER_B);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
    }

    @Test
    //Player A makes move and it ends up in his house, so player gets extra move
    public void testMakeMove_moveEndsUpInHouse_Player_A() {
        //given
        int stones = 4;
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, stones));
        pits.add(new Pit(PIT_2_PLAYER_A, stones));
        pits.add(new Pit(PIT_3_PLAYER_A, stones));
        pits.add(new Pit(PIT_4_PLAYER_A, stones));
        pits.add(new Pit(PIT_5_PLAYER_A, stones));
        pits.add(new Pit(PIT_6_PLAYER_A, stones));
        pits.add(new Pit(HOUSE_PLAYER_A, 0));

        pits.add(new Pit(PIT_1_PLAYER_B, stones));
        pits.add(new Pit(PIT_2_PLAYER_B, stones));
        pits.add(new Pit(PIT_3_PLAYER_B, stones));
        pits.add(new Pit(PIT_4_PLAYER_B, stones));
        pits.add(new Pit(PIT_5_PLAYER_B, stones));
        pits.add(new Pit(PIT_6_PLAYER_B, stones));
        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        kalaha_1.setPits(pits);

        String expectedPits = "[1:4, 2:4, 3:0, 4:5, 5:5, 6:5, 7:1, 8:4, 9:4, 10:4, 11:4, 12:4, 13:4, 14:0]";
        Player expectedPlayer = Player.PLAYER_A;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_3_PLAYER_A);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
    }

    @Test
    //Player B makes move and it ends up in his house, so player gets extra move
    public void testMakeMove_moveEndsUpInHouse_Player_B() {
        //given
        int stones = 4;
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, stones));
        pits.add(new Pit(PIT_2_PLAYER_A, stones));
        pits.add(new Pit(PIT_3_PLAYER_A, stones));
        pits.add(new Pit(PIT_4_PLAYER_A, stones));
        pits.add(new Pit(PIT_5_PLAYER_A, stones));
        pits.add(new Pit(PIT_6_PLAYER_A, stones));
        pits.add(new Pit(HOUSE_PLAYER_A, 0));

        pits.add(new Pit(PIT_1_PLAYER_B, stones));
        pits.add(new Pit(PIT_2_PLAYER_B, stones));
        pits.add(new Pit(PIT_3_PLAYER_B, stones));
        pits.add(new Pit(PIT_4_PLAYER_B, stones));
        pits.add(new Pit(PIT_5_PLAYER_B, stones));
        pits.add(new Pit(PIT_6_PLAYER_B, stones));
        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_B);

        String expectedPits = "[1:4, 2:4, 3:4, 4:4, 5:4, 6:4, 7:0, 8:4, 9:4, 10:0, 11:5, 12:5, 13:5, 14:1]";
        Player expectedPlayer = Player.PLAYER_B;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_3_PLAYER_B);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
    }

    @Test
    //Player A makes move. Last stone appears in his own empty pit and opposite pit of another player is not empty.
    //Player A gets stones from opposite pit along with stone in his own ex empty pit.
    public void testMakeMove_moveEndsUpInPlayerOwnEmptyPit_WithNotEmptyOppositePit_Player_A() {
        //given
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, 0));
        pits.add(new Pit(PIT_2_PLAYER_A, 0));
        pits.add(new Pit(PIT_3_PLAYER_A, 0));
        pits.add(new Pit(PIT_4_PLAYER_A, 0));
        pits.add(new Pit(PIT_5_PLAYER_A, 10));
        pits.add(new Pit(PIT_6_PLAYER_A, 0));
        pits.add(new Pit(HOUSE_PLAYER_A, 0));

        pits.add(new Pit(PIT_1_PLAYER_B, 0));
        pits.add(new Pit(PIT_2_PLAYER_B, 0));
        pits.add(new Pit(PIT_3_PLAYER_B, 0));
        pits.add(new Pit(PIT_4_PLAYER_B, 0));
        pits.add(new Pit(PIT_5_PLAYER_B, 3));
        pits.add(new Pit(PIT_6_PLAYER_B, 0));
        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_A);

        String expectedPits = "[1:1, 2:0, 3:0, 4:0, 5:0, 6:1, 7:6, 8:1, 9:1, 10:1, 11:1, 12:0, 13:1, 14:0]";
        Player expectedPlayer = Player.PLAYER_B;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_5_PLAYER_A);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
    }

    @Test
    //Player B makes move. Last stone appears in his own empty pit and opposite pit of another player is not empty.
    //Player B gets stones from opposite pit along with stone in his own ex empty pit.
    public void testMakeMove_moveEndsUpInPlayerOwnEmptyPit_WithNotEmptyOppositePit_Player_B() {
        //given
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, 0));
        pits.add(new Pit(PIT_2_PLAYER_A, 0));
        pits.add(new Pit(PIT_3_PLAYER_A, 0));
        pits.add(new Pit(PIT_4_PLAYER_A, 0));
        pits.add(new Pit(PIT_5_PLAYER_A, 3));
        pits.add(new Pit(PIT_6_PLAYER_A, 0));
        pits.add(new Pit(HOUSE_PLAYER_A, 0));

        pits.add(new Pit(PIT_1_PLAYER_B, 0));
        pits.add(new Pit(PIT_2_PLAYER_B, 0));
        pits.add(new Pit(PIT_3_PLAYER_B, 0));
        pits.add(new Pit(PIT_4_PLAYER_B, 0));
        pits.add(new Pit(PIT_5_PLAYER_B, 10));
        pits.add(new Pit(PIT_6_PLAYER_B, 0));
        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_B);

        String expectedPits = "[1:1, 2:1, 3:1, 4:1, 5:0, 6:1, 7:0, 8:1, 9:0, 10:0, 11:0, 12:0, 13:1, 14:6]";
        Player expectedPlayer = Player.PLAYER_A;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_5_PLAYER_B);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
    }

    @Test
    //Player B makes move. Last stone appears in his own empty pit and opposite pit of another player is empty.
    //Player B just puts his last stone in empty pit and that's it.
    public void testMakeMove_moveEndsUpInPlayerOwnEmptyPit_WithEmptyOppositePit_Player_B() {
        //given
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, 0));
        pits.add(new Pit(PIT_2_PLAYER_A, 0));
        pits.add(new Pit(PIT_3_PLAYER_A, 0));
        pits.add(new Pit(PIT_4_PLAYER_A, 0));
        pits.add(new Pit(PIT_5_PLAYER_A, 0));
        pits.add(new Pit(PIT_6_PLAYER_A, 0));
        pits.add(new Pit(HOUSE_PLAYER_A, 0));

        pits.add(new Pit(PIT_1_PLAYER_B, 0));
        pits.add(new Pit(PIT_2_PLAYER_B, 2));
        pits.add(new Pit(PIT_3_PLAYER_B, 0));
        pits.add(new Pit(PIT_4_PLAYER_B, 0));
        pits.add(new Pit(PIT_5_PLAYER_B, 0));
        pits.add(new Pit(PIT_6_PLAYER_B, 0));
        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_B);

        String expectedPits = "[1:0, 2:0, 3:0, 4:0, 5:0, 6:0, 7:0, 8:0, 9:0, 10:1, 11:1, 12:0, 13:0, 14:0]";
        Player expectedPlayer = Player.PLAYER_A;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_2_PLAYER_B);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
    }

    @Test
    //Player A makes move. Last stone appears in his own empty pit and opposite pit of another player is empty.
    //Player A just puts his last stone in empty pit and that's it.
    public void testMakeMove_moveEndsUpInPlayerOwnEmptyPit_WithEmptyOppositePit_Player_A() {
        //given
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, 0));
        pits.add(new Pit(PIT_2_PLAYER_A, 2));
        pits.add(new Pit(PIT_3_PLAYER_A, 0));
        pits.add(new Pit(PIT_4_PLAYER_A, 0));
        pits.add(new Pit(PIT_5_PLAYER_A, 0));
        pits.add(new Pit(PIT_6_PLAYER_A, 0));
        pits.add(new Pit(HOUSE_PLAYER_A, 0));

        pits.add(new Pit(PIT_1_PLAYER_B, 0));
        pits.add(new Pit(PIT_2_PLAYER_B, 0));
        pits.add(new Pit(PIT_3_PLAYER_B, 0));
        pits.add(new Pit(PIT_4_PLAYER_B, 0));
        pits.add(new Pit(PIT_5_PLAYER_B, 0));
        pits.add(new Pit(PIT_6_PLAYER_B, 0));
        pits.add(new Pit(HOUSE_PLAYER_B, 0));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_A);

        String expectedPits = "[1:0, 2:0, 3:1, 4:1, 5:0, 6:0, 7:0, 8:0, 9:0, 10:0, 11:0, 12:0, 13:0, 14:0]";
        Player expectedPlayer = Player.PLAYER_B;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_2_PLAYER_A);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
    }


    @Test
    //Player A makes move. After move Player has all empty pits. Because of this winner will be decided
    public void testMakeMove_decideWinner_AllEmptyPits_Player_A() {
        //given
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, 0));
        pits.add(new Pit(PIT_2_PLAYER_A, 0));
        pits.add(new Pit(PIT_3_PLAYER_A, 0));
        pits.add(new Pit(PIT_4_PLAYER_A, 0));
        pits.add(new Pit(PIT_5_PLAYER_A, 0));
        pits.add(new Pit(PIT_6_PLAYER_A, 1));
        pits.add(new Pit(HOUSE_PLAYER_A, 25));

        pits.add(new Pit(PIT_1_PLAYER_B, 0));
        pits.add(new Pit(PIT_2_PLAYER_B, 0));
        pits.add(new Pit(PIT_3_PLAYER_B, 0));
        pits.add(new Pit(PIT_4_PLAYER_B, 0));
        pits.add(new Pit(PIT_5_PLAYER_B, 1));
        pits.add(new Pit(PIT_6_PLAYER_B, 1));
        pits.add(new Pit(HOUSE_PLAYER_B, 20));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_A);

        String expectedPits = "[1:0, 2:0, 3:0, 4:0, 5:0, 6:0, 7:26, 8:0, 9:0, 10:0, 11:0, 12:1, 13:1, 14:20]";
        Player expectedPlayer = Player.PLAYER_A;
        Player expectedWinner = Player.PLAYER_A;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_6_PLAYER_A);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
        Assertions.assertEquals(expectedWinner, actualKalaha.getWinner());
    }

    @Test
    //Player B makes move. After move Player has all empty pits. Because of this winner will be decided
    public void testMakeMove_decideWinner_AllEmptyPits_Player_B() {
        //given
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, 0));
        pits.add(new Pit(PIT_2_PLAYER_A, 0));
        pits.add(new Pit(PIT_3_PLAYER_A, 0));
        pits.add(new Pit(PIT_4_PLAYER_A, 0));
        pits.add(new Pit(PIT_5_PLAYER_A, 1));
        pits.add(new Pit(PIT_6_PLAYER_A, 1));
        pits.add(new Pit(HOUSE_PLAYER_A, 20));

        pits.add(new Pit(PIT_1_PLAYER_B, 0));
        pits.add(new Pit(PIT_2_PLAYER_B, 0));
        pits.add(new Pit(PIT_3_PLAYER_B, 0));
        pits.add(new Pit(PIT_4_PLAYER_B, 0));
        pits.add(new Pit(PIT_5_PLAYER_B, 0));
        pits.add(new Pit(PIT_6_PLAYER_B, 1));
        pits.add(new Pit(HOUSE_PLAYER_B, 25));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_B);

        String expectedPits = "[1:0, 2:0, 3:0, 4:0, 5:1, 6:1, 7:20, 8:0, 9:0, 10:0, 11:0, 12:0, 13:0, 14:26]";
        Player expectedPlayer = Player.PLAYER_B;
        Player expectedWinner = Player.PLAYER_B;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_6_PLAYER_B);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
        Assertions.assertEquals(expectedWinner, actualKalaha.getWinner());
    }

    @Test
    //Player B makes move. After move both players have empty pits. Because of this winner will be decided
    public void testMakeMove_decideWinner_AllEmptyPits_Both_Players() {
        //given
        List<Pit> pits = new ArrayList<>();
        pits.add(new Pit(PIT_1_PLAYER_A, 0));
        pits.add(new Pit(PIT_2_PLAYER_A, 0));
        pits.add(new Pit(PIT_3_PLAYER_A, 0));
        pits.add(new Pit(PIT_4_PLAYER_A, 0));
        pits.add(new Pit(PIT_5_PLAYER_A, 0));
        pits.add(new Pit(PIT_6_PLAYER_A, 1));
        pits.add(new Pit(HOUSE_PLAYER_A, 27));

        pits.add(new Pit(PIT_1_PLAYER_B, 0));
        pits.add(new Pit(PIT_2_PLAYER_B, 0));
        pits.add(new Pit(PIT_3_PLAYER_B, 0));
        pits.add(new Pit(PIT_4_PLAYER_B, 0));
        pits.add(new Pit(PIT_5_PLAYER_B, 0));
        pits.add(new Pit(PIT_6_PLAYER_B, 0));
        pits.add(new Pit(HOUSE_PLAYER_B, 20));
        kalaha_1.setPits(pits);
        kalaha_1.setActivePlayer(Player.PLAYER_A);

        String expectedPits = "[1:0, 2:0, 3:0, 4:0, 5:0, 6:0, 7:28, 8:0, 9:0, 10:0, 11:0, 12:0, 13:0, 14:20]";
        Player expectedPlayer = Player.PLAYER_A;
        Player expectedWinner = Player.PLAYER_A;

        //when
        Kalaha actualKalaha = kalahaService.makeMove(kalaha_1.getId(), PIT_6_PLAYER_A);

        //then
        Assertions.assertEquals(expectedPits, actualKalaha.getPits().toString());
        Assertions.assertEquals(expectedPlayer, actualKalaha.getActivePlayer());
        Assertions.assertEquals(expectedWinner, actualKalaha.getWinner());
    }

    @Test()
    public void testMakeMove_RequestedPitId_Negative() {
        //given
        int requestedPitId = -5;
        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                kalahaService.makeMove(kalaha_1.getId(), requestedPitId));
        //then
        String expectedExceptionMessage = "Can't sow because requested pit doesn't exist";
        String actualExceptionMessage = exception.getMessage();

        assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));
    }

    @Test()
    public void testMakeMove_RequestedPitId_Zero() {
        //given
        int requestedPitId = 0;
        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                kalahaService.makeMove(kalaha_1.getId(), requestedPitId));
        //then
        String expectedExceptionMessage = "Can't sow because requested pit doesn't exist";
        String actualExceptionMessage = exception.getMessage();

        assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));
    }

    @Test
    //Player requests pitId bigger than max amount of pits
    public void testMakeMove_RequestedPitId_15() {
        //given
        int requestedPitId = 15;
        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                kalahaService.makeMove(kalaha_1.getId(), requestedPitId));
        //then
        String expectedExceptionMessage = "Can't sow because requested pit doesn't exist";
        String actualExceptionMessage = exception.getMessage();

        assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateKalaha_WithZeroStones() {
        //given
        int stones = 0;
        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                kalahaService.createKalaha(Player.PLAYER_A, stones));
        //then
        String expectedExceptionMessage = "Can't create kalaha with zero or less stones";
        String actualExceptionMessage = exception.getMessage();

        assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void testCreateKalaha_WithNegativeStones() {
        //given
        int stones = -5;
        //when
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                kalahaService.createKalaha(Player.PLAYER_A, stones));
        //then
        String expectedExceptionMessage = "Can't create kalaha with zero or less stones";
        String actualExceptionMessage = exception.getMessage();

        assertTrue(actualExceptionMessage.contains(expectedExceptionMessage));
    }

    @Test
    public void test() {
        List<String> letters = Arrays.asList("A", "B", "D", "F", "H", "K", "L", "N", "P", "R", "S", "T", "U", "W", "X", "Y", "Z");

        Map<String, String> dashCodeTitelMap = new HashMap<>();
        dashCodeTitelMap.put("-30", "Lichamelijk onderzoek volledig");
        dashCodeTitelMap.put("-31", "Lichamelijk onderzoek gericht");
        dashCodeTitelMap.put("-32", "Gevoeligheidstest");
        dashCodeTitelMap.put("-33", "Microbiologisch/immunologisch onderzoek");
        dashCodeTitelMap.put("-34", "Bloedonderzoek");
        dashCodeTitelMap.put("-35", "Urineonderzoek");
        dashCodeTitelMap.put("-36", "Faecesonderzoek");
        dashCodeTitelMap.put("-37", "Histologie/exfoliatieve cytologie");//
        dashCodeTitelMap.put("-38", "Ander laboratoriumonderzoek");
        dashCodeTitelMap.put("-39", "Functieonderzoek");
        dashCodeTitelMap.put("-40", "Endoscopie");
        dashCodeTitelMap.put("-41", "Röntgen-/beeldvormend onderzoek");
        dashCodeTitelMap.put("-42", "Elektrische afleidingen");
        dashCodeTitelMap.put("-43", "Ander diagnostisch onderzoek");
        dashCodeTitelMap.put("-44", "Immunisatie/preventieve medicatie");
        dashCodeTitelMap.put("-45", "Advies/observatie/voorlichting/dieet");
        dashCodeTitelMap.put("-46", "Overleg binnen eerste lijn");
        dashCodeTitelMap.put("-47", "Overleg met specialist");
        dashCodeTitelMap.put("-48", "vervallen");
        dashCodeTitelMap.put("-49", "Andere preventieve verrichting");
        dashCodeTitelMap.put("-50", "Medicatie/recept/injectie");
        dashCodeTitelMap.put("-51", "Incisie/drain/aspiratie [ex. catheterisatie -53]");
        dashCodeTitelMap.put("-52", "Excisie/biopsie/débridement/cauterisatie");
        dashCodeTitelMap.put("-53", "Instrumentatie/catheterisatie/intubatie/dilatatie");
        dashCodeTitelMap.put("-54", "Hechting/gipsspalk/prothese");
        dashCodeTitelMap.put("-55", "Lokale injectie/infiltratie");
        dashCodeTitelMap.put("-56", "Verband/compressie/tamponade");
        dashCodeTitelMap.put("-57", "Revalidatie");
        dashCodeTitelMap.put("-58", "Therapeutisch gesprek/counselen");
        dashCodeTitelMap.put("-59", "Andere therapeutische verrichting");
        dashCodeTitelMap.put("-60", "Uitslag onderzoek/verrichting");
        dashCodeTitelMap.put("-61", "Uitslag/verslag andere hulpverlener");
        dashCodeTitelMap.put("-62", "Administratieve verrichting");
        dashCodeTitelMap.put("-63", "Vervolgcontact niet gespecificeerd");
        dashCodeTitelMap.put("-64", "Episode op initiatief huisarts");
        dashCodeTitelMap.put("-65", "Episode op initiatief derde");
        dashCodeTitelMap.put("-66", "Verwijzing eerstelijnshulpverlener [ex. arts]");
        dashCodeTitelMap.put("-67", "Verwijzing specialist/ziekenhuis");
        dashCodeTitelMap.put("-68", "Andere verwijzing");
        dashCodeTitelMap.put("-69", "Andere reden voor contact");

/*        List<String> dashCodes = Arrays.asList("-30","-31", "-32", "-33", "-34", "-35", "-36", "-37", "-38", "-39", "-40",
                "-41", "-42", "-43", "-44", "-45", "-46", "-47", "-48", "-49", "-50", "-51", "-52", "-53", "-54", "-55",
                "-56", "-57", "-58", "-59", "-60", "-61", "-62", "-63", "-64", "-65", "-66", "-67", "-68", "-69");*/

        for(Map.Entry<String, String> dashCodeTitel: dashCodeTitelMap.entrySet()) {
            for(String letter: letters) {
                String icpcCode = dashCodeTitel.getKey();
                String titel = dashCodeTitel.getValue();
                System.out.println(String.format("insert into icpc_rubriek (icpc_code, icpc_titel) values('%s', '%s');", icpcCode.replace("-", letter), titel));
            }
        }
    }

}
