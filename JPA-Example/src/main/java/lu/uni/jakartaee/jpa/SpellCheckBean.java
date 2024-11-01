package lu.uni.jakartaee.jpa;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Named("spellCheck")
@SessionScoped
public class SpellCheckBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private String inputText;
    private List<SpellError> errors;

    @Inject
    private SpellCheckService spellCheckService;

    public void checkSpelling() {
        try {
            errors = spellCheckService.checkText(inputText);
        } catch (IOException e) {
            // Handle error
        }
    }

    public String getInputText() {
        return inputText;
    }

    public void setInputText(String inputText) {
        this.inputText = inputText;
    }

    public List<SpellError> getErrors() {
        return errors;
    }

    public void setErrors(List<SpellError> errors) {
        this.errors = errors;
    }
}