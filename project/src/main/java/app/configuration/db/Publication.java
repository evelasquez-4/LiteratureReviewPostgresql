package app.configuration.db;

public class Publication {
	int id;
	String title;
	String publicationType;
	String abstractField;
	String doi;
	int year;
	
	public Publication(String title, String publicationType, String doi, int year) {
	
		this.title = title;
		this.publicationType = publicationType;
		this.doi = doi;
		this.year = year;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(String publicationType) {
		this.publicationType = publicationType;
	}

	public String getAbstractField() {
		return abstractField;
	}

	public void setAbstractField(String abstractField) {
		this.abstractField = abstractField;
	}

	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
