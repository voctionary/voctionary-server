package vsb.fei.voctionary.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
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
@Table(name = "headword")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Headword {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Long id;
	
	@Column(name = "text", nullable = false, updatable = false)
	private String text;
	
	@Column(name = "part_of_speech", updatable = false)
	private String partOfSpeech;
	
	@Column(name = "gender", updatable = false)
	private String gender;
	
	@Column(name = "number", updatable = false)
	private String number;
	
	@Column(name = "subcategorization", updatable = false)
	private String subcategorization;
	
	@Column(name = "subcategory", updatable = false)
	private String subcategory;
	
	@Column(name = "register", updatable = false)
	private String register;
    
    @ElementCollection
    @CollectionTable(name = "pronunciation", joinColumns = @JoinColumn(name = "headword_id", updatable = false))
    @Column(name = "pronunciation", updatable = false)
    private List<String> pronunciations = new ArrayList<>();
	
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false, updatable = false)
    private Word word;
    
}
