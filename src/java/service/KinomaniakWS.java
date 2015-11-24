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
    
    private int trySaveToDB(Object o){
        Session session = HibernateUtil.getSessionFactory().openSession();
        try{
            session.beginTransaction(); 
            session.save(o);
            session.getTransaction().commit();
        }catch(HibernateException he){
            he.printStackTrace();
            session.getTransaction().rollback();
            return -1;
        }
        return 0;
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
    public List getCast(@WebParam(name = "movieid") int movieid) {
        //TODO write your implementation code here:
//        List result = executeHQLQuery("select c.movie.name, c.actor.firstName, c.actor.lastName from Cast c where c.movie.id = " + movieid);
        List result = executeHQLQuery("Select c.actor From Cast c where c.movie.id = " + movieid);
        if(result != null){
//            ArrayList<Cast> clist = new ArrayList<Cast>();
//            for(Object o: result){
//                clist.add((Cast)o);
//            }
//            return clist.get(0);
            ArrayList<Actor> clist = new ArrayList<Actor>();
            for(Object o: result){
                clist.add((Actor)o);
            }
            return clist;
        }
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getMoviesByGenre")
    public List getMoviesByGenre(@WebParam(name = "genreid") int genreid) {
        List result = executeHQLQuery("from Movie m where m.genre.id = " + genreid);
        if(!result.isEmpty()){
            List movies = new ArrayList<Movie>();
            for(Object o : result){
                Movie m  = (Movie)o;
                movies.add(m);
            }
            return movies;
        }
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getMoviesByActor")
    public List getMoviesByActor(@WebParam(name = "actorid") int actorid) {
        List result = executeHQLQuery("from Cast c where c.actor.id = " + actorid);
        List movies = new ArrayList<Movie>();
        if(result != null)
        if(!result.isEmpty()){            
            for(Object o : result){
                Cast c = (Cast)o;
                movies.add(c.getMovie());
            }
//            return movies;
        }
        return movies;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getActorList")
    public List getActorList() {
        List result = executeHQLQuery("From Actor");
        if(!result.isEmpty()){
            List actors = new ArrayList<Actor>();
            for(Object o : result){
                actors.add((Actor)o);
            }
            return actors;
        }
        return new ArrayList<Actor>();
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getShowList")
    public List getShowList() {
        List result = executeHQLQuery("From Show");
        if(!result.isEmpty()){
            List shows = new ArrayList<Show>();
            for (Object o: result){
                shows.add((Show)o);
            }
            return shows;
        }
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getShow")
    public Show getShow(@WebParam(name = "id") int id) {
        List result = executeHQLQuery("From Show s Where s.id = " + id);
        if(!result.isEmpty()){
            Show s = (Show)result.get(0);
//            List shows = new ArrayList<Show>();
//            for(Object o: result){
//                shows.add((Show)o);
//            }
//            return shows.get(0);
            return s;
        }
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "listReservations")
    public List listReservations() {
        List result = executeHQLQuery("From Reservation");
        List reservations = new ArrayList<Reservation>();
        if(result != null)
            if(!result.isEmpty()){
                for(Object o: result){
                    reservations.add((Reservation)o);
                }
            }
        return reservations;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getReservationById")
    public Reservation getReservationById(@WebParam(name = "resid") int resid) {
        List result = executeHQLQuery("From Reservation r Where r.id = " + resid);
        if(result != null)
            if(!result.isEmpty()){
                return (Reservation)result.get(0);
            }
        return null;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getReservationsByShow")
    public List getReservationsByShow(@WebParam(name = "showid") int showid) {
        List result = executeHQLQuery("From Reservation r Where r.show.id = " + showid);
        List reservations = new ArrayList<Reservation>();
        if(result != null)
            if(!result.isEmpty()){
                for(Object o: result)
                    reservations.add((Reservation)o);
            }
        return reservations;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "bookTicketForShow")
    public int bookTicketForShow(@WebParam(name = "showid") int showid, @WebParam(name = "userid") int userid, @WebParam(name = "seat") int seat) {
        //TODO write your implementation code here:
        Show sh = new Show();
        sh.setId(showid);
        User usr = new User();
        usr.setId(userid);
        Reservation res = new Reservation(sh, usr, false, false, seat);
        return trySaveToDB(res);
//        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getReservationsByUser")
    public List getReservationsByUser(@WebParam(name = "userid") int userid) {
        List result = executeHQLQuery("From Reservation r Where r.user.id = " + userid);
        List reservations = new ArrayList<Reservation>();
        if(result != null)
            if(!result.isEmpty()){
                for(Object o: result)
                    reservations.add((Reservation)o);
            }
        return reservations;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "getOccupiedSeatsForShow")
    public List getOccupiedSeatsForShow(@WebParam(name = "showid") int showid) {
        List result = executeHQLQuery("Select r.seat From Reservation r Where r.show.id = " + showid);
        List seats = new ArrayList<Integer>();
        if(result != null)
            if(!result.isEmpty()){
                for(Object o: result){
//                    Reservation r = (Reservation)o;
                    seats.add((Integer)o);
                }
            }
        return seats;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "bookTicketForShowObj")
    public int bookTicketForShowObj(@WebParam(name = "show") Show show, @WebParam(name = "user") User user, @WebParam(name = "seat") int seat) {
        return bookTicketForShow(show.getId(), user.getId(), seat);
//        return 0;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "userLogin")
    public int userLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) {
        List result = executeHQLQuery("From User u Where u.name = '" + username + "' AND u.password = '" + password + "'");
        if(result != null)
            if(!result.isEmpty()){
                User u = (User) result.get(0);
                return u.getId();
            }
        return -1;
    }
    
    
}
