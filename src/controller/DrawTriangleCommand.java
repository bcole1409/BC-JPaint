package controller;

import java.awt.*;
import java.util.ArrayList;
import model.*;
import model.ShapeCommand;
import model.interfaces.IShape;
import view.interfaces.PaintCanvasBase;
import model.Point;

//inheritance
public class DrawTriangleCommand extends ShapeCommand {

    public DrawTriangleCommand(ArrayList<IShape> myDrawList, PaintCanvasBase base, Point p1,
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
        int height = BoundsUtility.calcHeight(p1, p2);

        super.setActiveColor(myGraphics2D, primaryColor);

        if(sShadingType == ShapeShadingType.FILLED_IN){
            myGraphics2D.fillPolygon(BoundsUtility.calcXPoints(p1,p2), BoundsUtility.calcYPoints(p1,p2), 3);
        }
        if(sShadingType == ShapeShadingType.OUTLINE){
            myGraphics2D.drawPolygon(BoundsUtility.calcXPoints(p1,p2), BoundsUtility.calcYPoints(p1,p2), 3); //X,Y IS THE TOP LEFT CORNER
        }
        if(sShadingType == ShapeShadingType.OUTLINE_AND_FILLED_IN){
            myGraphics2D.fillPolygon(BoundsUtility.calcXPoints(p1,p2), BoundsUtility.calcYPoints(p1,p2), 3);
            super.setActiveColor(myGraphics2D, secondaryColor);
            myGraphics2D.setStroke(new BasicStroke(5));
            myGraphics2D.drawPolygon(BoundsUtility.calcXPoints(p1,p2), BoundsUtility.calcYPoints(p1,p2), 3);
            //X,Y IS THE TOP LEFT CORNER
        }
    }
}
