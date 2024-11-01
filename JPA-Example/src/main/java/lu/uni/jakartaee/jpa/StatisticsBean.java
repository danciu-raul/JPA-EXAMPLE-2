package lu.uni.jakartaee.jpa;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.inject.Inject;
import java.io.Serializable;
import java.util.List;

@Named("statistics")
@SessionScoped
public class StatisticsBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private int topCount = 10;
    private List<SpellStatistics> topErrors;

    @Inject
    private SpellCheckService spellCheckService;

    public void loadStatistics() {
        topErrors = spellCheckService.getTopErrors(topCount);
    }

    public int getTopCount() {
        return topCount;
    }

    public void setTopCount(int topCount) {
        this.topCount = topCount;
    }

    public List<SpellStatistics> getTopErrors() {
        return topErrors;
    }

    public void setTopErrors(List<SpellStatistics> topErrors) {
        this.topErrors = topErrors;
    }
}