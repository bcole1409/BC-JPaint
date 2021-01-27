package model;

public class Point {
    public int x;
    public int y;

    public Point(int myX, int myY){
        this.x = myX;
        this.y = myY;
    }

    public int calcWidth(Point p1, Point p2){
        int width = p2.x-p1.x;
        return width;
    }

    public int calcHeight(Point p1, Point p2){
        int height = p2.y-p1.y;
        return height;
    }
}
