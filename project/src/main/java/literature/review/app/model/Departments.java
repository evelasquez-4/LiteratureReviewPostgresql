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
 * Departments generated by hbm2java
 */
@Entity
@Table(name = "departments", schema = "slr")
@JsonIgnoreProperties({"hibernateLazyinitializer","handler"})
public class Departments implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", unique = true, nullable = false)
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "institution_id")
	@JsonIgnore
	private Institutions institutions;
	
	
	private String description;
	private String position;
	private String skills;
	private Date createdAt;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "departments")
	private Set<Authors> authorses = new HashSet<Authors>(0);

	public Departments() {
	}

	public Departments(long id) {
		this.id = id;
	}

	public Departments(long id, Institutions institutions, String description, String position, String skills,
			Date createdAt, Set<Authors> authorses) {
		this.id = id;
		this.institutions = institutions;
		this.description = description;
		this.position = position;
		this.skills = skills;
		this.createdAt = createdAt;
		this.authorses = authorses;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	
	public Institutions getInstitutions() {
		return this.institutions;
	}

	public void setInstitutions(Institutions institutions) {
		this.institutions = institutions;
	}

	@Column(name = "description")
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "position")
	public String getPosition() {
		return this.position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	@Column(name = "skills")
	public String getSkills() {
		return this.skills;
	}

	public void setSkills(String skills) {
		this.skills = skills;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "created_at", length = 13)
	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	
	public Set<Authors> getAuthorses() {
		return this.authorses;
	}

	public void setAuthorses(Set<Authors> authorses) {
		this.authorses = authorses;
	}

}