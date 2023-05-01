package vsb.fei.voctionary.model.enums.converters;

import javax.persistence.AttributeConverter;

import vsb.fei.voctionary.model.enums.TokenType;

public class TokenTypeConverter implements AttributeConverter<TokenType, String> {

	@Override
	public String convertToDatabaseColumn(TokenType attribute) {
		return attribute.getName();
	}

	@Override
	public TokenType convertToEntityAttribute(String dbData) {
		return TokenType.find(dbData);
	}

}
