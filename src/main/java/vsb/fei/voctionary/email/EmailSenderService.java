package vsb.fei.voctionary.email;

import vsb.fei.voctionary.email.EmailSenderServiceImpl.EmailType;
import vsb.fei.voctionary.model.User;

public interface EmailSenderService {
	
	void send(EmailType emailType, User user, String confirmationLink);
	
}
