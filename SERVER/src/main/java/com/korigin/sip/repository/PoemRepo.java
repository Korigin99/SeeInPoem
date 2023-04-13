package com.korigin.sip.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.korigin.sip.model.Poem;

public interface PoemRepo extends JpaRepository<Poem, Long>{
    
}
