package pl.edu.atar.recruitment.worker;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.edu.atar.recruitment.model.Mail;
import java.util.HashMap;
import java.util.Map;

@Component
public class MailXWorker {

    private static Logger LOGGER = LoggerFactory.getLogger(MailXWorker.class);

    @Value("${mail.atar.edu.pl.mailsmtphost:N/A}")
    private String mailSmtpHost;

    @Value("${mail.atar.edu.pl.mailuser:N/A}")
    private String mailUser;

    @Value("${mail.atar.edu.pl.mailpassword:N/A}")
    private String mailPassword;

    @Value("${mail.atar.edu.pl.mailsendermail:N/A}")
    private String mailSenderMail;

    @Value("${mail.atar.edu.pl.mailsendername:N/A}")
    private String mailSenderName;

    @JobWorker(type = "sendEmailX")
    public Map<String, Object> sendEmailX(final JobClient client, final ActivatedJob job) {
        HashMap<String, Object> jobResultVariables = new HashMap<>();

        LOGGER.info("Job sendEmail is started.");
        final Map<String, Object> jobVariables = job.getVariablesAsMap();
        for (Map.Entry<String, Object> entry : jobVariables.entrySet()) {
            LOGGER.info("Job variables (process & task input): {}", entry.getKey() + " : " + entry.getValue());
        }

        Mail mt = new Mail(0, (String) job.getVariablesAsMap().get("firstName") + " " + (String) job.getVariablesAsMap().get("lastName"), (String) job.getVariablesAsMap().get("email"), null, (Integer) job.getVariablesAsMap().get("applicationId"));
        try {
            mt.sendMail(mailSmtpHost, mailUser, mailPassword, mailSenderMail, mailSenderName);
            LOGGER.info("Sending mail succeeded.");
            jobResultVariables.put("mailSendingResult", true);
        } catch (Exception e) {
            LOGGER.error("Sending mail failed.");
            jobResultVariables.put("mailSendingResult", false);
        }

        return jobResultVariables;
    }
}