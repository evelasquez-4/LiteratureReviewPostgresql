package literature.review.app.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

public class SpringerApi {

	private JSONObject obj;
	private String abstract_,title;
	@SuppressWarnings("unused")
	private List<String> authors;
	@SuppressWarnings("unused")
	private List<String> keywords;
	private Integer numElements;
	
	
	public SpringerApi() {
		this.obj = new JSONObject();
		this.abstract_ = "";
		this.title = "";
		this.authors = new ArrayList<String>();
		this.keywords = new ArrayList<String>();
		this.numElements = 0;
		
	}

	public SpringerApi(JSONObject obj)
	{
		this.obj = obj;
		sizeResult();//count results
		if(this.numElements > 0)
		{
			this.authors = getAuthors();
			this.keywords = getKeywords();
			obtainTitle();
			obtainAbstract();
		}else
			new SpringerApi();
	}
	
	public List<String> getAuthors() throws JSONException
	{
		List<String> res = new ArrayList<String>();
		try 
		{
				this.obj.getJSONArray("records")
				.iterator()
				.forEachRemaining( records -> {
					 ((JSONObject) records)
					 .getJSONArray("creators")
					 .iterator().forEachRemaining( (creators)->{
						res.add( reverseAuthorNames( ((JSONObject) creators).getString("creator")) );
					 });
				});
		
		} catch (JSONException e) {
			System.out.println("error ->"+e.getMessage());
		}
		
		return res;
	} 
	
	public List<String> getKeywords()
	{
		List<String> res = new ArrayList<String>();
		try {
			this.obj.getJSONArray("facets")
			.iterator().forEachRemaining( (facet) -> {
		
				if( ((JSONObject) facet).getString("name").equals("keyword") )
				{
					((JSONObject) facet).getJSONArray("values")
					.iterator().forEachRemaining( keywords -> {
						
						res.add( ((JSONObject) keywords).getString("value") );
					});
				}
			});
		} catch (JSONException e) {
			System.out.println(e.getMessage());
		}
		return res;
	}
	
	public void obtainAbstract()
	{
		this.obj.getJSONArray("records").iterator().forEachRemaining((element)->{
			this.abstract_ = ((JSONObject) element).has("abstract")?
					((JSONObject) element).getString("abstract"):"";
		});
				
	}
	
	public void obtainTitle()
	{
		this.obj.getJSONArray("records").iterator().forEachRemaining((element)->{
			this.title = ((JSONObject) element).has("title")?
					((JSONObject) element).getString("title"):"";
		});
	}
	
	public void sizeResult()
	{
		  this.obj.getJSONArray("result")
				 .iterator()
				 .forEachRemaining((element)->{
					this.numElements =  Integer.parseInt( ((JSONObject) element).getString("total") );
				 });
	}
	
	public String reverseAuthorNames(String names)
	{
		String res = "";
		if(names.contains(",")) 
		{
			List<String> lista = Arrays.asList(names.split(","));
			Collections.reverse(lista);
			for (String cad : lista) {
				res += cad+" ";
			}
			return res.substring(0, res.length()-1);
		}else
			return names;
	}

	
	public JSONObject getObj() {
		return obj;
	}

	public void setObj(JSONObject obj) {
		this.obj = obj;
	}

	public String getAbstract_() {
		return abstract_;
	}

	public void setAbstract_(String abstract_) {
		this.abstract_ = abstract_;
	}

	public void setAuthors(List<String> authors) {
		this.authors = authors;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getNumElements() {
		return numElements;
	}

	public void setNumElements(Integer numElements) {
		this.numElements = numElements;
	}	
	
}
