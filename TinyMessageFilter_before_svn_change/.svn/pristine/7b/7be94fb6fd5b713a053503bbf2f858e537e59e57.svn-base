/**
 * 
 */
package smf.local.CustomTypes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import smf.local.R;

/**
 * @author Vitaly.Chupaev
 *
 */
public class UserPrefs 
{
	private boolean isBlock;
	private boolean isKeepSpam;
	private boolean isBlockUnknown;
	private boolean isNotifyForUnknown;
	private boolean isShowPlane;
	private boolean isDoLogging;
    private boolean isShowSentTime;
	
	public UserPrefs(Context context)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		setBlackList(prefs.getBoolean(context.getString(R.string.pref_block), false));
		setKeepSpam(prefs.getBoolean(context.getString(R.string.pref_keepSpam), false));
		setBlockUnknown(prefs.getBoolean(context.getString(R.string.pref_blockUnknown), false));
		setNotifyOfBlockedMessages(prefs.getBoolean(context.getString(R.string.pref_notify), false));
		//setShowCustomIcons(prefs.getBoolean(context.getString(R.string.pref_plane), false));
		setDoLogging(prefs.getBoolean(context.getString(R.string.pref_trace), false));
        setShowSentTime(prefs.getBoolean(context.getString(R.string.pref_sentTime), false));
	}

	public boolean useBlackList() {
		return isBlock;
	}

	private void setBlackList(boolean isBlock) {
		this.isBlock = isBlock;
	}

	public boolean keepSpam() {
		return isKeepSpam;
	}

	public void setKeepSpam(boolean isKeepSpam) {
		this.isKeepSpam = isKeepSpam;
	}

	public boolean blockUnknown() {
		return isBlockUnknown;
	}

	private void setBlockUnknown(boolean isBlockUnknown) {
		this.isBlockUnknown = isBlockUnknown;
	}

	public boolean notifyOfBlockedMessages() {
		return isNotifyForUnknown;
	}

	private void setNotifyOfBlockedMessages(boolean isNotifyForUnknown) {
		this.isNotifyForUnknown = isNotifyForUnknown;
	}

	public boolean showCustomIcons() {
		return isShowPlane;
	}

	 private void setShowCustomIcons(boolean isShowPlane) {
		this.isShowPlane = isShowPlane;
	}
	 
	public boolean isDoLogging() {
		return isDoLogging;
	}

	private void setDoLogging(boolean isDoLogging) {
		this.isDoLogging = isDoLogging;
	}

    public boolean isShowSentTime() {
        return isShowSentTime;
    }

    public void setShowSentTime(boolean showSentTime) {
        isShowSentTime = showSentTime;
    }
}
