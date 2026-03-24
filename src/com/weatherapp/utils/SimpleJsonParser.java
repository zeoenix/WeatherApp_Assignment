package com.weatherapp.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple lightweight recursive descent JSON parser to avoid third-party dependencies.
 * Converts JSON objects to Map<String, Object> and arrays to List<Object>.
 */
public class SimpleJsonParser {

    private int pos = 0;
    private final String json;

    public SimpleJsonParser(String json) {
        this.json = json;
    }

    public Object parse() {
        skipWhitespace();
        if (pos >= json.length()) return null;
        char c = json.charAt(pos);
        if (c == '{') return parseObject();
        if (c == '[') return parseArray();
        if (c == '"') return parseString();
        if (c == 't' || c == 'f') return parseBoolean();
        if (c == 'n') return parseNull();
        if (c == '-' || Character.isDigit(c)) return parseNumber();
        throw new RuntimeException("Unexpected byte: " + c + " at " + pos);
    }

    private void skipWhitespace() {
        while (pos < json.length() && Character.isWhitespace(json.charAt(pos))) {
            pos++;
        }
    }

    private Map<String, Object> parseObject() {
        Map<String, Object> map = new HashMap<>();
        pos++; // Skip '{'
        skipWhitespace();
        if (json.charAt(pos) == '}') {
            pos++;
            return map;
        }
        while (true) {
            skipWhitespace();
            String key = parseString();
            skipWhitespace();
            if (json.charAt(pos) != ':') throw new RuntimeException("Expected ':' at " + pos);
            pos++; // Skip ':'
            Object value = parse();
            map.put(key, value);
            skipWhitespace();
            char c = json.charAt(pos);
            if (c == '}') {
                pos++;
                break;
            } else if (c == ',') {
                pos++;
            } else {
                throw new RuntimeException("Expected '}' or ',' at " + pos);
            }
        }
        return map;
    }

    private List<Object> parseArray() {
        List<Object> list = new ArrayList<>();
        pos++; // Skip '['
        skipWhitespace();
        if (json.charAt(pos) == ']') {
            pos++;
            return list;
        }
        while (true) {
            list.add(parse());
            skipWhitespace();
            char c = json.charAt(pos);
            if (c == ']') {
                pos++;
                break;
            } else if (c == ',') {
                pos++;
            } else {
                throw new RuntimeException("Expected ']' or ',' at " + pos);
            }
        }
        return list;
    }

    private String parseString() {
        pos++; // Skip open quote
        StringBuilder sb = new StringBuilder();
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (c == '"') {
                pos++;
                break;
            } else if (c == '\\') {
                pos++;
                if (pos < json.length()) {
                    char esc = json.charAt(pos);
                    if (esc == 'n') sb.append('\n');
                    else if (esc == 'r') sb.append('\r');
                    else if (esc == 't') sb.append('\t');
                    else if (esc == '"' || esc == '\\' || esc == '/') sb.append(esc);
                    else if (esc == 'u') {
                        String hex = json.substring(pos + 1, pos + 5);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos += 4;
                    }
                    pos++;
                }
            } else {
                sb.append(c);
                pos++;
            }
        }
        return sb.toString();
    }

    private Number parseNumber() {
        int start = pos;
        while (pos < json.length()) {
            char c = json.charAt(pos);
            if (Character.isDigit(c) || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E') {
                pos++;
            } else {
                break;
            }
        }
        String numStr = json.substring(start, pos);
        if (numStr.contains(".") || numStr.contains("e") || numStr.contains("E")) {
            return Double.parseDouble(numStr);
        } else {
            long l = Long.parseLong(numStr);
            if (l >= Integer.MIN_VALUE && l <= Integer.MAX_VALUE) {
                return (int) l;
            }
            return l;
        }
    }

    private Boolean parseBoolean() {
        if (json.startsWith("true", pos)) {
            pos += 4;
            return true;
        } else if (json.startsWith("false", pos)) {
            pos += 5;
            return false;
        }
        throw new RuntimeException("Expected true/false at " + pos);
    }

    private Object parseNull() {
        if (json.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        throw new RuntimeException("Expected null at " + pos);
    }
}
