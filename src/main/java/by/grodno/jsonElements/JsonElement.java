package by.grodno.jsonElements;

public interface JsonElement {
    void add(JsonElement element);
    String convert2Xml();
    void print();
}
