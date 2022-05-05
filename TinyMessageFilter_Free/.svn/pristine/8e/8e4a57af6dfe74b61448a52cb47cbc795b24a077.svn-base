/**
 * 
 */
package smf.local.Traces;

import android.content.Context;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.SystemAccess;

import java.util.List;

public class TracesModelHelper 
{
	private List<TraceModel> traces;
	private Context context;
	
	public TracesModelHelper(Context context)
	{
		if (this.context == null) 
		{
			this.context = context;
			SystemAccess.Initialize(context);
			
			traces = DbHelper.getTraces();
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
	
	
    /**
     * Loads the list of contacts with appropriate state.
     * @return
     */
    public List<TraceModel> loadModel()
    {
		return traces;
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
