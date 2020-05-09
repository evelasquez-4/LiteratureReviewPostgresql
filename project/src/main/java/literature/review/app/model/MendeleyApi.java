package literature.review.app.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MendeleyApi {
	
	private JSONArray array;
	private String title,abstract_ = "";
	private List<String> authors = new ArrayList<String>();
	private List<String> identifiers = new ArrayList<String>();
	private List<String> keywords = new ArrayList<String>();
	private int numElements = 0;

	public MendeleyApi() {
		this.array = new JSONArray();
		this.title = "";
		this.abstract_ = "";
		this.authors = new ArrayList<String>();
		this.identifiers = new ArrayList<String>();
		this.keywords = new ArrayList<String>();
		this.numElements = 0;
	}

	public MendeleyApi(JSONArray json, Publications pub) throws JSONException
	{
		
		try {
			this.array = json;
			String doi = pub.extractDOI();
			this.numElements = 0;
			if( doi.equals(""))
			{
				//search by title
				instanceByTitle(pub.getTitle());
				
			}else {
				this.array.iterator().forEachRemaining(element->{
					this.title = obtainTitle((JSONObject) element);
					this.abstract_ = obtainAbstract((JSONObject) element);
					this.authors = obtainAuthors((JSONObject) element);
					this.keywords = obtainKeywords((JSONObject) element);
					this.numElements++;
				});
			}
	
		} catch (JSONException e) 
		{
			System.err.println("Mendeley API error"+ e.getMessage());
		}
	}

	public void instanceByTitle(String tit)
	{
		String titulo = modifyTitle(tit);
		this.array.iterator().forEachRemaining( 
			(element)->{
				
				if( ((JSONObject) element).getString("title").equalsIgnoreCase (titulo) )
				{
					this.authors = obtainAuthors(((JSONObject) element));
					this.keywords = obtainKeywords( ((JSONObject) element));
					this.title = obtainTitle( (JSONObject)element );
					this.abstract_ = obtainAbstract((JSONObject) element);
					this.numElements++;
				}
			});
	}
	
	public String obtainTitle(JSONObject json)
	{
		return	json.has("title")? json.getString("title"):"";
	}
	public String obtainAbstract(JSONObject json)
	{
		return json.has("abstract") ? json.getString("abstract") : "";
	}
	
	public List<String> obtainAuthors(JSONObject json)
	{
		List<String> autores = new ArrayList<String>();
		if(json.has("authors"))
		{
			json.getJSONArray("authors").iterator().forEachRemaining(author->{
				String auth = ((JSONObject) author).getString("first_name")+" "+((JSONObject) author).getString("last_name");
				autores.add(auth);
			});
		}
		
		return autores;
	}
	
	public List<String> obtainKeywords(JSONObject json)
	{
		List<String> res = new ArrayList<String>();
		if(json.has("keywords"))
		{
			json.getJSONArray("keywords").iterator().forEachRemaining(key->{
				res.add(key.toString());
			});
		}
		return res;
	}
	public boolean hasResults()
	{
		return this.array.length() > 0;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAbstract_() {
		return abstract_;
	}

	public void setAbstract_(String abstract_) {
		this.abstract_ = abstract_;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
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

	public String modifyTitle(String cadena)
	{
		return cadena.endsWith(".")?cadena.substring(0,cadena.length()-1):cadena;
	}

	public int getNumElements() {
		return numElements;
	}

	public void setNumElements(int numElements) {
		this.numElements = numElements;
	}
	
}
