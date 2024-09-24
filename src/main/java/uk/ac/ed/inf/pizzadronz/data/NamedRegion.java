package uk.ac.ed.inf.pizzadronz.data;

public record NamedRegion(String name,LongLat [] vertices) {
    public String getName() {
        return name;
    }

    public LongLat[] getVertices() {
        return vertices;
    }
}
