package smf.local.Files;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import smf.local.Helpers.CacheHelper;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Helpers.SystemAccess;
import smf.local.R;
import smf.local.SimpleActivities.Statistics;

public class FileList extends ListActivity {

    private final static int UPDATE_LIST = 0;
    private final static int SHOW_ERROR = 1;

    ArrayAdapter<FileModel> adapter;
    FileModelHelper modelHelper;
    Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_LIST:
                    setListAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                case SHOW_ERROR:
                    ErrorHelper.ShowError((Exception) msg.obj);
                    break;
            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        registerForContextMenu(getListView());

        try {
            DbHelper.Initialize(this);
            ErrorHelper.Initialize(this);
            SystemAccess.Initialize(this);

            Intent intent = getIntent();
            String query = "";

            if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                query = intent.getStringExtra(SearchManager.QUERY);
            }

            loadContacts(query, false);
        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu1, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        FileModel mm = adapter.getItem(info.position);
        menu1.setHeaderTitle((mm.getName().length() == 0) ? mm.getNumber() : mm.getName());

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_context_menu, menu1);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            FileModel mm = adapter.getItem(info.position);

            // Handle item selection
            switch (item.getItemId()) {
                case R.id.editContactButton:
                    SystemAccess.startPatternDialog(this, R.layout.sender_pattern, mm);
                    break;
                case R.id.removeContactButton:
                    DbHelper.deleteFromFile(mm.getId());
                    adapter.remove(mm);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return super.onOptionsItemSelected(item);
            }

        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.file_menu, menu);
        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.searchButton:
                    onSearchRequested();
                    break;
                case R.id.unblockButton:
                    modelHelper.clearList();
                    adapter.notifyDataSetChanged();
                    break;
                case R.id.homeButton:
                    Intent cIntent = new Intent(this, Statistics.class);
                    startActivity(cIntent);
                    break;
                case R.id.refreshButton:
                    loadContacts("", true);
                    break;
                case R.id.helpButton:
                    SystemAccess.showHelpDialog(this, R.layout.file_help);
                    break;
                case R.id.addButton:
                    SystemAccess.startPatternDialog(this, R.layout.sender_pattern, null);
                    break;
                case R.id.exportButton:
                    String date = DbHelper.getTimeStamp("yyyy_MM_dd__HH_mm_ss");
                    String fname = "Senders-" + date + ".dat";
                    modelHelper = new FileModelHelper(this);
                    if (SystemAccess.serializeToFile("/TinyMessageFilter/Senders", fname, modelHelper.getCurrentOrAllFiles()))
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                default:
                    return super.onOptionsItemSelected(item);
            }

            clearCache();
        } catch (Exception ex) {
            ErrorHelper.ShowError(ex);
        }
        return true;
    }

    public void endPatternDialog(FileModel m) {
        DbHelper.insertFileModel(m);
        loadContacts("", true);
    }

    private void loadContacts(final String query, boolean clearCache) {
        final ListActivity view = this;

        if (clearCache) clearCache();

        final ProgressDialog pdlg = new ProgressDialog(view);
        pdlg.setMessage("Loading contacts...");
        pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdlg.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    modelHelper = new FileModelHelper(view);
                    adapter = new FileArrayAdapter(view, modelHelper.loadModel(query, true));
                    loadHandler.sendEmptyMessage(UPDATE_LIST);
                } catch (Exception ex) {
                    Message msg = loadHandler.obtainMessage();
                    msg.what = SHOW_ERROR;
                    msg.obj = ex;
                    loadHandler.sendMessage(msg);
                }
                pdlg.dismiss();
            }
        }).start();
    }

    /**
     *
     */
    private void clearCache() {
        CacheHelper.clear();
        SystemAccess.clear();
    }
}