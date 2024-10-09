package uk.ac.ed.inf.pizzadronz.data;

import javax.validation.constraints.NotNull;
import java.util.List;


public record NamedRegion(@NotNull String name,@NotNull List<LngLat>  vertices) {
    public NamedRegion {
        if (name==null) {
            throw new RuntimeException("Name is null");
        }
    }

}
