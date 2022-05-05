/**
 * 
 */
package smf.local.Rules;

import android.app.Activity;
import android.content.Context;
import smf.local.CustomTypes.ArrayListEx;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Helpers.SystemAccess;

import java.util.List;

public class RuleModelHelper
{
    private ArrayListEx<RuleModel> files;
	private Activity context;
	
	public RuleModelHelper(Context context)
	{
		if (this.context == null) 
		{
			this.context = (Activity)context;
			SystemAccess.Initialize(context);
            files = new ArrayListEx<RuleModel>();
		}
	}

	/*---------------------------------------------------------------------------
	 * 
	 * 
	 *                               Public methods
	 * 
	 * 
	 *---------------------------------------------------------------------------
	*/

    public  ArrayListEx<RuleModel> getCurrentOrAllFiles()
    {
        if (files == null || files.size() == 0)
            loadContacts("");
        return files;
    }
	
    /**
     * Loads the list of contacts with appropriate state.
     * @return
     * @param query
     */
    public List<RuleModel> loadModel(String query, boolean reloadContacts)
    {
        files.addAll(DbHelper.getFileList(), query);

        if (files.size() == 0 || reloadContacts)
        {
            loadContacts(query);
        }

    	try
    	{
            List<RuleModel> toTop = new ArrayListEx<RuleModel>();
            for (RuleModel m : files)
            {
                if (m.getLoveIt() || m.getBlockIt() || m.getMuteIt())
                {
                    // reverse here
                    toTop.add(0, m);
                }
            }

            if (toTop.size() > 0)
            {
                for (RuleModel m : toTop)
                {
                    files.remove(m);
                    // restore here
                    files.add(0, m);
                }
            }
    	}
    	catch(Exception ex)
    	{
    		ErrorHelper.ShowError("Rule loadModel() exception (" + DbHelper.getTimeStamp() + "): ", ex);
    	} 
		return files;
    }


	public void clearList()
    {
		try
		{
			setListSelection(false);
            DbHelper.updateFileList(files);
		}
    	catch(Exception ex)
    	{
            ErrorHelper.ShowError("clearBlackList() exception (" + DbHelper.getTimeStamp() + "): ", ex);
    	} 		
    }

	/*---------------------------------------------------------------------------
	 * 
	 * 
	 *                                Private methods
	 * 
	 * 
	 *---------------------------------------------------------------------------
	*/

    public void loadContacts(String query)
    {
        ArrayListEx<RuleModel> contacts = SystemAccess.getPhoneContacts();
        ArrayListEx<RuleModel> senders = SystemAccess.getSenderContacts();

        DbHelper.insertFileList(contacts);
        DbHelper.insertFileList(senders);

        files = new ArrayListEx<RuleModel>();
        files.addAll(DbHelper.getFileList(), query);
    }

	private void setListSelection(boolean checked)
	{
        for (RuleModel m : files)
        {
            m.setMuteIt(checked);
            m.setLoveIt(checked);
            m.setBlockIt(checked);
            m.setRejectCallsFromIt(checked);
        }
	}
}
