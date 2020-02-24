package eg.edu.alexu.csd.oop.draw;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class LineSegmant extends GeoShape {
    public LineSegmant() {
        properties = new TreeMap<>();
        properties.put("x2",0d);
        properties.put("y2",0d);
        color=Color.black;
        fillColor=Color.black;
        position = new Point(0,0);
    }

    public void setPosition(java.awt.Point position) {
        this.position = position;
    }

    public java.awt.Point getPosition() {
        return position;
    }

    public LineSegmant(Color color,Point p1,Point p2) {
        position = p1;
        properties = new TreeMap<>();
        properties.put("x2",p2.getX());
        properties.put("y2",p2.getY());
        this.color = color;

    }

    public void draw(java.awt.Graphics canvas) {
        canvas.setColor(color);
        canvas.drawLine(position.x, position.y, properties.get("x2").intValue(), properties.get("y2").intValue());
    } // redraw the shape on the canvas

    public Object clone() throws CloneNotSupportedException {
        Color cColor = new Color(color.getRGB());
        Point cPoistion = new Point(position.x,position.y);
        Shape clone = new LineSegmant(cColor,new Point(0,0),new Point(properties.get("x2").intValue(),properties.get("y2").intValue()));
        clone.setPosition(cPoistion);
        return clone;

    } // create a deep clone of the shape
}
