package uk.ac.ed.inf.pizzadronz.Data;

import java.time.DayOfWeek;

public record Restaurant(
        String name,
        LngLat location,
        DayOfWeek[] openingDays,
        Pizza[] menu
) {
}