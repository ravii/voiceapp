package com.myapp.voiceapp.controllers;

import com.myapp.voiceapp.models.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.myapp.voiceapp.repositories.CategoryRepository;
import com.myapp.voiceapp.repositories.QueryRepository;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    QueryRepository queryRepository;
    
    @Autowired
    CategoryRepository categoryRepository;


    @GetMapping(value = "/")
    public String index(Model model) {
        List<Query> allQueries = queryRepository.findAll();
        model.addAttribute("queries", allQueries);
        model.addAttribute("categories", categoryRepository.findAll());

        return "queries/list";
    }
}
