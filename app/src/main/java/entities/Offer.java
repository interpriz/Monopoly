package entities;

import enums.OfferStates;
import enums.OfferTypes;
import services.MapService;

// sold (продажа):
// создатель предложения хочет продать свою senderProperty
// получателю предложения за sum денег
//___________________________________
// buy (покупка):
// создатель предложения хочет купить recipientProperty
// у получателя предложения за sum денег
//___________________________________
// change (обмен):
// создатель предложения хочет обменять свою senderProperty
// на recipientProperty получателя предложения
// sum при этом: =0 - простой обмен, <0 - с доплатой отправителя, >0 - с доплатой получателя

public class Offer {
    public int senderID;
    public int recipientID;
    public int senderPropertyID; // номер собственности
    public int sum;
    public OfferTypes type;
    public OfferStates state; //1 - new_offer; //2 - accept_offer; //3 - reject_offer;
    public int recipientPropertyID;

    public Offer(int senderID, int recipientID, Property senderProperty, int sum, OfferTypes type, OfferStates state, Property recipientProperty) {
        this.senderID = senderID;
        this.senderPropertyID = MapService.getInstance().map.indexOf(senderProperty);
        if ((type == OfferTypes.buy || type == OfferTypes.sold) && sum < 0) {
            this.sum = -sum;
        } else
            this.sum = sum;

        this.type = type;
        this.state = state;
        this.recipientPropertyID = MapService.getInstance().map.indexOf(recipientProperty);
        this.recipientID = recipientID;
    }

    public Offer(int senderID, int recipientID, int senderProperty, int sum, OfferTypes type, OfferStates state, int recipientProperty) {
        this.senderID = senderID;
        this.senderPropertyID = senderProperty;
        if ((type == OfferTypes.buy || type == OfferTypes.sold) && sum < 0) {
            this.sum = -sum;
        } else
            this.sum = sum;

        this.type = type;
        this.state = state;
        this.recipientPropertyID = recipientProperty;
        this.recipientID = recipientID;
    }

    public Offer() {
    }

    public Property senderProperty() {
        if(senderPropertyID==-1)
            return null;
        return MapService.getInstance().getPropertyByPosition(senderPropertyID);
    }

    public Property recipientProperty() {
        if(recipientPropertyID==-1)
            return null;
        return MapService.getInstance().getPropertyByPosition(recipientPropertyID);
    }
}


