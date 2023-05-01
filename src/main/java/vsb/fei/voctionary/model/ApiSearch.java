package vsb.fei.voctionary.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vsb.fei.voctionary.model.enums.Language;
import vsb.fei.voctionary.model.enums.converters.LanguageConverter;

@Entity
@Table(name = "api_search")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiSearch {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private int id;
	
	@Column(name = "language", nullable = false, updatable = false)
	@Convert(converter = LanguageConverter.class)
	private Language language;
	
	@Column(name = "text", nullable = false, updatable = false)
	private String text;
	
	@Column(name = "created_at", insertable = false, updatable = false, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant createdAt; 
	
    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    @JoinTable(
    		name = "api_search_word",
    		joinColumns = @JoinColumn(name = "api_search_id", nullable = false, updatable = false),
    		inverseJoinColumns = @JoinColumn(name = "word_id", nullable = false, updatable = false)
    		)
    private List<Word> words = new ArrayList<>();


}
