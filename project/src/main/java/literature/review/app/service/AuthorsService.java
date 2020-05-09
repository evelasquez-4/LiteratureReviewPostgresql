package literature.review.app.service;

import java.util.Date;
import java.util.Optional;

import javax.persistence.ParameterMode;

import org.hibernate.Session;
import org.hibernate.procedure.ProcedureCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.config.HibernateUtil;
import literature.review.app.model.Authors;
import literature.review.app.repository.AuthorsRespository;

@Service
public class AuthorsService {

	@Autowired
	private AuthorsRespository author;
	@Autowired
	private DepartmentsService department;
	
	
	public Optional<Authors> findById(Long id)
	{
		return author.findById(id);
	}
	
	public Authors getDefaultAuthor()
	{
		return author.findById((long) 0).get();
	}
	
	public Authors save(Authors auth)
	{
		return author.save(auth);
	}
	
	public Optional<Authors> getAuthorByNames(String names)
	{
		return author.findByNames(names.toLowerCase());
	}

	public Authors registerAuthors(String names)
	{
		Authors res = new Authors();
		Optional<Authors> opt = this.getAuthorByNames(names);
		
		if(opt.isPresent())
			res = opt.get();
		else {
			res.setCreatedAt(new Date());
			res.setDepartments(this.department.getDefaultDepartment());
			res.setNames(names);
			
			res = this.author.saveAndFlush(res);
		}
		return res;
	}
	
	public boolean processAuthors(String doc_type, int limite)
	{
		Session session = null;
		
		try 
		{
			session = HibernateUtil.getSession();
			ProcedureCall procedureCall = session
					.createStoredProcedureCall("slr.slr_process_authors");
			procedureCall.registerParameter("accion",String.class,ParameterMode.IN).bindValue("PROC_AUTHORS");
			procedureCall.registerParameter("doc_type",String.class,ParameterMode.IN).bindValue(doc_type);
			procedureCall.registerParameter("limite",Integer.class,ParameterMode.IN).bindValue(limite);
			procedureCall.registerParameter("res", String.class, ParameterMode.OUT);
			
			String res = (String) procedureCall.getOutputs().getOutputParameterValue("res"); 
			
			if(session != null) 
				session.close();
					
			return res.equals("success");
			
		}catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		finally {
			try {	if(session != null) 
				session.close();
			}catch(Exception ex){}
		}	
	}

}
