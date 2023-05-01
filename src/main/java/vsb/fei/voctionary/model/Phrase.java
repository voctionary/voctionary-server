package vsb.fei.voctionary.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "phrase")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Phrase {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "text", nullable = false, updatable = false)
	private String text;
	
	@Column(name = "definition", updatable = false)
	private String definition;
	
	@Column(name = "part_of_speech", updatable = false)
	private String partOfSpeech;

	@Column(name = "english_translation_text", updatable = false)
	private String englishTranslationText;
	
	@Column(name = "english_translation_definition", updatable = false)
	private String englishTranslationDefinition;
	
	@Column(name = "register", updatable = false)
	private String register;
	
	@Column(name = "semantic_category", updatable = false)
	private String semanticCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_id", nullable = false, updatable = false)
    private Sense sense;
	
    @OneToMany(mappedBy = "phrase", fetch = FetchType.LAZY,  cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private List<Example> examples = new ArrayList<>();

}
