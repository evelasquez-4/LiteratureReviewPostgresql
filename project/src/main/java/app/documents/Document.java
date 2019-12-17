package app.documents;

import java.util.ArrayList;

public class Document {
	
	String code;
	String title;
	String doi;
	String type;
	String url;
	String anio;
	String volumen;
	String key;
	String pages;
	String journal;
	String ee;
	String year;
	String publisher;
	String school;
	String isbn;
	String note;
	
	String abstractDocument;
	ArrayList<String> authors = new ArrayList<>();
	ArrayList<String> keywords = new ArrayList<>(); 
	
	boolean complete;
    boolean active;    
    String mendeleyState;
    String mdate;
    String number;
    
	
    public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getMdate() {
		return mdate;
	}
	public void setMdate(String mdate) {
		this.mdate = mdate;
	}
	public String getJournal() {
		return journal;
	}
	public void setJournal(String journal) {
		this.journal = journal;
	}
	public String getEe() {
		return ee;
	}
	public void setEe(String ee) {
		this.ee = ee;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getPublisher() {
		return publisher;
	}
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
    
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDoi() {
		return doi;
	}
	public void setDoi(String doi) {
		this.doi = doi;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getAbstractDocument() {
		return abstractDocument;
	}
	public void setAbstractDocument(String abstractDocument) {
		this.abstractDocument = abstractDocument;
	}
	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public String getVolumen() {
		return volumen;
	}
	public void setVolumen(String volumen) {
		this.volumen = volumen;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPages() {
		return pages;
	}
	public void setPages(String pages) {
		this.pages = pages;
	}
	public ArrayList<String> getAuthors() {
		return authors;
	}
	public void setAuthors(ArrayList<String> authors) {
		this.authors = authors;
	}
	public ArrayList<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(ArrayList<String> keywords) {
		this.keywords = keywords;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public boolean isComplete() {
		return complete;
	}
	public void setComplete(boolean complete) {
		this.complete = complete;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public String getMendeleyState() {
		return mendeleyState;
	}
	public void setMendeleyState(String mendeleyState) {
		this.mendeleyState = mendeleyState;
	}
	
	public String toString()
	{
		String aut = "";
		for (String string : this.authors) {
			aut += string+" ";
		}
		String res = "";
		
		res += "key -> "+this.key+"\nmadate->"+this.mdate+"\ntitle->"+
		this.title+"\nauthors->"+aut+"\nyear->"+this.year+"\nschool->"+this.school
		+"\npages->"+this.pages;
		
		return res;
	}

}
