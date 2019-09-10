package literature.review.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class DblpDocument {
	int id;
	String key_dblp;
	String mdate;
	Map<Integer, String> authors =new HashMap<Integer, String>();
	String doc_type;
	String editor;
	String booktitle;
	String pages;
	int year;
	String title;
	String address;
	String journal;
	String volume;
	String number;
	String month;
	String url;
	String ee;
	String cdrom;
	String cite;
	String publisher;
	String note;
	String crossref;
	String isbn;
	String series;
	String school; 
	String chapter;
	String pblnr;
	Map<String, String> unknow_fields;
	Map<String, String> unknow_atts;
	
	public DblpDocument() {}

	public ArrayList<String> getElementsAsString(){
		ArrayList<String> res = new ArrayList<String>();
		
		res.add("key_dblp");
		res.add("mdate");
		res.add("authors");
		res.add("doc_type");
		res.add("editor");
		res.add("booktitle");
		res.add("pages");
		res.add("year");
		res.add("title");
		res.add("address");
		res.add("journal");
		res.add("volume");
		res.add("number");
		res.add("month");
		res.add("url");
		res.add("ee");
		res.add("cdrom");
		res.add("cite");
		res.add("publisher");
		res.add("note");
		res.add("crossref");
		res.add("isbn");
		res.add("series");
		res.add("school");
		res.add("chapter");
		res.add("pblnr");
		res.add("unknow_fields");
		res.add("unknow_atts");
		
		return res;
	}
	
	public int getId() {
		return id;
	}

	public String getKey_dblp() {
		return key_dblp;
	}

	public String getMdate() {
		return mdate;
	}

	public  Map<Integer, String> getAuthors() {
		return authors;
	}

	public String getDoc_type() {
		return doc_type;
	}

	public String getEditor() {
		return editor;
	}

	public String getBooktitle() {
		return booktitle;
	}

	public String getPages() {
		return pages;
	}

	public int getYear() {
		return year;
	}

	public String getTitle() {
		return title;
	}

	public String getAddress() {
		return address;
	}

	public String getJournal() {
		return journal;
	}


	public String getNumber() {
		return number;
	}

	public String getMonth() {
		return month;
	}

	public String getUrl() {
		return url;
	}

	public String getEe() {
		return ee;
	}

	public String getCdrom() {
		return cdrom;
	}

	public String getCite() {
		return cite;
	}

	public String getPublisher() {
		return publisher;
	}

	public String getNote() {
		return note;
	}

	public String getCrossref() {
		return crossref;
	}

	public String getIsbn() {
		return isbn;
	}

	public String getSeries() {
		return series;
	}

	public String getSchool() {
		return school;
	}

	public String getChapter() {
		return chapter;
	}

	public String getPblnr() {
		return pblnr;
	}

	public Map<String, String> getUnknow_fields() {
		return unknow_fields;
	}

	public Map<String, String> getUnknow_atts() {
		return unknow_atts;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setKey_dblp(String key_dblp) {
		this.key_dblp = key_dblp;
	}

	public void setMdate(String mdate) {
		this.mdate = mdate;
	}

	public void setAuthors(Map<Integer, String> authors ) {
		this.authors = authors;
	}

	public void setDoc_type(String doc_type) {
		this.doc_type = doc_type;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public void setBooktitle(String booktitle) {
		this.booktitle = booktitle;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	
	public void setNumber(String number) {
		this.number = number;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setEe(String ee) {
		this.ee = ee;
	}

	public void setCdrom(String cdrom) {
		this.cdrom = cdrom;
	}

	public void setCite(String cite) {
		this.cite = cite;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setCrossref(String crossref) {
		this.crossref = crossref;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	public void setPblnr(String pblnr) {
		this.pblnr = pblnr;
	}

	public void setUnknow_fields(Map<String, String> unknow_fields) {
		this.unknow_fields = unknow_fields;
	}

	public void setUnknow_atts(Map<String, String> unknow_atts) {
		this.unknow_atts = unknow_atts;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}
	
	
}
