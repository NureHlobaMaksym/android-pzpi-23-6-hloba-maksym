package nure.hloba.maksym.pract_task3_part3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private String[] mData;

    public MyAdapter(String[] mData) {
        this.mData = mData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        LinearLayout itemLayout;
        public ViewHolder(View v) {
            super(v);
            itemLayout = v.findViewById(R.id.itemLayout);
            textView = v.findViewById(R.id.textView);
        }
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(mData[position]);
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(holder.itemView.getContext(), "The item №" +  (holder.getAdapterPosition() + 1) + " was pressed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }
}