package vsb.fei.voctionary.model;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vsb.fei.voctionary.model.enums.TestType;

@Entity
@Table(name = "word_test")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WordTest {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "created_at", insertable = false, updatable = false, nullable = false,
			columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Instant createdAt;
	
	@Column(name = "finished_at", insertable = false)
	private Instant finishedAt;
	
	@Column(name = "type", nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
	private TestType type;
	
	@Column(name = "success", updatable = false)
	private Boolean success = false;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_list_one_id", nullable = false, updatable = false)
	private WordListItem wordListItem;
	
	@Column(name = "response_1", nullable = false, updatable = false)
	private String responseOne;
	
	@Column(name = "response_2", updatable = false)
	private String responseTwo;
	
	@Column(name = "response_3", updatable = false)
	private String responseThree;
	
	public WordTest(WordListItem wordListItem, TestType type, String responseOne, String responseTwo, String responseThree){
		this.wordListItem = wordListItem;
		this.type = type;
		this.responseOne = responseOne;
		this.responseTwo = responseTwo;
		this.responseThree = responseThree;
	}
	
}
