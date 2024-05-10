import java.util.HashMap;

public class SalvoBattleshipPlayerV1 extends BattleshipPlayer {
    private final boolean debug = true;

    // Opponent data
    private OppBoard board = new OppBoard();


    public SalvoBattleshipPlayerV1() {
        System.out.println(board.getBoard());
    }

    public class OppBoard {
        private final HashMap<Location, Square> board = new HashMap<>();

        public OppBoard() {
            for (int letter = 0; letter < 10; letter++) {
                for (int number = 0; number < 10; number++) {
                    board.put(new Location(letter, number), Square.UNKNOWN);
                }
            }
        }

        public HashMap<Location, Square> getBoard() {
            return board;
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

        public class Location {
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
    }

    private void debug(String message) {
        System.out.println(message);
    }
}
