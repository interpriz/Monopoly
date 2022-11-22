package entities;

public class Debt {
    // должник
    public Player debtor;
    public Player recipient;
    public int sum;

    public Debt(Player debtor, Player recipient, int sum) {
        this.debtor = debtor;
        this.recipient = recipient;
        this.sum = sum;
    }
}
