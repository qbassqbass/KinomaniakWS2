/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

//import entity.Movie;
import entity.*;
import java.util.ArrayList;
import utils.HibernateUtil;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;

/**
 *
 * @author Jakub
 */
@WebService(serviceName = "KinomaniakWS")
public class KinomaniakWS {
    
    private List executeHQLQuery(String hql) {
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Query q = session.createQuery(hql);
            List resultList = q.list();
            if(!resultList.isEmpty())
                return resultList;
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return null;
    }

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "hello")
    public String hello(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }

    /**
     * Web service operation
     */
    
    @WebMethod(operationName = "getMovieList")
    public List getMovieList(){
        List l = new ArrayList<Movie>();
        List result = executeHQLQuery("from Movie m");
        if(!result.isEmpty())
            for(Object o : result){
                Movie m = (Movie)o;
                l.add(m);
            }
        return l;
    }
    @WebMethod(operationName = "getMovie")
    public Movie getMovie(@WebParam(name = "id") final int id) {
        //TODO write your implementation code here:
        List result = executeHQLQuery("From Movie m where m.id = " + id);
        Movie m = (Movie)result.get(0);
        List cast = executeHQLQuery("From Cast c where c.movie.id = " + id);
        if(!cast.isEmpty())
            for(Object o : cast){
                Cast c = (Cast)o; 
               m.addActor(c.getActor());
            }
        return m;
//        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getCast")
    public Cast getCast(@WebParam(name = "movieid") int movieid) {
        //TODO write your implementation code here:
//        List result = executeHQLQuery("select c.movie.name, c.actor.firstName, c.actor.lastName from Cast c where c.movie.id = " + movieid);
        List result = executeHQLQuery("From Cast c where c.movie.id = " + movieid);
        if(result != null){
            ArrayList<Cast> clist = new ArrayList<Cast>();
            for(Object o: result){
                clist.add((Cast)o);
            }
            return clist.get(0);
        }
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getMoviesByGenre")
    public List getMoviesByGenre(@WebParam(name = "genreid") int genreid) {
        //TODO write your implementation code here:
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getMoviesByActor")
    public List getMoviesByActor(@WebParam(name = "actorid") int actorid) {
        //TODO write your implementation code here:
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getActorList")
    public List getActorList() {
        //TODO write your implementation code here:
        return null;
    }
}
