package pharmacie.entity;


import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString

public class Dispensaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE) // la clé est autogénérée par la BD, On ne veut pas de "setter"
    private Integer id;

    @NonNull
    @Size(max = 255)
    @Column(length = 255, unique = true)
    @NotBlank // pour éviter les libellés vides
    private String code;

    @NonNull
    @Size(max = 255)
    @Column(length = 255)
    @NotBlank // pour éviter les libellés vides
    private String nom;

    @NonNull
    @NotBlank
    @Size(max = 255)
    @Column(length = 255)
    private String adresse;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String ville;

    @NonNull
    @NotBlank
    @Size(max = 20)
    @Column(length = 20)
    private String code_postal;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String pays;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String region;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String fax;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String telephone;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String contact;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String fonction;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dispensaire")
    private List<Commande> commandes = new LinkedList<>();
}
