package com.tkoyat.bubblecloudlauncher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;


public class BubbleRecyclerViewAdapter extends RecyclerView.Adapter<BubbleRecyclerViewAdapter.Holder> {
    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<AppDetail> mItems;
    private OnRecyclerViewListener onRecyclerViewListener;


    public interface OnRecyclerViewListener {
        void onItemClick(View view, int i);
        void onItemLongClick(View view, int i);
    }

    public void setOnRecyclerViewListener(OnRecyclerViewListener onRecyclerViewListener) {
        this.onRecyclerViewListener = onRecyclerViewListener;
    }

    public BubbleRecyclerViewAdapter(Context context, List<AppDetail> list) {
        this.mContext = context;
        this.mItems = list;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    /* renamed from: onCreateViewHolder  reason: collision with other method in class */
    public Holder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Holder holder = new Holder(LayoutInflater.from(this.mContext).inflate(R.layout.app_item_layout, viewGroup, false));
        holder.itemView.setScaleX(1.0f);
        holder.itemView.setScaleY(1.0f);

        return holder;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(final Holder holder, @SuppressLint("RecyclerView") final int i) {
        if (this.mItems.isEmpty()) {
            return;
        }
        AppDetail appDetail = this.mItems.get(i);
        if (appDetail.getmViewType() == 2) {
            return;
        }
        holder.bind(appDetail);
        if (this.onRecyclerViewListener == null) {
            return;
        }
//        holder.itemView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int x, int y, int oldX, int oldY) {
//                int aa = 1;
//            }
//        });
//        item 选定监听（OnItemSelectedListener）
//        item 点击监听（OnItemClickListener）
//        item 长按监听（OnItemLongClickListener）
//        遥控器其他按键监听（OnItemKeyListener）

        // 设置 itemView 可以获得焦点
        holder.itemView.setFocusable(true);
//        holder.itemView.setTag(position);
        holder.itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
//                    currentPosition = (int) holder.itemView.getTag();
//                    mOnItemSelectedListener.onItemSelected(v, currentPosition);
//                    currentPosition = (int) holder.itemView.getTag();
//                    mOnItemSelectedListener.OnItemSelected(v, currentPosition);
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                BubbleRecyclerViewAdapter.this.onRecyclerViewListener.onItemLongClick(view, i);
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
//                BubbleRecyclerViewAdapter.this.onRecyclerViewListener.onItemClick(holder.itemView, i);
                BubbleRecyclerViewAdapter.this.onRecyclerViewListener.onItemClick(view, i);
            }
        });
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mItems.size();
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemViewType(int i) {
        return this.mItems.get(i).getmViewType();
    }

    class Holder extends RecyclerView.ViewHolder {
        BubbleImageView mImageView;

        Holder(View itemView) {
            super(itemView);
            this.mImageView = (BubbleImageView) itemView.findViewById(R.id.icon_image_view);
        }

        public void bind(AppDetail appDetail) {
            this.mImageView.setImageDrawable(appDetail.getIcon());
        }
    }

}