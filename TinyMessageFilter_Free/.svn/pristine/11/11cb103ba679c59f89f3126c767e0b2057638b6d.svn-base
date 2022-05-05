package smf.local.Files;

import smf.local.CustomTypes.IComparableEx;
import smf.local.CustomTypes.IQueryableContact;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Vitaly.Chupaev
 * Date: 1/25/13
 * Time: 4:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileModel implements Serializable, IComparableEx, IQueryableContact {
    private Integer id;
    private Boolean blockIt;
    private Boolean loveIt;
    private Boolean muteIt;
    private Boolean isContact;
    private String name;
    private String code;

    /* TODO: implement change state */

    public FileModel(Integer id, int blockIt, int loveIt, int muteIt, int isContact, String name, String code) {
        this.id = id;
        this.name = name;
        this.blockIt = blockIt == 1 ? true : false;
        this.loveIt = loveIt == 1 ? true : false;
        this.muteIt = muteIt == 1 ? true : false;
        this.isContact = isContact == 1 ? true : false;
        this.code = code;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getBlockIt() {
        return blockIt;
    }

    public void setBlockIt(Boolean blockIt) {
        this.blockIt = blockIt;
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

    public String getNumber() {
        return code == null ? "" : code;
    }

    public void setNumber(String code) {
        this.code = code;
    }

    public String getName() {
        return name == null ? "" : name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUniqueKey() {
        return name + code;
    }

    public Boolean getIsContact() {
        return isContact;
    }

    public void setIsContact(Boolean contact) {
        isContact = contact;
    }
}
