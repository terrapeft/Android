package smf.local.Messages;

import smf.local.Helpers.DbHelper;

import java.net.URLEncoder;
import java.util.List;

public class MessageModel 
{

	private Integer id;
	private String sender;
	private String body;
	private String date;
	//private boolean isSelected;

    public static String GetXml(List<MessageModel> models)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<spambox exportDate=\"" + DbHelper.getTimeStamp("dd.MM.yyyy") + "\">\n");

        for (MessageModel m:models)
        {
            sb.append("\t<message>\n");

            sb.append("\t\t<sender>" + URLEncoder.encode(m.getSender()) + "</sender>\n");
            sb.append("\t\t<date>" + m.getDate() + "</date>\n");
            sb.append("\t\t<body>" + URLEncoder.encode(m.getBody()) + "</body>\n");

            sb.append("\t</message>\n");
        }

        sb.append("</spambox>\n");
        return sb.toString();
    }

	public MessageModel(Integer id, String sender, String body, String date) 
	{
		this.setId(id);
		this.setSender(sender);
		this.setBody(body);
		this.setDate(date);
		//this.isSelected = false;
	}

	/**
	 * @param sender
	 * @param body
	 * @param timestampMillis
	 */
	public MessageModel(String sender, String body, long timestampMillis) 
	{
		this.setSender(sender);
		this.setBody(body);
		this.setDate(Long.toString(timestampMillis));
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

} 
