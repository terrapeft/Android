/*
package smf.local.CustomTypes;

import java.util.Date;

public class ContactModel implements IComparableEx, IQueryableContact
{

	private String contactId; 
	private String contactNumber;
	private String contactName;
	private boolean isSelected;
	private boolean isContact;
	private boolean isFavorite;
    private Date lastDate;

    public ContactModel(String id, String name, String number)
	{
		this.contactId = id;
		this.contactName = name;
		this.contactNumber = number;
		this.isSelected = false;
		this.isFavorite = false;
		this.isContact = true;
	}

	public ContactModel(String id, String name, String number, Date date, Boolean isContact)
	{
		this.contactId = id;
		this.contactName = name;
		this.contactNumber = number;
		this.isSelected = false;
		this.isFavorite = false;
		this.isContact = isContact;
        lastDate = date;
	}

    public String getName() {
		return contactName == null ? "" : contactName;
	}

	public void setName(String name) {
		this.contactName = name;
	}

    public Date getDate() {
        return lastDate;
    }

    public void setDate(Date date) {
        this.lastDate = date;
    }

    public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		this.isSelected = selected;
    }

	public String getNumber() {
		return contactNumber;
	}

	public void setNumber(String number) {
		this.contactNumber = number;
	}

	public boolean isContact() {
		return isContact;
	}

	public void setContact(boolean isContact) {
		this.isContact = isContact;
	}

	public String getContactId() {
		return contactId;
	}

	public void setContactId(String contactId) {
		this.contactId = contactId;
	}

	public boolean isFavorite() {
		return isFavorite;
	}

	public void setFavorite(boolean isFavorite) {
		this.isFavorite = isFavorite;
	}

    @Override
    public String getUniqueKey() {
        return contactName + contactNumber;
    }

}
*/
