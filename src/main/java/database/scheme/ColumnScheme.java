package database.scheme;

public class ColumnScheme {
    private String name;
    private Integer type;
    private Integer size;

    public ColumnScheme(String name, Integer type, Integer size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public Integer getSize() {
        return size;
    }
}
