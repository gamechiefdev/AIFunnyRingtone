package com.gamechief.aifunnyringtone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.gamechief.aifunnyringtone.R;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {
    private final Context context;
    private final OnItemRemoveListener removeListener;
    private final List<ResultItem> resultList;

    public interface OnItemRemoveListener {
        void onItemRemoved(int i);
    }

    public ResultAdapter(Context context2, List<ResultItem> resultList2, OnItemRemoveListener removeListener2) {
        this.context = context2;
        this.resultList = resultList2;
        this.removeListener = removeListener2;
    }

    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ResultViewHolder(LayoutInflater.from(this.context).inflate(R.layout.result_itemlayout, parent, false));
    }

    public void onBindViewHolder(ResultViewHolder holder, int position) {
        holder.resultNum.setText(String.valueOf(position + 1));
        holder.personName.setText(this.resultList.get(position).getPersonName());
        holder.dellItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultList.remove(position);
                notifyDataSetChanged();
                if (removeListener != null) {
                    removeListener.onItemRemoved(position);
                }
            }
        });
    }



    public int getItemCount() {
        return this.resultList.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        ImageView dellItem;
        TextView personName;
        TextView resultNum;

        public ResultViewHolder(View itemView) {
            super(itemView);
            this.personName = (TextView) itemView.findViewById(R.id.personName);
            this.resultNum = (TextView) itemView.findViewById(R.id.resultNum);
            this.dellItem = (ImageView) itemView.findViewById(R.id.dellItem);
        }
    }

    public static class ResultItem {
        private final String personName;
        private final String resultText;

        public ResultItem(String personName2, String resultText2) {
            this.personName = personName2;
            this.resultText = resultText2;
        }

        public String getPersonName() {
            return this.personName;
        }

        public String getResultText() {
            return this.resultText;
        }
    }
}
