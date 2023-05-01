package vsb.fei.voctionary.model;

import java.time.Instant;
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
@Table(name = "word_list_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordListItem {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "created_at", insertable = false, updatable = false, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant createdAt;
	
	@Column(name = "last_listed_at", insertable = false, updatable = true, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant lastListedAt;
	
	@Column(name = "is_listed", nullable = false)
	private Boolean listed = true;
	
	@Column(name = "is_learned", nullable = false)
	private Boolean learned = false;
	
	@Column(name = "last_tested_at")
	private Instant lastTestedAt;
	
	@Column(name = "all_tests", nullable = false)
	private Integer allTests = 0;
	
	@Column(name = "successful_tests", nullable = false)
	private Integer successfulTests = 0;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "word_id", nullable = false, updatable = false)
	private Word word;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
    @JoinColumn(name = "word_list_id", nullable = false, updatable = false)
	private WordList wordList;
	
	@OneToMany(mappedBy = "wordListItem", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	private List<WordTest> wordTests = new ArrayList<>();
	
	public WordListItem(Word word, WordList wordList) {
		this.word = word;
		this.wordList = wordList;
	}

}
