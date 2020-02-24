package eg.edu.alexu.csd.oop.draw;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public abstract class GeoShape implements Shape {
    Point position;
    Map<String, Double> properties;
    Color color;
    Color fillColor;

    public void setPosition(java.awt.Point position) {
        this.position = position;
    }

    public java.awt.Point getPosition() {
        return position;
    }

    // update shape specific properties (e.g., radius)
    public void setProperties(java.util.Map<String, Double> properties) {
        this.properties = properties;
    }

    public java.util.Map<String, Double> getProperties() {
        return properties;
    }

    public void setColor(java.awt.Color color) {
        this.color = color;
    }

    public java.awt.Color getColor() {
        return color;
    }

    public void setFillColor(java.awt.Color color) {
        fillColor = color;
    }

    public java.awt.Color getFillColor() {
        return fillColor;
    }

    public abstract void draw(java.awt.Graphics canvas);
    // redraw the shape on the canvas

    public abstract Object clone() throws CloneNotSupportedException;
    // create a deep clone of the shape
}