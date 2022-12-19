package entities;

public class FieldDB {
    public int owner;
    public int houses;
    public boolean deposit;

    public FieldDB(int owner, int houses) {
        this.owner = owner;
        this.houses = houses;
        deposit = false;
    }

    public FieldDB() {
    }
}
