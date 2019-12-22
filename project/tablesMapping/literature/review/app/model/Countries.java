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
 * Countries generated by hbm2java
 */
@Entity
@Table(name = "countries", schema = "slr")
public class Countries implements java.io.Serializable {

	private long id;
	private String countryName;
	private String code;
	private Date createdAt;
	private Set<Institutions> institutionses = new HashSet<Institutions>(0);

	public Countries() {
	}

	public Countries(long id) {
		this.id = id;
	}

	public Countries(long id, String countryName, String code, Date createdAt, Set<Institutions> institutionses) {
		this.id = id;
		this.countryName = countryName;
		this.code = code;
		this.createdAt = createdAt;
		this.institutionses = institutionses;
	}

	@Id

	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "country_name")
	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	@Column(name = "code", length = 5)
	public String getCode() {
		return this.code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "created_at", length = 13)
	public Date getCreatedAt() {
		return this.createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "countries")
	public Set<Institutions> getInstitutionses() {
		return this.institutionses;
	}

	public void setInstitutionses(Set<Institutions> institutionses) {
		this.institutionses = institutionses;
	}

}
