package com.tkoyat.bubblecloudlauncher;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;


public class BubbleHexagon {
    int floor;
    int floors;
    int index;
    int items;
    int side;
    int triangleD;
    int triangleH;
    List<Integer> x = new ArrayList();
    List<Integer> y = new ArrayList();

    public BubbleHexagon(int i, int i2, int i3, int i4, int i5) {
        this.triangleD = i4;
        this.triangleH = i5;
        this.items = i;
        this.floors = 0;
        while (true) {
            int i6 = this.floors;
            if ((i6 * 3 * i6) + (i6 * 3) + 1 >= this.items) {
                break;
            }
            this.floors = i6 + 1;
        }
        this.x.add(0, Integer.valueOf(i2));
        this.y.add(0, Integer.valueOf(i3));
        this.floor = 0;
        int i7 = 1;
        while (this.floor <= this.floors) {
            while (this.side <= 5) {
                while (this.index <= this.floor) {
                    Log.i("TEXT", "index=" + this.index + "side=" + this.side + "floor=" + this.floor + "i=" + i7);
                    int i8 = this.side;
                    if (i8 == 0) {
                        this.x.add(i7, Integer.valueOf((this.index * i5) + i2));
                        this.y.add(i7, Integer.valueOf((i3 - ((this.floor + 1) * i4)) + ((this.index * i4) / 2)));
                    } else if (i8 == 1) {
                        this.x.add(i7, Integer.valueOf(((this.floor + 1) * i5) + i2));
                        this.y.add(i7, Integer.valueOf((i3 - (((this.floor + 1) * i4) / 2)) + (this.index * i4)));
                    } else if (i8 == 2) {
                        this.x.add(i7, Integer.valueOf((((this.floor + 1) * i5) + i2) - (this.index * i5)));
                        this.y.add(i7, Integer.valueOf((((this.floor + 1) * i4) / 2) + i3 + ((this.index * i4) / 2)));
                    } else if (i8 == 3) {
                        this.x.add(i7, Integer.valueOf(i2 - (this.index * i5)));
                        this.y.add(i7, Integer.valueOf((((this.floor + 1) * i4) + i3) - ((this.index * i4) / 2)));
                    } else if (i8 == 4) {
                        this.x.add(i7, Integer.valueOf(i2 - ((this.floor + 1) * i5)));
                        this.y.add(i7, Integer.valueOf(((((this.floor + 1) * i4) / 2) + i3) - (this.index * i4)));
                    } else if (i8 == 5) {
                        this.x.add(i7, Integer.valueOf((i2 - ((this.floor + 1) * i5)) + (this.index * i5)));
                        this.y.add(i7, Integer.valueOf((i3 - (((this.floor + 1) * i4) / 2)) - ((this.index * i4) / 2)));
                    }
                    this.index++;
                    i7++;
                    if (i7 == this.items) {
                        break;
                    }
                }
                this.index = 0;
                this.side++;
                if (i7 == this.items) {
                    break;
                }
            }
            this.side = 0;
            this.floor++;
            if (i7 == this.items) {
                return;
            }
        }
    }
}