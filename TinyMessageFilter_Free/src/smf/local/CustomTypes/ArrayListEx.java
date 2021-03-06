package smf.local.CustomTypes;

import smf.local.Helpers.SystemAccess;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vitaly.chupaev
 * Date: 12/13/12
 * Time: 2:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class ArrayListEx<T extends IComparableEx> extends ArrayList<T> {
    public ArrayListEx distinct() {

        ArrayListEx newList = new ArrayListEx();
        for (IComparableEx k : this) {
            if (!this.containsKey(k.getUniqueKey()))
                newList.add(k);
        }
        return newList;
    }

    public boolean containsKey(String key) {
        for (IComparableEx k : this) {
            if (k.getUniqueKey().equals(key))
                return true;
        }
        return false;
    }

    public boolean containsNumber(String key) {
        for (IComparableEx k : this) {
            if (k.getNumber().equals(key))
                return true;
        }
        return false;
    }


    public void addAll(int index, List<T> list, String query) {
        if (SystemAccess.isNullOrBlank(query)) {
            addAll(index, list);
            return;
        }

        for (T c : list) {
            IQueryableContact ct = (IQueryableContact) c;

            if (containsText(ct.getNumber(), query) || containsText(ct.getName(), query)) {
                this.add(index, c);
            }
        }
    }

    public void addAll(List<T> list, String query) {
        if (SystemAccess.isNullOrBlank(query)) {
            addAll(list);
            return;
        }

        for (T c : list) {
            IQueryableContact ct = (IQueryableContact) c;

            if (containsText(ct.getNumber(), query) || containsText(ct.getName(), query)) {
                this.add(c);
            }
        }
    }

    public T getByKey(String key) {
        for (IComparableEx k : this) {
            if (k.getUniqueKey().equals(key))
                return (T) k;
        }
        return null;
    }

    public boolean removeByKey(String key) {
        for (IComparableEx k : this) {
            if (k.getUniqueKey().equals(key))
                this.remove(k);
        }
        return false;
    }

    public boolean removeByNumber(String number) {
        for (IComparableEx k : this) {
            if (k.getNumber().equals(number))
                this.remove(k);
        }
        return false;
    }

    public ArrayListEx<T> except(ArrayListEx<T> list) {
        ArrayListEx<T> newList = new ArrayListEx<T>();

        for (T c : this) {
            if (!list.containsNumber(c.getNumber()))
                newList.add(c);
        }

        return newList;
    }

    private boolean containsText(String text, String srchStr) {
        if (SystemAccess.isNull(text)) return false;

        return text.toLowerCase().contains(srchStr.toLowerCase());
    }
}
