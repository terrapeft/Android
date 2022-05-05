/**
 * 
 */
package smf.local.Helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import smf.local.CustomTypes.ArrayListEx;
import smf.local.CustomTypes.SmsMessageEx;
import smf.local.Rules.RuleList;
import smf.local.Rules.RuleModel;
import smf.local.Messages.MessageModel;
import smf.local.R;

import java.io.*;
import java.util.*;

/**
 * @author Vitaly.Chupaev
 *
 */
public class SystemAccess 
{
	private static ArrayListEx<RuleModel> contacts;
	private static ArrayListEx<RuleModel> senders;
	private static List<String> contactsStr;
	private static List<String> sendersStr;

	private static Context context;
	
	
	// All available column names in SMS table
    // [_id, thread_id, address, 
	// person, date, protocol, read, 
	// status, type, reply_path_present, 
	// subject, body, service_center, 
	// locked, error_code, seen]
	
	// 1 Inbox = "content://sms/inbox"
	// 2 Failed = "content://sms/failed" 
	// 3 Queued = "content://sms/queued" 
	// 4 Sent = "content://sms/sent" 
	// 5 Draft = "content://sms/draft"
	// 6 Outbox = "content://sms/outbox"
	// 7 Undelivered = "content://sms/undelivered"
	// 8 All = "content://sms/all"
	// 9 Conversations = "content://sms/conversations"
	
	public static final String SMS_EXTRA_NAME = "pdus";
	public static final String SMS_URI = "content://sms";
	public static final String SMS_INBOX = "content://sms/inbox";
	
	public static final String ADDRESS = "address";
    public static final String PERSON = "person";
    public static final String DATE = "date";
    public static final String READ = "read";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final String SEEN = "seen";
    
    public static final int MESSAGE_TYPE_INBOX = 1;
    public static final int MESSAGE_TYPE_SENT = 2;
    
    public static final int MESSAGE_IS_NOT_READ = 0;
    public static final int MESSAGE_IS_READ = 1;
    
    public static final int MESSAGE_IS_NOT_SEEN = 0;
    public static final int MESSAGE_IS_SEEN = 1;


	public static void Initialize(Context context)
	{
        if (SystemAccess.context != null) return;
		SystemAccess.context = context;
		contactsStr = new ArrayList<String>();
		sendersStr = new ArrayList<String>();
	}

    public static void startPatternDialog(final Activity activity, int resource, final RuleModel em)
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(resource, (ViewGroup)activity.findViewById(R.id.patternDlg));

        if (em != null)
        {
            ((EditText) layout.findViewById(R.id.patternName)).setText(em.getName());
            ((EditText) layout.findViewById(R.id.editPattern)).setText(em.getNumber());
            ((EditText) layout.findViewById(R.id.editBodyPattern)).setText(em.getBodyPattern());

            ((CheckBox) layout.findViewById(R.id.checkbox_block)).setChecked(em.getBlockIt());
            //((CheckBox) layout.findViewById(R.id.checkbox_fav)).setChecked(em.getLoveIt());
            ((CheckBox) layout.findViewById(R.id.checkbox_calls)).setChecked(em.getIgnoreIt());
            ((CheckBox) layout.findViewById(R.id.checkbox_mute)).setChecked(em.getMuteIt());
            ((ImageView) layout.findViewById(R.id.planeImage)).setImageResource(em.getPlaneId());
        }

        final AlertDialog.Builder ad = new AlertDialog.Builder(activity);
        ad.setTitle("Add sender or pattern");
        ad.setView(layout);
        ad.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String codePattern = ((EditText) layout.findViewById(R.id.editPattern)).getText().toString();
                String bodyPattern = ((EditText) layout.findViewById(R.id.editBodyPattern)).getText().toString();
                String name = ((EditText) layout.findViewById(R.id.patternName)).getText().toString();
                Integer blockIt = ((CheckBox) layout.findViewById(R.id.checkbox_block)).isChecked() ? 1 : 0;
                //Integer loveIt = ((CheckBox) layout.findViewById(R.id.checkbox_fav)).isChecked() ? 1 : 0;
                Integer ignoreIt = ((CheckBox) layout.findViewById(R.id.checkbox_calls)).isChecked() ? 1 : 0;
                Integer muteIt = ((CheckBox) layout.findViewById(R.id.checkbox_mute)).isChecked() ? 1 : 0;

                RuleModel fm = new RuleModel(-1, blockIt, ignoreIt, 0, muteIt, 0, codePattern, bodyPattern, R.drawable.paper_plane_24, name, -1);

                if (em != null) fm.setId(em.getId());

                ((RuleList) activity).endPatternDialog(fm);
            }
        });
        ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        ad.create().show();
    }

    public static void showHelpDialog(Activity activity, int resource)
    {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(resource, (ViewGroup)activity.findViewById(R.id.fileHelpDlg));

        AlertDialog ad = new AlertDialog.Builder(activity).create();
        ad.setIcon(R.drawable.help);
        ad.setTitle("Quick help");
        ad.setView(layout);
        ad.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            } });
        ad.show();
    }

	/**
	 * @return contacts from the phone address book.
	 */
	public static ArrayListEx<RuleModel> getPhoneContacts()
	{
		if (contacts == null)
			loadContacts();
		return contacts;
	}
	
	/**
	 * @return senders from the sms inbox.
	 */
	public static ArrayListEx<RuleModel> getSenderContacts()
	{
		if (senders == null)
			loadUniqueSenders();
		return senders;
	}
	
	/**
	 * @return contacts from the phone address book.
	 */
	public static List<String> getPhoneNumbers()
	{
		if (contactsStr.size() == 0)
			loadContacts();
		return contactsStr;
	}
	
	/**
	 * @return contacts from the phone address book.
	 */
	public static List<String> getSenderNumbers()
	{
		if (sendersStr.size() == 0)
			loadUniqueSenders();
		return sendersStr;
	}
	
	public static void clear()
	{
		contacts = null;
		senders = null;
		contactsStr = new ArrayList<String>();
		sendersStr = new ArrayList<String>();
	}

    public static void RemoveNotification()
    {
        DbHelper.addTrace("RemoveNotification()");
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(SmsMessageEx.NOTIFICATION_ID);
    }


	public static void putSmsToInbox(ContentResolver contentResolver, MessageModel mm)
	{
		try
		{
			pushMessage(contentResolver, mm);
        }
        catch (Exception e) 
        {
            ErrorHelper.ShowError("putSmsToInbox() exception (" + DbHelper.getTimeStamp() + "): ", e);
        }
	}

    public static void putManySmsToInbox(ContentResolver contentResolver, String sender)
    {
        try
        {
            DbHelper.addTrace("Copy all to inbox from " + sender);
            List<MessageModel> mms = DbHelper.getMessages(sender);
            DbHelper.addTrace(mms.size() + " messages found.");
            for (MessageModel mm : mms)
            {
                pushMessage(contentResolver, mm);
            }
        }
        catch (Exception e)
        {
            ErrorHelper.ShowError("putManySmsToInbox() exception (" + DbHelper.getTimeStamp() + "): ", e);
        }
    }

    public static String getContactName(String number)
	{
        try
		{
			List<RuleModel> list = getPhoneContacts();

            for (RuleModel mm : list) {
                if (mm.getNumber().equals(number)) {
                    return mm.getName();
                }
            }
		}
        catch (Exception e) 
        {
            ErrorHelper.ShowError("getContactName() exception (" + DbHelper.getTimeStamp() + "): ", e);
        }
		return number;
	}

    public static boolean serializeToFile (String dir, String fileName, Object obj)
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            ErrorHelper.Log("serializeToFile(): Media state: " + Environment.getExternalStorageState());
            Toast.makeText(context, "No mounted storage found", Toast.LENGTH_LONG).show();
            return false;
        }

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + dir);
        myDir.mkdirs();

        File file = new File (myDir, fileName);
        if (file.exists ()) file.delete ();

        try
        {
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(obj);
            os.close();
            return true;
        }
        catch (Exception e)
        {
            ErrorHelper.ShowError("serializeToFile() exception (" + DbHelper.getTimeStamp() + "): ", e);
            return false;
        }
    }

    public static ArrayListEx<RuleModel> deserializeRules (String dir, String fileName)
    {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + dir);

        ArrayListEx<RuleModel> rules = new ArrayListEx<RuleModel>();
        File file = new File (myDir, fileName);

        try {

            FileInputStream fint = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fint);
            rules = (ArrayListEx<RuleModel>) ois.readObject();
            ois.close();
        }
        catch (Exception e) { e.printStackTrace(); }
            return rules;
    }

    public static boolean saveTextFile (String dir, String fileName, String data)
    {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            ErrorHelper.Log("saveTextFile(): Media state: " + Environment.getExternalStorageState());
            Toast.makeText(context, "No mounted storage found", Toast.LENGTH_LONG).show();
            return false;
        }

        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + dir);
        myDir.mkdirs();

        File file = new File (myDir, fileName);
        if (file.exists ()) file.delete ();
        try
        {
            FileOutputStream out = new FileOutputStream(file);
            out.write(data.getBytes());
            out.flush();
            out.close();
            return true;
        }
        catch (Exception e)
        {
            ErrorHelper.ShowError("saveTextFile() exception (" + DbHelper.getTimeStamp() + "): ", e);
            return false;
        }
    }

    public static boolean isNull(String str)
    {
        return str == null;
    }

    public static boolean isNullOrBlank(String param) {
        return isNull(param) || param.trim().length() == 0;
    }

    public static int getNewMessagesCount()
    {
        Cursor c = context.getContentResolver().query(Uri.parse(SystemAccess.SMS_INBOX), null, "read = 0", null, null);
        int unreadMessagesCount = c.getCount();
        c.deactivate();
        return unreadMessagesCount;
    }


	/*---------------------------------------------------------------------------
	 * 
	 * 
	 *                               Private methods
	 * 
	 * 
	 *---------------------------------------------------------------------------
	*/
	
	private static void pushMessage(ContentResolver contentResolver, MessageModel mm)
	{
		try
		{
			// Create SMS row
	        ContentValues values = new ContentValues();
	        values.put(ADDRESS, mm.getSender());
	        values.put(DATE, Long.parseLong(mm.getDate()));
	        values.put(READ, MESSAGE_IS_NOT_READ);
	        values.put(STATUS, -1);
	        values.put(TYPE, MESSAGE_TYPE_INBOX);
	        values.put(SEEN, MESSAGE_IS_NOT_SEEN);
	      	values.put(BODY, mm.getBody());
	        
	        // Push row into the SMS table
	        contentResolver.insert( Uri.parse( SMS_URI ), values );
		}
        catch (Exception e) 
        {
            ErrorHelper.ShowError("pushMessage() exception (" + DbHelper.getTimeStamp() + "): ", e);
        }
	}

	/**
	 * Loads distinct collection of senders who are also not in contact list.
	 */
	private static void loadUniqueSenders()
	{
		try 
		{
			senders = new ArrayListEx<RuleModel>();
			
			Uri uri = Uri.parse(SystemAccess.SMS_INBOX);
			
			Cursor c = context.getContentResolver().query(uri, null, null ,null,null);
			
			while(c.moveToNext()) 
			{
				String number = c.getString(c.getColumnIndex(SystemAccess.ADDRESS));
				String name = c.getString(c.getColumnIndex(SystemAccess.PERSON));
                Date date = new Date(c.getLong(c.getColumnIndex(SystemAccess.DATE)));

                RuleModel cm = new RuleModel(0, name, number, date, false);
                String key = cm.getUniqueKey();

                if (!senders.containsKey(key))
                {
                    senders.add(cm);
                }
			}
            c.close();

            if (contacts == null || contacts.size() == 0) loadContacts();
            senders = senders.except(contacts);

            Collections.sort(senders, new Comparator<RuleModel>() {
                public int compare(RuleModel cm1, RuleModel cm2) {
                    return cm1.getDate().compareTo(cm2.getDate());
                }
            });

            Collections.reverse(senders);

            for(RuleModel m : senders)
            {
                sendersStr.add(m.getNumber());
            }
		}
        catch (Exception e) 
        {
            ErrorHelper.ShowError("loadUniqueSenders() exception (" + DbHelper.getTimeStamp() + "): ", e);
        }
	}

    /**
     * Loads contacts from the address book.
     */
    private static void loadContacts()
    {
    	try
    	{
	    	contacts = new ArrayListEx<RuleModel>();
	    	Cursor c = context.getContentResolver().query(
	    			ContactsContract.Contacts.CONTENT_URI,
	    			new String[] {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
	    			null, null, null);

            if (c == null) return;

	    	while (c.moveToNext())
	    	{
	    		Integer id  = c.getInt(c.getColumnIndex(ContactsContract.Contacts._ID));
				String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Cursor c2 = context.getContentResolver().query(Phone.CONTENT_URI,
                        null, Phone.CONTACT_ID + "=?", new String[] { id.toString() }, null);

                if (c2 == null) continue;
                c2.moveToFirst();

                while (!c2.isAfterLast())
                {
                    String number = c2.getString(c2.getColumnIndex(Phone.NUMBER));

                    if (!contactsStr.contains(number))
                    {
                        contactsStr.add(number);
                        contacts.add(new RuleModel(id, name, number));
                    }

                    c2.moveToNext();
                }
	    	}
            c.close();

            Collections.sort(contactsStr);
            Collections.sort(contacts, new Comparator<RuleModel>() {
                public int compare(RuleModel cm1, RuleModel cm2) {
                    return cm1.getName().compareTo(cm2.getName());
                }
            });
    	}
        catch (Exception e) 
        {
            ErrorHelper.ShowError("loadContacts() exception (" + DbHelper.getTimeStamp() + "): ", e);
        }
    }
    
    /**
     * @return the photo URI
     */
    private static Uri getPhotoUri(String contactId) 
    {
        try 
        {
            Cursor cur = context.getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.CONTACT_ID + "=" + contactId + " AND "
                            + ContactsContract.Data.MIMETYPE + "='"
                            + ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE + "'", null,
                    null);
            
            if (cur != null) 
            {
                if (!cur.moveToFirst()) 
                {
                    return null; // no photo
                }
            } 
            else 
            {
                return null; // error in cursor process
            }
        } 
        catch (Exception e) 
        {
            ErrorHelper.ShowError("getPhotoUri() exception (" + DbHelper.getTimeStamp() + "): ", e);
            return null;
        }

        Uri person = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(contactId));
        return Uri.withAppendedPath(person, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
    }

    public static void removeNotification(int id)
    {
        ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(id);
    }
}
