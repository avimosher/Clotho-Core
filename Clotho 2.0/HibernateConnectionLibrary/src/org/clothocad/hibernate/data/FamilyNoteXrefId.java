package org.clothocad.hibernate.data;
// Generated Jul 26, 2010 11:53:27 AM by Hibernate Tools 3.2.1.GA



/**
 * FamilyNoteXrefId generated by hbm2java
 */
public class FamilyNoteXrefId  implements java.io.Serializable {


     private String familyId;
     private String noteId;

    public FamilyNoteXrefId() {
    }

    public FamilyNoteXrefId(String familyId, String noteId) {
       this.familyId = familyId;
       this.noteId = noteId;
    }
   
    public String getFamilyId() {
        return this.familyId;
    }
    
    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }
    public String getNoteId() {
        return this.noteId;
    }
    
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }


   public boolean equals(Object other) {
         if ( (this == other ) ) return true;
		 if ( (other == null ) ) return false;
		 if ( !(other instanceof FamilyNoteXrefId) ) return false;
		 FamilyNoteXrefId castOther = ( FamilyNoteXrefId ) other; 
         
		 return ( (this.getFamilyId()==castOther.getFamilyId()) || ( this.getFamilyId()!=null && castOther.getFamilyId()!=null && this.getFamilyId().equals(castOther.getFamilyId()) ) )
 && ( (this.getNoteId()==castOther.getNoteId()) || ( this.getNoteId()!=null && castOther.getNoteId()!=null && this.getNoteId().equals(castOther.getNoteId()) ) );
   }
   
   public int hashCode() {
         int result = 17;
         
         result = 37 * result + ( getFamilyId() == null ? 0 : this.getFamilyId().hashCode() );
         result = 37 * result + ( getNoteId() == null ? 0 : this.getNoteId().hashCode() );
         return result;
   }   


}


