package eg.edu.alexu.csd.oop.draw;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class Rectangle extends GeoShape {


    public Rectangle() {
        properties = new TreeMap<>();
        properties.put("width", 0d);
        properties.put("height",0d);
        color=Color.black;
        fillColor=Color.black;
        position = new Point(0,0);
    }

    public Rectangle(Color color, Color fillColor,Point p1,Point p2) {
        if (p2.x <= p1.x) {
            if (p2.y <= p1.y) {
                position= p2;
            } else {
                position= new Point(p1.x - Math.abs(p1.x-p2.x), p1.y);
            }
        } else {
            if (p2.y <= p1.y) {
                position= new Point(p2.x - Math.abs(p1.x-p2.x), p2.y);
            } else {
                position=p1;
            }
        }
        properties = new TreeMap<>();
        properties.put("width", Math.abs(p1.getX()-p2.getX()));
        properties.put("height",Math.abs(p1.getY()-p2.getY()));
        this.color = color;
        this.fillColor = fillColor;
    }

    public void draw(java.awt.Graphics canvas) {
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.fillRect(position.x, position.y, properties.get("width").intValue(), properties.get("height").intValue());
        }
        canvas.setColor(color);
        canvas.drawRect(position.x, position.y, properties.get("width").intValue(), properties.get("height").intValue());

    } // redraw the shape on the canvas

    public Object clone() throws CloneNotSupportedException {
        Color cColor = new Color(color.getRGB());
        Color cFColor=null;
        if(fillColor!=null){
            cFColor = new Color(fillColor.getRGB());
        }        Point cPoistion = new Point(position.x,position.y);
        Shape clone = new Rectangle(cColor,cFColor,new Point(0,0),new Point(0,0));
        clone.setPosition(cPoistion);
        clone.getProperties().put("width",properties.get("width"));
        clone.getProperties().put("height",properties.get("height"));
        return clone;
    } // create a deep clone of the shape
}