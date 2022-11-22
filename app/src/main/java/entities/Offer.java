package entities;

import enums.OfferStates;
import enums.OfferTypes;
import enums.Participants;

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
    public Player sender;
    public Player recipient;
    public Property senderProperty; // номер собственности
    public int sum;
    public OfferTypes type;
    public OfferStates state; //1 - new_offer; //2 - accept_offer; //3 - reject_offer;
    public Property recipientProperty;

    public Offer(Player sender, Player recipient, Property senderProperty, int sum, OfferTypes type, OfferStates state, Property recipientProperty) {
        this.sender = sender;
        this.senderProperty = senderProperty;
        if ((type == OfferTypes.buy || type == OfferTypes.sold) && sum < 0) {
            this.sum = -sum;
        } else
            this.sum = sum;

        this.type = type;
        this.state = state;
        this.recipientProperty = recipientProperty;
        this.recipient = recipient;
    }

    public int getFullSum(Participants player) {


        switch (type) {
            case buy:
                switch (player) {
                    case sender:
                        return
                                sum + (recipientProperty.deposit ?
                                        (sender.repayment ?
                                                recipientProperty.redemptionPrice :
                                                recipientProperty.tenPercent)
                                        : 0);
                    case recipient:
                        return 0;
                }

            case sold:
                switch (player) {
                    case sender:
                        return 0;
                    case recipient:
                        return
                                sum + (senderProperty.deposit ?
                                        (recipient.repayment ?
                                                senderProperty.redemptionPrice :
                                                senderProperty.tenPercent)
                                        : 0);

                }
                ;
            case change:
                switch (player) {
                    case sender:
                        return
                                (sum < 0 ? -1 * sum : 0) + (recipientProperty.deposit ?
                                        (sender.repayment ?
                                                recipientProperty.redemptionPrice :
                                                recipientProperty.tenPercent)
                                        : 0);
                    case recipient:
                        return
                                (sum > 0 ? sum : 0) + (senderProperty.deposit ?
                                        (recipient.repayment ?
                                                senderProperty.redemptionPrice :
                                                senderProperty.tenPercent)
                                        : 0);
                }
        }
        return -1;
    }
}


