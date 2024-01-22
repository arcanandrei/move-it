package moveit;

import java.awt.*;

public class Mover implements Runnable {
    private int pixels;
    private int seconds;
    private boolean move;
    private enum Move {
        UP,DOWN,LEFT,RIGHT;
    }

    Mover(){
    }

    Mover(int pixels, int seconds, boolean move){
        this.pixels = pixels;
        this.seconds = seconds;
        this.move = move;
    }

    public int getPixels(){
        return pixels;
    }

    public void setPixels(int pixels){
        this.pixels = pixels;
    }

    public int getSeconds(){
        return seconds;
    }

    public void setSeconds(int seconds){
        this.seconds = seconds;
    }

    public void setMove(boolean move){
        this.move = move;
    }

    public boolean getMove(){
        return move;
    }

    @Override
    public void run() {
        try {
            Robot robot = new Robot();
            Move nextMove = Move.RIGHT;
            Thread.sleep(1000 * seconds);
            while (move) {
                int x = (int) MouseInfo.getPointerInfo().getLocation().getX();
                int y = (int) MouseInfo.getPointerInfo().getLocation().getY();

                //System.out.println("X: " + x + "  Y: " + y);

                switch(nextMove) {
                    case UP:
                        robot.mouseMove(x, y - pixels);
                        nextMove = Move.LEFT;
                        break;
                    case DOWN:
                        robot.mouseMove(x, y + pixels);
                        nextMove = Move.RIGHT;
                        break;
                    case LEFT:
                        robot.mouseMove(x - pixels, y);
                        nextMove = Move.DOWN;
                        break;
                    case RIGHT:
                        robot.mouseMove(x + pixels, y);
                        nextMove = Move.UP;
                        break;
                }

                Thread.sleep(1000 * seconds);
            }
        }catch (InterruptedException e){
            System.out.println("Thread is interrupted");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
