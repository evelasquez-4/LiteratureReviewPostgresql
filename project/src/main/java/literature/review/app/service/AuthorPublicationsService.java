package literature.review.app.service;

import java.util.Optional;

import javax.persistence.ParameterMode;

import org.hibernate.Session;
import org.hibernate.procedure.ProcedureCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.config.HibernateUtil;
import literature.review.app.model.AuthorPublications;
import literature.review.app.model.Authors;
import literature.review.app.repository.AuthorPublicationsRepository;

@Service
public class AuthorPublicationsService {

	@Autowired
	private AuthorPublicationsRepository author_publications;
	
	public AuthorPublications save(AuthorPublications data)
	{
		return author_publications.save(data);
	}
	
	public Optional<Authors> findAuthorByPublication(int id)
	{
		return author_publications.findAuthorByPublication(id);
	}
	
	
	@SuppressWarnings("unchecked")
	public boolean authorPublicationsInsert(String docs_type , int limite)
	{
		Session session = null;

		try {
			session = HibernateUtil.getSession();
			ProcedureCall procedureCall = session
					.createStoredProcedureCall("slr.slr_author_publication_iud");
			procedureCall.registerParameter("accion", String.class, ParameterMode.IN).bindValue("AUTHPUB_INS");;
			procedureCall.registerParameter("author_id", Integer.class,ParameterMode.IN).enablePassingNulls(true);
			procedureCall.registerParameter("publication_id", Integer.class,ParameterMode.IN).enablePassingNulls(true);
			procedureCall.registerParameter("publication_type", String.class,ParameterMode.IN).bindValue(docs_type);
			procedureCall.registerParameter("limite", Integer.class, ParameterMode.IN).bindValue(limite);
			procedureCall.registerParameter("herarchy", Integer.class, ParameterMode.IN).enablePassingNulls(true);
			procedureCall.registerParameter("res", String.class, ParameterMode.OUT);
			
			procedureCall.getParameterRegistration("author_id").bindValue(null);
			procedureCall.getParameterRegistration("publication_id").bindValue(null);
			procedureCall.getParameterRegistration("herarchy").bindValue(null);
						
			procedureCall.execute();
			
			String res = (String) procedureCall.getOutputs().getOutputParameterValue("res");
			
			if(session != null) 
				session.close();
			
			return res.equals("success");
			
		} 
		catch (Exception e) {
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
