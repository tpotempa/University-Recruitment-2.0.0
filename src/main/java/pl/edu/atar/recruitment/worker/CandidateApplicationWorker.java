package pl.edu.atar.recruitment.worker;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.edu.atar.recruitment.service.DatabaseService;
import pl.edu.atar.recruitment.model.CandidateApplication;

@Component
public class CandidateApplicationWorker {

    private static Logger LOGGER = LoggerFactory.getLogger(CandidateApplicationWorker.class);

    @Value("${database.postgresql.dburl:N/A}")
    private String dbUrl;

    @Value("${database.postgresql.dbuser:N/A}")
    private String dbUser;

    @Value("${database.postgresql.dbpassword:N/A}")
    private String dbPassword;

    @JobWorker(type = "registerApplication")
    public Map<String, Object> registerApplication(final JobClient client, final ActivatedJob job) {
        HashMap<String, Object> jobResultVariables = new HashMap<>();

        LOGGER.info("Job registerApplication is started.");

        final Map<String, Object> jobVariables = job.getVariablesAsMap();
        for (Map.Entry<String, Object> entry : jobVariables.entrySet()) {
            LOGGER.info("Job variable (process variable & inputted variable): {} : {}", entry.getKey(), entry.getValue());
        }

        int points = 0;
        try {
            points = Integer.parseInt((String) job.getVariablesAsMap().get("points"));
            LOGGER.info("Points. {}", points, "points.");
        } catch (NumberFormatException e) {
            LOGGER.info("Cannot convert String to int. {}", e);
        }

        CandidateApplication candidateApplication = new CandidateApplication((String) job.getVariablesAsMap().get("firstName"), (String) job.getVariablesAsMap().get("lastName"), (String) job.getVariablesAsMap().get("email"), points, (String) job.getVariablesAsMap().get("faculty"), (boolean) job.getVariablesAsMap().get("olympic"));

        int applicationId = DatabaseService.addApplication(candidateApplication, dbUrl, dbUser, dbPassword);
        if (applicationId > 0) {
            candidateApplication.setApplicationId(applicationId);
            LOGGER.info("Application registered. Application ID: {}", applicationId);
            jobResultVariables.put("applicationRegistered", true);
        } else {
            LOGGER.info("Application not registered.");
            jobResultVariables.put("applicationRegistered", false);
        }
        jobResultVariables.put("candidateApplication", candidateApplication);
        jobResultVariables.put("applicationId", applicationId);

        return jobResultVariables;
    }

    @JobWorker(type = "registerDecision")
    public Map<String, Object> registerDecision(final JobClient client, final ActivatedJob job) {
        HashMap<String, Object> jobResultVariables = new HashMap<>();

        LOGGER.info("Job registerDecision is started.");
        final Map<String, Object> jobVariables = job.getVariablesAsMap();
        for (Map.Entry<String, Object> entry : jobVariables.entrySet()) {
            LOGGER.info("Job variable (process variable & inputted variable): {} : {}", entry.getKey(), entry.getValue());
        }

        String decision = (String) job.getVariablesAsMap().get("decision");
        int applicationId = (Integer) job.getVariablesAsMap().get("applicationId");

        int countUpdatedRows = DatabaseService.updateApplicationDecision(applicationId, decision, dbUrl, dbUser, dbPassword);
        if (countUpdatedRows > 0) {
            LOGGER.info("Decision registered. Application ID: {}", applicationId + " / " + "Decision: " + decision);
            jobResultVariables.put("decisionRegistered", true);
        } else {
            LOGGER.info("Decision not registered.");
            jobResultVariables.put("decisionRegistered", false);
        }

        return jobResultVariables;
    }
}