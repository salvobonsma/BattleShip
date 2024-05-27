import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class SalvoBattleshipPlayerV3 extends BattleshipPlayer {
    @SuppressWarnings("FieldCanBeLocal")
    private boolean debug = false;

    private static ArrayList<HashMap<Location, Square>> historicalBoard = new ArrayList<>();
    private static int repeatCount = 0;
    private static int gameIndex = -1;

    private Mode mode = Mode.HUNTING;
    private final HashMap<Location, Square> oppBoard = new HashMap<>();

    // Hunting
    private final ArrayList<Integer> boatSizesLeft = new ArrayList<>();

    // Boat Direction
    private BoatDirection boatDirection;
    private boolean alreadyFlipped = true;
    private ArrayList<BoatDirection> attemptedDirections = new ArrayList<>();

    private Location firstHitOfBoat = new Location(0);
    private Location lastHit = new Location(0);
    private boolean lastMoveWasHit = false;

    public SalvoBattleshipPlayerV3() {
        gameIndex++;

        historicalBoard.add(new HashMap<>());
//
//        if (gameIndex < 2) return;
//
//        for (Map.Entry<Location, Square> entryA : historicalBoard.get(gameIndex - 1).entrySet()) {
//            Square entryB = historicalBoard.get(gameIndex - 2).get(entryA.getKey());
//
//            if (entryB == null) break;
//            if (!entryA.getValue().equals(entryB)) return;
//        }
//
//        mode = Mode.STATIC_BOATS;
//        System.out.println("true");
    }

    public SalvoBattleshipPlayerV3(boolean debug) {
        this();
        this.debug = debug;
    }

    @Override
    public int getMove() {

        debug(gameIndex + "");
        switch (mode) {
            case STATIC_BOATS:
                return 0;
            case HUNTING:
                return new ProbabilityMatrix(oppBoard, boatSizesLeft).nextMove().getAsIndex();
            case BOAT_DIRECTION:
                alreadyFlipped = false;

                debug("A" + attemptedDirections);

                for (BoatDirection direction : BoatDirection.values()) {
                    if (!attemptedDirections.contains(direction)) {
                        attemptedDirections.add(direction);

                        Location nextMove = direction.of(lastHit);
                        if (nextMove == null) {
                            return getMove();
                        } else {
                            return nextMove.getAsIndex();
                        }
                    }
                }

                debug("B" + attemptedDirections);

                // This should never be reached.
                break;
            case BOAT_DESTRUCTION:
                if (lastMoveWasHit) {
                    Location location = boatDirection.of(lastHit);

                    if (location != null && oppBoard.get(location) == null) return location.getAsIndex();
                }
                debug("already flipped" + alreadyFlipped);
                if (alreadyFlipped) {
                    mode = Mode.BOAT_DIRECTION;
                    return getMove();
                }

                alreadyFlipped = true;

                boatDirection = boatDirection.opposite();
                //noinspection DataFlowIssue
                return boatDirection.of(firstHitOfBoat).getAsIndex();
        }

        // Worst case, go random.
        debug("Going random!");
        return random(new Location((int) (Math.random() * 100))).getAsIndex();
    }

    @Override
    public void response(int location, boolean hit, int sinkLength) {
        // debug("Location: %s, Hit: %s, Sunk ship: %s", new Location(location), hit, sinkLength != -1);
        lastMoveWasHit = hit;
        if (hit) lastHit = new Location(location);
        oppBoard.put(new Location(location), hit ? Square.HIT : Square.MISS);

        historicalBoard.set(gameIndex, oppBoard);

        if (hit) {
            switch (mode) {
                case HUNTING:
                    mode = Mode.BOAT_DIRECTION;
                    firstHitOfBoat = new Location(location);
                    debug("first hit of boat" + firstHitOfBoat);

                    debug("should be nothing" + attemptedDirections);
                    for (BoatDirection direction : BoatDirection.values()) {
                        Square square = oppBoard.get(direction.of(firstHitOfBoat));
                        if (square == null) continue;
                        if (square.equals(Square.HIT) || square.equals(Square.MISS))
                            attemptedDirections.add(direction);

                        debug(direction.toString());
                    }
                    break;
                case BOAT_DIRECTION:
                    boatDirection = attemptedDirections.get(attemptedDirections.size() - 1);
                    attemptedDirections = new ArrayList<>();
                    mode = Mode.BOAT_DESTRUCTION;
                    break;
            }
        } else {
            if (mode == Mode.STATIC_BOATS) mode = Mode.HUNTING;
        }

        if (sinkLength != -1) {
            debug("Boat of length %d down!", sinkLength);
            mode = Mode.HUNTING;
            lastMoveWasHit = false;
            firstHitOfBoat = null;
        }
    }

    @Override
    public void enemyAttack(int location) {
        // debug("Opponent played: %s", new Location(location).toString());
    }

    @Override
    public ArrayList<Boat> getShips(int[] sizes) {
        for (int size : sizes) {
            boatSizesLeft.add(size);
        }

        ArrayList<Boat> boats = new ArrayList<>();

        switch (gameIndex % 2) {
            case 0:
                boats.add(new Boat(38, 39));
                boats.add(new Boat(9, 19, 29));
                boats.add(new Boat(7, 17, 27));
                boats.add(new Boat(67, 77, 87, 97));
                boats.add(new Boat(59, 69, 79, 89, 99));
                break;
            case 1:
                boats.add(new Boat(6, 16));
                boats.add(new Boat(8, 18, 28));
                boats.add(new Boat(49, 48, 47));
                boats.add(new Boat(66, 76, 86, 96));
                boats.add(new Boat(58, 68, 78, 88, 98));
                break;
        }

        return boats;
    }

    private Location random(Location location) {
        if (oppBoard.get(location) == null) return location;

        return new Location((int) (Math.random() * 100));
    }

    public enum Mode {
        STATIC_BOATS,
        HUNTING,
        BOAT_DIRECTION,
        BOAT_DESTRUCTION;
    }

    public enum BoatDirection {
        NORTH,
        EAST,
        SOUTH,
        WEST;

        public BoatDirection opposite() {
            switch (this) {
                case NORTH: return SOUTH;
                case EAST: return WEST;
                case SOUTH: return NORTH;
                case WEST: return EAST;
            };

            return null;
        }

        public Location of(Location location) {
            Location newLocation = new Location(0);
            switch (this) {
                case NORTH: newLocation = new Location(location.letter - 1, location.number); break;
                case EAST: newLocation = new Location(location.letter, location.number + 1); break;
                case SOUTH: newLocation = new Location(location.letter + 1, location.number); break;
                case WEST: newLocation = new Location(location.letter, location.number - 1); break;
            };

            if (!newLocation.isValid()) return null;
            return newLocation;
        }
    }

    public static class ProbabilityMatrix {
        private final Integer[][] matrix = new Integer[10][10];

        public ProbabilityMatrix(HashMap<Location, Square> oppBoard, ArrayList<Integer> boatSizesLeft) {
            for (int x = 0; x < matrix.length; x++) {
                for (int y = 0; y < matrix[0].length; y++) {
                    matrix[y][x] = 0;
                }
            }

            for (int boatLength : boatSizesLeft) {
                horizontalBoatToMatrix(boatLength);
                verticalBoatToMatrix(boatLength);
            }
        }

        private void horizontalBoatToMatrix(int length) {
            for (int y = 0; y < matrix.length; y++) {
                for (int x = 0; x < matrix[0].length - length + 1; x++) {
                    boolean validPlacement = true;

                    for (int i = 0; i < length; i++) {
                        if (matrix[y][x + i] == null) {
                            validPlacement = false;
                            break;
                        }
                    }

                    if (validPlacement) {
                        for (int i = 0; i < length; i++) {
                            matrix[y][x + i]++;
                        }
                    }
                }
            }
        }

        private void verticalBoatToMatrix(int length) {
            for (int x = 0; x < matrix[0].length; x++) {
                for (int y = 0; y < matrix.length - length + 1; y++) {
                    boolean validPlacement = true;

                    for (int i = 0; i < length; i++) {
                        if (matrix[y + i][x] == null) {
                            validPlacement = false;
                            break;
                        }
                    }

                    if (validPlacement) {
                        for (int i = 0; i < length; i++) {
                            matrix[y + i][x]++;
                        }
                    }
                }
            }
        }

        public Location nextMove() {
            ArrayList<Pair<Location, Integer>> locations = new ArrayList<>();
            for (int y = 0; y < matrix.length; y++) {
                for (int x = 0; x < matrix[0].length; x++) {
                    if (matrix[y][x] == null) continue;
                    locations.add(Pair.of(new Location(y, x), matrix[y][x]));
                }
            }

            locations.sort(
                    Comparator
                            .comparing((Pair<Location, Integer> pair) -> -pair.getSecond())
                            .thenComparingDouble(p -> p.getFirst().distanceToCenter())
            );

            return new Location(locations.get(0).getFirst().getAsIndex());
        }
    }

    public enum Square {
        MISS,
        HIT;

        @Override
        public String toString() {
            return name();
        }
    }

    public static class Location {
        private final int letter;
        private final int number;

        public Location(Letter letter, int number) {
            this.letter = letter.getI();
            this.number = number;
        }

        public Location(int letter, int number) {
            this.letter = letter;
            this.number = number;
        }

        public Location(int index) {
            this.letter = index % 10;
            this.number = index / 10;
        }

        public double distanceToCenter() {
            return Math.sqrt(Math.pow(letter - 4, 2) + Math.pow(number - 4, 2));
        }

        public boolean isValid() {
            return letter >= 0 && letter < 10 && number >= 0 && number < 10;
        }

        public int getAsIndex() {
            return (number * 10) + letter;
        }

        @Override
        public String toString() {
            return String.format("(%s, %d)", letter, number);
        }
    }

    public enum Letter {
        A(0), B(1), C(2), D(3), E(4), F(5), G(6), H(7), I(8), J(9);

        private final int i;

        Letter(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }

        @Override
        public String toString() {
            return name();
        }
    }

    public static class Pair<A, B> {
        private final A m_first;
        private final B m_second;

        public Pair(A first, B second) {
            m_first = first;
            m_second = second;
        }

        public static <A, B> Pair<A, B> of(A a, B b) {
            return new Pair<>(a, b);
        }

        public A getFirst() {
            return m_first;
        }

        public B getSecond() {
            return m_second;
        }

        @Override
        public String toString() {
            return "(" + getFirst() + ", " + getSecond() + ")";
        }
    }

    private void debug(String format, Object... args) {
        if (debug) {
            System.out.printf("(Mode: %s): %s%n", mode, String.format(format, args));
        }
    }
}
