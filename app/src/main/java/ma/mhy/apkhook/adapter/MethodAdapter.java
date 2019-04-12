package ma.mhy.apkhook.adapter;

/**
 * 项目名 HookAPK
 * 所在包 ma.mhy.hookapk.adapter
 * 作者 mahongyin
 * 时间 2019/4/7 22:53
 * 邮箱 mhy.work@qq.com
 * 描述 说明:
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ma.mhy.apkhook.R;

public class MethodAdapter extends RecyclerView.Adapter<MethodAdapter.ViewHolder> implements OnClickListener {
    private List<String> data = new ArrayList();
    private OnItemClickListener mOnItemClickListener = (OnItemClickListener)null;

    public MethodAdapter(List<String> paramList)
    {
        this.data = paramList;
    }

    @Override
    public int getItemCount()
    {
        return this.data.size();
    }

    @Override
    public void onClick(View paramView)
    {
        if (this.mOnItemClickListener != null) {
            this.mOnItemClickListener.onItemClick(paramView, ((Integer)paramView.getTag()).intValue());
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int paramInt) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.xml_card, (ViewGroup)null);
        viewGroup.setOnClickListener(this);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.info.setText(data.get(position).toString());
        holder.itemView.setTag(new Integer(position));
    }

    public void setOnItemClickListener(OnItemClickListener paramOnItemClickListener) {
        this.mOnItemClickListener = paramOnItemClickListener;
    }

    public static interface OnItemClickListener {
        public abstract void onItemClick(View paramView, int paramInt);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView info;
        public ViewHolder(View paramView) {
            super(paramView);
            this.info = paramView.findViewById(R.id.name);
        }
    }
}

