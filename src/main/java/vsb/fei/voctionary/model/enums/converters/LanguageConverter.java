package vsb.fei.voctionary.model.enums.converters;

import javax.persistence.AttributeConverter;

import vsb.fei.voctionary.model.enums.Language;

public class LanguageConverter implements AttributeConverter<Language, String> {

	@Override
	public String convertToDatabaseColumn(Language attribute) {
		return attribute.getKey();
	}

	@Override
	public Language convertToEntityAttribute(String dbData) {
		return Language.findByKey(dbData);
	}

}
