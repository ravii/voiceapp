package com.myapp.voiceapp.controllers;

import com.myapp.voiceapp.models.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myapp.voiceapp.models.Category;
import com.myapp.voiceapp.repositories.QueryRepository;
import com.myapp.voiceapp.repositories.CategoryRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping(value = "/queries")
public class QueryController extends AbstractBaseController {

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping
    public String listEvents(Model model) {
        List<Query> allQueries = queryRepository.findAll();
        model.addAttribute("queries", allQueries);
        model.addAttribute("categories", categoryRepository.findAll());
        return "queries/list";
    }
    
    @GetMapping(value="search")
    public String FilterByCategories(Model model,@RequestParam(name = "categories") String categoryId) {
    	
    	List<Integer> ids = new ArrayList<>();
    	List<Query> allQueries = new ArrayList<>();
    	if(categoryId.equalsIgnoreCase("ALL")) {
    		allQueries = queryRepository.findAll();
    	} else {
    		ids.add(new Integer(categoryId));
    		allQueries = queryRepository.findByCategories(ids);
    	}
    	
        
        System.out.println("allQueries.........."+allQueries);
        model.addAttribute("queries", allQueries);
        model.addAttribute("categories", categoryRepository.findAll());
        return "queries/list"; 
    }
    
    	

    @GetMapping(value = "create")
    public String displayCreateEventForm(Model model, HttpServletRequest request) {
        model.addAttribute(new Query());
        model.addAttribute("actionUrl", request.getRequestURI());
        model.addAttribute("title", "Create Query");
        model.addAttribute("categories", categoryRepository.findAll());
        return "queries/create-or-update";
    }

    @PostMapping(value = "create")
    public String processCreateEventForm(@Valid @ModelAttribute Query query,
                                         Errors errors,
                                         @RequestParam(name = "categories", required = false) List<Integer> categoryIds) {

        if (errors.hasErrors())
            return "queries/create-or-update";

        syncCategoryLists(categoryIds, query.getCategories());

        User usr = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        query.setUserId(usr.getUsername());
        queryRepository.save(query);
        return "redirect:/queries";
    }

    @GetMapping(value = "detail/{uid}")
    public String displayEventDetails(@PathVariable int uid, Model model) {

        model.addAttribute("title", "Query Details");

        Optional<Query> result = queryRepository.findById(uid);
        if (result.isPresent()) {
            Query query = result.get();
            model.addAttribute(query);
            model.addAttribute("categoryNames", query.getCategoriesFormatted());
        } else {
            model.addAttribute(MESSAGE_KEY, "warning|No event found with id: " + Integer.toString(uid));
        }

        return "queries/details";
    }
    
    @PostMapping(value = "updateRatingCount/{uid}")
    public String updateRatingCount(@PathVariable int uid,@RequestParam(name = "type") String type, HttpServletRequest request) {
    	
    	  Query query = queryRepository.findById(uid).get();
    	  if(type!=null && type.equals("option_1")) {
    		  query.setOption_1_count(query.getOption_1_count()+1);
    	  } else if(type!=null && type.equals("option_2")) {
    		  query.setOption_2_count(query.getOption_2_count()+1);
    	  } else if(type!=null && type.equals("likeCount")) {
    		  query.setLikeCount(query.getLikeCount()+1);
    	  } else if(type!=null && type.equals("dislikeCount")) {
    		  query.setDislikeCount(query.getDislikeCount()+1);
    	  }
    	  
    	  queryRepository.save(query);
    	  return "redirect:/queries";
    }

    @GetMapping(value = "update/{uid}")
    public String displayUpdateEventForm(@PathVariable int uid, Model model, HttpServletRequest request) {

        model.addAttribute("title", "Update Query");
        model.addAttribute("actionUrl", request.getRequestURI());

        Optional<Query> query = queryRepository.findById(uid);
        if (query.isPresent()) {
            model.addAttribute(query.get());
            model.addAttribute("categories", categoryRepository.findAll());
        } else {
            model.addAttribute(MESSAGE_KEY, "warning|No event found with id: " + Integer.toString(uid));
        }

        return "queries/create-or-update";
    }

    @PostMapping(value = "update/{uid}")
    public String processUpdateEventForm(@Valid @ModelAttribute Query query,
                                         RedirectAttributes model,
                                         Errors errors,
                                         @RequestParam(name = "categories", required = false) List<Integer> categories) {

        if (errors.hasErrors())
            return "queries/create-or-update";

        syncCategoryLists(categories, query.getCategories());
        User usr = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        query.setUserId(usr.getUsername());
        queryRepository.save(query);
        model.addFlashAttribute(MESSAGE_KEY, "success|Updated queries: " + query.getTitle());

        return "redirect:/queries";
    }

    @PostMapping(value = "delete/{uid}")
    public String processDeleteEventForm(@PathVariable int uid, RedirectAttributes model) {

        Optional<Query> result = queryRepository.findById(uid);
        if (result.isPresent()) {
            queryRepository.delete(result.get());
            model.addFlashAttribute(MESSAGE_KEY, "success|Query deleted");
            return "redirect:/queries";
        } else {
            model.addFlashAttribute(MESSAGE_KEY, "danger|Query with ID does not exist: " +  uid);
            return "redirect:/queries";
        }
    }

    private void syncCategoryLists(List<Integer> volunteerUids, List<Category> categories) {

        if (volunteerUids == null)
            return;

        List<Category> newCategoryList = categoryRepository.findAllById(volunteerUids);
        categories.removeIf(v -> volunteerUids.contains(v.getUid()));
        categories.addAll(newCategoryList);
    }

}
