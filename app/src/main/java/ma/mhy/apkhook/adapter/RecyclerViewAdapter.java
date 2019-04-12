package ma.mhy.apkhook.adapter;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.adapter
 * 作者 mahongyin
 * 时间 2019/4/7 22:55
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import ma.mhy.apkhook.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements OnClickListener {
    private List<String> data = new ArrayList();
    private Context mContext;
    private OnItemClickListener mOnItemClickListener = (OnItemClickListener)null;
    public RecyclerViewAdapter(List<String> dataList) {
        this.data = dataList;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.xml_card, parent, false);
        view.setOnClickListener(this);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolder recyclerViewHolder = (ViewHolder) holder;
        recyclerViewHolder.info.setText((String)this.data.get(position));
        recyclerViewHolder.itemView.setTag(new Integer(position));

//        recyclerViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, data.get(position), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void clearAll() {
        this.data.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public void onClick(View paramView) {
        if (this.mOnItemClickListener != null) {
            this.mOnItemClickListener.onItemClick(paramView, ((Integer)paramView.getTag()).intValue());
        }
    }


    public void setOnItemClickListener(OnItemClickListener paramOnItemClickListener) {
        this.mOnItemClickListener = paramOnItemClickListener;
    }

    public static interface OnItemClickListener {
        public abstract void onItemClick(View paramView, int paramInt);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
         TextView info;
         ViewHolder(View itemView) {
            super(itemView);
            info = ((TextView)itemView.findViewById(R.id.name));
        }
    }

}

