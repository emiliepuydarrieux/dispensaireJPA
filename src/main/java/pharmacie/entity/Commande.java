package pharmacie.entity;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString

public class Commande {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Setter(AccessLevel.NONE) // la clé est autogénérée par la BD, On ne veut pas de "setter"
	private Integer numero = null;

    @NonNull
    @NotNull
    @Column(name = "SAISIELE")
    private Date saisiele;

    @Column(name = "ENVOYELE")
    private Date envoyele;

    @PositiveOrZero
	private int port = 0;

    @PositiveOrZero
	private int remise = 0;

    @ManyToOne
	private Dispensaire dispensaire;

	@NotBlank
	@Size(max = 255)
	@Column(length = 255)
	private String code_postal;

	@NotBlank
	@Size(max = 100)
	@Column(length = 100)
	private String pays;

	@NotBlank
	@Size(max = 100)
	@Column(length = 100)
	private String ville;

	@NotBlank
	@Size(max = 255)
	@Column(length = 255)
	private String adresse;

	@NotBlank
	@Size(max = 100)
	@Column(length = 100)
	private String region;

	@NotBlank
	@Size(max = 255)
	@Column(length = 255)
	private String destinataire;

    @ToString.Exclude
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "commande")
    private List<Ligne> lignes = new LinkedList<>();
}

