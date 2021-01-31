package controller;

import java.awt.*;
import java.util.ArrayList;
import model.*;
import model.ShapeCommand;
import model.interfaces.IShape;
import view.interfaces.PaintCanvasBase;
import model.Point;

//inheritance
public class DrawRectangleCommand extends ShapeCommand {

    public DrawRectangleCommand(ArrayList<IShape> myDrawList, PaintCanvasBase base, Point p1,
                                Point p2, ShapeColor myPrimaryColor, ShapeColor mySecondaryColor, ShapeShadingType myShadingType) {

        super(myDrawList,base,p1,p2, myPrimaryColor, mySecondaryColor, myShadingType);
        drawMe();
    }

    @Override
    public void drawMe() {
        //safe way to extend
        super.drawMe();
        int width = calcWidth(p1, p2);
        int height = calcHeight(p1, p2);

        Point TopLeft = calcTopLeftCorner(p1,p2);
        super.setActiveColor(myGraphics2D, primaryColor);

        if(sShadingType == ShapeShadingType.FILLED_IN){
            myGraphics2D.fillRect(TopLeft.x, TopLeft.y, width, height); //X,Y IS THE TOP LEFT CORNER
        }
        if(sShadingType == ShapeShadingType.OUTLINE){
            myGraphics2D.drawRect(TopLeft.x, TopLeft.y, width, height); //X,Y IS THE TOP LEFT CORNER
        }
        if(sShadingType == ShapeShadingType.OUTLINE_AND_FILLED_IN){
            myGraphics2D.fillRect(TopLeft.x, TopLeft.y, width, height);
            super.setActiveColor(myGraphics2D, secondaryColor);
            myGraphics2D.setStroke(new BasicStroke(5));
            myGraphics2D.drawRect(TopLeft.x, TopLeft.y, width, height);
            //X,Y IS THE TOP LEFT CORNER
        }

    }

    private int calcWidth(Point p1, Point p2){
        int width = Math.abs(p2.x-p1.x);
        return width;
    }

    private int calcHeight(Point p1, Point p2){
        int height = Math.abs(p2.y-p1.y);
        return height;
    }

    private Point calcTopLeftCorner(Point p1, Point p2){
        int x = Math.min(p1.x, p2.x);
        int y = Math.min(p1.y, p2.y);
        Point topLeft = new Point(x,y);
        return topLeft;
    }
}
