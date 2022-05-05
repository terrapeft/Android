package smf.local.Tests;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly.Chupaev
 * Date: 5/24/13
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleModelCollectionTest {

    @Test
    public void testMatch()
    {
        //RuleModelCollection rmc = new RuleModelCollection();
        //rmc.add(new RuleModel(0, "Test", "123$"));
        //rmc.match("123");

        Matcher matcher = Pattern.compile("123-17-56").matcher("56");
        Boolean f = matcher.find();
        Boolean m = matcher.matches();
        String g = "";
        if (m) g = matcher.group();


        Matcher matcher1 = Pattern.compile("123$").matcher("23");
        Boolean f1 = matcher1.find();
        Boolean m1 = matcher1.matches();
        String g1 = "";
        if (m1) g1 = matcher1.group();

        Matcher matcher2 = Pattern.compile("123$").matcher("77");
        Boolean f2 = matcher2.find();
        Boolean m2 = matcher2.matches();
        String g2 = "";
        if (m2) g2 = matcher2.group();

        Matcher matcher3 = Pattern.compile("123").matcher("23");
        Boolean f3 = matcher3.find();
        Boolean m3 = matcher3.matches();
        String g3 = "";
        if (m3) g3 = matcher3.group();

        String s = "a";
    }
}
