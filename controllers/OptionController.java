package mini.projet.archi.controllers;

import mini.projet.archi.models.Options;
import mini.projet.archi.services.OptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/options")
public class OptionController {

    @Autowired
    private OptionService optionService;

    @GetMapping
    public String listOptions(Model model) {
        List<Options> options = optionService.findAll();
        model.addAttribute("options", options);
        return "options/list";
    }

    @PostMapping
    public String saveOption(@ModelAttribute Options option) {
        optionService.save(option);
        return "redirect:/options";
    }

    @DeleteMapping("/{id}")
    public String deleteOption(@PathVariable Long id) {
        optionService.deleteById(id);
        return "redirect:/options";
    }
}