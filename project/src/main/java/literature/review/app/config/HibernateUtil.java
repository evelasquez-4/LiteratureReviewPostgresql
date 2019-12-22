package literature.review.app.config;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import literature.review.app.model.*;

public class HibernateUtil {
	
	private static SessionFactory sessionFactory = null;
	 
    static {
        try{
            loadSessionFactory();
        }catch(Exception e){
            System.err.println("Exception while initializing hibernate util.. ");
            e.printStackTrace();
        }
    }
 
    public static void loadSessionFactory(){
    	 
        Configuration configuration = new Configuration();
        configuration.configure("/hibernate.cfg.xml");
        
        configuration.addAnnotatedClass(Authors.class);
		configuration.addAnnotatedClass(AuthorPublications.class);
		configuration.addAnnotatedClass(Books.class);
		configuration.addAnnotatedClass(BookChapters.class);
		configuration.addAnnotatedClass(Conferences.class);
		configuration.addAnnotatedClass(ConferenceEditorials.class);
		configuration.addAnnotatedClass(ConferencePapers.class);
		configuration.addAnnotatedClass(Countries.class);
		configuration.addAnnotatedClass(Departments.class);
		configuration.addAnnotatedClass(Editions.class);
		configuration.addAnnotatedClass(Institutions.class);
		configuration.addAnnotatedClass(Journals.class);
		configuration.addAnnotatedClass(JournalEditorials.class);
		configuration.addAnnotatedClass(JournalPapers.class);
		configuration.addAnnotatedClass(Keywords.class);
		configuration.addAnnotatedClass(Publications.class);
		configuration.addAnnotatedClass(PublicationKeywords.class);
		configuration.addAnnotatedClass(Publishers.class);
		configuration.addAnnotatedClass(VolumeNumbers.class);
        
        
        ServiceRegistry srvcReg = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
        sessionFactory = configuration.buildSessionFactory(srvcReg);
    }
	
    public static Session getSession() throws HibernateException {
    	 
        Session retSession=null;
            try {
                retSession = sessionFactory.openSession();
            }catch(Throwable t){
            System.err.println("Exception while getting session.. ");
            t.printStackTrace();
            }
            if(retSession == null) {
                System.err.println("session is discovered null");
            }
 
            return retSession;
    }
    
//	private static StandardServiceRegistry registry;
//    private static SessionFactory sessionFactory;
//    
//    public static SessionFactory getSessionFactory() {
//        if (sessionFactory == null) {
//            try {
//                // Create registry
//                registry = new StandardServiceRegistryBuilder().configure().build();
//                // Create MetadataSources
//                MetadataSources sources = new MetadataSources(registry);
//                // Create Metadata
//                Metadata metadata = sources.getMetadataBuilder().build();
//                // Create SessionFactory
//                sessionFactory = metadata.getSessionFactoryBuilder().build();
//            } catch (Exception e) {
//                e.printStackTrace();
//                if (registry != null) {
//                    StandardServiceRegistryBuilder.destroy(registry);
//                }
//            }
//        }
//        return sessionFactory;
//    }
//    
//    public static void shutdown() {
//        if (registry != null) {
//            StandardServiceRegistryBuilder.destroy(registry);
//        }
//    }
	
//	@Autowired
//	private ApplicationContext context;
//
//	@Bean
//	public LocalSessionFactoryBean getSessionFactory() {
//		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
//		factoryBean.setConfigLocation(context.getResource("classpath:hibernate.cfg.xml"));
//		factoryBean.setAnnotatedClasses(Author.class);
//		return factoryBean;
//	}
//
//	@Bean
//	public HibernateTransactionManager getTransactionManager() {
//		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
//		transactionManager.setSessionFactory(getSessionFactory().getObject());
//		return transactionManager;
//	}
	
//	private static StandardServiceRegistry registry;
//	private static SessionFactory sessionFactory;
//	
//	public static SessionFactory getSessionFactory() {
//        if (sessionFactory == null) {
//            try {
//            	StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();
//
//                //Configuration properties
//                Map<String, Object> settings = new HashMap<>();
//                settings.put(Environment.DRIVER, "org.postgresql.Driver");
//                settings.put(Environment.URL, "jdbc:postgresql://localhost:5432/dbslr");
//                settings.put(Environment.USER, "postgres");
//                settings.put(Environment.PASS, "postgres");
//                settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL92Dialect");
//                settings.put(Environment.HBM2DDL_AUTO, "none");
//                settings.put(Environment.SHOW_SQL, "true");
//
//                registryBuilder.applySettings(settings);
//                registry = registryBuilder.build();
//                
//                MetadataSources sources = new MetadataSources(registry);
//                sources.addAnnotatedClass(Author.class);
//                sources.addAnnotatedClass(AuthorPublications.class);
//                sources.addAnnotatedClass(Book.class);
//                sources.addAnnotatedClass(BookChapter.class);
//                sources.addAnnotatedClass(Conference.class);
//                sources.addAnnotatedClass(ConferenceEditorial.class);
//                sources.addAnnotatedClass(ConferencePaper.class);
//                sources.addAnnotatedClass(Country.class);
//                sources.addAnnotatedClass(Department.class);
//                sources.addAnnotatedClass(Edition.class);
//                sources.addAnnotatedClass(Institution.class);
//                sources.addAnnotatedClass(Journal.class);
//                sources.addAnnotatedClass(JournalEditorial.class);
//                sources.addAnnotatedClass(JournalPaper.class);
//                sources.addAnnotatedClass(Keyword.class);
//                sources.addAnnotatedClass(Publication.class);
//                sources.addAnnotatedClass(PublicationKeywords.class);
//                sources.addAnnotatedClass(Publisher.class);
//                sources.addAnnotatedClass(VolumeNumber.class);
//
//                Metadata metadata = sources.getMetadataBuilder().build();
//                
//				sessionFactory = metadata.getSessionFactoryBuilder().build();
//			} catch (Exception e) {
//				if (registry != null) {
//					StandardServiceRegistryBuilder.destroy(registry);
//				}
//				e.printStackTrace();
//			}
//		}
//		return sessionFactory;
//	}
//
//	public static void shutdown() {
//		if (registry != null) {
//			StandardServiceRegistryBuilder.destroy(registry);
//		}
//	}

}
