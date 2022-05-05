package smf.local.Rules;

import smf.local.CustomTypes.IComparableEx;
import smf.local.CustomTypes.IQueryableContact;
import smf.local.R;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly.Chupaev
 * Date: 1/25/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class RuleModel implements Serializable, IComparableEx, IQueryableContact {
    private Integer id;
    private Boolean blockIt;
    private Boolean ignoreIt;
    private Boolean loveIt;
    private Boolean muteIt;
    private Boolean isContact;
    private String codePattern;
    private String bodyPattern;
    private Integer planeId;
    private String name;
    private int order;
    private Date lastDate;

    /* TODO: implement change state */

    public RuleModel(Integer id, int blockIt, int ignoreIt, int loveIt, int muteIt, int isContact, String codePattern, String bodyPattern, int planeId, String name, int order) {
        this.id = id;
        this.name = name;
        this.blockIt = blockIt == 1;
        this.ignoreIt = ignoreIt == 1;
        this.loveIt = loveIt == 1;
        this.muteIt = muteIt == 1;
        this.isContact = isContact == 1;
        this.bodyPattern = bodyPattern;
        this.planeId = planeId;
        this.codePattern = codePattern;
    }

    public RuleModel(Integer id, String name, String number, Date date, Boolean isContact)
    {
        this.id = id;
        this.name = name;
        this.codePattern = number;
        this.isContact = isContact;
        lastDate = date;
    }

    public RuleModel(Integer id, String name, String number)
    {
        this.id = id;
        this.name = name;
        this.codePattern = number;
/*
        this.isSelected = false;
        this.isFavorite = false;
*/
        this.isContact = true;
    }


    public Date getDate() {
        return lastDate;
    }

    public void setDate(Date date) {
        this.lastDate = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer orderNum) {
        this.order = order;
    }

    public Boolean getBlockIt() {
        return blockIt;
    }

    public void setBlockIt(Boolean blockIt) {
        this.blockIt = blockIt;
    }

    public Boolean getIgnoreIt() {
        return ignoreIt;
    }

    public void setIgnoreIt(Boolean ignoreIt) {
        this.ignoreIt = ignoreIt;
    }

    public Boolean getLoveIt() {
        return loveIt;
    }

    public void setLoveIt(Boolean loveIt) {
        this.loveIt = loveIt;
    }

    public Boolean getMuteIt() {
        return muteIt;
    }

    public void setMuteIt(Boolean muteIt) {
        this.muteIt = muteIt;
    }

    public String getPatternString() {
        String c = codePattern == null ? "" : codePattern;
        String b = bodyPattern == null ? "" : bodyPattern;
        if (c.length() > 0 && b.length() > 0) c += ", ";

        return c + b;
    }

    public String getNumber() {
        return codePattern == null ? "" : codePattern;
    }

    public void setNumber(String code) {
        this.codePattern = code;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUniqueKey() {
        return name + codePattern;
    }

    public Boolean getIsContact() {
        return isContact;
    }

    public void setIsContact(Boolean contact) {
        isContact = contact;
    }

    public String getBodyPattern() {
        return bodyPattern == null ? "" : bodyPattern;
    }

    public void setBodyPattern(String pattern) {
        bodyPattern = pattern;
    }

    public int getPlaneId() {
        return planeId <= 0 ? R.drawable.paper_plane_24 : planeId;
    }

    public void setPlaneId(int id) {
        planeId = id;
    }

}
