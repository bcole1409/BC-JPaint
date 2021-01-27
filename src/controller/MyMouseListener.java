package controller;

import model.Point;
import model.interfaces.IApplicationState;
import view.interfaces.IUiModule;
import view.interfaces.PaintCanvasBase;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MyMouseListener implements MouseListener {
        private IJPaintController controller;
        private Point mousePressedPoint;
        private Point mouseReleasedPoint;

        public MyMouseListener(IJPaintController myController) {
                this.controller = myController;
        }

        public void mousePressed(MouseEvent e) {
                System.out.println("mouse pressed: " + e.getX() + " " + e.getY());
                mousePressedPoint = new Point(e.getX(),e.getY());
        }

        public void mouseReleased(MouseEvent e) {
                System.out.println("mouse released: " + e.getX() + " " + e.getY());
                mouseReleasedPoint = new Point(e.getX(),e.getY());
                controller.mouseReleased(mousePressedPoint, mouseReleasedPoint);
        }

        public void mouseEntered(MouseEvent e) {

                saySomething("Mouse entered", e);
        }

        public void mouseExited(MouseEvent e) {
                saySomething("Mouse exited", e);
        }

        public void mouseClicked(MouseEvent e) {
                saySomething("Mouse clicked (# of clicks: "
                + e.getClickCount() + ")", e);
        }

        void saySomething(String eventDescription, MouseEvent e) {
                System.out.println(eventDescription + " detected on "
                + e.getComponent().getClass().getName()
                + ".");

        //textArea.append(eventDescription + " detected on "
        //+ e.getComponent().getClass().getName()
        //+ "." + newline);
        }

}
