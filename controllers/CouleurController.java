package mini.projet.archi.controllers;
import mini.projet.archi.models.Couleur;
import mini.projet.archi.services.CouleurService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/couleurs")
public class CouleurController {

    @Autowired
    private CouleurService couleurService;

    @GetMapping
    public String listCouleurs(Model model) {
        List<Couleur> couleurs = couleurService.findAll();
        model.addAttribute("couleurs", couleurs);
        return "couleurs/list";
    }

    @PostMapping
    public String saveCouleur(@ModelAttribute Couleur couleur) {
        couleurService.save(couleur);
        return "redirect:/couleurs";
    }

    @DeleteMapping("/{id}")
    public String deleteCouleur(@PathVariable Long id) {
        couleurService.deleteById(id);
        return "redirect:/couleurs";
    }
}
