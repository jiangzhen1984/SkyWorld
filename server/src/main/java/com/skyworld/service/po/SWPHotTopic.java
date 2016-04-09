package com.skyworld.service.po;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * SWPHotTopic entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name="SW_HOT_TOPIC")

public class SWPHotTopic  implements java.io.Serializable {


    // Fields    

     private long id;
     private Integer type;
     private Integer priority;
     private String name;
     private Date updateTime;
     private boolean state;


    // Constructors

    /** default constructor */
    public SWPHotTopic() {
    }

    
    /** full constructor */
    public SWPHotTopic(Integer type, Integer priority, String name, Date updateTime, boolean state) {
        this.type = type;
        this.priority = priority;
        this.name = name;
        this.updateTime = updateTime;
        this.state = state;
    }

   
    // Property accessors
    @Id @GeneratedValue(strategy=IDENTITY)
    
    @Column(name="id", unique=true, nullable=false)

    public long getId() {
        return this.id;
    }
    
    public void setId(long id) {
        this.id = id;
    }
    
    @Column(name="type", nullable=false)

    public Integer getType() {
        return this.type;
    }
    
    public void setType(Integer type) {
        this.type = type;
    }
    
    @Column(name="priority", nullable=false)

    public Integer getPriority() {
        return this.priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
    
    @Column(name="name", nullable=false, length=200)

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Column(name="update_time", nullable=false, length=19)

    public Date getUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    
    @Column(name="state", nullable=false)

    public boolean getState() {
        return this.state;
    }
    
    public void setState(boolean state) {
        this.state = state;
    }
   
}