package vsb.fei.voctionary.externalApi.service;

import java.io.IOException;
import java.util.List;

import vsb.fei.voctionary.model.Word;
import vsb.fei.voctionary.model.enums.Language;

public interface ExternalApiService {
	
	/** Function to get specific word by its ID from external API Lexicala
	 * 
	 * @param wordId
	 * @return word
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Word getWord(String wordId) throws IOException, InterruptedException;
	
	/** Function to find words from external API Lexicala
	 * 
	 * @param language
	 * @param text
	 * @return list of words
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<Word> findWord(Language language, String text) throws IOException, InterruptedException;

	/** Function to get random words from external API Lexicala
	 * 
	 * @param language
	 * @param number of random words
	 * @return list of random words
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<Word> getRandomWords(Language language, int number) throws IOException, InterruptedException;
	
	/** Function to get random word from external API Lexicala
	 * 
	 * @param language
	 * @param number of random words
	 * @return random word
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Word getRandomWord(Language language) throws IOException, InterruptedException;
	
	/** Function to translate text from the specific language to the other language
	 * 
	 * @param from language
	 * @param to language
	 * @param text to translate
	 * @return translated text
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public String translate(Language from, Language to, String text) throws IOException, InterruptedException;
	
	/** Function to translate collection of texts from the specific language to the other language
	 * 
	 * @param from language
	 * @param to language
	 * @param texts to translate
	 * @return translated texts
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public List<String> translate(Language from, Language to, List<String> texts) throws IOException, InterruptedException;
	
	/** Function to translate all necessary Word attributes, because some attributes provided by the External API are not in the desired English language (for example Italian word has only examples in Italian language)
	 * 
	 * @param word whose attributes will be update
	 * @return word with translated attributed
	 */
	public Word translateWordValues(Word word) throws IOException, InterruptedException;
	
}