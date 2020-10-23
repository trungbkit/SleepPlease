package vn.edu.hcmut.komorebi.sleepplease;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by trungbkit on 08/01/2017.
 */

class RecyclerDataAdapter extends RecyclerView.Adapter<RecyclerDataAdapter.DataViewHolder> {

    private Context context;
    private RecyclerDataAdapterOnClickListener clickListener;

    public interface RecyclerDataAdapterOnClickListener {
        void onItemClick(View view, int position, Switch clickedSwitch);
        boolean onItemLongClick(View view, int position);
    }

    public RecyclerDataAdapter(Context context, RecyclerDataAdapterOnClickListener listener) {
        this.context = context;
        clickListener = listener;
    }

    @Override
    public int getItemCount() {
        List<Alarm> list_alarm = MainActivity.listOfAlarm;
        return list_alarm == null ? 0 : list_alarm.size();
    }

    @Override
    public RecyclerDataAdapter.DataViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_names, parent, false);

        return new DataViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerDataAdapter.DataViewHolder holder, int position) {
        String name = MainActivity.listOfAlarm.get(position).toString();
        holder.tvAlarm.setText(name);
        holder.aSwitch.setChecked(MainActivity.listOfAlarm.get(position).isActived);
    }

    /**
     * Data ViewHolder class.
     */
    public class DataViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        private TextView tvAlarm;
        private Switch aSwitch;
        private Toast mToast;

        public DataViewHolder(View itemView) {
            super(itemView);
            tvAlarm = (TextView) itemView.findViewById(R.id.textview_alarm);
            aSwitch = (Switch) itemView.findViewById(R.id.switch_alarm);
            aSwitch.setChecked(true);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            aSwitch.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
          clickListener.onItemClick(v, getAdapterPosition(), aSwitch);
        }

        @Override
        public boolean onLongClick(View v) {
            return clickListener.onItemLongClick(v, getAdapterPosition());
        }
    }
}


