package path_finding;

import java.io.Serializable;

/**
 * @author thanhLe1547
 */
public class Map implements Serializable {
    private static final long serialVersionUID = 6181512265542053592L;

    private int cells = 20;
    private double dense = .5;
    private int capacity = cells * cells;
    private double density = capacity * .5;
    private int startx = -1;
    private int starty = -1;
    private int finishx = -1;
    private int finishy = -1;
    private Node[][] map;

    public int getCells() {
        return cells;
    }

    public void setCells(int cells) {
        this.cells = cells;
        this.capacity = cells * cells;
    }

    public double getDense() {
        return dense;
    }

    public void setDense(double dense) {
        this.dense = dense;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }
    
    public void updateDensity() {
        this.density = capacity * dense;
    }

    public int getStartX() {
        return startx;
    }

    public void setStartX(int startx) {
        this.startx = startx;
    }

    public int getStartY() {
        return starty;
    }

    public void setStartY(int starty) {
        this.starty = starty;
    }

    public int getFinishX() {
        return finishx;
    }

    public void setFinishX(int finishx) {
        this.finishx = finishx;
    }

    public int getFinishY() {
        return finishy;
    }

    public void setFinishY(int finishy) {
        this.finishy = finishy;
    }

    public Node[][] getMap() {
        return map;
    }

    public void setMap(Node[][] map) {
        this.map = map;
    }
    
    public void setMap(Map m) {
        this.cells = m.getCells();
        this.dense = m.getDense();
        this.capacity = m.getCapacity();
        this.density = m.getDensity();
        this.startx = m.getStartX();
        this.starty = m.getStartY();
        this.finishx = m.getFinishX();
        this.finishy = m.getFinishY();
        this.map = m.getMap();
    }

    public void setNewMap() {
        this.map = new Node[cells][cells];
    }

    public Node getStartNode() {
        return map[startx][starty];
    }
    
    public Node getFinishNode() {
        return map[finishx][finishy];
    }

    public boolean hasStartNode() {
        return startx > -1 && starty > -1;
    }

    public boolean hasFinishNode() {
        return finishx > -1 && finishy > -1;
    }
}