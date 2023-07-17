package rules;

import org.junit.After;
import org.junit.Before;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import entities.Street;
import services.GameService;


public class GameInitialiseRule implements TestRule {

    private GameService gameService;



    public GameInitialiseRule(String gameName) {
        gameService = new GameService(gameName);
    }

    @Override
    public Statement apply(final Statement s, Description d) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                try {
                    gameService.gameInitialise("God", 4);
                    for(int i=1; i<gameService.getGame().maxPLayers;i++){
                        gameService.enterGame("player_"+i);
                    }
                } finally {
                    gameService.deleteGame(gameService.getPlayer(0));
                }
            }
        };
    }
}
