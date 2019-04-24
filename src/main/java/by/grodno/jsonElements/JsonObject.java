package by.grodno.jsonElements;

import java.util.LinkedList;
import java.util.List;

public class JsonObject implements JsonElement{
    List<JsonElement> elements = new LinkedList<JsonElement>();
    public void add(JsonElement element) {
        elements.add(element);
    }

    public String convert2Xml() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("<object>");
        for (JsonElement element:elements){
            stringBuilder.append(element.convert2Xml());
        }
        stringBuilder.append("</object>");

        return stringBuilder.toString();
    }

    public void print() {
        System.out.println("<object>");

        for(JsonElement element:elements){
            element.print();
        }

        System.out.println("</object>");
    }


}
