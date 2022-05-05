/**
 * 
 */
package smf.local.Helpers;

import smf.local.CustomTypes.ArrayListEx;
import smf.local.Rules.RuleModel;
import smf.local.Rules.RuleModelCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author Vitaly.Chupaev
 *
 */
public class CacheHelper 
{
	private static List<String> blackList;
	private static List<String> favList;
	private static List<String> silentList;
    private static List<String> callersList;
    private static RuleModelCollection fileList;

    public static RuleModel getFile(String sender)
    {
        if (fileList == null)
            loadLists();

        return fileList.bySender(sender);
    }


    public static ArrayListEx<RuleModel> getFileList()
    {
        if (fileList == null)
            loadLists();

        return fileList;
    }

    public static List<String> getIgnoredCallersList()
    {
        if (callersList == null)
            loadLists();

        return callersList;
    }

    public static List<String> getBlockBodyPatterns(String sender)
    {
        if (fileList == null)
            loadLists();

        return fileList.bodyBlockPatternsBySenderPattern(sender);
    }

    public static List<String> getBlackList()
	{
		if (blackList == null)
            loadLists();

		return blackList;
	}

    public static List<String> getFavorities()
	{
		if (favList == null)
            loadLists();

		return favList;
	}

    public static List<String> getSilentList()
    {
        if (silentList == null)
            loadLists();

        return silentList;
    }

    public static void clear()
	{
		fileList = null;
        blackList = null;
		favList = null;
		silentList = null;
        callersList = null;
	}

    public static boolean contains(List<String> collection, String searchFor)
    {
        for (String ci : collection)
        {
            if (ci.equalsIgnoreCase(searchFor)) return true;
        }

        return false;
    }

    public static boolean containsRegExp(List<String> collection, String searchFor)
	{
        Boolean _return = false;
		for (String ci : collection)
		{
            _return = Pattern.compile(ci).matcher(searchFor).find();
            if (_return) break;
		}
		
		return _return;
	}

    public static boolean containsRegExp(List<String> collection, String searchFor, StringBuilder usedRule)
    {
        Boolean _return = false;
        for (String ci : collection)
        {
            _return = Pattern.compile(ci).matcher(searchFor).find();
            if (_return)
            {
                usedRule.append(ci);
                break;
            }
        }

        return _return;
    }

    private static void loadLists()
    {
        fileList = DbHelper.getFileList();

        blackList = new ArrayList<String>();
        favList = new ArrayList<String>();
        silentList = new ArrayList<String>();
        callersList = new ArrayList<String>();


        for (RuleModel m : fileList.exceptBodyPatterns())
        {
            if (m.getRejectCallsFromIt()) callersList.add(m.getNumber());
            if (m.getBlockIt()) blackList.add(m.getNumber());
            if (m.getLoveIt()) favList.add(m.getNumber());
            if (m.getMuteIt()) silentList.add(m.getNumber());
        }
    }

}
