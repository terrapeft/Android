/**
 * 
 */
package smf.local.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Vitaly.Chupaev
 *
 */
public class ErrorHelper {
	
	private static AlertDialog errorBox;
	private static Context context;
	
	public static void Initialize(Context context)
	{
		ErrorHelper.context = context;
	}


    public static void ShowError(String info, Exception ex)
    {
        ShowError(info, ex, false);
    }

    public static void ShowError(Exception ex)
    {
        ShowError("", ex, false);
    }

    /**
	 * @param ex
	 */
	public static void ShowError(String info, Exception ex, boolean showError)
    {
        if (ex == null || context == null) return;

        String msg = ex.getMessage();
        String report = msg;
        if (msg == null) msg = ex.toString();

        Log(info + (info.length() > 0 ? ",\n" : "") + report + ", " + Log.getStackTraceString(ex));

        if (!showError) return;

        if (errorBox == null) InitDialog();
        errorBox.setMessage(msg);
        errorBox.show();
    }
	
	/**
	 * @param msg
	 */
	public static void ShowMessage(String msg) 
	{
		if (context == null) return;
		if (errorBox == null) InitDialog();
		
		errorBox.setMessage(msg);
		errorBox.show();
	}

    public static void Log(String message)
    {
        DbHelper.addTrace(message);
    }
	
	public static void ShowBalloon(String msg)
	{
		if (context == null) return;
		Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * 
	 */
	private static void InitDialog()
	{
		errorBox = new AlertDialog.Builder(context).create();
		errorBox.setCancelable(false); // This blocks the 'BACK' button
		errorBox.setButton("OK", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.dismiss();                    
		    }
		});
	}

}
