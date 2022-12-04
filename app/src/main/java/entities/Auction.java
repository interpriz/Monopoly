package entities;

import java.util.ArrayList;
import static entities.StaticMessages.*;

public class Auction {

    public Property property;

    public int bet;

    public int winner = -1;

    public ArrayList<Integer> participants = new ArrayList<>();

    public Auction(Property property) {
        this.property = property;
        this.bet = 0;
    }

    public Auction() {
    }
}
