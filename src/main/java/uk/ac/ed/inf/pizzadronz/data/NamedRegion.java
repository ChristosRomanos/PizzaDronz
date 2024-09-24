package uk.ac.ed.inf.pizzadronz.data;

import java.util.List;


public record NamedRegion(String name, List<LngLat>  vertices) {
    public NamedRegion {
        if (name.isEmpty() || vertices==null) {
            throw new IllegalArgumentException();
        }
    }

}
