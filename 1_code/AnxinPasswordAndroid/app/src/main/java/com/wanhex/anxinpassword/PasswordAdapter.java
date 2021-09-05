package com.wanhex.anxinpassword;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.wanhex.anxinpassword.db.Password;

import java.util.List;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.ViewHolder>{

    private List<Password> mPasswordList;

    private AdapterView.OnItemClickListener mOnItemClickListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rootLayout;
        ImageView accountImage;
        TextView summary;
        TextView timeStamp;

        public ViewHolder(View view) {
            super(view);
            accountImage = (ImageView) view.findViewById(R.id.iv_account_icon);
            summary = (TextView) view.findViewById(R.id.tv_summary);
            timeStamp = (TextView) view.findViewById(R.id.tv_time_stamp);
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public PasswordAdapter(List<Password> passwordList) {
        mPasswordList = passwordList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.password_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int pos = position;
        Password password = mPasswordList.get(pos);
        holder.accountImage.setImageResource(password.getImageId());
        holder.summary.setText(password.getSummary() + pos);
        holder.timeStamp.setText(password.getTimeStampStr());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClickListener.onItemClick(null, holder.itemView, pos, pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPasswordList.size();
    }
}
