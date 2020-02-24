package eg.edu.alexu.csd.oop.draw.gui;

import com.sun.xml.internal.ws.api.ha.StickyFeature;
import eg.edu.alexu.csd.oop.draw.*;
import eg.edu.alexu.csd.oop.draw.Rectangle;
import eg.edu.alexu.csd.oop.draw.Shape;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class Controller {
    @FXML
    GridPane grid;
    @FXML
    ListView<String> listofshapes;
    @FXML
    Canvas paint;
    @FXML
    ColorPicker colorPick, fillColorPick;
    @FXML
    ToggleGroup shapeChoice, editChoice;
    @FXML
    CheckBox fillEnable;
    @FXML
    Button deleteButton, undoButton, redoButton;
    @FXML
    VBox shapes;
    private int steps;
    private int undoCount;

    private FXGraphics2D bridge;
    private static DrawingEngine painter = new DEngine();
    private static Point fPoint;
    private static Shape cSelected;
    private static Color originalColor;

    public void initialize() {
        bridge = new FXGraphics2D(paint.getGraphicsContext2D());
        bridge.setStroke(new BasicStroke(4));
        steps = 0;
        undoCount = 0;
    }

    @FXML
    private void storefPoint(MouseEvent e) {
        fPoint = new Point((int) e.getX(), (int) e.getY());
    }

    @FXML
    private void handle(MouseEvent e) {
        Point sPoint = new Point((int) e.getX(), (int) e.getY());
        if (fPoint.equals(sPoint)) {
            Shape s = SelectionTool.selectedShape(painter.getShapes(), sPoint);
            handleSelection(s);
            return;
        }
        if (cSelected != null && SelectionTool.isInside(cSelected, fPoint)) {
            edit(sPoint);
            return;
        }
        painter.addShape(getNewShape(((RadioButton) shapeChoice.getSelectedToggle()).getText(), sPoint));
        steps = (steps == 20 ? steps : steps + 1);
        undoCount = 0;
        enableUndoRedo();
        painter.refresh(bridge);

    }

    @FXML
    void liveEffect(MouseEvent e) {
        if (cSelected == null && isOurShape()) {
            Point sPoint = new Point((int) e.getX(), (int) e.getY());
            refresh();
            getNewShape(((RadioButton) shapeChoice.getSelectedToggle()).getText(), sPoint).draw(bridge);
        }
    }

    @FXML
    private void enableFill() {
        if (fillEnable.isSelected()) {
            fillColorPick.setDisable(false);
        } else {
            fillColorPick.setDisable(true);
        }
    }

    private Color fxToAwtColor(javafx.scene.paint.Color color) {
        return new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity());
    }

    private Color checkFill() {
        if (fillEnable.isSelected()) {
            return fxToAwtColor(fillColorPick.getValue());
        }
        return null;
    }

    private Shape getNewShape(String s, Point sPoint) {
        switch (s) {
            case "Line":
                return (new LineSegmant(fxToAwtColor(colorPick.getValue()).darker(), fPoint, sPoint));
            case "Square":
                return (new Square(fxToAwtColor(colorPick.getValue()), checkFill(), fPoint, sPoint));
            case "Rectangle":
                return (new Rectangle(fxToAwtColor(colorPick.getValue()), checkFill(), fPoint, sPoint));
            case "Triangle ▲":
                return (new Triangle(fxToAwtColor(colorPick.getValue()), checkFill(), fPoint, sPoint, 2));
            case "Triangle ◣":
                return (new Triangle(fxToAwtColor(colorPick.getValue()), checkFill(), fPoint, sPoint, 1));
            case "Circle":
                return (new Circle(fxToAwtColor(colorPick.getValue()), checkFill(), fPoint, sPoint));
            case "Ellipse":
                return (new Ellipse(fxToAwtColor(colorPick.getValue()), checkFill(), fPoint, sPoint));
            default:
                for (int i = 0; i < painter.getSupportedShapes().size(); i++) {
                    try {
                        if (painter.getSupportedShapes().get(i).toString().contains(s)) {
                            Constructor<?> ctor = painter.getSupportedShapes().get(i).getConstructor();
                            Object object = ctor.newInstance();
                            getProperties((Shape) object);
                            return (Shape) object;
                        }
                    } catch (Exception e) {
                        showError("Error drawing!");
                    }

                }
        }
        return null;
    }

    private void handleSelection(Shape selected) {
        if (selected == null) {
            unselect();
        } else if (cSelected == null) {
            select(selected);
        } else if (cSelected != selected) {
            unselect();
            select(selected);
        } else {
            unselect();
        }

    }

    private void select(Shape selected) {
        cSelected = selected;
        originalColor = new Color(selected.getColor().getRGB());
        selected.setColor(originalColor.darker());
        refresh();
        enableDelete();
    }

    private void unselect() {
        if (cSelected == null)
            return;
        cSelected.setColor(new Color(originalColor.getRGB()));
        refresh();
        cSelected = null;
        enableDelete();
    }

    private void move(Point sPoint) {
        try {
            int xDistance = sPoint.x - fPoint.x;
            int yDistance = sPoint.y - fPoint.y;
            Shape newShape = (Shape) cSelected.clone();
            newShape.setPosition(new Point(xDistance + newShape.getPosition().x, yDistance + newShape.getPosition().y));
            if (newShape instanceof LineSegmant) {
                int x2 = newShape.getProperties().get("x2").intValue();
                int y2 = newShape.getProperties().get("y2").intValue();
                newShape.getProperties().put("x2", ((double) x2 + xDistance));
                newShape.getProperties().put("y2", ((double) y2 + yDistance));
            }
            if (newShape instanceof Triangle) {
                int x2 = newShape.getProperties().get("x2").intValue();
                int y2 = newShape.getProperties().get("y2").intValue();
                int x3 = newShape.getProperties().get("x3").intValue();
                int y3 = newShape.getProperties().get("y3").intValue();
                newShape.getProperties().put("x2", ((double) x2 + xDistance));
                newShape.getProperties().put("y2", ((double) y2 + yDistance));
                newShape.getProperties().put("x3", ((double) x3 + xDistance));
                newShape.getProperties().put("y3", ((double) y3 + yDistance));
            }
            painter.updateShape(cSelected, newShape);
            steps = (steps == 20 ? steps : steps + 1);
            undoCount = 0;
            cSelected = newShape;
            paint.getGraphicsContext2D().clearRect(0, 0, 1920, 1080);
            painter.refresh(bridge);

            enableUndoRedo();
        } catch (CloneNotSupportedException e) {
            showError("Error moving!");
        }
    }

    private void refresh() {
        paint.getGraphicsContext2D().clearRect(0, 0, 1920, 1080);
        painter.refresh(bridge);
    }

    @FXML
    private void delete() {
        painter.removeShape(cSelected);
        steps = (steps == 20 ? steps : steps + 1);
        undoCount = 0;
        cSelected = null;
        enableDelete();
        enableUndoRedo();
        refresh();

    }

    private void enableDelete() {
        if (cSelected == null) {
            deleteButton.setDisable(true);
        } else {
            deleteButton.setDisable(false);
        }
    }

    private void edit(Point sPoint) {
        if (((RadioButton) editChoice.getSelectedToggle()).getText().equals("Move")) {
            move(sPoint);
        } else {
            resize(sPoint);

        }
    }

    private void resize(Point sPoint) {
        switch (((RadioButton) shapeChoice.getSelectedToggle()).getText()) {
            case "Line":
                lineResize(sPoint);
                break;
            case "Square":
                squareResize(sPoint);
                break;
            case "Rectangle":
                rectangleResize(sPoint);
                break;
            case "Triangle ▲":
                triangleResize(sPoint);
                break;
            case "Triangle ◣":
                triangleResize(sPoint);
                break;
            case "Circle":
                circleResize(sPoint);
                break;
            case "Ellipse":
                ellipseResize(sPoint);
                break;

        }
        steps = (steps == 20 ? steps : steps + 1);
        undoCount = 0;
        refresh();
        enableUndoRedo();


    }

    private void lineResize(Point sPoint) {
        try {
            Shape clone = (Shape) cSelected.clone();
            clone.getProperties().put("x2", (double) sPoint.x);
            clone.getProperties().put("y2", (double) sPoint.y);
            clone.setColor(new Color(originalColor.getRGB()));
            select(clone);
        } catch (CloneNotSupportedException e) {
            showError("Error resizing!");
        }

    }

    private void squareResize(Point sPoint) {
        try {
            Shape clone = (Shape) cSelected.clone();
            int side = 0;
            if (sPoint.y > clone.getPosition().y && sPoint.x > clone.getPosition().x) {
                side = sPoint.x - clone.getPosition().x;

            }
            if (side < 10) {
                side = 10;
            }
            clone.getProperties().put("side", (double) side);
            clone.setColor(new Color(originalColor.getRGB()));
            painter.updateShape(cSelected, clone);
            select(clone);

        } catch (CloneNotSupportedException e) {
            showError("Error resizing!");
        }
    }

    private void rectangleResize(Point sPoint) {
        try {
            Shape clone = (Shape) cSelected.clone();
            int width;
            int height;
            if (sPoint.y > clone.getPosition().y) {
                height = sPoint.y - clone.getPosition().y;
            } else {
                height = 10;
            }
            if (sPoint.x > clone.getPosition().x) {
                width = sPoint.x - clone.getPosition().x;
            } else {
                width = 10;
            }
            clone.getProperties().put("width", (double) width);
            clone.getProperties().put("height", (double) height);
            clone.setColor(new Color(originalColor.getRGB()));
            painter.updateShape(cSelected, clone);

            select(clone);
        } catch (CloneNotSupportedException e) {
            showError("Error resizing!");
        }

    }

    private void circleResize(Point sPoint) {
        try {
            Shape clone = (Shape) cSelected.clone();
            double oldRadius = cSelected.getProperties().get("radius");
            Point center = new Point((int) (clone.getPosition().x + oldRadius), (int) (clone.getPosition().y + oldRadius));
            clone.setPosition(new Point(((int) (center.x - center.distance(sPoint))), ((int) (center.y - center.distance(sPoint)))));
            clone.getProperties().put("radius", center.distance(sPoint));
            clone.setColor(new Color(originalColor.getRGB()));
            painter.updateShape(cSelected, clone);

            select(clone);
        } catch (CloneNotSupportedException e) {
            showError("Error resizing!");
        }


    }

    private void ellipseResize(Point sPoint) {
        try {
            Shape clone = (Shape) cSelected.clone();
            double oldWidth = cSelected.getProperties().get("width");
            double oldHeight = cSelected.getProperties().get("height");
            Point center = new Point((int) (clone.getPosition().x + oldWidth), (int) (clone.getPosition().y + oldHeight));
            double width = Math.abs(center.x - sPoint.x);
            double height = Math.abs(center.y - sPoint.y);
            clone.setPosition(new Point(((int) (center.x - width)), ((int) (center.y - height))));
            clone.getProperties().put("width", width);
            clone.getProperties().put("height", height);
            clone.setColor(new Color(originalColor.getRGB()));
            painter.updateShape(cSelected, clone);
            select(clone);

        } catch (CloneNotSupportedException e) {
            showError("Error resizing!");
        }

    }

    private void triangleResize(Point sPoint) {
        try {
            Shape clone = (Shape) cSelected.clone();
            Point p3;
            Point p1 = clone.getPosition();
            int xdistance = sPoint.x - fPoint.x;
            int ydistance = sPoint.y - fPoint.y;

            Point p2 = new Point(clone.getProperties().get("x2").intValue() + xdistance, clone.getProperties().get("y2").intValue() + ydistance);
            if (clone.getProperties().get("type") == 1) {
                p3 = new Point(p2.x, p1.y);
            } else {
                p3 = new Point(2 * p2.x - p1.x, p1.y);
            }
            clone.getProperties().put("x2", (double) p2.x);
            clone.getProperties().put("y2", (double) p2.y);
            clone.getProperties().put("x3", (double) p3.x);
            clone.getProperties().put("y3", (double) p3.y);
            clone.setColor(new Color(originalColor.getRGB()));
            painter.updateShape(cSelected, clone);
            select(clone);


        } catch (CloneNotSupportedException e) {
            showError("Error resizing!");
        }


    }

    @FXML
    private void undo() {
        painter.undo();
        refresh();
        undoCount++;
        steps--;
        enableUndoRedo();


    }

    @FXML
    private void redo() {
        painter.redo();
        refresh();
        steps++;
        undoCount--;
        enableUndoRedo();


    }

    private void enableUndoRedo() {
        if (steps > 0) {
            undoButton.setDisable(false);
        } else {
            undoButton.setDisable(true);
        }
        if (undoCount > 0) {
            redoButton.setDisable(false);
        } else {
            redoButton.setDisable(true);
        }
    }

    @FXML
    private void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML doc(*.xml)", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON doc(*.json)", "*.json"));
        File selectedFile = fileChooser.showSaveDialog(null);
        if (selectedFile != null) {
            painter.save(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    private void load() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML doc(*.xml)", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON doc(*.json)", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            painter.load(selectedFile.getAbsolutePath());
            steps = 0;
            undoCount = 0;
            enableUndoRedo();
            refresh();

        }

    }

    @FXML
    private void changeColor() {
        try {
            if (cSelected != null) {
                Shape clone = (Shape) cSelected.clone();
                clone.setColor(fxToAwtColor(colorPick.getValue()));
                painter.updateShape(cSelected, clone);
                select(clone);
                steps = (steps == 20 ? steps : steps + 1);
                undoCount = 0;
                refresh();

                enableUndoRedo();
            }
        } catch (CloneNotSupportedException e) {
            showError("Error changing color!");
        }
    }

    @FXML
    private void changeFillColor() {
        try {
            if (cSelected != null) {
                Shape clone = (Shape) cSelected.clone();
                clone.setFillColor(fxToAwtColor(fillColorPick.getValue()));
                clone.setColor(new Color(originalColor.getRGB()));
                painter.updateShape(cSelected, clone);
                select(clone);
                steps = (steps == 20 ? steps : steps + 1);
                undoCount = 0;
                refresh();
                enableUndoRedo();
            }
        } catch (CloneNotSupportedException e) {
            showError("Error changing fill color ");
        }
    }

    @FXML
    private void addPlugin() {
        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);
        painter.installPluginShape(selectedFile.getAbsolutePath());
        addToShapes();
    }

    private void addToShapes() {
        String shapeName = painter.getSupportedShapes().get(painter.getSupportedShapes().size() - 1).toString();
        String[] arr = shapeName.split("\\.");
        RadioButton m = new RadioButton();
        m.setToggleGroup(shapeChoice);
        m.setText(arr[arr.length - 1]);
        shapes.getChildren().add(m);
    }

    private void getProperties(Shape s) {
        final Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.initOwner((Stage) paint.getScene().getWindow());
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        Scene dialogScene = new Scene(gridPane, 300, 250);
        gridPane.setAlignment(Pos.CENTER);
        popup.setScene(dialogScene);
        String[] str = new String[s.getProperties().size()];
        Double[] val = new Double[s.getProperties().size()];
        s.getProperties().keySet().toArray(str);
        s.getProperties().values().toArray(val);
        for (int i = 0; i < s.getProperties().size(); i++) {
            gridPane.add(new Label(str[i]), 0, i);
            gridPane.add(new TextField(), 1, i);
        }
        Button enter = new Button("Enter");
        gridPane.add(enter, 1, s.getProperties().size());
        enter.addEventHandler(MouseEvent.MOUSE_CLICKED,
                e -> {
                    boolean flag = true;
                    for (int i = 0; i < s.getProperties().size(); i++) {
                        try {
                            val[i] = Double.parseDouble(((TextField) gridPane.getChildren().get(2 * i + 1)).getText());
                        } catch (Exception q) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        popup.hide();
                        popup.close();
                    } else {
                        showError("Invalid Format");
                    }

                });
        popup.showAndWait();
        for (int i = 0; i < val.length; i++) {
            s.getProperties().put(str[i], val[i]);
        }
        s.setPosition(new Point(fPoint.x, fPoint.y));
        s.setColor(fxToAwtColor(colorPick.getValue()));
        if (fillEnable.isSelected()) {
            s.setFillColor(fxToAwtColor(fillColorPick.getValue()));
        }
    }

    private void showError(String str) {
        Alert m = new Alert(Alert.AlertType.ERROR);
        m.setTitle("Error");
        m.setHeaderText("Look details below !");
        m.setContentText(str);
        m.showAndWait();
    }

    private boolean isOurShape() {
        switch (((RadioButton) shapeChoice.getSelectedToggle()).getText()) {
            case "Line":
                return true;
            case "Square":
                return true;
            case "Rectangle":
                return true;
            case "Triangle ▲":
                return true;
            case "Triangle ◣":
                return true;
            case "Circle":
                return true;
            case "Ellipse":
                return true;
            default:
                return false;
        }
    }


}
