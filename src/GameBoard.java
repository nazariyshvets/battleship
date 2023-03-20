import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.HashSet;
import java.util.Collections;
import java.awt.Point;

public class GameBoard {
    private static ArrayList<Ship> ships = new ArrayList<>();
    private static int userAttemptsCount = 0;
    private static final int SIZE = 10;
    private static char[][] grid = new char[SIZE][SIZE];
    private static HashSet<Point> occupiedCells = new HashSet<>();
    private static final String COLUMN_HEADER = "   A B C D E F G H I J";
    private static final String ROW_FORMAT = "%-3s";
    
    public static void main(String[] args) {
        setUpGame();
        
        while(true) {
            printGrid();
            
            Point userGuessCell = parseUserInput(getUserInput("Your guess"));
            char charAtUserGuessCell = grid[userGuessCell.x][userGuessCell.y];
            
            if(charAtUserGuessCell == Placeholder.MISS.getPlaceholder() || charAtUserGuessCell == Placeholder.HIT.getPlaceholder()) {
                System.out.println("You've already tried this one!");
                continue;
            } else if(charAtUserGuessCell == Placeholder.OCCUPIED.getPlaceholder()) {
                System.out.println("You cannot choose this cell!");
                continue;
            }
            
            boolean shipIsHit = false;
            
            for(Ship ship : ships) {
                if(ship.contains(userGuessCell)) {
                    System.out.println("You hit a ship. Keep it up!");
                    grid[userGuessCell.x][userGuessCell.y] = Placeholder.HIT.getPlaceholder();
                    shipIsHit = true;
                    ship.deleteCell(userGuessCell);
                    if(ship.isSunk()) {
                        addOccupiedCells(ship.getInitialCells(), true);
                        System.out.println("You sunk a " + ship.getNumOfDecks() + "-deck ship!");
                    }
                    break;
                }
            }
            
            if(!shipIsHit) {
                grid[userGuessCell.x][userGuessCell.y] = Placeholder.MISS.getPlaceholder();
                System.out.println("You missed but that's ok, try again!");
            }
            
            userAttemptsCount++;
            
            if(allShipsAreSunk()) {
                System.out.println("You sunk all the ships and did it in " + userAttemptsCount + " attempts!");
                break;
            }
        }
    }
    
    private static void setUpGame() {
        initializeGrid();
        placeShipsRandomly();
    }
    
    private static void initializeGrid() {
        for(int i = 0; i < SIZE; i++)
            for(int j = 0; j < SIZE; j++) {
                grid[i][j] = Placeholder.DEFAULT.getPlaceholder();
            }
    }

    private static void printGrid() {
        StringBuilder sb = new StringBuilder();
        sb.append(COLUMN_HEADER).append(System.lineSeparator());
        for(int row = 0; row < SIZE; row++) {
            sb.append(String.format(ROW_FORMAT, row + 1));
            for(int column = 0; column < SIZE; column++) {
                sb.append(grid[row][column]).append(" ");
            }
            sb.append(System.lineSeparator());
        }
        System.out.print(sb.toString());
    }
    
    private static void placeShipsRandomly() {
        placeShipRandomly(5);
        placeShipRandomly(4);
        placeShipRandomly(4);
        placeShipRandomly(3);
        placeShipRandomly(3);
        placeShipRandomly(3);
        placeShipRandomly(2);
        placeShipRandomly(2);
        placeShipRandomly(2);
        placeShipRandomly(2);
    }
    
    private static boolean placeShipRandomly(int numOfDecks) {
        int maxAttemptsPerOrientation = 100;
        int attempts = 0;
        Random rand = new Random();
        boolean isVertical = rand.nextBoolean();
        while (attempts < maxAttemptsPerOrientation) {
            Point startCell = new Point(rand.nextInt(SIZE), rand.nextInt(SIZE));
            if (isVertical) {
                if (createVerticalShip(startCell, numOfDecks)) {
                    return true;
                }
            } else {
                if (createHorizontalShip(startCell, numOfDecks)) {
                    return true;
                }
            }
            attempts++;
        }
        isVertical = !isVertical;
        attempts = 0;
        while (attempts < maxAttemptsPerOrientation) {
            Point startCell = new Point(rand.nextInt(SIZE), rand.nextInt(SIZE));
            if (isVertical) {
                if (createVerticalShip(startCell, numOfDecks)) {
                    return true;
                }
            } else {
                if (createHorizontalShip(startCell, numOfDecks)) {
                    return true;
                }
            }
            attempts++;
        }
        return false;
    }

    private static boolean createVerticalShip(Point startCell, int numOfDecks) {
        ArrayList<Point> shipCells = new ArrayList();
        boolean placed = placeShip(startCell, "up", numOfDecks, shipCells);
        if(!placed) {
            placed = placeShip(new Point(startCell.x + 1, startCell.y), "down", numOfDecks, shipCells);
        }
        if(placed) {
            sortShipCells(shipCells ,"row");
            ships.add(new Ship(shipCells));
            addOccupiedCells(shipCells, false);
            return true;
        } else {
            return false;
        }
    }
    
    private static boolean createHorizontalShip(Point startCell, int numOfDecks) {
        ArrayList<Point> shipCells = new ArrayList();
        boolean placed = placeShip(startCell, "left", numOfDecks, shipCells);
        if (!placed) {
            placed = placeShip(new Point(startCell.x, startCell.y + 1), "right", numOfDecks, shipCells);
        }

        if (placed) {
            sortShipCells(shipCells, "column");
            ships.add(new Ship(shipCells));
            addOccupiedCells(shipCells, false);
            return true;
        } else {
            return false;
        }
    }
    
    private static boolean placeShip(Point startCell, String direction, int numOfDecks, ArrayList<Point> shipCells) {
        Point tempCell = new Point(startCell);
        boolean foundAvailableCell = cellIsAvailable(tempCell);

        while (shipCells.size() != numOfDecks && foundAvailableCell) {
            shipCells.add(new Point(tempCell));

            switch (direction) {
                case "up" -> tempCell.x--;
                case "down" -> tempCell.x++;
                case "left" -> tempCell.y--;
                case "right" -> tempCell.y++;
            }

            foundAvailableCell = cellIsAvailable(tempCell);
        }

        return shipCells.size() == numOfDecks;
    }
    
    private static boolean cellIsAvailable(Point cell) {
        int row = cell.x;
        int column = cell.y;
        
        return !(row < 0 || row >= SIZE || column < 0 || column >= SIZE || occupiedCells.contains(cell));
    }
    
    private static void addOccupiedCells(ArrayList<Point> shipCells, boolean isForGrid) {
        Point firstCell = shipCells.get(0);
        Point lastCell = shipCells.get(shipCells.size() - 1);
        
        for(int row = firstCell.x - 1; row <= lastCell.x + 1; row++)
            for(int column = firstCell.y - 1; column <= lastCell.y + 1; column++) {
                if(row >= 0 && row < SIZE && column >= 0 && column < SIZE) {
                    if(isForGrid) {
                        addOccupiedCellToGrid(shipCells, row, column);
                    } else {
                        addOccupiedCellToSet(row, column);
                    }
                }
            }
    }
    
    private static void addOccupiedCellToGrid(ArrayList<Point> shipCells, int row, int column) {
        if(!shipCells.contains(new Point(row, column))) {
            grid[row][column] = Placeholder.OCCUPIED.getPlaceholder();
        }
    }
    
    private static void addOccupiedCellToSet(int row, int column) {
        if(!occupiedCells.contains(new Point(row, column))) {
            occupiedCells.add(new Point(row, column));
        }
    }
    
    private static void sortShipCells(ArrayList<Point> shipCells, String sort) {
        Collections.sort(shipCells, (Point cell1, Point cell2) -> {
            if(sort.equals("row")) return cell1.x - cell2.x;
            else return cell1.y - cell2.y;
        });
    }
    
    private static boolean allShipsAreSunk() {
        for(Ship ship : ships) {
            if(!ship.isSunk()) return false;
        }
        return true;
    }
    
    private static String getUserInput(String message) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder allowedChars = new StringBuilder();
        String userInput;
        String pattern;
        for(char c = 'a'; c < 'a' + SIZE; c++) {
            allowedChars.append(c);
        }
        pattern = String.format("^[%s]([1-9]|10)$", allowedChars);
        while(true) {
            System.out.print(message + ": ");
            userInput = scanner.nextLine().toLowerCase();
            if(userInput.matches(pattern)) {
                return userInput;
            }
            System.out.println("Invalid input! It must be, for instance, 'a7'");
        }
    }
    
    private static Point parseUserInput(String input) {
        int row = Integer.parseInt(input.substring(1)) - 1;
        int column = input.charAt(0) - 'a';
        return new Point(row, column);
    }
}
