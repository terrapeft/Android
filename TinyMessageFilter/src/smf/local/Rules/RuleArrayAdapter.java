package smf.local.Rules;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import smf.local.CustomTypes.ArrayListEx;
import smf.local.Helpers.CacheHelper;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.R;

import java.util.List;

public class RuleArrayAdapter extends ArrayAdapter<RuleModel> {

	private final List<RuleModel> list;
	private final Activity context;
    private boolean isUser = false;

	public RuleArrayAdapter(Activity context, List<RuleModel> list)
	{
		super(context, R.layout.rule_list, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder 
	{
		protected TextView name;
		protected TextView number;
		protected CheckBox checkboxBlock;
        protected CheckBox checkboxCall;
		protected CheckBox checkboxFav;
        protected CheckBox checkboxMute;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View view = null;
		try 
		{
			if (convertView == null) {
				LayoutInflater inflator = context.getLayoutInflater();
				view = inflator.inflate(R.layout.rule_list, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.number = (TextView) view.findViewById(R.id.number_entry);
				viewHolder.name = (TextView) view.findViewById(R.id.name_entry);
				viewHolder.checkboxBlock = (CheckBox) view.findViewById(R.id.checkbox_block);
                viewHolder.checkboxCall = (CheckBox) view.findViewById(R.id.checkbox_calls);
				viewHolder.checkboxFav = (CheckBox) view.findViewById(R.id.checkbox_fav);
                viewHolder.checkboxMute = (CheckBox) view.findViewById(R.id.checkbox_mute);

                setCheckBoxListener(viewHolder.checkboxCall);
				setCheckBoxListener(viewHolder.checkboxBlock);
				setCheckBoxListener(viewHolder.checkboxFav);
                setCheckBoxListener(viewHolder.checkboxMute);
				
				view.setTag(viewHolder);
                viewHolder.checkboxCall.setTag(list.get(position));
				viewHolder.checkboxBlock.setTag(list.get(position));
				viewHolder.checkboxFav.setTag(list.get(position));
                viewHolder.checkboxMute.setTag(list.get(position));
			} 
			else 
			{
				view = convertView;
                ((ViewHolder) view.getTag()).checkboxCall.setTag(list.get(position));
				((ViewHolder) view.getTag()).checkboxBlock.setTag(list.get(position));
				((ViewHolder) view.getTag()).checkboxFav.setTag(list.get(position));
                ((ViewHolder) view.getTag()).checkboxMute.setTag(list.get(position));
			}

            isUser = false;
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.number.setText(list.get(position).getPatternString());
			holder.name.setText(list.get(position).getName());
            holder.checkboxCall.setChecked(list.get(position).getRejectCallsFromIt());
            holder.checkboxCall.setVisibility(IsVisible(list.get(position)));
			holder.checkboxBlock.setChecked(list.get(position).getBlockIt());
			holder.checkboxFav.setChecked(list.get(position).getLoveIt());
            holder.checkboxMute.setChecked(list.get(position).getMuteIt());
            isUser = true;


            int colorPos = position % 2;
            if (colorPos == 0)
                view.setBackgroundColor(0xFFAAAAAA);
            else
                view.setBackgroundColor(0xFF777777);

        }
		catch (Exception ex)
		{
			ErrorHelper.ShowError(ex);
		} 
		return view;
	}

    private int IsVisible(RuleModel ruleModel)
    {
        return ruleModel.getNumber().length() == 0 ? View.INVISIBLE : View.VISIBLE;
    }

    private void setCheckBoxListener(final CheckBox checkbox)
	{
		checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
			{
                RuleModel element = (RuleModel)checkbox.getTag();
                element.setPlaneId(R.drawable.paper_plane_24);

                if (buttonView.getId() == R.id.checkbox_block)
                {
                    element.setBlockIt(buttonView.isChecked());
                    CacheHelper.clear();
                }
                if (buttonView.getId() == R.id.checkbox_calls)
                {
                    element.setRejectCallsFromIt(buttonView.isChecked());
                    CacheHelper.clear();
                }
                if (buttonView.getId() == R.id.checkbox_fav)
                {
                    element.setLoveIt(buttonView.isChecked());
                    element.setPlaneId(R.drawable.rose);
                    CacheHelper.clear();
                }
                if (buttonView.getId() == R.id.checkbox_mute)
                {
                    element.setMuteIt(buttonView.isChecked());
                    CacheHelper.clear();
                }

                ArrayListEx<RuleModel> changeSet = new ArrayListEx<RuleModel>();
                changeSet.add(element);

                try
                {
                    DbHelper.updateFileList(changeSet);
                }
                catch(Exception ex)
                {
                    ErrorHelper.ShowError("Save changeSet exception (" + DbHelper.getTimeStamp() + "): ", ex);
                }
			}
		});		
	}

} 