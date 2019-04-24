package by.grodno;

import by.grodno.parser.Parser;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String xml = "";
        try {
            Parser parser = new Parser("JsonFile.json");
            parser.startParse();
            System.out.println(xml = parser.getObject().convert2Xml());
            System.out.println();
            parser.getObject().print();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //TODO: FIX VALID METHOD
}
