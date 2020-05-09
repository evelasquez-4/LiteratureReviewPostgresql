package literature.review.app.helpers;

import org.springframework.stereotype.Component;

@Component
public class ResearchGateInstitutions {

	private String institution_name;
	private String institution_url;
	private int number_members;
	
	public ResearchGateInstitutions() {
		this.institution_name = "";
		this.institution_url = "";
		this.number_members = 0;
	}

	public ResearchGateInstitutions(String name,String url, int members)
	{
		this.institution_name = name;
		this.institution_url= url;
		this.number_members = members;
	}
	public String getInstitution_name() {
		return institution_name;
	}

	public void setInstitution_name(String institution_name) {
		this.institution_name = institution_name;
	}

	public String getInstitution_url() {
		return institution_url;
	}

	public void setInstitution_url(String institution_url) {
		this.institution_url = institution_url;
	}

	public int getNumber_members() {
		return number_members;
	}

	public void setNumber_members(int number_members) {
		this.number_members = number_members;
	}
	
}
