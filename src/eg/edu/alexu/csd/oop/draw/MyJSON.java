package eg.edu.alexu.csd.oop.draw;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

 class MyJSON {

    private StringBuilder myJSON;
    private String readable;

     void setReadable(String readable) {
        this.readable = readable;
    }

     MyJSON() {
        myJSON = new StringBuilder();
    }

     String getMyJSON() {
        return myJSON.toString();
    }

     void build(ArrayList<Shape> shapes, List<Class<? extends Shape>> classes) {
        myJSON.append("{");
        writeShapes(shapes);
        myJSON.append(",");
        writeClasses(classes);
        myJSON.append("}");
    }

    private void writeClasses(List<Class<? extends Shape>> classes) {
        myJSON.append("\"supportedShapes\":[");
        for (Class<? extends Shape> aClass : classes) {
            myJSON.append("\"");
            myJSON.append(aClass.toString().substring(6));
            myJSON.append("\",");
        }
        myJSON.deleteCharAt(myJSON.length() - 1);
        myJSON.append("]");
    }

    private void writeShapes(ArrayList<Shape> shapes) {
        myJSON.append("\"shapes\":[");
        for (Shape shape : shapes) {
            myJSON.append("{\"");
            myJSON.append(shape.getClass().toString().substring(6));
            myJSON.append("\":");
            writeShape(shape);
            myJSON.append("},");
        }
        myJSON.deleteCharAt(myJSON.length() - 1);
        myJSON.append("]");
    }

    private void writeColor(Color c, String str) {
        myJSON.append("\"");
        myJSON.append(str);
        myJSON.append("\":[");
        if (c == null) {
            myJSON.append("null");
        } else {
            myJSON.append(c.getRed());
            myJSON.append(",");
            myJSON.append(c.getGreen());
            myJSON.append(",");
            myJSON.append(c.getBlue());
        }

        myJSON.append("]");
    }


    private void writeProperties(Map<String, Double> prop) {
        myJSON.append("\"properties\":[");
        if (prop == null) {
            myJSON.append("null");
        } else {
            String[] keys = new String[prop.size()];
            Double[] values = new Double[prop.size()];
            keys = prop.keySet().toArray(keys);
            values = prop.values().toArray(values);
            for (int i = 0; i < prop.size(); i++) {
                myJSON.append("{");
                myJSON.append("\"");
                myJSON.append(keys[i]);
                myJSON.append("\":");
                myJSON.append(values[i]);
                myJSON.append("},");
            }
            myJSON.deleteCharAt(myJSON.length() - 1);
        }

        myJSON.append("]");

    }

    private void writePosition(Point p) {
        myJSON.append("\"position\":[");
        if (p == null) {
            myJSON.append("null");
        } else {
            myJSON.append(p.x);
            myJSON.append(",");
            myJSON.append(p.y);
        }
        myJSON.append("]");
    }

    private void writeShape(Shape s) {
        myJSON.append("{");
        writePosition(s.getPosition());
        myJSON.append(",");
        writeColor(s.getColor(), "color");
        myJSON.append(",");
        writeColor(s.getFillColor(), "fillColor");
        myJSON.append(",");
        writeProperties(s.getProperties());
        myJSON.append("}");
    }

     List<Class<? extends Shape>> readClasses() {
        int l = readable.indexOf("\"supportedShapes\":");
        String str = readable.substring(l + 19);
        str = str.substring(0, str.length() - 2);
        String[] arr = str.split(",");
        List<Class<? extends Shape>> supportedShapes = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            Class<?> act;
            try {
                arr[i] = arr[i].substring(1, arr[i].length() - 1);
                act = Class.forName(arr[i]);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Error in parsing JSON");
            }
            supportedShapes.add((Class<? extends Shape>) act);
        }
        return supportedShapes;
    }

     ArrayList<Shape> readShapes() {
        int l = readable.indexOf("\"supportedShapes\":");
        String str = readable.substring(12, l - 4);
        String[] arr = str.split("}},\\{");
        ArrayList<Shape> shapes = new ArrayList<>();
        for (String s : arr) {
            shapes.add(readShape(s));
        }
        return shapes;
    }

    private Shape readShape(String str) {
        String className = str.substring(1, str.indexOf("\":{"));
        String positionStr = str.substring(str.indexOf("\"position\"") + 12, str.indexOf("],\"color\""));
        String colorStr = str.substring(str.indexOf("\"color\"") + 9, str.indexOf("],\"fillColor\""));
        String fillColorStr = str.substring(str.indexOf("\"fillColor\"") + 13, str.indexOf("],\"properties\""));
        String propStr = str.substring(str.indexOf("\"properties\"") + 14, str.length() - 1);
        Point position = positionStr.equals("null")?null:readPosition(positionStr);
        Color color = colorStr.equals("null")?null:readColor(colorStr);
        Color fillColor = fillColorStr.equals("null")?null:readColor(fillColorStr);
        Map<String, Double> properties = propStr.equals("null")?null:readProperties(propStr);
        try {
            Class<?> clazz = Class.forName(className);
            Constructor<?> ctor = clazz.getConstructor();
            Object object = ctor.newInstance();
            Shape newShape = (Shape) object;
            newShape.setPosition(position);
            newShape.setColor(color);
            newShape.setFillColor(fillColor);
            newShape.setProperties(properties);
            return newShape;
        } catch (Exception e) {
            throw new RuntimeException("Error in parsing JSON");
        }

    }

    private Map<String, Double> readProperties(String str) {
        Map<String,Double> properties = new TreeMap<>();
        String[] arr = str.split(",");
        for (String s : arr
        ) {
            s = s.substring(1, s.length() - 1);
            String[] kv = s.split(":");
            kv[0]=kv[0].substring(1,kv[0].length()-1);
            properties.put(kv[0],Double.parseDouble(kv[1]));
        }
        return properties;
    }

    private Point readPosition(String str) {
        String[] arr = str.split(",");
        return new Point(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
    }

    private Color readColor(String str) {
        String[] arr = str.split(",");
        return new Color(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
    }
}
