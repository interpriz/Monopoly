package entities;

public class Debt {
    // должник
    public int debtorID;
    public int recipientID;
    public int sum;

    public Debt(int debtor, int recipient, int sum) {
        this.debtorID = debtor;
        this.recipientID = recipient;
        this.sum = sum;
    }

    public Debt() {
    }
}
