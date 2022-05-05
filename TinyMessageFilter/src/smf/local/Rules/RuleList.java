package smf.local.Rules;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.*;
import android.widget.*;
import smf.local.CustomTypes.ArrayListEx;
import smf.local.Helpers.CacheHelper;
import smf.local.Helpers.DbHelper;
import smf.local.Helpers.ErrorHelper;
import smf.local.Helpers.SystemAccess;
import smf.local.R;
import smf.local.SimpleActivities.Statistics;

import java.util.ArrayList;

public class RuleList extends ListActivity {

    private final static int UPDATE_LIST = 0;
    private final static int SHOW_ERROR = 1;

    ArrayAdapter<RuleModel> adapter;
    RuleModelHelper modelHelper;
    Handler loadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case UPDATE_LIST:
                    setListAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;
                case SHOW_ERROR:
                    ErrorHelper.ShowError((Exception)msg.obj);
                    break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rule);
        registerForContextMenu(getListView());

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setSelector(android.R.color.white);

        SetPanelButtons();

        try
        {
            DbHelper.Initialize(this);
            ErrorHelper.Initialize(this);
            SystemAccess.Initialize(this);

            Intent intent = getIntent();
            String query = "";

            if (Intent.ACTION_SEARCH.equals(intent.getAction()))
            {
                query = intent.getStringExtra(SearchManager.QUERY);
            }

            loadContacts(query, false);
        }
        catch (Exception ex)
        {
            ErrorHelper.ShowError(ex);
        }
    }

    private void SetPanelButtons()
    {
        final Context ctx = this;

        final Spinner spinner1 = (Spinner) findViewById(R.id.spinner1);

        ArrayList<Integer> data = new ArrayList<Integer>();
        data.add(R.drawable.plane_black);
        data.add(R.drawable.plane_cyane);
        data.add(R.drawable.plane_green);
        data.add(R.drawable.plane_pink);
        data.add(R.drawable.plane_red);
        data.add(R.drawable.plane_violet);
        data.add(R.drawable.plane_yellow);

        PlaneIconsAdapter iAdapter = new PlaneIconsAdapter(this, android.R.layout.simple_spinner_item, data);
        spinner1.setAdapter(iAdapter);

        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ctx, "Here", Toast.LENGTH_SHORT).show();
                if(spinner1.getSelectedItem() != null)
                {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(ctx, "Plane", Toast.LENGTH_SHORT).show();
            }
        })  ;

        Button upButton = (Button)this.findViewById(R.id.pbutton_up);
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(ctx, "Up", Toast.LENGTH_SHORT).show();
            }
        });

        Button downButton = (Button)this.findViewById(R.id.pbutton_down);
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Toast.makeText(ctx, "Down", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu1, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        RuleModel mm = adapter.getItem(info.position);
        menu1.setHeaderTitle((mm.getName().length() == 0) ? mm.getNumber() : mm.getName());

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.file_context_menu, menu1);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        try
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            RuleModel mm = adapter.getItem(info.position);

            // Handle item selection
            switch (item.getItemId())
            {
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
            inflater.inflate(R.menu.file_menu, menu);
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
            // Handle item selection
            switch (item.getItemId())
            {
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
                    SystemAccess.showHelpDialog(this, R.layout.rule_help);
                    break;
                case R.id.addButton:
                    SystemAccess.startPatternDialog(this, R.layout.sender_pattern, null);
                    break;
                case R.id.exportButton:
                    String fname = "Rules.dat";
                    modelHelper = new RuleModelHelper(this);
                    if (SystemAccess.serializeToFile("/TinyMessageFilter/Rules", fname, modelHelper.getCurrentOrAllFiles()))
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.importButton:
                    String fname2 = "Rules.dat";
                    ArrayListEx<RuleModel> rules = SystemAccess.deserializeRules("/TinyMessageFilter/Rules", fname2);
                    DbHelper.deleteFromTable(DbHelper.FILE_TABLE);
                    DbHelper.insertFileList(rules);
                    loadContacts("", true);
                    Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                    break;
                 default:
                    return super.onOptionsItemSelected(item);
            }

            clearCache();
        }
        catch (Exception ex)
        {
            ErrorHelper.ShowError(ex);
        }
        return true;
    }

    public void endPatternDialog(RuleModel m)
    {
        DbHelper.insertFileModel(m);
        loadContacts("", true);
    }

    private void loadContacts(final String query, boolean clearCache)
    {
        final ListActivity view = this;

        if (clearCache) clearCache();

        final ProgressDialog pdlg = new ProgressDialog(view);
        pdlg.setMessage("Loading contacts...");
        pdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pdlg.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    if (modelHelper == null) modelHelper = new RuleModelHelper(view);
                    if (adapter == null) adapter = new RuleArrayAdapter(view, modelHelper.loadModel(query, true));
                    loadHandler.sendEmptyMessage(UPDATE_LIST);
                }
                catch (Exception ex)
                {
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
    private void clearCache()
    {
        adapter = null;
        modelHelper = null;
        CacheHelper.clear();
        SystemAccess.clear();
    }
}