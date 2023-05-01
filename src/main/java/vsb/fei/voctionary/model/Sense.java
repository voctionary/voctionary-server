package vsb.fei.voctionary.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sense")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sense {
	
	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private String id;
	
	@Column(name = "definition", updatable = false)
	private String definition;
	
    @ElementCollection
    @CollectionTable(name = "english_translation_word", joinColumns = @JoinColumn(name = "id", updatable = false))
    @Column(name = "english_translation_word", updatable = false)
	private List<String> englishTranslationWords = new ArrayList<>();
	
	@Column(name = "english_translation_definition", updatable = false)
	private String englishTranslationDefinition;

	@Column(name = "register", updatable = false)
	private String register;

	@Column(name = "subcategorization", updatable = false)
	private String subcategorization;
	
	@Column(name = "semantic_category", updatable = false)
	private String semanticCategory;
	
	@Column(name = "subcategory", updatable = false)
	private String subcategory;
	
	@Column(name = "see", updatable = false)
	private String see;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false, updatable = false)
    private Word word;
	
	@OneToMany(mappedBy = "sense", fetch = FetchType.LAZY,  cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private List<Example> examples = new ArrayList<>();
	
	@OneToMany(mappedBy = "sense", fetch = FetchType.LAZY,  cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private List<Phrase> phrases = new ArrayList<>();

}
