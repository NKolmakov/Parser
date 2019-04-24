package by.grodno.jsonElements;

import java.util.LinkedList;
import java.util.List;

public class Array implements JsonElement {
    List<JsonElement> elements = new LinkedList<JsonElement>();
    public void add(JsonElement element) {
        this.elements.add(element);
    }

    public String convert2Xml() {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("<array>");
        for (JsonElement element : elements) {
            stringBuffer.append(element.convert2Xml());
        }
        stringBuffer.append("</array>");

        return stringBuffer.toString();
    }

    public void print() {
        System.out.println("<array>");

        for(JsonElement element:elements){
            element.print();
        }

        System.out.println("</array>");
    }
}
