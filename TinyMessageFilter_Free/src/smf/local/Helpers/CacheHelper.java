/**
 *
 */
package smf.local.Helpers;

import smf.local.Files.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vitaly.Chupaev
 */
public class CacheHelper {
    private static List<String> blackList;
    private static List<String> favList;
    private static List<String> silentList;

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
        blackList = null;
        favList = null;
        silentList = null;
    }

    public static boolean contains(List<String> collection, String searchFor) {
        for (String ci : collection) {
            if (ci.equalsIgnoreCase(searchFor)) return true;
        }

        return false;
    }

    private static void loadLists() {
        List<FileModel> fm = DbHelper.getFileList();

        blackList = new ArrayList<String>();
        favList = new ArrayList<String>();
        silentList = new ArrayList<String>();

        for (FileModel m : fm) {
            if (m.getBlockIt()) blackList.add(m.getNumber());
            if (m.getLoveIt()) favList.add(m.getNumber());
            if (m.getMuteIt()) silentList.add(m.getNumber());
        }
    }

}
