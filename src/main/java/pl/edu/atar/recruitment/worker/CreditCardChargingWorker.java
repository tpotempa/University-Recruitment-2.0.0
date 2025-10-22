package pl.edu.atar.recruitment.worker;

import io.camunda.client.CamundaClient;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.edu.atar.recruitment.exception.InvalidCreditCardException;
import pl.edu.atar.recruitment.service.CreditCardService;

@Component
public class CreditCardChargingWorker {

    Logger LOGGER = LoggerFactory.getLogger(CreditCardChargingWorker.class);

    private CamundaClient client;

    @JobWorker(type = "credit-card-charging", autoComplete = false)
    public void handleCreditCardCharging(final JobClient jobClient, final ActivatedJob job) {
        LOGGER.info("Task/Job definition type: {}", job.getType());
        final Map<String, Object> jobVariables = job.getVariablesAsMap();
        for (Map.Entry<String, Object> entry : jobVariables.entrySet()) {
            LOGGER.info("Process variables & task/job variables (e.g. data submitted by user): {} : {}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> variables = job.getVariablesAsMap();
        String cardNumber = variables.get("cardNumber").toString();
        String cvc = variables.get("cvc").toString();
        String expiryDate = variables.get("expiryDate").toString();
        Double amount = Double.valueOf(variables.get("openAmount").toString());

        try {
            new CreditCardService().chargeAmount(cardNumber, cvc, expiryDate, amount);

            jobClient.newCompleteCommand(job).send().join();
        } catch (InvalidCreditCardException e) {
            client.newThrowErrorCommand(job).errorCode("creditCardChargeError").send().join();
        } catch (Exception e) {
            jobClient.newFailCommand(job).retries(3).errorMessage(e.getMessage()).send().join();
        }
    }
}
