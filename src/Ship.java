import java.awt.Point;
import java.util.ArrayList;

public class Ship {
    private final int numOfDecks;
    private ArrayList<Point> cells;
    private final ArrayList<Point> initialCells;
    
    public Ship(ArrayList<Point> cells) {
        this.cells = new ArrayList<>(cells);
        numOfDecks = cells.size();
        initialCells = new ArrayList<>(cells);
    }
    
    public int getNumOfDecks() {
        return numOfDecks;
    }
    
    public ArrayList<Point> getInitialCells() {
        return initialCells;
    }
    
    public boolean isSunk() {
        return cells.isEmpty();
    }
    
    public boolean contains(Point cell) {
        return cells.contains(cell);
    }
    
    public void deleteCell(Point cell) {
        cells.remove(cell);
    }
}
