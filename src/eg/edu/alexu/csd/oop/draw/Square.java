package eg.edu.alexu.csd.oop.draw;

import java.awt.*;
import java.util.Map;
import java.util.TreeMap;

public class Square extends GeoShape {

    public Square(Color color, Color fillColor, Point p1, Point p2) {
        int side;
        if (p2.x <= p1.x) {
            if (p2.y <= p1.y) {
                side = (p1.y-p2.y);
                position=new Point(p1.x-(side),p2.y);
            } else {
                side=p2.y-p1.y;
                position= new Point(p1.x-(side),p1.y);
            }
        } else {
            if (p2.y <= p1.y) {
                side=p1.y-p2.y;
                position= new Point(p1.x,p2.y);
            } else {
                side=p2.y-p1.y;
                position=p1;

            }
        }
        properties = new TreeMap<>();
        properties.put("side",(double)side);
        this.color = color;
        this.fillColor = fillColor;
    }

    public Square() {
        properties = new TreeMap<>();
        properties.put("side",0d);
        color=Color.black;
        fillColor=Color.black;
        position = new Point(0,0);
    }

    public void draw(java.awt.Graphics canvas) {
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.fillRect(position.x,position.y, properties.get("side").intValue(), properties.get("side").intValue());
        }
        canvas.setColor(color);
        canvas.drawRect(position.x,position.y, properties.get("side").intValue(), properties.get("side").intValue());

    } // redraw the shape on the canvas

    public Object clone() throws CloneNotSupportedException {
        Color cColor = new Color(color.getRGB());
        Color cFColor=null;
        if(fillColor!=null){
            cFColor = new Color(fillColor.getRGB());
        }        Point cPoistion = new Point(position.x,position.y);
        Shape clone = new Square(cColor,cFColor,new Point(0,0),new Point(0,0));
        clone.setPosition(cPoistion);
        clone.getProperties().put("side",properties.get("side"));
        return clone;
    } // create a deep clone of the shape
}
