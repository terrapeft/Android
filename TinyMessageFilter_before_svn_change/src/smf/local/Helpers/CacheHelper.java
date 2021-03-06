/**
 *
 */
package smf.local.Helpers;

import smf.local.CustomTypes.ArrayListEx;
import smf.local.Files.FileModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vitaly.Chupaev
 */
public class CacheHelper {
    private static List<String> blackList;
    private static List<String> favList;
    private static List<String> silentList;
    private static List<String> callersList;
    private static ArrayListEx<FileModel> fileList;

    public static FileModel getFile(String sender) {
        if (fileList == null)
            loadLists();

        return fileList.bySender(sender);
    }


    public static ArrayListEx<FileModel> getFileList() {
        if (fileList == null)
            loadLists();

        return fileList;
    }

    public static List<String> getIgnoredCallersList() {
        if (callersList == null)
            loadLists();

        return callersList;
    }

    public static List<String> getBlackList() {
        if (blackList == null)
            loadLists();

        return blackList;
    }

    public static List<String> getFavorities() {
        if (favList == null)
            loadLists();

        return favList;
    }

    public static List<String> getSilentList() {
        if (silentList == null)
            loadLists();

        return silentList;
    }

    public static void clear() {
        fileList = null;
        blackList = null;
        favList = null;
        silentList = null;
        callersList = null;
    }

    public static boolean contains(List<String> collection, String searchFor) {
        for (String ci : collection) {
            if (ci.equalsIgnoreCase(searchFor)) return true;
        }

        return false;
    }

    public static boolean containsRegExp(List<String> collection, String searchFor) {
        for (String ci : collection) {
            Pattern pattern = Pattern.compile(ci);
            Matcher matcher = pattern.matcher(searchFor);

            if (matcher.find()) return true;
        }

        return false;
    }

    private static void loadLists() {
        fileList = DbHelper.getFileList();

        blackList = new ArrayList<String>();
        favList = new ArrayList<String>();
        silentList = new ArrayList<String>();
        callersList = new ArrayList<String>();


        for (FileModel m : fileList) {
            if (m.getIgnoreIt()) callersList.add(m.getNumber());
            if (m.getBlockIt()) blackList.add(m.getNumber());
            if (m.getLoveIt()) favList.add(m.getNumber());
            if (m.getMuteIt()) silentList.add(m.getNumber());
        }
    }

}
