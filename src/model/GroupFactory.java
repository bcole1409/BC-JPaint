package model;

import controller.GroupCommand;
import controller.UngroupCommand;

import java.awt.*;
import java.util.ArrayList;

public class GroupFactory  {
    private GroupFactory() { }

    public static GroupCommand getNewGroupCommand(ArrayList<ShapeCommand> myGroupMemberList, Graphics2D myGraphics2D){
        return new GroupCommand(myGroupMemberList,myGraphics2D);
    }

    public static UngroupCommand getNewUngroupCommand(ArrayList<GroupCommand> myGroupsToUngroup){
        return new UngroupCommand(myGroupsToUngroup);
    }
}

