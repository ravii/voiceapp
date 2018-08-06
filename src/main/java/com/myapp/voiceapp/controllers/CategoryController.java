package com.myapp.voiceapp.controllers;

import com.myapp.voiceapp.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.myapp.voiceapp.repositories.CategoryRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping(value = "categories")
public class CategoryController extends AbstractBaseController {

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping
    public String listVolunteers(Model model) {
        model.addAttribute("title", "categories");
        model.addAttribute("categories", categoryRepository.findAll());
        return "categories/list";
    }

    @GetMapping(value = "create")
    public String displayCreateVolunteerForm(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Create Category");
        model.addAttribute(new Category());
        model.addAttribute("actionUrl", request.getRequestURI());
        return "categories/create-or-update.html";
    }

    @PostMapping(value = "create")
    public String processCreateVolunteerForm(@ModelAttribute @Valid Category category,
                                             Errors errors,
                                             RedirectAttributes model) {

        if (errors.hasErrors())
            return "categories/create-or-update";

        categoryRepository.save(category);
        model.addFlashAttribute(MESSAGE_KEY, "success|New category added: " + category.getName());

        return "redirect:/categories";
    }
}
