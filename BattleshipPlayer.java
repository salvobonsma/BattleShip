// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;

public class BattleshipPlayer {
    public BattleshipPlayer() {
    }

    public int getMove() {
        return (int) (Math.random() * 99);
    }

    public void response(int location, boolean hit, int sinkLength) {
    }

    public void enemyAttack(int location) {
    }

    public ArrayList<Boat> getShips(int[] sizes) {
        ArrayList<Boat> ships = new ArrayList();
        ships.add(new Boat(new int[]{10, 20}));
        ships.add(new Boat(new int[]{11, 21, 31}));
        ships.add(new Boat(new int[]{59, 69, 79}));
        ships.add(new Boat(new int[]{50, 51, 52, 53}));
        ships.add(new Boat(new int[]{90, 91, 92, 93, 94}));
        return ships;
    }
}
