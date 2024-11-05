package uk.ac.ed.inf.pizzadronz.data;

import java.util.Date;

public record Order(
        String orderNo,
        Date orderDate,
        Integer priceTotalInPence,
        Pizza[] pizzasInOrder,
        CreditCardInformation creditCardInformation
){
}
