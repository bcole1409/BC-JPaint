package model;

import controller.DrawEllipseCommand;
import controller.DrawRectangleCommand;
import controller.DrawTriangleCommand;
import model.interfaces.IShape;
import view.interfaces.PaintCanvasBase;

import java.util.ArrayList;

public class ShapeFactory  {
    private ShapeFactory() { }

    public static ShapeCommand getDrawEllipseCommand(ArrayList<IShape> myDrawList, PaintCanvasBase base, Point p1,
                                                     Point p2, ShapeColor myPrimaryColor, ShapeColor mySecondaryColor, ShapeShadingType myShadingType) {
        return new DrawEllipseCommand(myDrawList, base, p1, p2, myPrimaryColor, mySecondaryColor,
                myShadingType);
    }

    public static ShapeCommand getDrawRectangleCommand(ArrayList<IShape> myDrawList, PaintCanvasBase base, Point p1,
                                                 Point p2, ShapeColor myPrimaryColor, ShapeColor mySecondaryColor, ShapeShadingType myShadingType) {
        return new DrawRectangleCommand(myDrawList, base, p1, p2, myPrimaryColor, mySecondaryColor,
                myShadingType);
    }

    public static ShapeCommand getDrawTriangleCommand(ArrayList<IShape> myDrawList, PaintCanvasBase base, Point p1,
                                     Point p2, ShapeColor myPrimaryColor, ShapeColor mySecondaryColor, ShapeShadingType myShadingType) {
        //return new Triangle(3, isEquilateral);
        return new DrawTriangleCommand(myDrawList, base, p1, p2, myPrimaryColor, mySecondaryColor,
                myShadingType);
    }
}

