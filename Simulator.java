import lib.Pair;
import lib.Tri;

import java.util.ArrayList;
import java.util.Objects;

public class Simulator {
    private static BattleshipPlayer p1;
    private static ArrayList<Boat> p1boats = new ArrayList<>();

    private static BattleshipPlayer p2;
    private static ArrayList<Boat> p2boats = new ArrayList<>();

    public static void initializeGame() {
        p1 = new BattleshipPlayer();
        p2 = new BattleshipPlayer();

        p1boats = p1.getShips(new int[]{2, 3, 3, 4, 5});
        p2boats = p2.getShips(new int[]{2, 3, 3, 4, 5});
    }

    public static void main(String[] args) {
        new SalvoBattleshipPlayerV1();
//        int samples = 1;
//
//        Pair<PlayerStats, PlayerStats> stats = run(samples);
//
//        System.out.printf("%d samples%n%n", samples);
//
//        System.out.printf("Player 1: %s%n", stats.getFirst());
//        System.out.printf("Player 2: %s%n", stats.getSecond());
//
//        System.out.print(stats.getFirst().getWins() > stats.getSecond().getWins() ? "Player 1 won" : "Player 2 won");
//        System.out.printf(" by %.2f percent.",
//                (stats.getFirst().getWins() > stats.getSecond().getWins() ?
//                        (double) stats.getFirst().getWins() / samples :
//                        (double) stats.getSecond().getWins() / samples) * 100
//        );
    }

    public static Pair<PlayerStats, PlayerStats> run(int samples) {
        Pair<PlayerStats, PlayerStats> playerStats = Pair.of(new PlayerStats(), new PlayerStats());

        for (int i = 0; i < samples; i++) {
            initializeGame();
            while (true) {
                Tri<Integer, Boolean, Integer> response1 = playerMove(p1, p2boats);
                p1.response(response1.getFirst(), response1.getSecond(), response1.getThird());
                p2.enemyAttack(response1.getFirst());

                if (response1.getSecond()) {
                    playerStats.getFirst().newHit();
                } else {
                    playerStats.getFirst().newMiss();
                }

                if (hasNoBoatsLeft(p2boats)) {
                    playerStats.getFirst().newWin();
                    break;
                }

                Tri<Integer, Boolean, Integer> response2 = playerMove(p2, p1boats);
                p2.response(response2.getFirst(), response2.getSecond(), response2.getThird());
                p1.enemyAttack(response2.getFirst());

                if (response2.getSecond()) {
                    playerStats.getSecond().newHit();
                } else {
                    playerStats.getSecond().newMiss();
                }

                if (hasNoBoatsLeft(p1boats)) {
                    playerStats.getSecond().newWin();
                    break;
                }
            }
        }

        return playerStats;
    }

    public static Tri<Integer, Boolean, Integer> playerMove(BattleshipPlayer player, ArrayList<Boat> boats) {
        Integer playerMove = player.getMove();

        for (Boat boat : boats) {
            for (int i = 0; i < boat.getPositions().size(); i++) {
                Integer pos = boat.getPositions().get(i);

                // Hit
                if (Objects.equals(pos, playerMove)) {
                    boat.getPositions().set(i, null);

                    // Check to see if any part of the boat is still alive
                    for (Integer otherPos : boat.getPositions()) {
                        if (otherPos != null) return Tri.of(playerMove, true, -1);
                    }

                    return Tri.of(playerMove, true, boat.getPositions().size());
                }
            }
        }

        // No hit
        return Tri.of(playerMove, false, -1);
    }

    public static boolean hasNoBoatsLeft(ArrayList<Boat> boats) {
        for (Boat boat : boats) {
            for (Integer pos : boat.getPositions()) {
                if (pos != null) return false;
            }
        }

        // All the pos of all the boats are null
        return true;
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
