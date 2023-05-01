package vsb.fei.voctionary.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vsb.fei.voctionary.model.enums.Language;
import vsb.fei.voctionary.model.enums.converters.LanguageConverter;

@Entity
@Table(name = "word")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {

	@Id
	@Column(name = "id", nullable = false, updatable = false)
	private String id;
	
	@Column(name = "language", nullable = false, updatable = false)
	@Convert(converter = LanguageConverter.class)
	private Language language;
	
	@Column(name = "part_of_speech", updatable = false)
	private String partOfSpeech;
	
	@Column(name = "created_at", insertable = false, updatable = false, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant createdAt;
	
    @OneToMany(mappedBy = "word", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE } )
	private List<Headword> headwords  = new ArrayList<>();
	
    @OneToMany(mappedBy = "word", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE } )
	private List<Sense> senses = new ArrayList<>();
    
    @ManyToMany(mappedBy = "words", fetch = FetchType.LAZY, cascade = { } )
    private List<ApiSearch> apiSearches = new ArrayList<>();
    
    @OneToMany(mappedBy = "word", fetch = FetchType.LAZY, cascade = { } )
    private List<WordListItem> WordListItems = new ArrayList<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "related_entry", joinColumns = @JoinColumn(name = "word_id", updatable = false))
    @Column(name = "related_entry", updatable = false)
    private List<String> relatedEntries = new ArrayList<>();
    
    @Transient
    private List<Word> relatedWords = new ArrayList<>();

}
