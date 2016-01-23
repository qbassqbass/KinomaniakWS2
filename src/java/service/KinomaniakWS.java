/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

//import entity.Movie;
import entity.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import utils.HibernateUtil;
import java.util.List;
import java.util.Set;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import utils.HashUtil;

/**
 * Główna klasa usługi sieciowej
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
            session.close();
            return -1;
        }
        session.close();
        return 0;
    }
    
    private int trySaveUpdateToDB(Object o){
        Session session = HibernateUtil.getSessionFactory().openSession();
        try{
            session.beginTransaction(); 
            session.saveOrUpdate(o);
            session.getTransaction().commit();
        }catch(HibernateException he){
            he.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            return -1;
        }
        session.close();
        return 0;
    }   
    
    private int executeDeleteQuery(String hql){
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();            
            Query q = session.createQuery(hql);
            q.executeUpdate();
            session.getTransaction().commit();
        } catch (ConstraintViolationException cve){
            return -2;
        } catch (HibernateException he) {
            he.printStackTrace();
            return -1;
        }
        return 0;
    }
    
    
    private Object getFromDB(Class cl, Serializable ser){
        Session session = HibernateUtil.getSessionFactory().openSession();
        Object o = session.get(cl, ser);
        session.close();
//        try{
        return o;
//        }
    }

    /**
     * Zwraca listę wszystkich filmów w bazie
     * @return Lista filmów w bazie jako ArrayList of Movie
     * @see Movie
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

    /**
     * Zwraca film (obiekt Movie) na podstawie konkretnego identyfikatora
     * @param id identyfikator filmu
     * @return obiekt Movie (film)
     * @see Movie
     */
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
     * Zwraca listę aktorów w danym filmie na podstawie id filmu
     * @param movieid id filmu
     * @return lista aktorów (obiektów Actor) 
     * @see Actor
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
     * Metoda zwraca listę filmów (obiekt Movie) na podstawie identyfikatora rodzaju filmu (Genre)
     * @param genreid id rodzaju
     * @return lista filmów
     * @see Genre
     * @see Movie
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
     * Zwraca listę filmów na podstawie id aktora
     * @param actorid id aktora
     * @return lista filmów
     * @see Movie
     * @see Actor
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
     * Zwraca listę wszystkich aktorów
     * @return lista aktorów w bazie jako ArrayList
     * @see Actor
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
     * Zwraca listę wszystkich seansów zapisanych w bazie
     * @return lista seansów (jako ArrayList)
     * @see Show
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
     * Zwraca konkretny seans podstawie podanego identyfikator
     * @param id id seansu
     * @return obiekt Show konkretnego seansu
     * @see Show
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
     * Zwraca listę wszystkich rezerwacji
     * @return lista rezerwacji (jako Arraylist)
     * @see Reservation
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
     * Zwraca konkretną rezerwację na podstawie jego id
     * @param resid id rezerwacji
     * @return obiekt Reservation
     * @see Reservation
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
     * Zwraca listę rezerwacji na podany seans na podstawie id seansu
     * @param showid id seansu
     * @return lista rezerwacji jako ArrayList of Reservation
     * @see Reservation
     * @see Show
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
     * Metoda rezerwująca bilet na dany seans
     * @param showid id seansu
     * @param seat id miejsca w sali 
     * @param userid id użytkownika rezerwującego
     * @return 0 jeśli jest OK
     * @see Show
     * @see Room
     * @see User
     */
    @WebMethod(operationName = "bookTicketForShow")
    public int bookTicketForShow(@WebParam(name = "showid") int showid, @WebParam(name = "userid") int userid, @WebParam(name = "seat") int seat) {
        //TODO write your implementation code here:
        Show sh = new Show();
        sh.setId(showid);
        User usr = new User();
        usr.setId(userid);
        ReservationId resid = new ReservationId(getLastReservationId()+1, seat);
        Reservation res = new Reservation(resid, sh, usr, false, false);
        return trySaveToDB(res);
//        return 0;
    }

    /**
     * Pobranie rezerwacji danego użytkownika
     * @param userid id użytkownika
     * @return lista rezerwacji jako ArrayList of Reservation
     * @see User
     * @see Reservation
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
     * Zwraca identyfikatory zajętych miejsc na danym seansie
     * @param showid id seansu
     * @return lista zajętych miejsc jako ArrayList of Integer
     * @see Show
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
    
    private int getLastReservationId(){
        int id = 0;
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();            
            Query q = session.createQuery("Select r.id.id From Reservation r Order by r.id.id DESC").setMaxResults(1);
            
            List resultList = q.list();
            if(!resultList.isEmpty())
                return (int)resultList.get(0);
            session.getTransaction().commit();
        } catch (HibernateException he) {
            he.printStackTrace();
        }
        return id;
    }

    /**
     * Rezerwacja miejsca na dany seans (wykorzystująca obiekty zamiast identyfikatorów)
     * @param show obiekt typu Show seansu, na który chcemy rezerwować miejsce
     * @param user obiekt typu User użytkownika, który rezerwuje miejsce
     * @param seat id miejsca, które rezerwuje użytkownik
     * @return 0 jeśli OK
     * @see Show
     * @see User
     */
    @WebMethod(operationName = "bookTicketForShowObj")
    public int bookTicketForShowObj(@WebParam(name = "show") Show show, @WebParam(name = "user") User user, @WebParam(name = "seat") int seat) {
//        return bookTicketForShow(show.getId(), user.getId(), seat);
        ReservationId resid = new ReservationId(getLastReservationId()+1, seat);
        return trySaveToDB(new Reservation(resid, show, user, false, false));
//        return 0;
    }
    
    /**
     * Rezerwacja miejsca na dany seans (wykorzystująca obiekty zamiast identyfikatorów)
     * @param show obiekt typu Show seansu, na który chcemy rezerwować miejsce
     * @param user obiekt typu User użytkownika, który rezerwuje miejsce
     * @param seat List of Integer z miejscami zarezerwowanymi
     * @return 0 jeśli OK
     * @see Show
     * @see User
     */
    @WebMethod(operationName = "bookTicketForShowObjSeatList")
    public int bookTicketForShowObjSeatList(@WebParam(name = "show") Show show, @WebParam(name = "user") User user, @WebParam(name = "seat") List<Integer> seat) {
//        return bookTicketForShow(show.getId(), user.getId(), seat);        
        int lastResId = getLastReservationId() + 1;
        for(int i: seat){
            ReservationId resid = new ReservationId(lastResId, i);
            Reservation res = new Reservation(resid, show, user, false, false);
            trySaveToDB(res);
        }
        return 0;
//        return 0;
    }

    /**
     * sprawdzenie logowania użytkownika
     * @param username nazwa użytkownika
     * @param password hasło (w formie czystej)
     * @return id użytkownika jeśli para użytkownik/hasło jest poprawna, -1 jeśli nie ma takiego użytkownika lub błędne hasło
     * @see User
     */
    @WebMethod(operationName = "userLogin")
    public int userLogin(@WebParam(name = "username") String username, @WebParam(name = "password") String password) {
        HashUtil.toSHA1(password.getBytes());
        List result = executeHQLQuery("From User u Where u.name = '" + username + "' AND u.password = '" + password + "'");
        if(result != null)
            if(!result.isEmpty()){
                User u = (User) result.get(0);
                return u.getId();
            }
        return -1;
    }
    /**
     * sprawdzenie logowania użytkownika wykorzystując zahashowane już hasło - bezpieczniejesze
     * @param username
     * @param hashPass
     * @return 
     */
    @WebMethod(operationName = "userLoginHashed")
    public int userLoginHashed(@WebParam(name = "username") String username, @WebParam(name = "hashPass") String hashPass){
        List result = executeHQLQuery("From User u Where u.name = '" + username + "'AND u.password = '" + hashPass +"'");
        if(result != null)
            if(!result.isEmpty()){
                User u = (User) result.get(0);
                return u.getId();
            }
        return -1;
    }

    /**
     * Metoda do potwierdzania rezerwacji
     * @param resid id rezerwacji
     * @return 0 jeśli ok
     * @see Reservation
     */
    @WebMethod(operationName = "confirmReservation")
    public int confirmReservation(@WebParam(name = "resid") int resid) {
//        Reservation res = (Reservation) getFromDB(Reservation.class, resid);
//        res.setChecked(true);
        int result = -1;
//        return trySaveUpdateToDB(res);
        Session session = HibernateUtil.getSessionFactory().openSession();
        try{
            session.beginTransaction(); 
            Query query = session.createQuery("update Reservation r set checked = 1" +
    				" where r.id.id = :id");
            query.setParameter("id", resid);
            result = query.executeUpdate();
            session.getTransaction().commit();
        }catch(HibernateException he){
            he.printStackTrace();
            session.getTransaction().rollback();
            session.close();
            return -1;
        }
        session.close();
        return result;
//        return 0;
    }

    /**
     * Zwraca listę sugerowanych filmów na podstawie id rodzaju filmu
     * @param genreid id rodzaju filmu
     * @return lista filmów jako ArrayList of Movie
     * @see Movie
     * @see Genre
     */
    @WebMethod(operationName = "movieSuggestionsByGenre")
    public List movieSuggestionsByGenre(@WebParam(name = "genreid") int genreid) {
        //TODO write your implementation code here:
        List result = executeHQLQuery("From Movie m Where m.genre.id = '" + genreid + "'");
        List movies = new ArrayList<Movie>();
        if(result != null)
            if(!result.isEmpty()){
                for(Object o : result){
                    movies.add((Movie)o);
                }
            }
        return movies;
    }

    /**
     * Zwraca listę sugerowanych filmów na podstawie id aktora
     * @param actorid id aktora
     * @return lista filmów jako ArrayList of Movie
     * @see Movie
     * @see Actor
     */
    @WebMethod(operationName = "movieSuggestionsByCast")
    public List movieSuggestionsByCast(@WebParam(name = "actorid") int actorid) {
        //TODO write your implementation code here:
        List cResult = executeHQLQuery("Select c.movie.id From Cast c Where c.actor.id = '" + actorid + "'");
//        List movIds = new ArrayList<Integer>();
        List movies = new ArrayList<Movie>();
        String preparedWhereQuery = "";
        if(cResult != null){
            if(!cResult.isEmpty()){
                for(Object o : cResult){
//                    movIds.add((int)o);
                    preparedWhereQuery += "m.id = '"+(int)o+"' OR ";
                }
                preparedWhereQuery = preparedWhereQuery.substring(0, preparedWhereQuery.length()-4);
            }
        }else return movies;
        List result = executeHQLQuery("From Movie m Where " + preparedWhereQuery);
        if(result != null)
            if(!result.isEmpty()){
                for(Object o : result){
                    movies.add((Movie)o);
                }
            }
        return movies;
    }
    /**
     * Zwraca listę sugerowanych filmów na podstawie wybranego filmu
     * @param movie film wybrany przez użytkownika
     * @return lista filmów jako ArrayList of Movie
     */
    @WebMethod(operationName = "movieSuggestionsByMovie")
    public List movieSuggestionsByMovie(@WebParam(name = "movie") Movie movie){
        List movies = new ArrayList<Movie>();
        movies.add(movieSuggestionsByGenre(movie.getGenre().getId()));
        List cast = getCast(movie.getId());
        for(Object o: cast){
            Actor a = (Actor)o;
            movies.add(movieSuggestionsByCast(a.getId()));
        }
        Set<Movie> movieSet = new HashSet<>();
        movieSet.addAll(movies);
        movies.clear();
        movies.add(movieSet);
        return movies;
    }
    
//    @WebMethod(operationName = "movieSuggestionsByMovieId")
//    public List movieSuggestionsByMovieId(@WebParam(name = "movie") int movieid){
//        List movies = new ArrayList<Movie>();
//        List result = executeHQLQuery("From Movie m Where m.id = '" + movieid + "'");
//        Movie movie = null;
//        if(result!=null){
//            if(!result.isEmpty()){
//                movie = (Movie)result.get(0);
//            }
//        }
//        movies.add(movieSuggestionsByGenre(movie.getGenre().getId()));
//        List cast = getCast(movie.getId());
//        for(Object o: cast){
//            Actor a = (Actor)o;
//            movies.add(movieSuggestionsByCast(a.getId()));
//        }
////        Set<Movie> movieSet = new HashSet<>();
////        movieSet.addAll(movies);
////        movies.clear();
////        movies.add(movieSet);
//        List<Movie> deduped = (List<Movie>) movies.stream().distinct().collect(java.util.stream.Collectors.toList());
//        return movies;
//    }
    
    /**
     * Zwraca listę sugerowanych filmów na podstawie listy aktorów
     * @param cast lista aktorów (List of Actor)
     * @return lista filmów jako ArrayList of Movie
     * @see Movie
     * @see Actor
     * @see Cast
     */
    @WebMethod(operationName = "movieSuggestionsByCastList")
    public List movieSuggestionsByCastList(@WebParam(name = "cast") java.util.List<Actor> cast) {
        //TODO write your implementation code here:
        return null;
    }
    
// <editor-fold defaultstate="collapsed" desc="Admin methods">
    /**
     * Metoda administracyjna - dodanie użytkownika
     * @param username nazwa użytkownika
     * @param password hasło użytkownika(czyste)
     * @param email adres email użytkownika
     * @param elevation uprawnienia użytkownika
     * @return 0 jeśli się powiedzie -1 jeśli nie
     * @see User
     */
    @WebMethod(operationName = "adminAddUser")
    public int adminAddUser(@WebParam(name = "username") String username, @WebParam(name = "password") String password, @WebParam(name = "email") String email, @WebParam(name = "elevation") int elevation) {
        User u = new User();
        u.setName(username);
        u.setPassword(HashUtil.toSHA1(password.getBytes()));
        u.setEmail(email);
        u.setType(elevation);
        int res = trySaveToDB(u);
        return res;
    }

    /**
     * Metoda administracyjna - dodanie seansu
     * @param movie film na danym seansie
     * @param room sala kinowa
     * @param time czas seasnu
     * @return 0 jeśli się powiedzie, -1 jeśli nie
     * @see Movie
     * @see Room
     * @see Date
     * @see Show
     */
    @WebMethod(operationName = "adminAddShow")
    public int adminAddShow(@WebParam(name = "movie") Movie movie, @WebParam(name = "room") Room room, @WebParam(name = "time") Date time) {
        Show s = new Show();
        s.setMovie(movie);
        s.setRoom(room);
        s.setTime(time);
        int res = trySaveToDB(s);
        return res;
    }

    /**
     * Metoda administracyjna - dodanie filmu
     * @param name tytuł filmu
     * @param description opis filmu
     * @param director reżyser
     * @param rating ograniczenia wiekowe
     * @param genre rodzaj filmu
     * @return 0 jeśli się powiedzie, -1 jeśli nie
     * @see Movie
     */
    @WebMethod(operationName = "adminAddMovie")
    public int adminAddMovie(@WebParam(name = "name") String name, @WebParam(name = "description") String description, @WebParam(name = "director") String director, @WebParam(name = "rating") int rating, @WebParam(name = "genre") Genre genre) {
        Movie m = new Movie();
        m.setDescription(description);
        m.setDirector(director);
        m.setGenre(genre);
        m.setName(name);
        m.setRating(rating);
        int res = trySaveToDB(m);
        return res;
    }

    /**
     * Metoda administracyjna - dodanie aktora
     * @param firstName imię aktora
     * @param lastName nazwisko aktora
     * @return 0 jeśli się powiedzie, -1 jeśli nie
     * @see Actor
     */
    @WebMethod(operationName = "adminAddActor")
    public int adminAddActor(@WebParam(name = "firstName") String firstName, @WebParam(name = "lastName") String lastName) {
        Actor a = new Actor();
        a.setFirstName(firstName);
        a.setLastName(lastName);
        
        int res = trySaveToDB(a);
        return res;
    }

    /**
     * Metoda administracyjna - dodanie rodzaju filmu
     * @param genre rodzaj filmu
     * @return 0 jeśli się powiedzie, -1 jeśli nie
     * @see Genre
     * @see Movie
     */
    @WebMethod(operationName = "adminAddGenre")
    public int adminAddGenre(@WebParam(name = "genre") String genre) {
        Genre g = new Genre();
        g.setGenre(genre);
        
        int res = trySaveToDB(g);
        return res;
    }

    /**
     * Metoda administracyjna - dodanie aktora do obsady filmu
     * @param actor aktor do dodania
     * @param movie film do zaktualizowania obsady
     * @return 0 jeśli się powiedzie, -1 jeśli nie
     * @see Cast
     * @see Movie
     * @see Actor
     */
    @WebMethod(operationName = "adminAddCast")
    public int adminAddCast(@WebParam(name = "actor") Actor actor, @WebParam(name = "movie") Movie movie) {
        Cast c = new Cast();
        c.setActor(actor);
        c.setMovie(movie);
        
        int res = trySaveToDB(c);
        return res;
    }
 // </editor-fold>  

    /**
     * Web service operation
     */
    @WebMethod(operationName = "movieSuggestionsByMovieId2")
    public List movieSuggestionsByMovieId2(@WebParam(name = "movieid") int movieid) {
        List result = executeHQLQuery("From Movie m Where m.id = '" + movieid + "'");
        Movie movie = null;
        if(result!=null){
            if(!result.isEmpty()){
                movie = (Movie)result.get(0);
            }
        }
        List movies = new ArrayList<Movie>();
        if(movie == null) return movies;
        movies.add(movieSuggestionsByGenre(movie.getGenre().getId()));
        List cast = getCast(movie.getId());
        for(Object o: cast){
            Actor a = (Actor)o;
            movies.add(movieSuggestionsByCast(a.getId()));
        }
//        Set<Movie> movieSet = new HashSet<>();
//        movieSet.addAll(movies);
//        movies.clear();
//        movies.add(movieSet);
        List<Movie> deduped = (List<Movie>) movies.stream().distinct().collect(java.util.stream.Collectors.toList());
        return movies;//deduped;
    }

    /**
     * Web service operation
     */
    @WebMethod(operationName = "operation")
    public int operation() {
        //TODO write your implementation code here:
        List<Integer> list = new ArrayList<>();
        list.add(3);
        list.add(5);
        list.add(6);
        bookTicketForShowSeatList(1, 1, list);
        return getLastReservationId();
    }

    /**
     * 
     * @param showid
     * @param uid
     * @param seatList
     * @return 
     */
    @WebMethod(operationName = "bookTicketForShowSeatList")
    public int bookTicketForShowSeatList(@WebParam(name = "showid") int showid, @WebParam(name = "uid") int uid, @WebParam(name = "seatList") List<Integer> seatList) {
        int lastId = getLastReservationId();
        for(int i: seatList){
            Show s = new Show();
            s.setId(showid);
            User u = new User();
            u.setId(uid);
            ReservationId resid = new ReservationId(lastId+1, i);
            Reservation res = new Reservation(resid, s, u, false, false);
            trySaveToDB(res);
        }
        return 0;
    }

    /**
     * Metoda zwraca listę użytkowników zarejestrowanych w systemie 
     * @return lista użytkowników jako ArrayList of User
     */
    @WebMethod(operationName = "getUserList")
    public List getUserList() {
        List result = executeHQLQuery("From User");
        if(!result.isEmpty()){
            List users = new ArrayList<User>();
            for(Object o : result){
                User u = (User)o;
                u.setPassword("*******");
                users.add(u);
            }
            return users;
        }
        return new ArrayList<User>();
    }

    /**
     * Metoda zwraca listę rodzajów filmów
     * @return lista rodzajów jako ArrayList of Genre
     */
    @WebMethod(operationName = "getGenreList")
    public List getGenreList() {
        List result = executeHQLQuery("From Genre");
        if(!result.isEmpty()){
            List genres = new ArrayList<Genre>();
            for(Object o : result){
                genres.add((Genre)o);
            }
            return genres;
        }
        return new ArrayList<Genre>();
    }

    /**
     * Metoda pozwalająca na edycję aktora
     * @param id id aktora do edycji
     * @param firstName nowe imię
     * @param lastName nowe nazwisko
     * @return 
     */
    @WebMethod(operationName = "adminEditActor")
    public int adminEditActor(@WebParam(name = "id") int id, @WebParam(name = "firstName") String firstName, @WebParam(name = "lastName") String lastName) {
        List result = executeHQLQuery("From Actor a Where a.id = "+id);
        if(!result.isEmpty()){
            Actor act = (Actor)result.get(0);
            act.setFirstName(firstName);
            act.setLastName(lastName);
            return trySaveUpdateToDB(act);
        }
        return -1;
    }

    /**
     * Metoda pozwalająca na edycję gatunku filmu
     * @param id id gatunku do edycji
     * @param genre nowa nazwa gatunku
     * @return 0 jeśli OK
     */
    @WebMethod(operationName = "adminEditGenre")
    public int adminEditGenre(@WebParam(name = "id") int id, @WebParam(name = "genre") String genre) {
        List result = executeHQLQuery("From Genre a Where a.id = "+id);
        if(!result.isEmpty()){
            Genre gen = (Genre)result.get(0);
            gen.setGenre(genre);
            return trySaveUpdateToDB(gen);
        }
        return -1;
    }

    /**
     * Metoda pozwalająca na edycję filmu
     * @param id id filmu do edycji
     * @param genre_id nowe id gatunku
     * @param name nowy tytuł
     * @param description nowy opis
     * @param rating nowy rating
     * @param director nowy reżyser
     * @return 0 jeśli OK
     */
    @WebMethod(operationName = "adminEditMovie")
    public int adminEditMovie(@WebParam(name = "id") int id, @WebParam(name = "genre_id") int genre_id, @WebParam(name = "name") String name, @WebParam(name = "description") String description, @WebParam(name = "rating") int rating, @WebParam(name = "director") String director) {
        List result = executeHQLQuery("From Movie a Where a.id = "+id);
        if(!result.isEmpty()){
            Movie mov = (Movie)result.get(0);
            Genre gen = new Genre();
            gen.setId(genre_id);
            mov.setGenre(gen);
            mov.setName(name);
            mov.setDescription(description);
            mov.setDirector(director);
            mov.setRating(rating);
            return trySaveUpdateToDB(mov);
        }
        return -1;
    }

    /**
     * Metoda pozwalająca na dodanie sali kinowej
     * @param id id sali kinowej
     * @return 0 jeśli OK
     */
    @WebMethod(operationName = "adminAddRoom")
    public int adminAddRoom(@WebParam(name = "id") int id) {
        //TODO write your implementation code here:
        return 0;
    }

    /**
     * Metoda pozwalająca na edycję seansu
     * @param id id seansu do edycji
     * @param movie_id id nowego filmu
     * @param room_id id nowej sali 
     * @param time nowy czas
     * @return 
     */
    @WebMethod(operationName = "adminEditShow")
    public int adminEditShow(@WebParam(name = "id") int id, @WebParam(name = "movie_id") int movie_id, @WebParam(name = "room_id") int room_id, @WebParam(name = "time") Date time) {
        List result = executeHQLQuery("From Show a Where a.id = "+id);
        if(!result.isEmpty()){
            Show sh = (Show)result.get(0);            
            Movie mov = new Movie();
            mov.setId(movie_id);
            Room rom = new Room();
            rom.setId(room_id);
            sh.setMovie(mov);
            sh.setRoom(rom);
            sh.setTime(time);
            return trySaveUpdateToDB(sh);
        }
        return -1;
    }

    /**
     * Metoda pozwalająca na edycję użytkownika
     * @param id id użytkownika do edycji
     * @param name nowa nazwa
     * @param password nowe hasło (zahashowane)
     * @param type nowy typ
     * @param email nowy email
     * @return 
     */
    @WebMethod(operationName = "adminEditUser")
    public int adminEditUser(@WebParam(name = "id") int id, @WebParam(name = "name") String name, @WebParam(name = "password") String password, @WebParam(name = "type") int type, @WebParam(name = "email") String email) {
        List result = executeHQLQuery("From User a Where a.id = "+id);
        if(!result.isEmpty()){
            User usr = (User)result.get(0);
            usr.setEmail(email);
            usr.setName(name);
            usr.setPassword(password);
            usr.setType(type);
            return trySaveUpdateToDB(usr);
        }
        return -1;
    }

    /**
     * 
     * @param id
     * @return 
     */
    @WebMethod(operationName = "adminDeleteActor")
    public int adminDeleteActor(@WebParam(name = "id") int id) {
        return executeDeleteQuery("Delete Actor where id = " + id);
    }

    /**
     * 
     * @param id
     * @return 
     */
    @WebMethod(operationName = "adminDeleteGenre")
    public int adminDeleteGenre(@WebParam(name = "id") int id) {
        return executeDeleteQuery("Delete Genre where id = " + id);
    }

    /**
     * 
     * @param id
     * @return 
     */
    @WebMethod(operationName = "adminDeleteMovie")
    public int adminDeleteMovie(@WebParam(name = "id") int id) {
        return executeDeleteQuery("Delete Movie where id = " + id);
    }

    /**
     * 
     * @param id
     * @return 
     */
    @WebMethod(operationName = "adminDeleteRoom")
    public int adminDeleteRoom(@WebParam(name = "id") int id) {
        return executeDeleteQuery("Delete Room where id = " + id);
    }

    /**
     * 
     * @param id
     * @return 
     */
    @WebMethod(operationName = "adminDeleteShow")
    public int adminDeleteShow(@WebParam(name = "id") int id) {
        return executeDeleteQuery("Delete Show where id = " + id);
    }

    /**
     * 
     * @param id
     * @return 
     */
    @WebMethod(operationName = "adminDeleteUser")
    public int adminDeleteUser(@WebParam(name = "id") int id) {
        return executeDeleteQuery("Delete User where id = " + id);
    }

    
    
}
