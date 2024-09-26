package uk.ac.ed.inf.pizzadronz.data;
import javax.validation.constraints.NotNull;

public record LngLat(@NotNull Double lng,@NotNull Double lat) {
}
