package uk.ac.ed.inf.pizzadronz.data;

import java.time.DayOfWeek;

public record Restaurant(
        String name,
        LngLat location,
        DayOfWeek[] openingDays,
        Pizza[] menu
) {
}