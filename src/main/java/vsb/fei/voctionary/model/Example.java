package vsb.fei.voctionary.model;

import javax.persistence.Column;
import javax.persistence.Entity;
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

@Entity
@Table(name = "example")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Example {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "text", nullable = false, updatable = false)
	private String text;
	
	@Column(name = "english_translation", updatable = false)
	private String englishTranslation;
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sense_id", updatable = false)
    private Sense sense;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phrase_id", updatable = false)
    private Phrase phrase;

}
