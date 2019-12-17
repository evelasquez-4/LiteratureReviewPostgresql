package app.literature.review.model;

import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "documents")
public class DblpDocument {
	
	@Id
	private String _id;
	
	String ee="";
	String note="";
	ArrayList<String> keywords = new ArrayList<String>();
	int year=0;
	String stateMendeley="";
	String isbn="";
	@Field("abstract")
	String abstractDocument="";
	String type="";
	String title="";
	boolean mendeleySearched = false;
	String url="";
	int volume=0;
	String journal="";
	String pages="";
	String mdate="";
	String school="";
	String lastDateSearched="";
	String publisher="";
	String key="";
	@Field("authors")
	ArrayList<String> autores=new ArrayList<String>();	
	String doi = "";
	
	
	public DblpDocument(
			String ee,String note,
			ArrayList<String> keywords,int year,String stateMendeley,String isbn,
			String abstractDocument,String type,String title,
			boolean mendeleySearched,String url,int volume,
			String journal,String pages,String mdate,
			String school,String lastDateSearched,String publisher,String key,
			ArrayList<String> autores,
			String doi
			) 
	{
		this.ee = ee;
		this.note = note;
		this.keywords = keywords;
		this.year = year;
		this.stateMendeley=stateMendeley;
		this.isbn=isbn;
		this.abstractDocument=abstractDocument;
		this.type=type;
		this.title=title;
		this.mendeleySearched=mendeleySearched;
		this.url=url;
		this.volume=volume;
		this.journal=journal;
		this.pages=pages;
		this.mdate=mdate;
		this.school=school;
		this.lastDateSearched=lastDateSearched;
		this.publisher=publisher;
		this.key=key;
		this.autores=autores;
		this.doi=doi;
	}
	
	public DblpDocument() {}
	
	public ArrayList<String> getElementsAsString() {
		ArrayList<String> res = new ArrayList<String>();
		
		res.add("type");
		res.add("key");
		res.add("mdate");
		res.add("title");
		res.add("author");
		res.add("journal");
		res.add("pages");
		res.add("ee");
		res.add("volume");
		res.add("year");
		res.add("publisher");
		res.add("url");
		res.add("school");
		res.add("isbn");
		res.add("note");
			
		return res;
	}

	
	
	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getEe() {
		return ee;
	}

	public void setEe(String ee) {
		this.ee = ee;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public ArrayList<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getStateMendeley() {
		return stateMendeley;
	}

	public void setStateMendeley(String stateMendeley) {
		this.stateMendeley = stateMendeley;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getAbstractDocument() {
		return abstractDocument;
	}

	public void setAbstractDocument(String abstractDocument) {
		this.abstractDocument = abstractDocument;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isMendeleySearched() {
		return mendeleySearched;
	}

	public void setMendeleySearched(boolean mendeleySearched) {
		this.mendeleySearched = mendeleySearched;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	public String getPages() {
		return pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public String getMdate() {
		return mdate;
	}

	public void setMdate(String mdate) {
		this.mdate = mdate;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getLastDateSearched() {
		return lastDateSearched;
	}

	public void setLastDateSearched(String lastDateSearched) {
		this.lastDateSearched = lastDateSearched;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ArrayList<String> getAutores() {
		return autores;
	}

	public void setAutores(ArrayList<String> autores) {
		this.autores = autores;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}	
	
	public String toString() {
		return getType()+"\n"+getTitle();
	}
}
