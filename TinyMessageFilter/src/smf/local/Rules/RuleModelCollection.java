package smf.local.Rules;

import smf.local.CustomTypes.ArrayListEx;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly.Chupaev
 * Date: 5/23/13
 * Time: 6:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleModelCollection extends ArrayListEx<RuleModel>
{

    public Boolean match(String pattern, String searchFor)
    {
        return Pattern.compile(pattern).matcher(searchFor).find();
    }

    public List<String> bodyBlockPatternsBySenderPattern(String sender)
    {
        List<String> l = new ArrayList<String>();
        for (RuleModel c : this)
        {
            if (c.getBlockIt() && (c.getNumber().length() == 0 || match(c.getNumber(), sender)))
                l.add(c.getBodyPattern());
        }
        return l;
    }

    public RuleModel bySender(String sender)
    {
        for (RuleModel c : this)
        {
            if (c.getNumber().equals(sender))
                return (RuleModel)c;
        }
        return null;
    }
    public void Up() {}
    public void Down() {}

    public List<RuleModel> exceptBodyPatterns() {
        List<RuleModel> l = new ArrayList<RuleModel>();
        for (RuleModel c : this)
        {
            if (c.getNumber().length() > 0 && c.getBodyPattern().length() == 0)
                l.add(c);
        }
        return l;
    }
}
