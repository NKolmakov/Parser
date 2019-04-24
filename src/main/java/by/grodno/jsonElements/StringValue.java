package by.grodno.jsonElements;

public class StringValue implements JsonElement{
    String value = "";
    public void add(JsonElement element) {

    }

    public String convert2Xml() {
        return "<value>"+value+"</value>";
    }

    public void print() {
        System.out.println("<value>"+value+"</value>");
    }

    public void setValue(String value){
        this.value = value;
    }
}
