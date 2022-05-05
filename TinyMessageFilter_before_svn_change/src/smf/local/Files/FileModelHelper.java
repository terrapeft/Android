/**
 *
 */
package smf.local.Files;

import android.app.Activity;
import android.content.Context;
import smf.local.CustomTypes.ArrayListEx;
import smf.local.CustomTypes.ContactModel;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Helpers.SystemAccess;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class FileModelHelper {
    private ArrayListEx<FileModel> files;
    private Activity context;

    public FileModelHelper(Context context) {
        if (this.context == null) {
            this.context = (Activity) context;
            SystemAccess.Initialize(context);
            files = new ArrayListEx<FileModel>();
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

    public ArrayListEx<FileModel> getCurrentOrAllFiles() {
        if (files == null || files.size() == 0)
            loadContacts("");
        return files;
    }

    /**
     * Loads the list of contacts with appropriate state.
     *
     * @param query
     * @return
     */
    public List<FileModel> loadModel(String query, boolean reloadContacts) {
        files.addAll(DbHelper.getFileList(), query);

        if (files.size() == 0 || reloadContacts) {
            loadContacts(query);
        }

        try {
            List<FileModel> toTop = new ArrayListEx<FileModel>();
            for (FileModel m : files) {
                if (m.getLoveIt() || m.getBlockIt() || m.getMuteIt()) {
                    // reverse here
                    toTop.add(0, m);
                }
            }

            if (toTop.size() > 0) {
                for (FileModel m : toTop) {
                    files.remove(m);
                    // restore here
                    files.add(0, m);
                }
            }
        } catch (Exception ex) {
            ErrorHelper.ShowError("File loadModel() exception (" + DbHelper.getTimeStamp() + "): ", ex);
        }
        return files;
    }


    public void clearList() {
        try {
            setListSelection(false);
            DbHelper.updateFileList(files);
        } catch (Exception ex) {
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

    public void loadContacts(String query) {
        ArrayListEx<ContactModel> contacts = SystemAccess.getPhoneContacts();
        ArrayListEx<ContactModel> senders = SystemAccess.getSenderContacts();

        DbHelper.insertFileList((ArrayListEx<ContactModel>) contacts);
        DbHelper.insertFileList((ArrayListEx<ContactModel>) senders);

        files = new ArrayListEx<FileModel>();
        files.addAll(DbHelper.getFileList(), query);
    }

    private void setListSelection(boolean checked) {
        for (FileModel m : files) {
            m.setMuteIt(checked);
            m.setLoveIt(checked);
            m.setBlockIt(checked);
        }
    }
}
