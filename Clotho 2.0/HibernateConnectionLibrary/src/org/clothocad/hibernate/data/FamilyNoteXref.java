package org.clothocad.hibernate.data;
// Generated Jul 26, 2010 11:53:27 AM by Hibernate Tools 3.2.1.GA


import java.util.Date;

/**
 * FamilyNoteXref generated by hbm2java
 */
public class FamilyNoteXref  implements java.io.Serializable {


     private FamilyNoteXrefId id;
     private NoteTable noteTable;
     private FamilyTable familyTable;
     private Date dateCreated;
     private Date lastModified;

    public FamilyNoteXref() {
    }

	
    public FamilyNoteXref(FamilyNoteXrefId id, NoteTable noteTable, FamilyTable familyTable) {
        this.id = id;
        this.noteTable = noteTable;
        this.familyTable = familyTable;

        //JCA added this:
       this.dateCreated = new Date();
       this.lastModified = new Date();
    }
    public FamilyNoteXref(FamilyNoteXrefId id, NoteTable noteTable, FamilyTable familyTable, Date dateCreated, Date lastModified) {
       this.id = id;
       this.noteTable = noteTable;
       this.familyTable = familyTable;
       this.dateCreated = dateCreated;
       this.lastModified = lastModified;
    }
   
    public FamilyNoteXrefId getId() {
        return this.id;
    }
    
    public void setId(FamilyNoteXrefId id) {
        this.id = id;
    }
    public NoteTable getNoteTable() {
        return this.noteTable;
    }
    
    public void setNoteTable(NoteTable noteTable) {
        this.noteTable = noteTable;
    }
    public FamilyTable getFamilyTable() {
        return this.familyTable;
    }
    
    public void setFamilyTable(FamilyTable familyTable) {
        this.familyTable = familyTable;
    }
    public Date getDateCreated() {
        return this.dateCreated;
    }
    
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
    public Date getLastModified() {
        return this.lastModified;
    }
    
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }




}


