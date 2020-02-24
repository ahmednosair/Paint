package eg.edu.alexu.csd.oop.draw.gui;

import eg.edu.alexu.csd.oop.draw.*;
import eg.edu.alexu.csd.oop.draw.Rectangle;
import eg.edu.alexu.csd.oop.draw.Shape;

import java.awt.*;
import java.awt.geom.Point2D;

import static java.lang.Math.pow;

class SelectionTool {
    static Shape selectedShape(Shape[] shapes, Point p) {
        for (int i = shapes.length - 1; i >= 0; i--) {
            if (shapes[i] instanceof LineSegmant) {
                if (onLine(shapes[i], p))
                    return shapes[i];
            } else if (shapes[i] instanceof Square) {
                if (inSquare(shapes[i], p))
                    return shapes[i];
            } else if (shapes[i] instanceof Rectangle) {
                if (inRectangle(shapes[i], p))
                    return shapes[i];
            } else if (shapes[i] instanceof Triangle) {
                if (inTriangle(shapes[i], p))
                    return shapes[i];
            } else if (shapes[i] instanceof Circle) {
                if (inCircle(shapes[i], p))
                    return shapes[i];
            } else if (shapes[i] instanceof Ellipse) {
                if (inEllipse(shapes[i], p))
                    return shapes[i];
            }
        }
        return null;
    }
    static boolean isInside (Shape s,Point p){
        if (s instanceof LineSegmant) {
            return onLine(s, p);
        } else if (s instanceof Square) {
            return inSquare(s, p);
        } else if (s instanceof Rectangle) {
            return inRectangle(s, p);
        } else if (s instanceof Triangle) {
            return inTriangle(s, p);
        } else if (s instanceof Circle) {
            return inCircle(s, p);
        } else if (s instanceof Ellipse) {
            return inEllipse(s, p);
        }
        return false;
    }



    private static boolean inRectangle(Shape rectangle, Point p) {
        Point topLeft = rectangle.getPosition();
        int height = rectangle.getProperties().get("height").intValue();
        int width = rectangle.getProperties().get("width").intValue();
        return p.x - topLeft.x <= width && p.x - topLeft.x >= 0 && p.y - topLeft.y <= height && p.y - topLeft.y >= 0;
    }

    private static boolean inEllipse(Shape ellipse, Point p) {
        double a=ellipse.getProperties().get("width");
        double b=ellipse.getProperties().get("height");
        Point2D center = new Point2D.Double(ellipse.getPosition().x+a,ellipse.getPosition().y+b);
        return ((pow((p.x - center.getX()), 2) / pow(a, 2)) + (pow((p.y  -center.getY()), 2) / pow(b, 2))) <= 1;
    }

    private static boolean inTriangle(Shape triangle, Point p) {
        Point p1 = triangle.getPosition();
        Point p2 = new Point(triangle.getProperties().get("x2").intValue(), triangle.getProperties().get("y2").intValue());
        Point p3 = new Point(triangle.getProperties().get("x3").intValue(), triangle.getProperties().get("y3").intValue());
        double A = area(p1.x, p1.y, p2.x, p2.y, p3.x, p3.y);
        double A1 = area(p.x, p.y, p2.x, p2.y, p3.x, p3.y);
        double A2 = area(p1.x, p1.y, p.x, p.y, p3.x, p3.y);
        double A3 = area(p1.x, p1.y, p2.x, p2.y, p.x, p.y);
        return (A == A1 + A2 + A3);
    }

    private static boolean onLine(Shape line, Point p) {
        Point p1 = line.getPosition();
        Point p2 = new Point(line.getProperties().get("x2").intValue(), line.getProperties().get("y2").intValue());
        double slope = Math.abs((p1.getY() - p2.getY()) / (p1.x - p2.x));
        double selectSlop = Math.abs((p1.getY() - p.getY()) / (p1.x - p.x));
        return Math.abs(slope - selectSlop) <= 0.08;
    }

    private static double area(int x1, int y1, int x2, int y2, int x3, int y3) {
        return Math.abs((x1 * (y2 - y3) + x2 * (y3 - y1) + x3 * (y1 - y2)) / 2.0);
    }
    private static boolean inCircle(Shape circle, Point p){
        double radius = circle.getProperties().get("radius");
        Point center = new Point((int)(circle.getPosition().x+radius),(int)(circle.getPosition().y+radius));
     return p.distance(center) <= radius ;
    }
    private static boolean inSquare(Shape square,Point p){
        Point topLeft =square.getPosition();
        int side = square.getProperties().get("side").intValue();
        return p.x - topLeft.x <= side && p.x - topLeft.x >= 0 && p.y - topLeft.y <= side && p.y - topLeft.y >= 0;
    }

}
