package com.example.monopoly;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.powermock.api.mockito.PowerMockito.when;
import static entities.StaticMessages.SUCCESS;

//import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import entities.Debt;
import entities.Player;
import entities.Street;
import repositories.FireBaseRepository;
import services.GameService;
import services.MapService;

@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(JUnit4.class)
@PrepareForTest({ FirebaseDatabase.class})
public class DebtTest {
    private final MapService mapService =MapService.getInstance();
    private GameService gameService;

    private Street gitnayaUl;
    private Player player;

    private DatabaseReference mockedDatabaseReference;

    @Before
    public void setUp() throws Exception {

        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);

        FireBaseRepository mockedFirebaseRepository = Mockito.mock(FireBaseRepository.class);

        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);

        PowerMockito.mockStatic(FirebaseDatabase.class);
        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
        //when(FirebaseDatabase.getInstance(anyString())).thenReturn(mockedFirebaseDatabase);
        when(mockedDatabaseReference.child(anyString())).thenReturn(mockedDatabaseReference);


        gameService = new GameService("testGame1");
        gameService.gameInitialise("God",4);
        for(int i=1; i<gameService.getGame().maxPLayers;i++){
            gameService.enterGame("player_"+i);
        }
        gitnayaUl = (Street) mapService.getPropertyByPosition(1);
        player = gameService.getGame().players.get(0);
    }

    @Test
    public void repayDebt(){

        player.debts.add(new Debt(0,1,200));
        String result  = gameService.repayDebt(player, player.getLastDebt());

        assertEquals(SUCCESS,result);
        assertEquals(1300,player.cash);
        assertEquals(1700,gameService.getGame().players.get(1).cash);
    }
}
