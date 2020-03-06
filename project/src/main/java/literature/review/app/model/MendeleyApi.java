package literature.review.app.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MendeleyApi {
	
	String id,title,type,abstract_,source,year = "";
	List<String> identifiers = new ArrayList<String>();
	List<String> keywords = new ArrayList<String>();
	boolean updated = false;
	
	public MendeleyApi(JSONArray array,Publications pub)
	{
		if(array.length() == 0)
			cargarValoresPorDefecto();
		else {
		
			try {
				JSONObject obj = array.getJSONObject(0);
				
				String resumen = obj.has("abstract")?obj.getString("abstract"):"";
				String titulo = obj.has("title")?obj.getString("title"):"";
				
				if(obj.has("keywords"))
				{
					//obj.getJSONArray("")
				} 
				
				setAbstract_(resumen);
				setTitle(titulo);
				
				
				this.updated = verifyUpdated(pub);
				
			} catch (JSONException e) 
			{
				System.err.println("Mendeley API error"+ e.getMessage());
			}
		}
	}
	
	public boolean cargarKeyword(JSONArray keywords)
	{
		
		//DBConnect conn = null;
		
		
		return false;
	}
	
	public void cargarValoresPorDefecto()
	{
		this.id="";
		this.title="";
		this.type="";
		this.abstract_="";
		this.source="";
		this.year = "";
		this.identifiers = new ArrayList<String>();
		this.keywords = new ArrayList<String>();
		this.updated = false;
	}
	
	public boolean verifyUpdated(Publications p)
	{
		return  !this.getAbstract_().isEmpty() || 
						!p.getTitle().equalsIgnoreCase(this.getTitle());
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAbstract_() {
		return abstract_;
	}
	public void setAbstract_(String abstract_) {
		this.abstract_ = abstract_;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public List<String> getIdentifiers() {
		return identifiers;
	}
	public void setIdentifiers(List<String> identifiers) {
		this.identifiers = identifiers;
	}
	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	public boolean isUpdated() {
		return updated;
	}
	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
}
