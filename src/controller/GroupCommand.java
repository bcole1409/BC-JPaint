package controller;

import model.BoundsUtility;
import model.CommandHistory;
import model.Point;
import model.ShapeCommand;
import model.interfaces.ICommand;
import model.interfaces.IShape;
import model.interfaces.IUndoable;

import java.awt.*;
import java.util.ArrayList;

public class GroupCommand implements IUndoable, IShape, ICommand{
    public ArrayList<ShapeCommand> groupList;
    public Graphics2D Graphics2D;

    public GroupCommand(ArrayList<ShapeCommand> myGroupList, Graphics2D myGraphics2D){
        this.groupList = myGroupList;
        this.Graphics2D = myGraphics2D;

    }

    @Override
    public void run() {
        CommandHistory.add(this);
        drawMe();
    }

    @Override
    public void drawMe() {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        //find min/max XY
        for(ShapeCommand shape : groupList){
            int[] xPoints = BoundsUtility.calcXPoints(shape.p1, shape.p2); //technically used for calculating triangle xCoord but will work for our algo
            int[] yPoints = BoundsUtility.calcYPoints(shape.p1, shape.p2); //technically used for calculating triangle yCoord but will work for our algo
            for(int i = 0; i < xPoints.length; i++){
                if (xPoints[i] < minX){
                    minX = xPoints[i];
                }
                if (xPoints[i] > maxX){
                    maxX = xPoints[i];
                }
                if (yPoints[i] < minY){
                    minY = yPoints[i];
                }
                if (yPoints[i] > maxY){
                    maxY = yPoints[i];
                }
            }
        }

        //OFFSET
        minX -= 5;
        minY -= 5;
        maxX += 5;
        maxY += 5;

        //draw New Outline
        Stroke stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[]{9}, 0);
        Graphics2D.setStroke(stroke);
        Graphics2D.setColor(Color.BLACK);
        Graphics2D.drawRect(minX, minY, maxX - minX, maxY-minY);
    }

    @Override
    public void undo() {

    }

    @Override
    public void redo() {

    }
}



