package by.grodno.parser;

import by.grodno.jsonElements.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
    private char[] beforeOpenCurlyBrace = new char[]{',', '['};
    private char[] notbeforeComma = new char[]{':', '{', '['};
    private char[] afterComma = new char[]{'"', '{'};
    private char[] notafterColon = new char[]{',', '{', '}', ']'};
    private char[] notbeforeCloseCurlyBrace = new char[]{',', ':', '}', '{', '['};
    private char[] afterCloseCurlyBrace = new char[]{',', ']'};
    private String jsonString = "";
    private JsonObject jsonObject = new JsonObject();
    private boolean isJsonValid = false;

    public Parser(String filePath) throws FileNotFoundException, IOException {
        if (isValid(convertFile2String(filePath))) {
            this.jsonString = convertFile2String(filePath);
            isJsonValid = true;
        } else {
            System.out.println("JSON file is invalid!");
        }
    }

    private String convertFile2String(String filePath) throws FileNotFoundException, IOException {
        FileReader reader = new FileReader(filePath);
        StringBuffer stringBuffer = new StringBuffer();
        int symbolCode;

        while ((symbolCode = reader.read()) != -1) {
            stringBuffer.append((char) symbolCode);
        }

        return stringBuffer.toString();
    }

    private boolean isValid(String jsonString) {
        boolean isValid = false;
        String str4Check = jsonString;
        int curlyBracesAmount = 0;
        int bracketsAmount = 0;
        boolean openBrace = false;
        boolean closeBrace = false;
        for (int i = 0; i < str4Check.length(); i++) {
            char currentCharacter = str4Check.charAt(i);

            if (currentCharacter == '{') curlyBracesAmount++;
            else if (currentCharacter == '}') curlyBracesAmount--;

            if (currentCharacter == '[') bracketsAmount++;
            else if (currentCharacter == ']') bracketsAmount--;

            //checking comma element
            if (currentCharacter == ',') {
                try {

                    //considered all comma wrong locates
                    if ((i == 0) ||                             //comma can't be first element
                            (i == str4Check.length() - 1) ||    //comma can't be last element
                            (Arrays.asList(notbeforeComma).contains(str4Check.charAt(i - 1))) ||
                            (!Arrays.asList(afterComma).contains(str4Check.charAt(i + 1)))) {
                        break;
                    }
                } catch (IndexOutOfBoundsException ex) {
                    break;
                }
            }

            //checking open curly brace
            if (currentCharacter == '{') {
                try {
                    //considered all open brace right locates
                    if ((i == 0 || i != str4Check.length() - 1) || (i > 1 && Arrays.asList(beforeOpenCurlyBrace).contains(str4Check.charAt(i - 1)))) {
                        isValid = true;
                    } else {
                        isValid = false;
                        break;
                    }
                } catch (IndexOutOfBoundsException ex) {
                    break;
                }
            }

        }
        if (curlyBracesAmount == 0 && bracketsAmount == 0) isValid = true;
        return isValid;
    }

    public void startParse() {
        if (isJsonValid) {
            for (int i = 0; i < jsonString.length(); i++) {
                char ch = jsonString.charAt(i);
                if (ch == '{') jsonObject.add(create("object", i));
                if (ch == '[') jsonObject.add(create("array", i));
                if (ch == '"') jsonObject.add(create("key", i));
                if (ch == ':') jsonObject.add(create("value", i));
            }
        } else {
            System.out.println("Can't parse invalid JSON");
        }
    }

    private JsonElement create(String elementType, int position) {
        JsonElement element = null;
        if (elementType.equalsIgnoreCase("object")) element = parseObject(position);
        if (elementType.equalsIgnoreCase("array")) element = parseArray(position);
        if (elementType.equalsIgnoreCase("key")) element = parseKey(position);
        if (elementType.equalsIgnoreCase("value")) element = parseValue(position);

        return element;
    }

    private JsonElement parseObject(int position) {
        JsonElement jsonObject = new JsonObject();
        char ch = jsonString.charAt(++position);

        if (ch == '"') jsonObject.add(parseKey(position));
        if (ch == ':') jsonObject.add(parseValue(position));

        return jsonObject;
    }

    private JsonElement parseArray(int position) {
        JsonElement jsonArray = new Array();
        char ch = jsonString.charAt(++position);

        if (ch == '"') jsonArray.add(parseKey(position));
        if (ch == ':') jsonArray.add(parseValue(position));

        return jsonArray;
    }

    private JsonElement parseKey(int position) {
        Key key = new Key();
        StringBuffer stringBuffer = new StringBuffer();
        ++position;

        for (int i = position; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(position);
            if (ch != '"') {
                stringBuffer.append(ch);
                position++;
            } else break;
        }

        key.setKey(stringBuffer.toString());
        return key;
    }

    private JsonElement parseValue(int position) {
        JsonElement value = null;
        char ch = jsonString.charAt(++position);

        if (ch == '[' || ch=='{'){
            value = new JsonValue();
            value.add(parseJsonElementValue(position));
        }
        else if(Character.isLetterOrDigit(ch)){
            value = new StringValue();
            ((StringValue) value).setValue(parseStringValue(position));
        }

        return value;
    }

    private JsonElement parseJsonElementValue(int position) {
        JsonElement element = null;
        char ch = jsonString.charAt(position);
        if(ch == '[') element = create("array",position);
        if(ch == '{') element = create("object",position);

        return element;
    }

    private JsonElement parseStringValue(int position) {
        StringValue stringValue = new StringValue();
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = position; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(i);
            if(ch !=',' || ch!=']' || ch !='}'){
                stringBuffer.append(ch);
                position++;
            }else break;
        }

        stringValue.setValue(stringBuffer.toString());
        return stringValue;
    }
}



