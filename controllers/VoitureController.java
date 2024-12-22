package mini.projet.archi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import mini.projet.archi.enumeration.Disponibilite;
import mini.projet.archi.models.Couleur;
import mini.projet.archi.models.Options;
import mini.projet.archi.models.Voiture;
import mini.projet.archi.services.CouleurService;
import mini.projet.archi.services.VoitureService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/voitures")
public class VoitureController {

    @Autowired
    private VoitureService voitureService;

    @Autowired
    private CouleurService couleurService;

    @GetMapping
    public String listVoitures(Model model) {
        List<Voiture> voitures = voitureService.findAll();
        model.addAttribute("voitures", voitures);
        return "voiture";
    }

    @GetMapping("/add")
    public String addVoitureForm(Model model) {
        model.addAttribute("voiture", new Voiture());
        return "voitures/add";
    }

    @PostMapping("/createVoiture")
    public String createVoiture(
        @RequestParam String modele,
        @RequestParam Double prix,
        @RequestParam String disponibilite,
        @RequestParam(required = false) String date_disponibilite,
        @RequestParam("photo") MultipartFile photo,  // Handle the photo as MultipartFile
        @RequestParam List<String> optionDescriptions,
        @RequestParam List<Double> optionPrices,
        @RequestParam List<String> colorNames,
        @RequestParam List<Double> colorPrices,
        RedirectAttributes redirectAttributes
    ) {
        // Process the photo
        String photoPath = saveUploadedFile(photo);

        Voiture voiture = new Voiture();
        voiture.setModele(modele);
        voiture.setPrix(prix);
        voiture.setDisponibilite(Disponibilite.valueOf(disponibilite));
        if (date_disponibilite != null && !date_disponibilite.isEmpty()) {
            LocalDate localDate = LocalDate.parse(date_disponibilite);
            Date date = Date.valueOf(localDate);
            voiture.setDate_disponibilite(date);
        }
        voiture.setPhoto(photoPath);  // Save the file path to the database

        // Process options and associate them with voiture
        List<Options> optionsList = new ArrayList<>();
        for (int i = 0; i < optionDescriptions.size(); i++) {
            Options option = new Options();
            option.setDescription(optionDescriptions.get(i));
            option.setPrix_option(optionPrices.get(i).floatValue());
            optionsList.add(option);
        }
        voiture.setOptions(optionsList);  // Set options in voiture

        // Save voiture (this will also persist options due to cascade)
        voitureService.save(voiture);

        // Process colors and associate them with voiture
        List<Couleur> couleursList = new ArrayList<>();
        for (int i = 0; i < colorNames.size(); i++) {
            Couleur couleur = new Couleur();
            couleur.setColor_name(colorNames.get(i));
            couleur.setPrix_couleur(colorPrices.get(i));
            couleur.setVoiture(voiture);  // Associate the saved voiture with the couleur
            couleurService.save(couleur);
            couleursList.add(couleur);
        }
        voiture.setCouleurs(couleursList);  // Set colors in voiture

        redirectAttributes.addFlashAttribute("message", "Voiture ajoutée avec succès!");
        return "redirect:/voitures";
    }

    private String saveUploadedFile(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IOException("File is empty.");
            }

            Path uploadDirectory = Paths.get("uploads");
            if (!Files.exists(uploadDirectory)) {
                Files.createDirectories(uploadDirectory);
            }

            String fileName = file.getOriginalFilename();
            Path destinationPath = uploadDirectory.resolve(fileName);

            Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

            return "uploads/" + fileName;  // Save the relative path to the database

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to store file: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    public String updateVoiture(@ModelAttribute Voiture voiture) {
        voitureService.updateVoiture(voiture);
        return "redirect:/voitures";
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVoiture(@PathVariable Long id) {
        voitureService.deleteById(id);
        return ResponseEntity.ok("Voiture supprimée avec succès");
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Voiture voiture = voitureService.getVoitureById(id);
        model.addAttribute("voiture", voiture);
        return "edit_voiture";
    }
}