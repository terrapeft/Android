package smf.local.Traces;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Helpers.SystemAccess;
import smf.local.R;
import smf.local.SimpleActivities.Statistics;

public class TracesList extends ListActivity
{
	ArrayAdapter<TraceModel> adapter;
	TracesModelHelper modelHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        //registerForContextMenu(getListView());
        
        try 
        {
	        DbHelper.Initialize(this);
	        ErrorHelper.Initialize(this);
	        SystemAccess.Initialize(this);

	        modelHelper = new TracesModelHelper(this);

			// Create an array of Strings, that will be put to our ListActivity
			adapter = new TracesArrayAdapter(this, modelHelper.loadModel());
			setListAdapter(adapter);
        }
		catch (Exception ex)
		{
			ErrorHelper.ShowError(ex);
		}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        try
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.log_menu, menu);
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
                    clearLog();
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.homeButton:
                    Intent cIntent = new Intent(this, Statistics.class);
                    startActivity(cIntent);
                    break;
                case R.id.saveSpamButton:
                    String date = DbHelper.getTimeStamp("yyyy_MM_dd__HH_mm_ss");
                    String fname = "Log-"+ date +".xml";
                    String data = TraceModel.GetXml(DbHelper.getLogs());
                    if (SystemAccess.saveTextFile("/TinyMessageFilter/Logs", fname, data))
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.helpButton:
                    SystemAccess.showHelpDialog(this, R.layout.save_log_help);
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

    private void clearLog()
    {
        DbHelper.deleteFromTable(DbHelper.TRACE_TABLE);
        adapter.clear();
        modelHelper = new TracesModelHelper(this);
        adapter.notifyDataSetChanged();
    }

}
