package eg.edu.alexu.csd.oop.draw;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.IntStream;

public class DEngine implements DrawingEngine {
    private ArrayList<Shape> shapes;
    private List<Class<? extends Shape>> supportedShapes;
    private Stack<ArrayList<Shape>> undo;
    private Stack<ArrayList<Shape>> redo;

    public DEngine() {
        this.shapes = new ArrayList<>();
        this.supportedShapes = new ArrayList<>();
        undo = new Stack<>();
        redo = new Stack<>();
        addToUndo();
        supportedShapes.add(LineSegmant.class);
        supportedShapes.add(Square.class);
        supportedShapes.add(Rectangle.class);
        supportedShapes.add(Triangle.class);
        supportedShapes.add(Circle.class);
        supportedShapes.add(Ellipse.class);
        installPluginShape("RoundRectangle.jar");
    }

    /* redraw all shapes on the canvas */
    public void refresh(java.awt.Graphics canvas) {
        IntStream.range(0, shapes.size()).forEach(i -> shapes.get(i).draw(canvas));
    }

    public void addShape(Shape shape) {
        redo.removeAllElements();
        shapes.add(shape);
        addToUndo();

    }

    public void removeShape(Shape shape) {
        redo.removeAllElements();
        shapes.remove(shape);
        addToUndo();

    }

    public void updateShape(Shape oldShape, Shape newShape) {
        redo.removeAllElements();
        shapes.remove(oldShape);
        shapes.add(newShape);
        addToUndo();

    }

    /* return the created shapes objects */
    public Shape[] getShapes() {
        Shape[] fReturn = new Shape[shapes.size()];
        return (shapes.toArray(fReturn));
    }

    /* return the classes (types) of supported shapes already exist and the
     * ones that can be dynamically loaded at runtime (see Part 3) */
    public java.util.List<Class<? extends Shape>> getSupportedShapes() {
        return this.supportedShapes;

    }

    /* add to the supported shapes the new shape class (see Part 3) */
     public void installPluginShape(String jarPath){try {
         JarFile jarFile = new JarFile(jarPath);
         Enumeration<JarEntry> e = jarFile.entries();
         URL[] urls = { new URL("jar:file:" + jarPath+"!/") };
         URLClassLoader cl = URLClassLoader.newInstance(urls);

         while (e.hasMoreElements()) {
             JarEntry je = e.nextElement();
             if(je.isDirectory() || !je.getName().endsWith(".class")){
                 continue;
             }
             // -6 because of .class
             String className = je.getName().substring(0,je.getName().length()-6);
             className = className.replace('/', '.');
             Class c = cl.loadClass(className);
             if(Shape.class.isAssignableFrom(c)){
                 supportedShapes.add((Class<? extends Shape>) c);
             }

         }
     }catch (Exception e){
         System.out.println(e.toString());
     }}

    /* limited to 20 steps. Only consider in undo & redo
     * these actions: addShape, removeShape, updateShape */
    public void undo() {
        if (undo.size() <= 1) {
            throw new RuntimeException("No steps to undo");
        }
        redo.push(undo.pop());
        shapes = backup(undo.peek());
    }

    public void redo() {
        if (redo.empty()) {
            throw new RuntimeException("No steps to redo");
        }
        undo.push(redo.pop());
        shapes = backup(undo.peek());

    }

    /* use the file extension to determine the type,
     * or throw runtime exception when unexpected extension */
    public void save(String path) {
        if (path.substring(path.length() - 4).toLowerCase().equals(".xml")) {
            try {
                FileOutputStream fos = new FileOutputStream(path);
                XMLEncoder encoder = new XMLEncoder(fos);
                encoder.writeObject(shapes);
                encoder.writeObject(supportedShapes);
                encoder.close();
                fos.close();
            } catch (Exception e) {
                throw new RuntimeException("Invalid file");
            }

        } else if (path.substring(path.length() - 5).toLowerCase().equals(".json")) {
            try {
                FileWriter fW = new FileWriter(path);
                MyJSON parser = new MyJSON();
                parser.build(shapes, supportedShapes);
                fW.write(parser.getMyJSON());
                fW.close();
            } catch (IOException e) {
                throw new RuntimeException("Invalid file");
            }
        } else {
            throw new RuntimeException("Invalid file");
        }
    }

    public void load(String path) {
        if (path.substring(path.length() - 4).toLowerCase().equals(".xml")) {
            try {
                FileInputStream fis = new FileInputStream(path);
                XMLDecoder decoder = new XMLDecoder(fis);
                shapes = (ArrayList<Shape>) decoder.readObject();
                supportedShapes =(List<Class<? extends Shape>>) decoder.readObject();
                undo.removeAllElements();
                redo.removeAllElements();
                decoder.close();
                fis.close();
                addToUndo();

            } catch (Exception e) {
                throw new RuntimeException("Invalid file");
            }

        } else if (path.substring(path.length() - 5).toLowerCase().equals(".json")) {
            try {
                MyJSON parser = new MyJSON();
                File file = new File(path);
                BufferedReader br = new BufferedReader(new FileReader(file));
                parser.setReadable(br.readLine());
                supportedShapes=parser.readClasses();
                shapes=parser.readShapes();
                undo.removeAllElements();
                redo.removeAllElements();
                addToUndo();
            } catch (IOException e) {
                throw new RuntimeException("Invalid file");
            }
        } else {
            throw new RuntimeException("Invalid file");
        }
    }

    private ArrayList<Shape> backup(ArrayList<Shape> arr) {
        ArrayList<Shape> backup = new ArrayList<>();
        try {
            for (Shape shape : arr) {
                backup.add((Shape) shape.clone());
            }
        } catch (CloneNotSupportedException e) {
            System.out.println("Error !");
        }
        return backup;

    }

    private void addToUndo() {
        if (undo.size() == 21) {
            undo.remove(0);
        }
        undo.add(backup(shapes));
    }
}
