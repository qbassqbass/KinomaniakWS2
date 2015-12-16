package entity;
// Generated 2015-10-30 14:40:09 by Hibernate Tools 4.3.1


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Movie generated by hbm2java
 */
public class Movie  implements java.io.Serializable {


     private Integer id;
     private Genre genre;
     private String name;
     private String description;
     private int rating;
     private String director;
     private Set shows = new HashSet(0);
     private Set casts = new HashSet(0);
     private ArrayList<Actor> cast = new ArrayList<Actor>();

    public ArrayList<Actor> getCast() {
        return cast;
    }

    public void setCast(ArrayList<Actor> cast) {
        this.cast = cast;
    }

    public Movie() {
    }

	
    public Movie(Genre genre, String name, String description, int rating, String director) {
        this.genre = genre;
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.director = director;
    }
    public Movie(Genre genre, String name, String description, int rating, String director, Set shows, Set casts) {
       this.genre = genre;
       this.name = name;
       this.description = description;
       this.rating = rating;
       this.director = director;
       this.shows = shows;
       this.casts = casts;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Genre getGenre() {
        return this.genre;
    }
    
    public void setGenre(Genre genre) {
        this.genre = genre;
    }
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    public int getRating() {
        return this.rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getDirector() {
        return this.director;
    }
    
    public void setDirector(String director) {
        this.director = director;
    }
    
    public Set getShows() {
        return this.shows;
    }
    
    public void setShows(Set shows) {
        this.shows = shows;
    }
    public Set getCasts() {
        return this.casts;
    }
    
    public void setCasts(Set casts) {
        this.casts = casts;
    }
    
    public void addActor(Actor a){
        this.cast.add(a);
    }

    @Override
    public String toString(){
        return this.getId()+". "+this.getName()+"\nDirector: "+this.getDirector()+"\nGenre: "+this.getGenre().getGenre()+"\nDescription: "+this.getDescription();
    }



}


