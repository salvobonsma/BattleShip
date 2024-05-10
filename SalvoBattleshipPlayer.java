import java.util.HashMap;

public class SalvoBattleshipPlayer extends BattleshipPlayer {
    // Opponent data
    private OppBoard board = new OppBoard();
    private OppType oppType = OppType.UNKNOWN;


    public SalvoBattleshipPlayer() {
        System.out.println(board.getBoard());
    }

    public enum Mode {
        OPP_TYPE_DETECTION,
        CHECKERBOARD,
        SHIP_DESTRUCTION
    }

    public enum OppType {
        BASIC, // Default battleship opp
        CHAT_GPT, // Generic Chat GPTed opp
        UNKNOWN
    }

    public class OppBoard {
        private HashMap<Location, Square> board = new HashMap<>();

        public OppBoard() {
            addRowFor(Letter.A);
            addRowFor(Letter.B);
            addRowFor(Letter.C);
            addRowFor(Letter.D);
            addRowFor(Letter.E);
            addRowFor(Letter.F);
            addRowFor(Letter.G);
            addRowFor(Letter.H);
            addRowFor(Letter.I);
            addRowFor(Letter.J);
        }

        private void addRowFor(Letter letter) {
            for (int i = 1; i <= 10; i++) {
                board.put(new Location(letter, i), Square.UNKNOWN);
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
            private final Letter letter;
            private final int number;

            public Location(Letter letter, int number) {
                this.letter = letter;
                this.number = number - 1;
            }

            public int getAsIndex() {
                return (number * 10) + letter.getI();
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
}
