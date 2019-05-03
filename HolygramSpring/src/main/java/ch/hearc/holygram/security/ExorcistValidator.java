package ch.hearc.holygram.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import ch.hearc.holygram.forms.UserForm;
import ch.hearc.holygram.models.Canton;
import ch.hearc.holygram.models.User;
import ch.hearc.holygram.repositories.CantonRepository;
import ch.hearc.holygram.repositories.ExorcistRepository;
import ch.hearc.holygram.services.UserService;

@Component
public class ExorcistValidator implements Validator {
    //@Autowired
    //private ExorcistRepository exorcistService;
	
	@Autowired
	private CantonRepository cantonRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
    	UserForm userForm = (UserForm) o;
        
        
        System.out.println("");
        System.out.println("acronym");
        System.out.println(userForm.getCantonAcronym());
        System.out.println("");

        if (cantonRepository.findByAcronym(userForm.getCantonAcronym()) == null) {
            errors.rejectValue("cantonAcronym", "Size.userForm.cantonAcronym", "Choose a canton");
        }
        
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "phoneNumber", "NotEmpty", "The phone number can't be empty");

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "description", "NotEmpty", "The description can't be empty");
    }
}