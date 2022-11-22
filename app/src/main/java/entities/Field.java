package entities;

import enums.FieldTypes;

public class Field{
    private final FieldTypes type;

    public Field(FieldTypes type) {
        this.type = type;
    }

    public FieldTypes getType() {
        return type;
    }
}
