package eg.edu.alexu.csd.oop.draw;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class Ellipse extends GeoShape {

    public Ellipse() {
        properties = new TreeMap<>();
        properties.put("width", 0d);
        properties.put("height",0d);
        color=Color.black;
        fillColor=Color.black;
        position = new Point(0,0);
    }

    public Ellipse(Color color, Color fillColor, Point p1, Point p2) {
        position = new Point ((int)(p1.x-(Math.abs(p1.getX()-p2.getX()))),(int)(p1.y-(Math.abs(p1.getY()-p2.getY()))));
        properties = new TreeMap<>();
        properties.put("width", Math.abs(p1.getX()-p2.getX()));
        properties.put("height",Math.abs(p1.getY()-p2.getY()));
        this.color = color;
        this.fillColor = fillColor;

    }

    public void draw(java.awt.Graphics canvas) {
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.fillOval(position.x ,position.y,2* properties.get("width").intValue(),2* properties.get("height").intValue());
        }
        canvas.setColor(color);
        canvas.drawOval(position.x ,position.y, 2*properties.get("width").intValue(), 2*properties.get("height").intValue());

    } // redraw the shape on the canvas

    public Object clone() throws CloneNotSupportedException {
        Color cColor = new Color(color.getRGB());
        Color cFColor=null;
        if(fillColor!=null){
            cFColor = new Color(fillColor.getRGB());
        }        Point cPoistion = new Point(position.x,position.y);
        Shape clone = new Ellipse(cColor,cFColor,new Point(0,0),new Point(0,0));
        clone.setPosition(cPoistion);
        clone.getProperties().put("width",properties.get("width"));
        clone.getProperties().put("height",properties.get("height"));
        return clone;
    } // create a deep clone of the shape
}
