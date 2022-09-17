package com.tkoyat.bubblecloudlauncher;

import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;


public class BubbleLayoutManager extends RecyclerView.LayoutManager {
    int ItemCounts;
    int TriangleD;
    int TriangleH;
    double Z;
    int centerX;
    int centerY;
    ComputeZ computeZ;
    boolean isScale;
    int itemX;
    int itemY;
    int pos;
    double r;
    float scale;
    BubbleHexagon bHexagon;
    int viewHeight;
    int viewWidth;
    int verticalScrollOffset = 0;
    int horizontalScrollOffset = 0;

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public boolean canScrollVertically() {
        return true;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(-2, -2);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);
        detachAndScrapAttachedViews(recycler);
        checkItems(recycler);
        detachAndScrapAttachedViews(recycler);
        putItems(recycler);
    }

    public int getPos() {
        return this.pos;
    }

    private void putItems(RecyclerView.Recycler recycler) {
        int i = 0;
        double d = 0.0d;
        int i2 = 0;
        while (i2 < getItemCount()) {
            View viewForPosition = recycler.getViewForPosition(i2);
            measureChildWithMargins(viewForPosition, i, i);
            addView(viewForPosition);
            float x = viewForPosition.getX() + (this.viewWidth / 2);
            float y = viewForPosition.getY() + (this.viewHeight / 2);
            this.computeZ = new ComputeZ(x, y, this.centerX, this.centerY, this.viewHeight);
            double d2 = d;
            this.Z = (this.r - Math.sqrt(Math.pow(x - this.centerX, 2.0d) + Math.pow(y - this.centerY, 2.0d))) / this.r;
            if (this.isScale) {
                float f = this.scale;
                if (f >= 0.0f) {
                    ViewCompat.animate(viewForPosition).setDuration(100L).scaleX(this.computeZ.Z).scaleY(this.computeZ.Z).alpha(this.computeZ.Z).start();
                } else if (f < 0.0f) {
                    ViewCompat.animate(viewForPosition).setDuration(100L).scaleX(0.7f).scaleY(0.7f).alpha(0.75f).start();
                }
            } else {
                float f2 = this.scale;
                if (f2 >= 0.0f) {
                    viewForPosition.setScaleX(this.computeZ.Z);
                    viewForPosition.setScaleY(this.computeZ.Z);
                    viewForPosition.setAlpha(this.computeZ.Z);
                } else if (f2 < 0.0f) {
                    viewForPosition.setScaleX(0.7f);
                    viewForPosition.setScaleY(0.7f);
                    viewForPosition.setAlpha(0.75f);
                }
            }
            viewForPosition.setTranslationX(this.computeZ.dx);
            viewForPosition.setTranslationY(this.computeZ.dy);
            double d3 = this.Z;
            if (d2 <= d3) {
                this.pos = i2;
                this.itemX = (int) x;
                this.itemY = (int) y;
                d = d3;
            } else {
                d = d2;
            }
            i2++;
            i = 0;
        }
    }

    private void checkItems(RecyclerView.Recycler recycler) {
        this.isScale = false;
        this.ItemCounts = getItemCount();
        this.centerX = getWidth() / 2;
        this.centerY = getHeight() / 2;
        View viewForPosition = recycler.getViewForPosition(0);
        measureChildWithMargins(viewForPosition, 0, 0);
        this.viewHeight = getDecoratedMeasuredHeight(viewForPosition);
        this.viewWidth = getDecoratedMeasuredWidth(viewForPosition);
        int i = this.viewHeight;
        this.TriangleD = i;
        this.TriangleH = (int) ((i * Math.sqrt(3.0d)) / 2.0d);
        this.r = Math.sqrt(Math.pow(this.centerX, 2.0d) + Math.pow(this.centerY, 2.0d));
        this.bHexagon = new BubbleHexagon(this.ItemCounts, this.centerX, this.centerY, this.TriangleD, this.TriangleH);
        for (int i2 = 0; i2 < this.ItemCounts; i2++) {
            View viewForPosition2 = recycler.getViewForPosition(i2);
            measureChildWithMargins(viewForPosition2, 0, 0);
            addView(viewForPosition2);
            layoutDecoratedWithMargins(viewForPosition2, this.bHexagon.x.get(i2).intValue() - (this.viewWidth / 2), this.bHexagon.y.get(i2).intValue() - (this.viewHeight / 2), this.bHexagon.x.get(i2).intValue() + (this.viewWidth / 2), this.bHexagon.y.get(i2).intValue() + (this.viewHeight / 2));
            ComputeZ computeZ = new ComputeZ(this.bHexagon.x.get(i2).intValue(), this.bHexagon.y.get(i2).intValue(), this.centerX, this.centerY, this.viewHeight);
            this.computeZ = computeZ;
            viewForPosition2.setScaleX(computeZ.Z);
            viewForPosition2.setScaleY(this.computeZ.Z);
            viewForPosition2.setAlpha(this.computeZ.Z);
            viewForPosition2.setTranslationX(this.computeZ.dx);
            viewForPosition2.setTranslationY(this.computeZ.dy);
        }
        detachAndScrapAttachedViews(recycler);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i2;
        int i3;
        if (this.verticalScrollOffset + i < (this.centerY - ((this.bHexagon.floor + 1) * this.bHexagon.triangleH)) - ((this.viewHeight * 3) / 2)) {
            i2 = ((-this.verticalScrollOffset) + this.centerY) - ((this.bHexagon.floor + 1) * this.bHexagon.triangleH);
            i3 = (this.viewHeight * 3) / 2;
        } else {
            if (this.verticalScrollOffset + i > (-this.centerY) + ((this.bHexagon.floor + 1) * this.bHexagon.triangleH) + ((this.viewHeight * 3) / 2)) {
                i2 = (-this.centerY) + ((this.bHexagon.floor + 1) * this.TriangleH) + ((this.viewHeight * 3) / 2);
                i3 = this.verticalScrollOffset;
            }
            this.verticalScrollOffset += i;
            offsetChildrenVertical(-i);
            detachAndScrapAttachedViews(recycler);
            putItems(recycler);
            return i;
        }
        i = i2 - i3;
        this.verticalScrollOffset += i;
        offsetChildrenVertical(-i);
        detachAndScrapAttachedViews(recycler);
        putItems(recycler);
        return i;
    }

//    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
//    public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        int i2;
//        int i3;
//        if (this.horizontalScrollOffset + i < (this.centerX - ((this.bHexagon.floor + 1) * this.bHexagon.triangleH)) - ((this.viewWidth * 3) / 2)) {
//            i2 = ((-this.horizontalScrollOffset) + this.centerX) - ((this.bHexagon.floor + 1) * this.bHexagon.triangleH);
//            i3 = (this.viewWidth * 3) / 2;
//        } else {
//            if (this.horizontalScrollOffset + i > (-this.centerX) + ((this.bHexagon.floor + 1) * this.bHexagon.triangleH) + ((this.viewWidth * 3) / 2)) {
//                i2 = (-this.centerX) + ((this.bHexagon.floor + 1) * this.TriangleH) + ((this.viewWidth * 3) / 2);
//                i3 = this.horizontalScrollOffset;
//            }
//            this.horizontalScrollOffset += i;
//            offsetChildrenHorizontal(-i);
//            detachAndScrapAttachedViews(recycler);
//            putItems(recycler);
//            return i;
//        }
//        i = i2 - i3;
//        this.horizontalScrollOffset += i;
//        offsetChildrenHorizontal(-i);
//        detachAndScrapAttachedViews(recycler);
//        putItems(recycler);
//        return i;
//    }

    @Override // androidx.recyclerview.widget.RecyclerView.LayoutManager
    public int scrollHorizontallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int i2;
        int i3;
        if (this.horizontalScrollOffset + i < (this.centerX - ((this.bHexagon.floor + 1) * this.bHexagon.triangleH)) - ((this.viewWidth * 3) / 2)) {
//            i2 = ((-this.horizontalScrollOffset) + this.centerX) - ((this.bHexagon.floor + 1) * this.bHexagon.triangleH);
//            i3 = (this.viewWidth * 3) / 2;
//            i = i2 - i3;
            this.horizontalScrollOffset += i;
            offsetChildrenHorizontal(-i);
            detachAndScrapAttachedViews(recycler);
            putItems(recycler);
            return i;
        } else {
//            if (this.horizontalScrollOffset + i > (-this.centerX) + ((this.bHexagon.floor + 1) * this.bHexagon.triangleH) + ((this.viewWidth * 3) / 2)) {
//                i2 = (-this.centerX) + ((this.bHexagon.floor + 1) * this.TriangleH) + ((this.viewWidth * 3) / 2);
//                i3 = this.horizontalScrollOffset;
//            }
            this.horizontalScrollOffset += i;
            offsetChildrenHorizontal(-i);
            detachAndScrapAttachedViews(recycler);
            putItems(recycler);
            return i;
        }
    }
}