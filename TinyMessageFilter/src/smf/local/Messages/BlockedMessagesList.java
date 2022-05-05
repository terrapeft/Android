package smf.local.Messages;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import smf.local.CustomTypes.References;
import smf.local.Helpers.CacheHelper;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Helpers.SystemAccess;
import smf.local.R;
import smf.local.SimpleActivities.Statistics;
import smf.local.SmsReceiver;

public class BlockedMessagesList extends ListActivity 
{
	ArrayAdapter<MessageModel> adapter;
	MessageModelHelper modelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        registerForContextMenu(getListView());
        References.BlockedMessagesActivity = this;

        try 
        {
	        DbHelper.Initialize(this);
	        ErrorHelper.Initialize(this);
	        SystemAccess.Initialize(this);

            if (SmsReceiver.lastMessageIsForSpamBox)
                SystemAccess.RemoveNotification();

	        modelHelper = new MessageModelHelper(this);
            loadData();
        }
		catch (Exception ex)
		{
			ErrorHelper.ShowError(ex);
		}
    }

    public void loadData()
    {
        // Create an array of Strings, that will be put to our ListActivity
        adapter = new MessagesArrayAdapter(this, modelHelper.loadModel());
        setListAdapter(adapter);
        DbHelper.markMessagesAsRead();
        adapter.notifyDataSetChanged();
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu1, View v, ContextMenuInfo menuInfo)
    {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
	    MessageModel mm = adapter.getItem(info.position); 
	    menu1.setHeaderTitle(mm.getSender());
        
	    MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_context_menu, menu1);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) 
    {
    	try 
    	{
    		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    		MessageModel mm = adapter.getItem(info.position); 
	        // Handle item selection
			switch (item.getItemId()) 
	        {
	            case R.id.allow_sender:
	            	DbHelper.upsertInBlackList(mm.getSender(), false);
	            	adapter.notifyDataSetChanged();
                    CacheHelper.clear();
	            	Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
	                break;
	            case R.id.block_sender:
                    DbHelper.upsertInBlackList(mm.getSender(), true);
	            	adapter.notifyDataSetChanged();
                    CacheHelper.clear();
	            	Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
	                break;
	            case R.id.remove_message:
	            	DbHelper.deleteSpamMessage(mm.getId());
	            	((MessagesArrayAdapter)adapter).removeModel(mm);
	                adapter.notifyDataSetChanged();
	                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
	                break;
	            case R.id.remove_messages:
	            	DbHelper.deleteSpamMessages(DbHelper.SPAM_TABLE, mm.getSender());
	            	((MessagesArrayAdapter)adapter).removeModels(mm.getSender());
	                adapter.notifyDataSetChanged();
	                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
	                break;
	            case R.id.push_message:
	            	SystemAccess.putSmsToInbox(getContentResolver(), mm);
	            	Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
	                //adapter.notifyDataSetChanged();
	                break;
	            case R.id.push_messages:
	            	SystemAccess.putManySmsToInbox(getContentResolver(), mm.getSender());
	            	Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
	                //adapter.notifyDataSetChanged();
	                break;
	            default:
	                return super.onOptionsItemSelected(item);
	        }
	        
    	}
		catch (Exception ex)
		{
			ErrorHelper.ShowError(ex);
		}
    	return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.message_menu, menu);
        }
        catch (Exception ex)
        {
            ErrorHelper.ShowError(ex);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        try
        {
            switch (item.getItemId())
            {
                case R.id.clearLogButton:
                    clearSpam();
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.homeButton:
                    Intent cIntent = new Intent(this, Statistics.class);
                    startActivity(cIntent);
                    break;
                case R.id.saveSpamButton:
                    String date = DbHelper.getTimeStamp("yyyy_MM_dd__HH_mm_ss");
                    String fname = "Spambox-"+ date +".xml";
                    String data = MessageModel.GetXml(DbHelper.getMessages());
                    if (SystemAccess.saveTextFile("/TinyMessageFilter/Spambox", fname, data))
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.helpButton:
                    SystemAccess.showHelpDialog(this, R.layout.save_spam_help);
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        catch (Exception ex)
        {
            ErrorHelper.ShowError(ex);
        }
        return true;
    }

    private void clearSpam()
    {
        DbHelper.deleteFromTable(DbHelper.SPAM_TABLE);
        adapter.clear();
        modelHelper = new MessageModelHelper(this);
        adapter.notifyDataSetChanged();
    }
}
