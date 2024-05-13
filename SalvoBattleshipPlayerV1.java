import lib.Pair;

import java.util.*;

public class SalvoBattleshipPlayerV1 extends BattleshipPlayer {
    @SuppressWarnings("FieldCanBeLocal")
    private final boolean debug = true;

    private Mode mode = Mode.HUNTING;
    private final HashMap<Location, Square> oppBoard = new HashMap<>();

    // Hunting
    private final ArrayList<Integer> boatSizesLeft = new ArrayList<>();

    // Boat Direction
    private BoatDirection boatDirection;
    private ArrayList<BoatDirection> attemptedDirections = new ArrayList<>();

    private Location firstHitOfBoat = new Location(0);
    private Location lastHit = new Location(0);
    private boolean lastMoveWasHit = false;

    @Override
    public int getMove() {
        return switch (mode) {
            case HUNTING -> new ProbabilityMatrix(oppBoard, boatSizesLeft).nextMove().getAsIndex();
            case BOAT_DIRECTION -> {
                for (BoatDirection direction : BoatDirection.values()) {
                    if (!attemptedDirections.contains(direction)) {
                        attemptedDirections.add(direction);

                        Location nextMove = direction.of(lastHit);
                        if (nextMove == null) {
                            yield getMove();
                        } else {
                            yield nextMove.getAsIndex();
                        }
                    }
                }

                // This should never be reached.
                yield 0;
            }
            case BOAT_DESTRUCTION -> {
                if (lastMoveWasHit) {
                    Location location = boatDirection.of(lastHit);

                    //noinspection DataFlowIssue
                    if (location.isValid()) yield location.getAsIndex();
                }
                boatDirection = boatDirection.opposite();
                //noinspection DataFlowIssue
                yield boatDirection.of(firstHitOfBoat).getAsIndex();
            }
        };
    }

    @Override
    public void response(int location, boolean hit, int sinkLength) {
        // debug("Location: %s, Hit: %s, Sunk ship: %s", new Location(location), hit, sinkLength != -1);

        debug("");

        if (!lastMoveWasHit && hit) firstHitOfBoat = new Location(location);
        lastMoveWasHit = hit;
        if (hit) lastHit = new Location(location);
        if (sinkLength != -1) {
            mode = Mode.HUNTING;
            firstHitOfBoat = null;
            return;
        }
        oppBoard.put(new Location(location), hit ? Square.HIT : Square.MISS);

        if (hit) {
            switch (mode) {
                case HUNTING -> mode = Mode.BOAT_DIRECTION;
                case BOAT_DIRECTION -> {
                    boatDirection = attemptedDirections.get(attemptedDirections.size() - 1);
                    attemptedDirections = new ArrayList<>();
                    mode = Mode.BOAT_DESTRUCTION;
                }
            }
        }

        super.response(location, hit, sinkLength);
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

        return super.getShips(sizes);
    }

    public enum Mode {
        HUNTING,
        BOAT_DIRECTION,
        BOAT_DESTRUCTION
    }

    public enum BoatDirection {
        NORTH,
        EAST,
        SOUTH,
        WEST;

        public BoatDirection opposite() {
            return switch (this) {
                case NORTH -> SOUTH;
                case EAST -> WEST;
                case SOUTH -> NORTH;
                case WEST -> EAST;
            };
        }

        public Location of(Location location) {
            Location newLocation = switch (this) {
                case NORTH -> new Location(location.letter - 1, location.number);
                case EAST -> new Location(location.letter, location.number + 1);
                case SOUTH -> new Location(location.letter + 1, location.number);
                case WEST -> new Location(location.letter, location.number - 1);
            };

            System.out.println(this + " " + location + " " + newLocation);

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

            for (Map.Entry<Location, Square> entry : oppBoard.entrySet()) {
                if (!entry.getValue().equals(Square.UNKNOWN)) matrix[entry.getKey().letter][entry.getKey().number] = null;
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
        UNKNOWN,
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

    private void debug(String format, Object... args) {
        if (debug) {
            System.out.printf("(Mode: %s): %s%n", mode, String.format(format, args));
        }
    }
}
