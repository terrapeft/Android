package smf.local.Messages;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.R;

import java.util.ArrayList;
import java.util.List;

public class MessagesArrayAdapter extends ArrayAdapter<MessageModel> {

	private final List<MessageModel> list;
	private final Activity context;

	public MessagesArrayAdapter(Activity context, List<MessageModel> list) 
	{
		super(context, R.layout.messages_row_layout, list);
		this.context = context;
		this.list = list;
	}

	static class ViewHolder 
	{
		protected TextView sender;
		protected TextView body;
		//protected CheckBox checkbox;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View view = null;
		
		try
		{
			if (convertView == null) {
				LayoutInflater inflator = context.getLayoutInflater();
				view = inflator.inflate(R.layout.messages_row_layout, null);
				final ViewHolder viewHolder = new ViewHolder();
				viewHolder.sender = (TextView) view.findViewById(R.id.msg_sender);
				viewHolder.body = (TextView) view.findViewById(R.id.msg_body);
				/*
				viewHolder.checkbox = (CheckBox) view.findViewById(R.id.list_checkbox);
				
				viewHolder.checkbox
					.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	
							@Override
							public void onCheckedChanged(CompoundButton buttonView,
									boolean isChecked) {
								MessageModel element = (MessageModel) viewHolder.checkbox
										.getTag();
								//element.setSelected(buttonView.isChecked());
	
							}
						});
				*/
				view.setTag(viewHolder);
				//viewHolder.checkbox.setTag(list.get(position));
			} 
			else 
			{
				view = convertView;
				//((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
			}

            MessageModel m = list.get(position);
			ViewHolder holder = (ViewHolder) view.getTag();
			holder.sender.setText(m.getSender() + " " + DbHelper.getTimeStamp(Long.parseLong(m.getDate())));
			holder.body.setText(m.getBody());
			//holder.checkbox.setChecked(list.get(position).isSelected());
			
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

	public void removeModel(MessageModel m)
	{
		super.remove(m);
	}
	
	public void removeModels(String sender)
	{
		List<MessageModel> forDelete = getMessageModels(sender);
		
		for (int k = 0; k < forDelete.size(); k++)
		{
			removeModel(forDelete.get(k));
		}
	}
	
	private List<MessageModel> getMessageModels(String sender)
	{
		List<MessageModel> newList = new ArrayList<MessageModel>();
		for (int k = 0; k < list.size(); k++)
		{
			MessageModel mm = list.get(k); 
			if (mm.getSender().equals(sender))
			{
				newList.add(mm);
			}
		}
		
		return newList;
	}

} 