package eg.edu.alexu.csd.oop.draw;

import java.awt.*;
import java.util.TreeMap;


public class Triangle extends GeoShape {

    public Triangle(Color color, Color fillColor, Point p1, Point p2, int type) {

        Point p3 = get3rdPoint(p1, p2, type);
        position = p1;
        properties = new TreeMap<>();
        properties.put("x2", p2.getX());
        properties.put("y2", p2.getY());
        properties.put("x3", p3.getX());
        properties.put("y3", p3.getY());
        properties.put("type", (double) type);
        this.color = color;
        this.fillColor = fillColor;

    }

    public Triangle() {
        properties = new TreeMap<>();
        properties.put("x2", 0d);
        properties.put("y2", 0d);
        properties.put("x3", 0d);
        properties.put("y3", 0d);
        properties.put("type",1d);
        color=Color.black;
        fillColor=Color.black;
        position = new Point(0,0);
    }

    public void draw(java.awt.Graphics canvas) {
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.fillPolygon(new int[]{position.x, properties.get("x2").intValue(), properties.get("x3").intValue()}, new int[]{position.y, properties.get("y2").intValue(), properties.get("y3").intValue()}, 3);
        }
        canvas.setColor(color);
        canvas.drawPolygon(new int[]{position.x, properties.get("x2").intValue(), properties.get("x3").intValue()}, new int[]{position.y, properties.get("y2").intValue(), properties.get("y3").intValue()}, 3);

    } // redraw the shape on the canvas

    public Object clone() throws CloneNotSupportedException {
        Color cColor = new Color(color.getRGB());
        Color cFColor=null;
        if(fillColor!=null){
             cFColor = new Color(fillColor.getRGB());
        }
        Point cPoistion = new Point(position.x, position.y);
        Shape clone = new Triangle(cColor, cFColor, new Point(0, 0), new Point(0, 0), properties.get("type").intValue());
        clone.setPosition(cPoistion);
        clone.getProperties().put("x2", properties.get("x2"));
        clone.getProperties().put("y2", properties.get("y2"));
        clone.getProperties().put("x3", properties.get("x3"));
        clone.getProperties().put("y3", properties.get("y3"));
        return clone;
    } // create a deep clone of the shape

    private Point get3rdPoint(Point p1, Point p2, int type) {
        if (type == 1) {
            return new Point(p2.x, p1.y);
        }
        return new Point(2 * p2.x - p1.x, p1.y);
    }
}
