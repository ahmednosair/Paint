package eg.edu.alexu.csd.oop.draw;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.TreeMap;

public class Circle extends GeoShape {

    public Circle() {
        properties = new TreeMap<>();
        properties.put("radius", 0d);
        color=Color.black;
        fillColor=Color.black;
        position = new Point(0,0);
    }

    public Circle(Color color, Color fillColor, Point p1, Point p2) {
        properties = new TreeMap<>();
        properties.put("radius", p1.distance(p2));
        position = new Point((int) (p1.x - p1.distance(p2)), (int) (p1.y - p1.distance(p2)));
        this.color = color;
        this.fillColor = fillColor;

    }

    public void draw(java.awt.Graphics canvas) {
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.fillOval(position.x, position.y, 2 * properties.get("radius").intValue(), 2 * properties.get("radius").intValue());
        }
        canvas.setColor(color);
        canvas.drawOval(position.x, position.y, 2 * properties.get("radius").intValue(), 2 * properties.get("radius").intValue());

    } // redraw the shape on the canvas

    public Object clone() throws CloneNotSupportedException {
        Color cColor = new Color(color.getRGB());
        Color cFColor = null;
        if (fillColor != null) {
            cFColor = new Color(fillColor.getRGB());
        }
        Point cPoistion = new Point(position.x, position.y);
        Shape clone = new Circle(cColor, cFColor, new Point(0, 0), new Point(0, 0));
        clone.setPosition(cPoistion);
        clone.getProperties().put("radius", properties.get("radius"));
        return clone;
    } // create a deep clone of the shape}
}