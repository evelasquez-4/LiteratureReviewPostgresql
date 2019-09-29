package app.configuration.db;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;


public class HibernateUtil 
{
	private static StandardServiceRegistry standardServiceRegistry;
	private static SessionFactory sessionFactory;
	private static final String HIBERNATECFGXML = "src/main/resources/hibernate.cfg.xml";
	
	static {
		
		try {
			if(sessionFactory == null)
			{
				standardServiceRegistry = new StandardServiceRegistryBuilder()
						.configure( new File(HIBERNATECFGXML) )
						.build();
				
				MetadataSources metadataSources = new MetadataSources(standardServiceRegistry);
				Metadata metadata = metadataSources.getMetadataBuilder().build();
				sessionFactory = metadata.getSessionFactoryBuilder().build();
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			if(standardServiceRegistry != null)
				StandardServiceRegistryBuilder.destroy(standardServiceRegistry);
		}
	}
	
	
    
//    private static SessionFactory buildSessionFactory()
//    {
//        try
//        {
//            // Create the SessionFactory from hibernate.cfg.xml
//            return new Configuration().configure(new File("/Users/raven/Documents/workspace-spring-tool-suite-4-4.3.2.RELEASE/LiteratureReview/src/main/resources/hibernate.cfg.xml")).buildSessionFactory();
//        }
//        catch (Throwable ex) {
//            // Make sure you log the exception, as it might be swallowed
//            System.err.println("Initial SessionFactory creation failed." + ex);
//            throw new ExceptionInInitializerError(ex);
//        }
//    }
  
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
  
    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
	
}
