package app.configuration.db.tables;
// Generated Sep 27, 2019, 11:41:57 PM by Hibernate Tools 5.2.12.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * ConferenceEditorial generated by hbm2java
 */
@Entity
@Table(name = "conference_editorial")
public class ConferenceEditorial implements java.io.Serializable {

	private int conferenceEditorialId;
	private Edition edition;
	private int id;
	private String title;
	private String publicationType;
	private String abstract_;
	private String doi;
	private Integer year;
	private Date createdAt;
	private String keyDblp;

	public ConferenceEditorial() {
	}

	public ConferenceEditorial(int conferenceEditorialId, int id) {
		this.conferenceEditorialId = conferenceEditorialId;
		this.id = id;
	}

	public ConferenceEditorial(int conferenceEditorialId, Edition edition, int id, String title, String publicationType,
			String abstract_, String doi, Integer year, Date createdAt, String keyDblp) {
		this.conferenceEditorialId = conferenceEditorialId;
		this.edition = edition;
		this.id = id;
		this.title = title;
		this.publicationType = publicationType;
		this.abstract_ = abstract_;
		this.doi = doi;
		this.year = year;
		this.createdAt = createdAt;
		this.keyDblp = keyDblp;
	}

	@Id

	@Column(name = "conference_editorial_id", unique = true, nullable = false)
	public int getConferenceEditorialId() {
		return this.conferenceEditorialId;
	}

	public void setConferenceEditorialId(int conferenceEditorialId) {
		this.conferenceEditorialId = conferenceEditorialId;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "edition_id")
	public Edition getEdition() {
		return this.edition;
	}

	public void setEdition(Edition edition) {
		this.edition = edition;
	}

	@Column(name = "id", nullable = false)
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "publication_type", length = 100)
	public String getPublicationType() {
		return this.publicationType;
	}

	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
	}

	@Column(name = "abstract")
	public String getAbstract_() {
		return this.abstract_;
	}

	public void setAbstract_(String abstract_) {
		this.abstract_ = abstract_;
	}

	@Column(name = "doi", length = 100)
	public String getDoi() {
		return this.doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	@Column(name = "year")
	public Integer getYear() {
		return this.year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", length = 29)
	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@Column(name = "key_dblp", length = 100)
	public String getKeyDblp() {
		return this.keyDblp;
	}

	public void setKeyDblp(String keyDblp) {
		this.keyDblp = keyDblp;
	}

}