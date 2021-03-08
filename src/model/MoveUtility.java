package model;

import controller.DrawEllipseCommand;
import controller.DrawRectangleCommand;
import controller.DrawTriangleCommand;
import controller.GroupCommand;
import model.interfaces.IShape;
import view.interfaces.PaintCanvasBase;

import java.awt.*;
import java.util.ArrayList;

public class MoveUtility {

    public static ShapeCommand CreateShapeGivenMovement(ArrayList<IShape> drawList, PaintCanvasBase paintCanvas, ShapeCommand mySelectedShape, int deltaX, int deltaY){
        Point origTopLeftCorner = BoundsUtility.calcTopLeftCorner(mySelectedShape.p1, mySelectedShape.p2);
        Point newTopLeftCorner = new Point(origTopLeftCorner.x + deltaX, origTopLeftCorner.y + deltaY);

        int shapeWidth = BoundsUtility.calcWidth(mySelectedShape.p1, mySelectedShape.p2);
        int shapeHeight = BoundsUtility.calcHeight(mySelectedShape.p1, mySelectedShape.p2);

        Point newBottomRightCorner = new Point(newTopLeftCorner.x + shapeWidth, newTopLeftCorner.y + shapeHeight);

        ShapeCommand shapeInNewPosition = null;
        //DrawTriangleCommand mySelectTriangle = (DrawTriangleCommand)mySelectedShape;
        if (mySelectedShape instanceof DrawTriangleCommand) {
            //System.out.println("ITS A TRIANGLE");
            shapeInNewPosition = ShapeFactory.getDrawTriangleCommand(drawList, paintCanvas, newTopLeftCorner, newBottomRightCorner, mySelectedShape.primaryColor,
                    mySelectedShape.secondaryColor, mySelectedShape.sShadingType);
        }
        if (mySelectedShape instanceof DrawRectangleCommand) {
            //System.out.println("ITS A RECTANGLE");
            shapeInNewPosition = ShapeFactory.getDrawRectangleCommand(drawList, paintCanvas, newTopLeftCorner, newBottomRightCorner, mySelectedShape.primaryColor,
                    mySelectedShape.secondaryColor, mySelectedShape.sShadingType);
        }
        if (mySelectedShape instanceof DrawEllipseCommand) {
            //System.out.println("ITS A ELLIPSE");
            shapeInNewPosition = ShapeFactory.getDrawEllipseCommand(drawList, paintCanvas, newTopLeftCorner, newBottomRightCorner, mySelectedShape.primaryColor,
                    mySelectedShape.secondaryColor, mySelectedShape.sShadingType);
        }

        return shapeInNewPosition;
    }

    public static GroupCommand CreateGroupGivenMovement(ArrayList<IShape> drawList, PaintCanvasBase paintCanvas, GroupCommand originalGroup, int deltaX, int deltaY){
        ArrayList<ShapeCommand>  newGroupMemberList = new ArrayList<ShapeCommand>();
        for(ShapeCommand memberShape : originalGroup.groupMemberList){
                ShapeCommand newMovedShape = CreateShapeGivenMovement(drawList,paintCanvas,memberShape, deltaX, deltaY);
                newGroupMemberList.add(newMovedShape);
        }

        GroupCommand newMovedGroupCommand = GroupFactory.getNewGroupCommand(newGroupMemberList, originalGroup.Graphics2D);
        return newMovedGroupCommand;
    }
}
