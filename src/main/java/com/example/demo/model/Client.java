package com.example.demo.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Document(collection = "clients")
public class Client {
    @Id
    private String id;

    @NotBlank(message = "Nom est obligatoire")
    private String nom;

    @NotBlank(message = "Adresse est obligatoire")
    private String adresse;

    @NotBlank(message = "Nom de société est obligatoire")
    private String nomSociete;

    @NotBlank(message = "Numéro de téléphone est obligatoire")
    private String numTel;

    @NotBlank(message = "Email est obligatoire")
    @Email(message = "Format email invalide")
    private String email;

    @NotBlank(message = "Mot de passe est obligatoire")
    private String password;

    // Liste des IDs de users associés (Many-to-Many)
    private List<String> userIds;

    // Getters et setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public String getNomSociete() { return nomSociete; }
    public void setNomSociete(String nomSociete) { this.nomSociete = nomSociete; }
    public String getNumTel() { return numTel; }
    public void setNumTel(String numTel) { this.numTel = numTel; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<String> getUserIds() { return userIds; }
    public void setUserIds(List<String> userIds) { this.userIds = userIds; }
} 