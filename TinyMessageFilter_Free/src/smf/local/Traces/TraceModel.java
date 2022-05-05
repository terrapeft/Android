package smf.local.Traces;

import smf.local.Helpers.DbHelper;

import java.net.URLEncoder;
import java.util.List;

public class TraceModel {

    private Integer id;
    private String description;

    public static String GetXml(List<TraceModel> models) {
        StringBuilder sb = new StringBuilder();
        sb.append("<log exportDate=\"" + DbHelper.getTimeStamp("dd.MM.yyyy") + "\">\n");

        for (TraceModel m : models) {
            sb.append("\t\t<record>" + URLEncoder.encode(m.getDescription()) + "</record>\n");
        }

        sb.append("</log>\n");
        return sb.toString();
    }

    public TraceModel(Integer id, String description) {
        this.setId(id);
        this.setDescription(description);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

} 
