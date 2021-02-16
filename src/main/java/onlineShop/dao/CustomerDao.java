package onlineShop.dao;

import onlineShop.entity.Authorities;
import onlineShop.entity.Customer;
import onlineShop.entity.User;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class CustomerDao {

    @Autowired
    private SessionFactory sessionFactory;

    public void addCustomer(Customer customer) {
        Authorities authorities = new Authorities();
        authorities.setAuthorities("ROLE_USER");
        authorities.setEmailId(customer.getUser().getEmailId());
        // Not HttpSession, it's a session for db connection
        Session session = null;

        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(authorities);
            session.save(customer);
            session.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            session.getTransaction().rollback();
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // username <==> email
    public Customer getCustomerByUserName(String userName) {
        User user = null;
        // no need to rollback for GET request, so we can declare session with try-with-resource
        // the session will close automatically by try-with-resource mechanism
        try (Session session = sessionFactory.openSession()) {
            // search in User table
            Criteria criteria = session.createCriteria(User.class);
            // select * from user where emailid=userName
            // work with different DBs, don't need to worry about syntax
            user = (User)criteria.add(Restrictions.eq("emailId", userName)).uniqueResult();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (user != null) {
            return user.getCustomer();
        }
        return null;
    }
}
