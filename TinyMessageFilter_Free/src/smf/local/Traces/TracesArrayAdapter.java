package smf.local.Traces;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import smf.local.Helpers.ErrorHelper;
import smf.local.R;

import java.util.List;

public class TracesArrayAdapter extends ArrayAdapter<TraceModel> {

    private final List<TraceModel> list;
    private final Activity context;

    public TracesArrayAdapter(Activity context, List<TraceModel> list) {
        super(context, R.layout.trace_row_layout, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView descr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        try {
            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                view = inflator.inflate(R.layout.trace_row_layout, null);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.descr = (TextView) view.findViewById(R.id.str_val);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }

            ViewHolder holder = (ViewHolder) view.getTag();
            holder.descr.setText(list.get(position).getDescription());

            int colorPos = position % 2;
            if (colorPos == 0)
                view.setBackgroundColor(0xFFAAAAAA);
            else
                view.setBackgroundColor(0xFF777777);

        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }
        return view;
    }

} 