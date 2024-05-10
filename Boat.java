// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Boat {
    private ArrayList<Integer> positions = new ArrayList();
    private ArrayList<Boolean> hits;

    public Boat(ArrayList<Integer> poses) {
        this.hits = new ArrayList();
        Iterator var2 = poses.iterator();

        while(var2.hasNext()) {
            int x = (Integer)var2.next();
            this.positions.add(x);
            this.hits.add(false);
        }

        Collections.sort(this.positions);
        if (!this.isLegitBoat()) {
            throw new IllegalArgumentException("Bad Boat Parameters (too short or not horizontal or vertical)");
        }
    }

    public Boat(int... pos) {
        int[] var2 = pos;
        int var3 = pos.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            int x = var2[var4];
            this.positions.add(x);
        }

        Collections.sort(this.positions);
        if (!this.isLegitBoat()) {
            throw new IllegalArgumentException("Bad Boat Parameters (too short, off grid, or not horizontal or vertical)");
        }
    }

    private boolean isLegitBoat() {
        boolean increasingHorizontal = true;
        boolean increasingVertical = true;
        boolean sameHorizontal = true;
        boolean sameVertical = true;
        if (this.positions.size() == 0) {
            return false;
        } else {
            Iterator var5 = this.positions.iterator();

            int i;
            do {
                if (!var5.hasNext()) {
                    int first = (Integer)this.positions.get(0);

                    for(i = 1; i < this.positions.size(); ++i) {
                        if ((Integer)this.positions.get(i) / 10 != first / 10) {
                            sameHorizontal = false;
                        }

                        if ((Integer)this.positions.get(i) % 10 != first % 10) {
                            sameVertical = false;
                        }

                        if ((Integer)this.positions.get(i) / 10 != first / 10 + i) {
                            increasingHorizontal = false;
                        }

                        if ((Integer)this.positions.get(i) % 10 != first % 10 + i) {
                            increasingVertical = false;
                        }
                    }

                    return sameHorizontal && increasingVertical || sameVertical && increasingHorizontal;
                }

                i = (Integer)var5.next();
            } while(i <= 99 && i >= 0);

            return false;
        }
    }

    public String toString() {
        return this.positions.toString();
    }

    public int getLength() {
        return this.positions.size();
    }

    public ArrayList<Integer> getPositions() {
        return this.positions;
    }

    public boolean attack(int position) {
        int hitPosition = this.positions.indexOf(position);
        if (hitPosition != -1 && !(Boolean)this.hits.get(hitPosition)) {
            this.hits.set(hitPosition, true);
            return true;
        } else {
            return false;
        }
    }

    public boolean isSunk() {
        return this.hits.contains(false);
    }
}
