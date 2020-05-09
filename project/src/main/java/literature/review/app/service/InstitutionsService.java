package literature.review.app.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.model.Countries;
import literature.review.app.model.Institutions;
import literature.review.app.repository.CountriesRepository;
import literature.review.app.repository.InstitutionsRepository;


@Service
public class InstitutionsService {
 
	@Autowired
	private InstitutionsRepository institution;
	@Autowired
	private CountriesRepository country;
	
	
	public List<Institutions> findInstitutionsByCountry(Integer countryId){
		return this.institution.findByCountryId(countryId);
	}
	
	public List<Institutions> findInstitutionByDescription(String description){
		return this.institution.findInstitutionByDescription(description);
	}
	
	public Optional<Institutions> findInstitutionById(Integer id)
	{
		return this.institution.findById((long)id);
	}
	
	public List<Institutions> findResearchGateInstitutionsByCountryCode(String code,Integer limit ,boolean updated)
	{
		return this.institution.findResearchGateInstitutionsByCountryCode(code, limit, updated);
	}
	
	public Institutions saveInstitutions(Institutions ins)
	{
		return this.institution.save(ins);
	}
	
	/*
	 * Carga archivo JSON en slr.institutions
	 * nota: no verifica duplicidad de registros
	 */
	public void loadInstitutionsFromWorldUniversitiesJSON() throws IOException 
	{
		File file = new File("../LiteratureReviewApp/src/main/resources/static/jsonFiles/world_universities_and_domains.json");
		//File file = new File("../LiteratureReviewApp/src/main/resources/static/jsonFiles/prueba.json");
		InputStream is = new FileInputStream(file);
		
		JSONTokener tokener = new JSONTokener(is);
		JSONArray array = new JSONArray(tokener);
		
		JSONObject json = new JSONObject();
		
		array.iterator().forEachRemaining(obj->{
		
			String country_code = ((JSONObject) obj).getString("alpha_two_code");
			String university_name = ((JSONObject) obj).getString("name");
			
			if( json.has(country_code) )
				json.getJSONArray(country_code).put(university_name);
			else
				json.append(country_code, university_name );
		});
		
		json.keySet().forEach(key->{
			Optional<Countries> code = this.country.findCountryByCode(key);
			
			if(code.isPresent())
			{
				json.getJSONArray(key).iterator().forEachRemaining( val->{
					Institutions ins = new Institutions();
					ins.setCountries(code.get());
					ins.setDescription((String)val);
					ins.setCreatedAt(new Date());
					
					this.institution.save(ins);
				});
			}
		});
			
		is.close();
	}
}
