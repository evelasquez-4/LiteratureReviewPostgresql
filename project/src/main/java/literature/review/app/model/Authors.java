package literature.review.app.model;
// Generated Dec 20, 2019, 7:40:21 PM by Hibernate Tools 5.2.12.Final

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Authors generated by hbm2java
 */
@Entity
@Table(name = "authors", schema = "slr")
@JsonIgnoreProperties({"hibernateLazyinitializer","handler"})
public class Authors implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	@JsonIgnore
	private Departments departments;
	
	@Column(name = "names")
	private String names;
	
	@Column(name = "email", length = 200)
	private String email;
	
	@Column(name = "picture_file")
	private String pictureFile;
	
	@Column(name = "home_page")
	private String homePage;
	
	@Temporal(TemporalType.DATE)
	@Column(name = "created_at", length = 13)
	private Date createdAt;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "authors")
	private Set<AuthorPublications> authorPublicationses = new HashSet<AuthorPublications>(0);

	public Authors() {
	}

	public Authors(long id) {
		this.id = id;
	}

	public Authors(long id, Departments departments, String names, String email, String pictureFile, String homePage,
			Date createdAt, Set<AuthorPublications> authorPublicationses) {
		this.id = id;
		this.departments = departments;
		this.names = names;
		this.email = email;
		this.pictureFile = pictureFile;
		this.homePage = homePage;
		this.createdAt = createdAt;
		this.authorPublicationses = authorPublicationses;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Departments getDepartments() {
		return this.departments;
	}

	public void setDepartments(Departments departments) {
		this.departments = departments;
	}

	public String getNames() {
		return this.names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPictureFile() {
		return this.pictureFile;
	}

	public void setPictureFile(String pictureFile) {
		this.pictureFile = pictureFile;
	}

	public String getHomePage() {
		return this.homePage;
	}

	public void setHomePage(String homePage) {
		this.homePage = homePage;
	}
	
	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Set<AuthorPublications> getAuthorPublicationses() {
		return this.authorPublicationses;
	}

	public void setAuthorPublicationses(Set<AuthorPublications> authorPublicationses) {
		this.authorPublicationses = authorPublicationses;
	}

}
