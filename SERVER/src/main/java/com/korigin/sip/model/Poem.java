package com.korigin.sip.model;

import java.sql.Date;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Poem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int poemIndex;

    @Column
    private String poemTitle;

    @UpdateTimestamp
    @Column
    private Date poemDate;
    
    @Column
    private String poemWriter;

    @Column(columnDefinition = "TEXT")
    private String poemText;
}
