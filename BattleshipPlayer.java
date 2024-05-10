package lib;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;

public class BattleshipPlayer {
    public BattleshipPlayer() {
    }

    public int getMove() {
        return 0;
    }

    public void response(int location, boolean hit, int sinkLength) {
    }

    public void enemyAttack(int location) {
    }

    public ArrayList<Boat> getShips(int[] sizes) {
        ArrayList<Boat> ships = new ArrayList();
        ships.add(new Boat(new int[]{0, 1}));
        ships.add(new Boat(new int[]{2, 3, 4}));
        ships.add(new Boat(new int[]{5, 6, 7}));
        ships.add(new Boat(new int[]{10, 11, 12, 13}));
        ships.add(new Boat(new int[]{14, 15, 16, 17, 18}));
        return ships;
    }
}
