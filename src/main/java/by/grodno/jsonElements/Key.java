package by.grodno.jsonElements;

public class Key implements JsonElement{
    String key = "";
    public void add(JsonElement element) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Key isn't a container. Use setKey to set a value");
    }

    public String convert2Xml() {
        return "<key>"+key+"</key>";
    }

    public void print() {
        System.out.println("<key>"+key+"</key>");
    }

    public void setKey(String key){
        this.key = key;
    }
}
