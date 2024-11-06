package uk.ac.ed.inf.pizzadronz.data;

import uk.ac.ed.inf.pizzadronz.constants.OrderValidationCode;

import java.util.Calendar;
import java.util.Date;

public record CreditCardInformation(
        String creditCardNumber,
        String creditCardExpiry,
        String cvv
) {

}
