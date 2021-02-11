package controller;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import model.*;
import model.ShapeCommand;
import model.interfaces.IShape;
import view.interfaces.PaintCanvasBase;
import model.Point;

//inheritance
public class DrawEllipseCommand extends ShapeCommand {

    public DrawEllipseCommand(ArrayList<IShape> myDrawList, PaintCanvasBase base, Point p1,
                                Point p2, ShapeColor myPrimaryColor, ShapeColor mySecondaryColor, ShapeShadingType myShadingType) {

        super(myDrawList,base,p1,p2,myPrimaryColor, mySecondaryColor, myShadingType);
    }

    @Override
    public void run(){
        super.run();
        drawMe();
        //add to command history

    }

    @Override
    public void drawMe() {
        //safe way to extend
        super.drawMe();
        int width = BoundsUtility.calcWidth(p1, p2);
        int height = BoundsUtility.calcHeight(p1, p2);

        Point TopLeft = BoundsUtility.calcTopLeftCorner(p1,p2);
        super.setActiveColor(myGraphics2D, primaryColor);

        if(sShadingType == ShapeShadingType.FILLED_IN){
            myGraphics2D.fillOval(TopLeft.x, TopLeft.y, width, height); //X,Y IS THE TOP LEFT CORNER
        }
        if(sShadingType == ShapeShadingType.OUTLINE){
            myGraphics2D.drawOval(TopLeft.x, TopLeft.y, width, height); //X,Y IS THE TOP LEFT CORNER
        }
        if(sShadingType == ShapeShadingType.OUTLINE_AND_FILLED_IN){
            myGraphics2D.fillOval(TopLeft.x, TopLeft.y, width, height);
            super.setActiveColor(myGraphics2D, secondaryColor);
            myGraphics2D.setStroke(new BasicStroke(5));
            myGraphics2D.drawOval(TopLeft.x, TopLeft.y, width, height);
            //X,Y IS THE TOP LEFT CORNER
        }

    }
}
