package by.grodno.jsonElements;

public class JsonValue implements JsonElement{
    JsonElement element;

    public void add(JsonElement element) {
        this.element = element;
    }

    public String convert2Xml() {
        return "<value>"+element.convert2Xml()+"</value>";
    }

    public void print() {
        System.out.println("<value>");
        element.print();
        System.out.println("</value>");
    }
}
