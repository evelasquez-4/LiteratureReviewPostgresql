package literature.review.app.service;

import java.io.IOException;
import java.util.HashMap;

import javax.persistence.ParameterMode;
import javax.xml.stream.XMLStreamException;

import org.hibernate.Session;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.hibernate.result.Output;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import literature.review.app.config.HibernateUtil;
import literature.review.app.staxparser.Parser;


@Service
public class DblpETLService {

	@Autowired
	AuthorsService author;
	@Autowired
	PublicationsService publication;
	@Autowired
	AuthorPublicationsService author_publication;
	@Autowired
	DepartmentsService department;

	public boolean firstTimeInsert() throws XMLStreamException, IOException
	{
		Parser parser = new Parser(getInitialInsertServices());
		return parser.readFirstTimeXml();
		
		
//		boolean res = false;
//		
//		Transaction transaction = null;
//		Session session = null;
//		Author aa = null;
//		
//		try{
//			session = HibernateUtil.getSession();
//			// start a transaction
//			transaction = session.beginTransaction();
//				
//				session
//					.createQuery("FROM Author WHERE id = 0",Author.class)
//					.getSingleResult();
//		
//			//System.out.println(a.get(0).getNames());
//			
//			// commit transaction
//			transaction.commit();
//		} catch (Exception e) {
//			if (transaction != null) {
//				transaction.rollback();
//			}
//			e.printStackTrace();
//		}
//		finally {
//			try {	if(session != null) 
//						session.close();
//			}catch(Exception ex){}
//		}
//	
//		
//		return res;
	}
	
	public HashMap<String, Object> getInitialInsertServices()
	{
		HashMap<String, Object> services = new HashMap<>();
		services.put("author", this.author);
		services.put("publication", this.publication);
		services.put("author_publication", this.author_publication);
		services.put("department", this.department);
		
		return services;
	}
	
	
	//llenado de datos en tabla public.dblp_document
	public boolean precargaDblpData() throws XMLStreamException, IOException
	{
		Parser res = new Parser();
		return res.readXml();
	}
	
	//llamada funcion para insertar datos en slr.author_publications
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
			
			//procedureCall.getParameterRegistration("accion").bindValue( "AUTHPUB_INS" );
			procedureCall.getParameterRegistration("author_id").bindValue(null);
			procedureCall.getParameterRegistration("publication_id").bindValue(null);
			//procedureCall.getParameterRegistration("publication_type").bindValue(docs_type);
			procedureCall.getParameterRegistration("herarchy").bindValue(null);
						
			procedureCall.execute();
			
			String res = (String) procedureCall.getOutputs().getOutputParameterValue("res");
			
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
