package pharmacie.entity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString

public class Commande {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE) // la clé est autogénérée par la BD, On ne veut pas de "setter"
	private Integer numero = null;

    @NonNull
    @Size(max = 255)
    @Column(length = 255)
    private Date envoyele;


    @ToString.Exclude
	@PositiveOrZero
	private int port = 0;

    @ToString.Exclude
	@PositiveOrZero
	private int remise = 0;

    @ToString.Exclude
	@PositiveOrZero
	private Date saisiele;

    @ToString.Exclude
	private Dispensaire dispensaire_code;

    @ToString.Exclude
	private String code_postal;

    @ToString.Exclude
	private String pays;


    @ToString.Exclude
	private String ville;


    @ToString.Exclude
	private String adresse;

    @ToString.Exclude
	private String region;

    @ToString.Exclude
	private String destinataire;

   


    
}
