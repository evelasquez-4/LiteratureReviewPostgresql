package literature.review.app.staxparser;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONObject;

import literature.review.app.config.DBConnect;


public class DblpIUD {
	
	
	public void precargaDataDBLP(List<DblpDocument> dblpList) throws SQLException
	{
		DBConnect conn = null;
		for (DblpDocument dblp : dblpList) 
		{
			
			try 
			{
				@SuppressWarnings("static-access")
				PreparedStatement st = conn.getInstance()
					.getConnection()
					.prepareStatement( "INSERT INTO slr.dblp_publication ( KEY_DBLP,AUTHORS,DOC_TYPE,EDITOR,"
							+ "PAGES,YEAR,TITLE,ADDRESS,JOURNAL,VOLUME,NUMBER,MONTH,URL,EE,CDROM,CITE,PUBLISHER,"
							+ "NOTE,CROSSREF,ISBN,SERIES,SCHOOL,CHAPTER,PUBLNR,MDATE)"
							+ "  VALUES (?,to_json(?::json), ? , ? ,"
							+ "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
							+ "?, ?, ?, ?, ?, ?, ?, ? )" );
				
				st.setString(1, dblp.getKey_dblp());//key
				st.setString(2, new JSONObject( parseListToMap(dblp.getAuthors())).toString() );//authors
				st.setString(3, dblp.getDoc_type()); //doc_type
				st.setString(4, dblp.getEditor());//editor
				
				st.setString(5, dblp.getPages());//pages
				st.setInt(6, dblp.getYear());//year
				st.setString(7, dblp.getTitle());//title
				st.setString(8, dblp.getAddress());//address
				st.setString(9, dblp.getJournal());//journal
				st.setString(10, dblp.getVolume());//volume
				st.setString(11, dblp.getNumber());//number
				st.setString(12, dblp.getMonth());//month
				st.setString(13, dblp.getUrl());//url
				st.setString(14, dblp.getEe());//ee
				st.setString(15, dblp.getCdrom());//cdrom
				st.setString(16, dblp.getCite());//cite
				st.setString(17, dblp.getPublisher());//publisher
				
				st.setString(18, dblp.getNote());//note
				st.setString(19, dblp.getCrossref());//crossref
				st.setString(20, dblp.getIsbn());//isbn
				st.setString(21, dblp.getSeries());//series
				st.setString(22, dblp.getSchool());//school
				st.setString(23, dblp.getChapter());//chapter
				st.setString(24, dblp.getPblnr());//publnr
				st.setString(25, dblp.getMdate());//mdate
				//st.setString(26, new Date().toString());//reg_date
				
				//System.out.println("QUERY -> "+st.toString());
				
				st.executeUpdate();
				
			}
			catch (SQLException e) {
			
				System.out.println(e.getMessage());
			}
			
		}		
	}
	
	public Map<Integer, String> parseListToMap(List<String> cadenas)
	{
		Map<Integer, String> res = new HashMap<Integer, String>();
		for (int i = 0; i < cadenas.size(); i++)
			res.put(i+1, cadenas.get(i));
			
		return res;
	}
	
/*
	
	@PersistenceContext
	private static EntityManager em;
	private static EntityManagerFactory factory;
	
	@Bean
    @Primary
    public DataSource dataSource()
    {
		final SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
		dataSource.setDriver(new org.postgresql.Driver());
		dataSource.setUrl("jdbc:postgresql://localhost:5432/dbslr"); 
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
		
		return dataSource;
    }
	

	
	public void insertPublicationAuthor()
	{
		
		
		Department default_dep =(Department)em
				.createNativeQuery("SELECT id FROM slr.department WHERE id =?")
				.setParameter(1, 0)
				.getSingleResult();
		
		System.out.println(default_dep.getDescription());
//		try
//		{
//			System.out.println( default_dep.getId() );
//			System.out.println( default_dep.getDescription() );
//		}
//		catch (SQLException ex) {
//            System.out.println(ex.getMessage());
//        }
	}

//	public static void main(String[] args) throws SQLException 
//	{
////		factory = Persistence.createEntityManagerFactory(null); 
////		em = factory.createEntityManager();
////
////		Department defaulDepartment = (Department)em
////				.createQuery("FROM Department WHERE id = 0")
////				.getSingleResult();
////		
////		System.out.println(defaulDepartment.getId()+'\n'+defaulDepartment.getDescription());
//		
////		DblpIUD a = new DblpIUD();
////		a.insertPublicationAuthor();
//		
//		
//		
//		
//		DblpIUD d = new DblpIUD();
//		System.out.println( d.dataSource().getLoginTimeout() );
//		
//		
//	}
 
 */
}
