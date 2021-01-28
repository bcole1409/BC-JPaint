package controller;
import model.Point;

public interface IJPaintController {
    void setup();
    void mouseReleasedController(Point pressedPoint, Point releasedPoint);
    //void drawTriangle(Point startingPoint, Point endingPoint);
    //void drawEllipse(Point startingPoint, Point endingPoint);

}
