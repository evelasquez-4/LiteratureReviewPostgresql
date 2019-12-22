package literature.review.app.model;
// Generated Dec 20, 2019, 7:40:21 PM by Hibernate Tools 5.2.12.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Publications generated by hbm2java
 */
@Entity
@Table(name = "publications", schema = "slr")
public class Publications implements java.io.Serializable {

	private long id;
	private String abstract_;
	private String title;
	private String pages;
	private Integer year;
	private String address;
	private String journal;
	private String volume;
	private String number;
	private String month;
	private String url;
	private String ee;
	private String cite;
	private String publisher;
	private String note;
	private String crossref;
	private String isbn;
	private String series;
	private String chapter;
	private String publnr;
	private String updatedState;
	private Date mdate;
	private String dblpKey;
	private String docType;
	private Set<AuthorPublications> authorPublicationses = new HashSet<AuthorPublications>(0);
	private Set<PublicationKeywords> publicationKeywordses = new HashSet<PublicationKeywords>(0);

	public Publications() {
	}

	public Publications(long id) {
		this.id = id;
	}

	public Publications(long id, String abstract_, String title, String pages, Integer year, String address,
			String journal, String volume, String number, String month, String url, String ee, String cite,
			String publisher, String note, String crossref, String isbn, String series, String chapter, String publnr,
			String updatedState, Date mdate, String dblpKey, String docType,
			Set<AuthorPublications> authorPublicationses, Set<PublicationKeywords> publicationKeywordses) {
		this.id = id;
		this.abstract_ = abstract_;
		this.title = title;
		this.pages = pages;
		this.year = year;
		this.address = address;
		this.journal = journal;
		this.volume = volume;
		this.number = number;
		this.month = month;
		this.url = url;
		this.ee = ee;
		this.cite = cite;
		this.publisher = publisher;
		this.note = note;
		this.crossref = crossref;
		this.isbn = isbn;
		this.series = series;
		this.chapter = chapter;
		this.publnr = publnr;
		this.updatedState = updatedState;
		this.mdate = mdate;
		this.dblpKey = dblpKey;
		this.docType = docType;
		this.authorPublicationses = authorPublicationses;
		this.publicationKeywordses = publicationKeywordses;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "abstract")
	public String getAbstract_() {
		return this.abstract_;
	}

	public void setAbstract_(String abstract_) {
		this.abstract_ = abstract_;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "pages", length = 100)
	public String getPages() {
		return this.pages;
	}

	public void setPages(String pages) {
		this.pages = pages;
	}

	@Column(name = "year")
	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Column(name = "address")
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Column(name = "journal", length = 200)
	public String getJournal() {
		return this.journal;
	}

	public void setJournal(String journal) {
		this.journal = journal;
	}

	@Column(name = "volume")
	public String getVolume() {
		return this.volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	@Column(name = "number")
	public String getNumber() {
		return this.number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	@Column(name = "month")
	public String getMonth() {
		return this.month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	@Column(name = "url")
	public String getUrl() {
		return this.url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Column(name = "ee")
	public String getEe() {
		return this.ee;
	}

	public void setEe(String ee) {
		this.ee = ee;
	}

	@Column(name = "cite")
	public String getCite() {
		return this.cite;
	}

	public void setCite(String cite) {
		this.cite = cite;
	}

	@Column(name = "publisher")
	public String getPublisher() {
		return this.publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	@Column(name = "note")
	public String getNote() {
		return this.note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Column(name = "crossref")
	public String getCrossref() {
		return this.crossref;
	}

	public void setCrossref(String crossref) {
		this.crossref = crossref;
	}

	@Column(name = "isbn")
	public String getIsbn() {
		return this.isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	@Column(name = "series")
	public String getSeries() {
		return this.series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	@Column(name = "chapter")
	public String getChapter() {
		return this.chapter;
	}

	public void setChapter(String chapter) {
		this.chapter = chapter;
	}

	@Column(name = "publnr")
	public String getPublnr() {
		return this.publnr;
	}

	public void setPublnr(String publnr) {
		this.publnr = publnr;
	}

	@Column(name = "updated_state", length = 150)
	public String getUpdatedState() {
		return this.updatedState;
	}

	public void setUpdatedState(String updatedState) {
		this.updatedState = updatedState;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "mdate", length = 13)
	public Date getMdate() {
		return this.mdate;
	}

	public void setMdate(Date mdate) {
		this.mdate = mdate;
	}

	@Column(name = "dblp_key")
	public String getDblpKey() {
		return this.dblpKey;
	}

	public void setDblpKey(String dblpKey) {
		this.dblpKey = dblpKey;
	}

	@Column(name = "doc_type", length = 100)
	public String getDocType() {
		return this.docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "publications")
	public Set<AuthorPublications> getAuthorPublicationses() {
		return this.authorPublicationses;
	}

	public void setAuthorPublicationses(Set<AuthorPublications> authorPublicationses) {
		this.authorPublicationses = authorPublicationses;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "publications")
	public Set<PublicationKeywords> getPublicationKeywordses() {
		return this.publicationKeywordses;
	}

	public void setPublicationKeywordses(Set<PublicationKeywords> publicationKeywordses) {
		this.publicationKeywordses = publicationKeywordses;
	}

}
