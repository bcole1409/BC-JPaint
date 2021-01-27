package controller;

import java.awt.*;

import view.interfaces.PaintCanvasBase;
import model.Point;

public class DrawRectangleCommand implements DrawCommand{
    Graphics2D ctx;
    Point p1,p2;
    Color color;

    public DrawRectangleCommand(PaintCanvasBase base, Point p1, Point p2, Color color){
        this.ctx = base.getGraphics2D();
        this.color = ctx.getColor();
        this.p1 = p1;
        this.p2 = p2;
        int width = p1.calcWidth(p1, p2);
        int height = p1.calcHeight(p1, p2);


        //setActiveColor(ctx);
        ctx.fillRect(p1.x, p1.y, width, height);

    }

    @Override
    public void redo() {

    }

    @Override
    public void undo() {

    }
}
