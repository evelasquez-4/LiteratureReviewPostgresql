package literature.review.app.helpers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ResearchGateMemberProfile { 

	private String names;
	private String profile_url;
	private List<String> disciplines;
	private List<String> skillsExpertise;
	private List<ResearchGateProfileContributions> contributions;
	
	public ResearchGateMemberProfile() {
		this.names = "";
		this.profile_url = "";
		this.disciplines = new ArrayList<>();
		this.skillsExpertise = new ArrayList<>();
		this.contributions = new ArrayList<>();
	}
	
	public ResearchGateMemberProfile(String name, String profile_url)
	{
		this.names = name;
		this.profile_url = profile_url;
		this.disciplines = new ArrayList<>();
		this.skillsExpertise = new ArrayList<>();
		this.contributions = new ArrayList<ResearchGateProfileContributions>();
	}
	
	
	public ResearchGateMemberProfile(String names, String profile_url, List<String> disciplines,
			List<String> skillsExpertise, List<ResearchGateProfileContributions> contributions) {
		this.names = names;
		this.profile_url = profile_url;
		this.disciplines = disciplines;
		this.skillsExpertise = skillsExpertise;
		this.contributions = contributions;
	}

	public String getNames() {
		return names;
	}

	public void setNames(String names) {
		this.names = names;
	}

	public String getProfile_url() {
		return profile_url;
	}

	public void setProfile_url(String profile_url) {
		this.profile_url = profile_url;
	}

	public List<String> getDisciplines() {
		return disciplines;
	}

	public void setDisciplines(List<String> disciplines) {
		this.disciplines = disciplines;
	}

	public List<String> getSkillsExpertise() {
		return skillsExpertise;
	}

	public void setSkillsExpertise(List<String> skillsExpertise) {
		this.skillsExpertise = skillsExpertise;
	}

	public List<ResearchGateProfileContributions> getContributions() {
		return contributions;
	}

	public void setContributions(List<ResearchGateProfileContributions> contributions) {
		this.contributions = contributions;
	}
	
	public void mostrar() {
		System.out.println("Names :"+this.names);
		System.out.println("Disciplines :");
		this.disciplines.forEach(dis->{
			System.out.println(dis);
		});
		System.out.println("----------------------------");
		System.out.println("Skills");
		this.skillsExpertise.forEach(s->{
			System.out.println(s);
		});
		System.out.println("----------------------------");
		System.out.println("Contributions :");
		this.contributions.forEach(cont->{
			cont.mostrar();
		});
	}
}


