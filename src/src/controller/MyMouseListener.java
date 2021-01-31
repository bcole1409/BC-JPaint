package controller;

import model.Point;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

//Special mouse functions that get automatically called by the java programming language when
//mouse events happen
public class MyMouseListener implements MouseListener {
        private IJPaintController controller; //a reference to JPaint controller a.k.a "the chef" of the program
        private Point mousePressedPoint;
        private Point mouseReleasedPoint;

        public MyMouseListener(IJPaintController myController) {
                this.controller = myController;
        }

        public void mousePressed(MouseEvent e) {
                //System.out.println("mouse pressed: " + e.getX() + " " + e.getY());
                mousePressedPoint = new Point(e.getX(),e.getY()); //save the point that the mouse pressed
        }

        public void mouseReleased(MouseEvent e) {
                //System.out.println("mouse released: " + e.getX() + " " + e.getY());
                mouseReleasedPoint = new Point(e.getX(),e.getY()); //save the point that the mouse released
                controller.mouseReleasedController(mousePressedPoint, mouseReleasedPoint); //"The chef" receiving the information from
                //"the waiter" on the mouse is released.
        }

        //unused
        public void mouseEntered(MouseEvent e) {
                //saySomething("Mouse entered", e);
        }

        public void mouseExited(MouseEvent e) {
                saySomething("Mouse exited", e);
        }

        public void mouseClicked(MouseEvent e) {
                //saySomething("Mouse clicked (# of clicks: "
                //+ e.getClickCount() + ")", e);
        }

        void saySomething(String eventDescription, MouseEvent e) {
                //System.out.println(eventDescription + " detected on "
                //+ e.getComponent().getClass().getName()
                //+ ".");

        //textArea.append(eventDescription + " detected on "
        //+ e.getComponent().getClass().getName()
        //+ "." + newline);
        }

}
