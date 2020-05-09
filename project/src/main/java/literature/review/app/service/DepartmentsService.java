package literature.review.app.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.helpers.ResearchGateInstitutions;
import literature.review.app.model.Departments;
import literature.review.app.model.Institutions;
import literature.review.app.repository.DepartmentsRepository;

@Service
public class DepartmentsService {
	@Autowired
	private DepartmentsRepository department;
	
	public Optional<Departments> findById(long id)
	{
		return department.findById(id);
	}
	
	public Departments getDefaultDepartment()
	{
		return department.findById((long)0).get();
	}
	
	public void save(Departments dep)
	{
		this.department.save(dep);
	}
	
	public void saveAllDepartments(List<Departments> deptos)
	{
		this.department.saveAll(deptos);
	}
	
	public List<Departments> listDepartmentsByInsitution(String country_code,Integer limit, String state){
		return this.department.listDepartmentsByInsitution(country_code, limit, state);
	}
	
	public void saveResearchGateDepartments(Institutions ins, List<ResearchGateInstitutions> lista)
	{
		lista.forEach((dep)->{
			Departments d = new Departments();
			d.setCreatedAt(new Date());
			d.setPosition("1.inserted");
			d.setInstitutions(ins);
			d.setDescription(dep.getInstitution_name());
			d.setLinks(dep.getInstitution_url());
			d.setMembers(dep.getNumber_members());
			d.setValidate(false);
			
			this.department.save(d);
		});
	}
	
	//Hashmap key->department name ; value -> department link in researchgate
	public void saveResearchGateDepartments(Institutions ins, HashMap<String,String> mapDepto)
	{		
		mapDepto.forEach((key,value)->{		
			Departments dep = new Departments();
			dep.setPosition("1.inserted");
			dep.setInstitutions(ins);
			dep.setCreatedAt(new Date());
			dep.setDescription(key);
			dep.setLinks(value);
			dep.setValidate(false);
			
			this.department.save(dep);
		});	
		
	}
}