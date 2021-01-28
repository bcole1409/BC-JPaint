package controller;

import java.awt.*;
import java.util.ArrayList;

import model.*;
import model.interfaces.IUndoable;
import view.interfaces.PaintCanvasBase;
import model.Point;

public class DrawRectangleCommand implements IUndoable {
    ArrayList<IUndoable> drawList;
    Graphics2D myGraphics2D;
    Point p1,p2;
    ShapeColor currentColor;
    ShapeShadingType sShadingType;


    public DrawRectangleCommand(ArrayList<IUndoable> myDrawList, PaintCanvasBase base, Point p1,
                                Point p2, ShapeColor myCurrentColor, ShapeShadingType myShadingType) {
        this.drawList = myDrawList;
        this.myGraphics2D = base.getGraphics2D();
        this.p1 = p1;
        this.p2 = p2;
        currentColor = myCurrentColor;
        sShadingType = myShadingType;

        drawMe();
    }

    @Override
    public void redo() { //Add yourself to the drawlist
        drawList.add(this);
    }

    @Override
    public void drawMe() {
        int width = calcWidth(p1, p2);
        int height = calcHeight(p1, p2);

        Point TopLeft = calcTopLeftCorner(p1,p2);
        setActiveColor(myGraphics2D);

        if(sShadingType == ShapeShadingType.FILLED_IN){
            Rectangle myRectangle = new Rectangle(TopLeft.x, TopLeft.y, width, height);
            myGraphics2D.fill(myRectangle);
        }
    }

    @Override
    public void undo() {
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

    private void setActiveColor(Graphics2D myGraphics2d) {
        switch (currentColor) {
            case BLACK:
                myGraphics2d.setColor(Color.BLACK);
                break;
            case BLUE:
                myGraphics2d.setColor(Color.BLUE);
                break;
            case CYAN:
                myGraphics2d.setColor(Color.CYAN);
                break;
            case DARK_GRAY:
                myGraphics2d.setColor(Color.DARK_GRAY);
                break;
            case GRAY:
                myGraphics2d.setColor(Color.GRAY);
                break;
            case GREEN:
                myGraphics2d.setColor(Color.GREEN);
                break;
            case LIGHT_GRAY:
                myGraphics2d.setColor(Color.LIGHT_GRAY);
                break;
            case MAGENTA:
                myGraphics2d.setColor(Color.MAGENTA);
                break;
            case ORANGE:
                myGraphics2d.setColor(Color.ORANGE);
                break;
            case PINK:
                myGraphics2d.setColor(Color.PINK);
                break;
            case RED:
                myGraphics2d.setColor(Color.RED);
                break;
            case WHITE:
                myGraphics2d.setColor(Color.WHITE);
                break;
            case YELLOW:
                myGraphics2d.setColor(Color.YELLOW);
                break;
        }
    }
}