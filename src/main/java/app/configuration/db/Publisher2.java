package app.configuration.db;

public class Publisher2 
{
	int id;
	String description;
	
	
	public Publisher2(String description) {
		this.description = description;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}
}
