package smf.local.Rules;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import smf.local.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly.Chupaev
 * Date: 5/23/13
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaneIconsAdapter extends ArrayAdapter<Integer>
{
    private Activity context;
    ArrayList<Integer> data = null;

    public PlaneIconsAdapter(Activity context, int resource, ArrayList<Integer> data)
    {
        super(context, resource, data);
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {   // Ordinary view in Spinner, we use android.R.layout.simple_spinner_item
        return super.getView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {   // This view starts when we click the spinner.
        View row = convertView;
        if(row == null)
        {
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.plane_spinner_layout, parent, false);
        }

        Integer item = data.get(position);

        if (item != null)
        {   // Parse the data from each object and set it.
            ImageView plane = (ImageView) row.findViewById(R.id.spinnerIcon);
            plane.setImageResource(item);
        }

        return row;
    }
}
