package model;

import controller.DrawEllipseCommand;
import controller.DrawRectangleCommand;
import controller.DrawTriangleCommand;
import model.interfaces.IShape;
import java.awt.*;

public class ShapeCommandProxy implements IShape {
    private ShapeCommand myInnerShapeCommand;

    public ShapeCommandProxy(ShapeCommand innerShapeCommand){
        myInnerShapeCommand = innerShapeCommand;
    }

    @Override
    public void drawMe() {
        myInnerShapeCommand.drawMe();
        //DRAW THE ACTUAL OUTLINE


        //CHECK INSTANCE OF SHAPETYPE
        Point p1 = myInnerShapeCommand.p1;
        Point p2 = myInnerShapeCommand.p2;

        int width = BoundsUtility.calcWidth(p1, p2);
        int height = BoundsUtility.calcHeight(p1, p2);

        width += 10;
        height += 10;

        Point TopLeft = BoundsUtility.calcTopLeftCorner(p1,p2);
        //TOP LEFT OFFSET
        TopLeft.x = TopLeft.x-5;
        TopLeft.y = TopLeft.y-5;

        int TrianglePadding = 8;
        Point TriangleBottomRight = new Point(TopLeft.x + width, TopLeft.y + height + TrianglePadding);
        Point TriangleTopLeft = new Point(TopLeft.x - TrianglePadding, TopLeft.y);

        Stroke stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[]{9}, 0);
        myInnerShapeCommand.myGraphics2D.setStroke(stroke);
        myInnerShapeCommand.myGraphics2D.setColor(Color.BLACK);

        if (myInnerShapeCommand instanceof DrawRectangleCommand) {
            //System.out.println("SC is equal its a Rectangle");
            myInnerShapeCommand.myGraphics2D.drawRect(TopLeft.x, TopLeft.y, width, height); //X,Y IS THE TOP LEFT CORNER
        }
        if (myInnerShapeCommand instanceof DrawTriangleCommand) {
            //System.out.println("SC is equal its a Triangle");
            myInnerShapeCommand.myGraphics2D.drawPolygon(BoundsUtility.calcXPoints(TriangleTopLeft,TriangleBottomRight), BoundsUtility.calcYPoints(TriangleTopLeft,TriangleBottomRight), 3);
        }

        if (myInnerShapeCommand instanceof DrawEllipseCommand) {
            //System.out.println("SC is equal its a Ellipse");
            myInnerShapeCommand.myGraphics2D.drawOval(TopLeft.x, TopLeft.y, width, height);
        }


    }
}
