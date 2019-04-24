package by.grodno.parser;

import by.grodno.jsonElements.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private int counter = 1; //global variable co control position in sentence

    public Parser(String filePath) throws FileNotFoundException, IOException {
        String temp = convertFile2String(filePath).replaceAll("\\s+","");
        if (isValid(temp)) {
            this.jsonString = temp;
            isJsonValid = true;
        } else {
            System.out.println("JSON file is invalid!");
        }
    }

    private boolean isContain(char[] array,char element){
        for (int i = 0; i < array.length; i++) {
            if(array[i] == element)return true;
        }
        return false;
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
        boolean valid = false;
        String str4Check = jsonString;
        int curlyBracesAmount = 0;
        int bracketsAmount = 0;

        for (int i = 0; i < str4Check.length(); i++) {
            char currentCharacter = str4Check.charAt(i);

            if (currentCharacter == '{') curlyBracesAmount++;
            else if (currentCharacter == '}') curlyBracesAmount--;

            if (currentCharacter == '[') bracketsAmount++;
            else if (currentCharacter == ']') bracketsAmount--;

            //checking comma element
            if (currentCharacter == ',') {
                try {
                    char ch = str4Check.charAt(i + 1);
                    boolean b = isContain(afterComma,ch);

                    //considered all comma wrong locates
                    if ((i == 0) ||                             //comma can't be first element
                            (i == str4Check.length() - 1) ||    //comma can't be last element
                            (isContain(notbeforeComma,str4Check.charAt(i - 1))) ||
                            (!isContain(afterComma,str4Check.charAt(i + 1)))) {
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
                    if ((i == 0 || i != str4Check.length() - 1) || (i > 1 && isContain(beforeOpenCurlyBrace,str4Check.charAt(i - 1)))) {
                        valid = true;
                    } else {
                        valid = false;
                        break;
                    }
                } catch (IndexOutOfBoundsException ex) {
                    break;
                }
            }

        }
        if (curlyBracesAmount == 0 && bracketsAmount == 0) valid = true;
        else valid = false;
        return valid;
    }

    public void startParse() {
        if (isJsonValid) {
            for (int i=counter; i < jsonString.length();) {
                char ch = jsonString.charAt(i);
                if (ch == '{') jsonObject.add(create("object", counter));
                else if (ch == '[') jsonObject.add(create("array", counter));
                else if (ch == '"') jsonObject.add(create("key", counter));
                else if (ch == ':') jsonObject.add(create("value", counter));
                else counter++;

                    i=counter;

            }
        } else {
            System.out.println("Can't parse invalid JSON");
        }
    }

    private JsonElement create(String elementType, int position) {
        JsonElement element = null;
        if (elementType.equalsIgnoreCase("object")) element = parseObject(counter);
        if (elementType.equalsIgnoreCase("array")) element = parseArray(counter);
        if (elementType.equalsIgnoreCase("key")) element = parseKey(counter);
        if (elementType.equalsIgnoreCase("value")) element = parseValue(counter);

        return element;
    }

    private JsonElement parseObject(int position) {
        JsonElement jsonObject = new JsonObject();
        position++;

        for (int i = position; i <jsonString.length() ;) {
            char ch = jsonString.charAt(i);
            if(ch != '}'){
                if (ch == '"'){
                    jsonObject.add(parseKey(i));
                   i= position = counter;
                   // i=position;
                }else
                if (ch == ':'){
                    jsonObject.add(parseValue(i));
                   i= position = counter;
                   // i=position;
                }else
                if(ch =='['){
                    jsonObject.add(parseArray(i));
                   i = position = counter;
                   // i=position;
                }else i= ++position;

            }else {
                position++;
                break;
            }
        }
        counter = position;

        return jsonObject;
    }

    private JsonElement parseArray(int position) {
        JsonElement jsonArray = new Array();
        position++;

        for (int i = position; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(i);

            if (ch != ']') {
                if (ch == '{'){
                    jsonArray.add(parseObject(position));
                    i=position=counter;
                }
                else
                if (ch == '['){
                    jsonArray.add(parseArray(position));
                    i=position=counter;
                }
                else
                if (ch == '"') {
                    jsonArray.add(parseKey(position));
                    i=position=counter;
                }
                else
                if (ch == ':'){
                    jsonArray.add(parseValue(position));
                    i=position=counter;
                }
                else{

                }
            } else {
                position++;
                break;
            }
        }
        counter = position;

        return jsonArray;
    }

    private JsonElement parseKey(int position) {
        Key key = new Key();
        StringBuffer stringBuffer = new StringBuffer();
        position++;

        for (int i = position; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(position);
            if (ch != '"') {
                stringBuffer.append(ch);
                position++;
            } else{
                position++;
                break;
            }
        }

        counter = position;

        key.setKey(stringBuffer.toString());
        return key;
    }

    private JsonElement parseValue(int position) {
        JsonElement value = null;
        char ch = jsonString.charAt(++counter);

        if (ch == '[' || ch=='{'){
            value = new JsonValue();
            value.add(parseJsonElementValue(counter));
        }
        else if(Character.isLetterOrDigit(ch) || ch=='"'){
            value =parseStringValue(counter);
        }

        return value;
    }

    private JsonElement parseJsonElementValue(int position) {
        JsonElement element = null;
        char ch = jsonString.charAt(counter);
        if(ch == '[') element = create("array",counter);
        if(ch == '{') element = create("object",counter);

        return element;
    }

    private JsonElement parseStringValue(int position) {
        StringValue stringValue = new StringValue();
        StringBuffer stringBuffer = new StringBuffer();

        for (int i = ++position; i < jsonString.length(); i++) {
            char ch = jsonString.charAt(i);
            if(ch !=',' && ch!=']' && ch !='}'&& ch !='"'){
                stringBuffer.append(ch);
                position++;
            }else{
                position++;
                break;
            }
        }
        counter = position;
        stringValue.setValue(stringBuffer.toString());
        return stringValue;
    }

    public JsonObject getObject(){
        return this.jsonObject;
    }
}



