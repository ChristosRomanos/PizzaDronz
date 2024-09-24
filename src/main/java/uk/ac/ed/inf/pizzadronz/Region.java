package uk.ac.ed.inf.pizzadronz;

import uk.ac.ed.inf.pizzadronz.data.LongLat;

import java.util.List;

public class Region {
    private String name;
    private List<LongLat> vertices;
    public String getName() {
        return name;
    }
    public List<LongLat> getVertices() {
        return vertices;
    }
    public Region(String name, List<LongLat> vertices) {
        this.name = name;
        this.vertices = vertices;
    }
}
