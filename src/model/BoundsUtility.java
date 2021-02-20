package model;

public class BoundsUtility  {
    private BoundsUtility() { }

    public static int calcWidth(Point p1, Point p2){
        int width = Math.abs(p2.x-p1.x);
        return width;
    }

    public static int calcHeight(Point p1, Point p2){
        int height = Math.abs(p2.y-p1.y);
        return height;
    }

    public static int calcDeltaX(Point startingPoint, Point endingPoint){
        int deltaX = endingPoint.x - startingPoint.x;
        return deltaX;
    }

    public static int calcDeltaY(Point startingPoint, Point endingPoint){
        int deltaY = endingPoint.y - startingPoint.y;
        return deltaY;
    }

    //p1 p2 should be pressed and released points
    public static Point calcTopLeftCorner(Point p1, Point p2){
        int x = Math.min(p1.x, p2.x);
        int y = Math.min(p1.y, p2.y);
        Point topLeft = new Point(x,y);
        return topLeft;
    }

    public static Point calcBottomRightCorner(Point p1, Point p2){ //UnTested
        Point myTopLeft = calcTopLeftCorner(p1,p2);
        int width = calcWidth(p1,p2);
        int height = calcHeight(p1,p2);
        Point myBottomRight = new Point(myTopLeft.x + width, myTopLeft.y + height);
        return myBottomRight;
    }

    public static int[] calcXPoints(Point p1, Point p2){
        Point TopLeft = BoundsUtility.calcTopLeftCorner(p1,p2);
        int width = BoundsUtility.calcWidth(p1, p2);
        int[] answer = {TopLeft.x, TopLeft.x + width, TopLeft.x + width};
        return answer;
    }

    public static int[] calcYPoints(Point p1, Point p2){
        Point TopLeft = BoundsUtility.calcTopLeftCorner(p1,p2);
        int height = BoundsUtility.calcHeight(p1, p2);
        int[] answer = {TopLeft.y, TopLeft.y, TopLeft.y + height};
        return answer;
    }
}
