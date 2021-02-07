package controller;
import model.Point;

public interface IJPaintController {
    void setup();
    void mouseReleasedController(Point pressedPoint, Point releasedPoint);
}
