package literature.review.app.model;
// Generated Dec 20, 2019, 7:40:21 PM by Hibernate Tools 5.2.12.Final

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * PublicationKeywords generated by hbm2java
 */
@Entity
@Table(name = "publication_keywords", schema = "slr")
@JsonIgnoreProperties({"hibernateLazyinitializer","handler"})
public class PublicationKeywords implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "keyword_id")
	@JsonIgnore
	private Keywords keywords;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "publication_id")
	@JsonIgnore
	private Publications publications;
	private Date createdAt;

	public PublicationKeywords() {
	}

	public PublicationKeywords(long id) {
		this.id = id;
	}

	public PublicationKeywords(long id, Keywords keywords, Publications publications, Date createdAt) {
		this.id = id;
		this.keywords = keywords;
		this.publications = publications;
		this.createdAt = createdAt;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	public Keywords getKeywords() {
		return this.keywords;
	}

	public void setKeywords(Keywords keywords) {
		this.keywords = keywords;
	}

	
	public Publications getPublications() {
		return this.publications;
	}

	public void setPublications(Publications publications) {
		this.publications = publications;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "created_at", length = 13)
	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
