package entity;
// Generated 2016-01-21 21:53:42 by Hibernate Tools 4.3.1



/**
 * ReservationId generated by hbm2java
 */
public class ReservationId  implements java.io.Serializable {


     private int id;
     private int seat;

    public ReservationId() {
    }

    public ReservationId(int id, int seat) {
       this.id = id;
       this.seat = seat;
    }
   
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public int getSeat() {
        return this.seat;
    }
    
    public void setSeat(int seat) {
        this.seat = seat;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof ReservationId) ) return false;
		 ReservationId castOther = ( ReservationId ) other; 
         
		 return (this.getId()==castOther.getId())
 && (this.getSeat()==castOther.getSeat());
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + this.getId();
         result = 37 * result + this.getSeat();
         return result;
   }   


}


