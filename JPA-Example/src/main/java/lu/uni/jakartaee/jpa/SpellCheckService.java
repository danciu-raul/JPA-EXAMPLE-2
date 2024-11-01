package lu.uni.jakartaee.jpa;

import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.languagetool.JLanguageTool;
import org.languagetool.language.BritishEnglish;
import org.languagetool.rules.RuleMatch;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Stateless
public class SpellCheckService {

    @PersistenceContext(unitName = "SpellCheck-Example")
    private EntityManager em;

    private final JLanguageTool langTool;

    public SpellCheckService() {
        this.langTool = new JLanguageTool(new BritishEnglish());
    }

    public List<SpellError> checkText(String text) throws IOException {
        List<RuleMatch> matches = langTool.check(text);
        List<SpellError> errors = new ArrayList<>();

        for (RuleMatch match : matches) {
            SpellError error = new SpellError();
            error.setWrongWord(text.substring(match.getFromPos(), match.getToPos()));
            error.setCorrectWord(match.getSuggestedReplacements().isEmpty() ? "" : match.getSuggestedReplacements().get(0));
            error.setContext(match.getContext().toString());
            error.setTimestamp(LocalDateTime.now());

            em.persist(error);
            errors.add(error);
            updateStatistics(error);
        }

        return errors;
    }

    private void updateStatistics(SpellError error) {
        SpellStatistics stats = em.createQuery(
                        "SELECT s FROM SpellStatistics s WHERE s.wrongWord = :wrongWord AND s.correctWord = :correctWord",
                        SpellStatistics.class)
                .setParameter("wrongWord", error.getWrongWord())
                .setParameter("correctWord", error.getCorrectWord())
                .getResultStream().findFirst().orElse(null);

        if (stats == null) {
            stats = new SpellStatistics();
            stats.setWrongWord(error.getWrongWord());
            stats.setCorrectWord(error.getCorrectWord());
            stats.setOccurrenceCount(1);
            em.persist(stats);
        } else {
            stats.setOccurrenceCount(stats.getOccurrenceCount() + 1);
            em.merge(stats);
        }
    }

    public List<SpellStatistics> getTopErrors(int limit) {
        return em.createQuery(
                        "SELECT s FROM SpellStatistics s ORDER BY s.occurrenceCount DESC",
                        SpellStatistics.class)
                .setMaxResults(limit)
                .getResultList();
    }
}