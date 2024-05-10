import lib.Pair;
import lib.Tri;

import java.util.ArrayList;

public class Simulator {
    private static BattleshipPlayer p1 = new BattleshipPlayer();
    private static ArrayList<Boat> p1boats = new ArrayList<>();
    private static ArrayList<Integer> p1boatsLoc = new ArrayList<>();

    private static BattleshipPlayer p2 = new BattleshipPlayer();
    private static ArrayList<Boat> p2boats = new ArrayList<>();
    private static ArrayList<Integer> p2boatsLoc = new ArrayList<>();

    public static void initializeGame() {
        p1 = new BattleshipPlayer();
        p2 = new BattleshipPlayer();

        p1boats = p1.getShips(new int[]{2, 3, 3, 4, 5});
        p2boats = p2.getShips(new int[]{2, 3, 3, 4, 5});

        p1boatsLoc = new ArrayList<>();
        p2boatsLoc = new ArrayList<>();

        for (Boat boat : p1boats) {
            p1boatsLoc.addAll(boat.getPositions());
        }
        for (Boat boat :p2boats) {
            p2boatsLoc.addAll(boat.getPositions());
        }
    }

    public static void main(String[] args) {
        int samples = 1000;

        Pair<PlayerStats, PlayerStats> stats = run(samples);

        System.out.printf("%d samples%n%n", samples);

        System.out.printf("Player 1: %s%n", stats.getFirst());
        System.out.printf("Player 2: %s%n", stats.getSecond());

        System.out.print(stats.getFirst().getWins() > stats.getSecond().getWins() ? "Player 1 won" : "Player 2 won");
        System.out.printf(" by %.2f percent.",
                (stats.getFirst().getWins() > stats.getSecond().getWins() ?
                        (double) stats.getFirst().getWins() / samples :
                        (double) stats.getSecond().getWins() / samples) * 100
        );
    }

    public static Pair<PlayerStats, PlayerStats> run(int samples) {
        Pair<PlayerStats, PlayerStats> playerStats = Pair.of(new PlayerStats(), new PlayerStats());

        for (int i = 0; i < samples; i++) {
            initializeGame();
            while (true) {
                Tri<Boolean, Integer, Integer> response1 = isHit(p1, p2boatsLoc, p2boats);
                p1.response(response1.getSecond(), response1.getFirst(), response1.getThird());
                p2.enemyAttack(response1.getSecond());

                if (response1.getFirst()) {
                    playerStats.getFirst().newHit();
                } else {
                    playerStats.getFirst().newMiss();
                }

                if (p2boatsLoc.isEmpty()) {
                    playerStats.getFirst().newWin();
                    break;
                }

                Tri<Boolean, Integer, Integer> response2 = isHit(p2, p1boatsLoc, p1boats);
                p1.response(response2.getSecond(), response2.getFirst(), response2.getThird());
                p2.enemyAttack(response2.getSecond());

                if (response2.getFirst()) {
                    playerStats.getSecond().newHit();
                } else {
                    playerStats.getSecond().newMiss();
                }

                if (p1boatsLoc.isEmpty()) {
                    playerStats.getSecond().newWin();
                    break;
                }
            }
        }

        return playerStats;
    }

    public static int getBoatLengthByLoc(int boatLoc, ArrayList<Boat> boats) {
        for (Boat boat : boats) {
            for (int boatLoc2 : boat.getPositions()) {
                if (boatLoc == boatLoc2) return boat.getLength();
            }
        }

        return -1;
    }

    public static Tri<Boolean, Integer, Integer> isHit(BattleshipPlayer player, ArrayList<Integer> boatLocs,
                                                       ArrayList<Boat> boats) {
        int move = player.getMove();

        for (int boat : boatLocs) {
            if (move == boat) {
                Tri<Boolean, Integer, Integer> tri = Tri.of(true, move, getBoatLengthByLoc(move, boats));
                boatLocs.remove((Integer) boat);
                return tri;
            }
        }

        return Tri.of(false, move, -1);
    }

    public static class PlayerStats {
        private int count = 0;

        private double misses = 0;
        private double hits = 0;

        private int wins = 0;

        public void newMiss() {
            misses++;
        }

        public void newHit() {
            hits++;
        }

        public void newWin() {
            wins++;
            count++;
        }

        public int getWins() {
            return wins;
        }

        @Override
        public String toString() {
            return String.format("""
                    
                    
                    Avg. Hits: %.2f
                    Avg. Misses: %.2f
                    H/M: %.2f
                    
                    Wins: %d
                    """,
                    hits / count,
                    misses / count,
                    (hits / count) / (misses / count),
                    wins
            );
        }
    }
}
