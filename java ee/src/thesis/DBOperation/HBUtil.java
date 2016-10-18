package thesis.DBOperation;


import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
/**
 * Created by Lin on 2016/1/28.
 */
public class HBUtil {
	
	private static final SessionFactory ourSessionFactory;
    private static final ServiceRegistry serviceRegistry;

    static {
        try {
            Configuration configuration = new Configuration();
            configuration.configure();

            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            ourSessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static Session getSession() throws HibernateException {
        return ourSessionFactory.openSession();
    }
    
//    private static HBUtil ourInstance = new HBUtil();
//
//    public static HBUtil getInstance() {
//        return ourInstance;
//    }
//
//    private HBUtil() {
//    }
//
//    public SessionFactory getSessionFactory()throws Exception{
//        SessionFactory sessionFactory = null;
//        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
//        		.configure()
//                .build();
//        try {
//            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
//        }
//        catch (Exception e) {
//            StandardServiceRegistryBuilder.destroy( registry );
//        }
//
//        return sessionFactory;
//    }
//    
//    protected void setUp() throws Exception {
//    	SessionFactory sessionFactory;
//    	// A SessionFactory is set up once for an application!
//    	final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
//    			.configure() // configures settings from hibernate.cfg.xml
//    			.build();
//    	try {
//    		sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
//    	}
//    	catch (Exception e) {
//    		// The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
//    		// so destroy it manually.
//    		StandardServiceRegistryBuilder.destroy( registry );
//    	}
//    }
};
