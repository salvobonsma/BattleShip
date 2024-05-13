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
        ships.add(new Boat(new int[]{0, 1}));
        ships.add(new Boat(new int[]{32, 33, 34}));
        ships.add(new Boat(new int[]{5, 6, 7}));
        ships.add(new Boat(new int[]{50, 51, 52, 53}));
        ships.add(new Boat(new int[]{80, 81, 82, 83, 84}));
        return ships;
    }
}
