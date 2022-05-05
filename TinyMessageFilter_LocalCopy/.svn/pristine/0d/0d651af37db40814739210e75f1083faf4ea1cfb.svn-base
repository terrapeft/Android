/**
 * 
 */
package smf.local.Messages;

import android.content.Context;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.SystemAccess;

import java.util.List;

public class MessageModelHelper 
{
	private List<MessageModel> messages;
	private Context context;
	
	public MessageModelHelper(Context context)
	{
        this.context = context;
        SystemAccess.Initialize(context);
    }
	
	
	/*---------------------------------------------------------------------------
	 * 
	 * 
	 *                               Public methods
	 * 
	 * 
	 *---------------------------------------------------------------------------
	*/
	
	
    /**
     * Loads the list of contacts with appropriate state.
     * @return
     */
    public List<MessageModel> loadModel()
    {
        return DbHelper.getMessages();
    }
    
	
	/*---------------------------------------------------------------------------
	 * 
	 * 
	 *                               Private methods
	 * 
	 * 
	 *---------------------------------------------------------------------------
	*/
	
}
