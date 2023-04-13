package com.korigin.sip.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.korigin.sip.model.Poem;
import com.korigin.sip.repository.PoemRepo;

@RestController
public class PoemController {

    @Autowired
    PoemRepo poemrepo;

    @GetMapping("/api/v1/poem")
    public List<Poem> callAllPoem() {
        return poemrepo.findAll();
    }

    @GetMapping("/api/v1/poem/{poemIndex}")
    public Poem callPoem(@PathVariable Long poemIndex) {
        return poemrepo.findById(poemIndex).get();
    }

    @PostMapping("/api/v1/poem")
    public Poem writePoem(@RequestBody Poem poem) {
        return poemrepo.save(poem);
    }

    @DeleteMapping("/api/v1/poem")
    public Boolean deletePoem(@RequestBody Long poemIndex) {
        try {
            poemrepo.deleteById(poemIndex);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
