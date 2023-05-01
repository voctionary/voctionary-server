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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vsb.fei.voctionary.model.enums.Language;
import vsb.fei.voctionary.model.enums.converters.LanguageConverter;

@Entity
@Table(name = "word_list")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordList {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "count", nullable = false)
    private Integer count = 0;
	
	@Column(name = "updated_at", insertable = false, updatable = false, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
	private Instant updatedAt;
	
	@Column(name = "created_at", insertable = false, updatable = false, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant createdAt;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;
    
	@OneToMany(mappedBy = "wordList", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    private List<WordListItem> wordListItems = new ArrayList<>();
    
	@Column(name = "language", nullable = false, updatable = false)
	@Convert(converter = LanguageConverter.class)
    private Language language;

	public WordList(User user, Language language) {
		this.user = user;
		this.language = language;
	}
	
}
