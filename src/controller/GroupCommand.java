package controller;

import model.BoundsUtility;
import model.CommandHistory;
import model.ShapeCommand;
import model.interfaces.ICommand;
import model.interfaces.IShape;
import model.interfaces.IUndoable;
import java.awt.*;
import java.util.ArrayList;

public class GroupCommand implements IUndoable, IShape, ICommand{
    public ArrayList<ShapeCommand> groupMemberList;
    public Graphics2D Graphics2D;
    private Point topLeftPoint;
    private Point bottomRightPoint;

    private int minX = -1;
    private int maxX = -1;
    private int minY = -1;
    private int maxY = -1;

    public GroupCommand(ArrayList<ShapeCommand> myGroupMemberList, Graphics2D myGraphics2D){
        this.groupMemberList = myGroupMemberList;
        this.Graphics2D = myGraphics2D;
        System.out.println("GroupCommand Created size is " + this.groupMemberList.size());
    }

    @Override
    public void run() {
        redo();
        CommandHistory.add(this);
    }

    @Override
    public void drawMe() {
        if(minX == -1 && maxX == -1 && minY == -1 && maxY == -1){
            minX = Integer.MAX_VALUE;
            minY = Integer.MAX_VALUE;
            maxX = Integer.MIN_VALUE;
            maxY = Integer.MIN_VALUE;

            //find min/max XY
            for(ShapeCommand shape : groupMemberList){
                int[] xPoints = BoundsUtility.calcXPoints(shape.p1, shape.p2); //technically used for calculating triangle xCoord but will work for algo
                int[] yPoints = BoundsUtility.calcYPoints(shape.p1, shape.p2); //technically used for calculating triangle yCoord but will work for algo

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
        }

        //used in didCollideWithMe
        topLeftPoint = new Point(minX,minY);
        bottomRightPoint = new Point(maxX,maxY);

        //draw New Outline
        Stroke stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1, new float[]{9}, 0);
        Graphics2D.setStroke(stroke);
        Graphics2D.setColor(Color.BLACK);
        Graphics2D.drawRect(minX, minY, maxX - minX, maxY-minY);
    }

    @Override
    public void redo() {
        drawMe();
        JPaintController.listOfGroups.add(this);
    }

    @Override
    public void undo() {
        ArrayList<GroupCommand> tempListOfGroups =  new ArrayList<GroupCommand>();
        for(GroupCommand myGroup : JPaintController.listOfGroups){
            if(!myGroup.isEqual(this)){
                tempListOfGroups.add(myGroup);
                //JPaintController.listOfGroups.remove(myGroup); //bug does not work after movement
            }
        }
        JPaintController.listOfGroups = tempListOfGroups;
        //System.out.println("GroupCommand.undo LOG size " + JPaintController.listOfGroups.size());
    }

    public boolean didCollideWithMe(int x, int y){
        if(x <= bottomRightPoint.x && x >= topLeftPoint.x){
            if(y <= bottomRightPoint.y && y >= topLeftPoint.y){
                return true;
            }
        }
        return false;
    }

    public boolean containsAtLeastOneShape(ArrayList<ShapeCommand> testShapes){
        for(ShapeCommand testShape : testShapes){
            for(ShapeCommand memberShape : groupMemberList){
                if(testShape.SCIsEqual(memberShape)) return true;
            }
        }
        return false;
    }

    public boolean isEqual(GroupCommand otherGroup){
        //check groupmemberlist.size() == otherGroup.size()
        //&& every element of A is in B & every element of B is in A
        if(groupMemberList.size() == otherGroup.groupMemberList.size()){
            boolean otherContainsThisGroup = true;
            for(ShapeCommand myShape : groupMemberList){
                ArrayList<ShapeCommand> wrapperList = new ArrayList<ShapeCommand>();
                wrapperList.add(myShape);
                if(!otherGroup.containsAtLeastOneShape(wrapperList)){
                    return false;
                }
            }

            for(ShapeCommand myOtherShape : otherGroup.groupMemberList){
                ArrayList<ShapeCommand> wrapperList = new ArrayList<ShapeCommand>();
                wrapperList.add(myOtherShape);
                if(!this.containsAtLeastOneShape(wrapperList)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}



