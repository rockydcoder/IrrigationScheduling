package dutta.sayon.com.irrigationscheduling;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

/**
 * Created by rocky on 22/3/15.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    List<String> data = Collections.emptyList();
    private ViewOnClickListener viewOnClickListener;

    public CustomAdapter(Context context, List<String> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = inflater.inflate(R.layout.custom_row, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {
        String currentField = data.get(i);
        myViewHolder.tvTitle.setText(currentField);
    }

    public void setViewOnClickListener(final ViewOnClickListener viewOnClickListener) {
        this.viewOnClickListener = viewOnClickListener;

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTitle;


        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvTitle = (TextView) itemView.findViewById(R.id.textView2);
        }

        @Override
        public void onClick(View view) {
            if(viewOnClickListener!=null)
                viewOnClickListener.onItemClick(view,getPosition());
        }
    }

    /**
     * For callback to any class to get the position of the item clicked
     */
    public interface ViewOnClickListener {
        public void onItemClick(View item, int position);
    }
}
