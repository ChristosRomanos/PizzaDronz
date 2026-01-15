package uk.ac.ed.inf.pizzadronz.Data;

import uk.ac.ed.inf.pizzadronz.Constants.OrderStatus;
import uk.ac.ed.inf.pizzadronz.Constants.OrderValidationCode;

public record OrderValidationResult(
        OrderStatus orderStatus,
        OrderValidationCode orderValidationCode
        ){
}
