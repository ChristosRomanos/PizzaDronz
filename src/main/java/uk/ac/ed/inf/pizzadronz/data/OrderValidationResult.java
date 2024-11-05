package uk.ac.ed.inf.pizzadronz.data;

import uk.ac.ed.inf.pizzadronz.constants.OrderStatus;
import uk.ac.ed.inf.pizzadronz.constants.OrderValidationCode;

public record OrderValidationResult(
        OrderStatus orderStatus,
        OrderValidationCode orderValidationCode
        ){
}
