package entity;
// Generated 2015-10-30 14:40:09 by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Room generated by hbm2java
 */
public class Room  implements java.io.Serializable {


     private Integer id;
     private Set shows = new HashSet(0);

    public Room() {
    }

    public Room(Set shows) {
       this.shows = shows;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public Set getShows() {
        return this.shows;
    }
    
    public void setShows(Set shows) {
        this.shows = shows;
    }




}


