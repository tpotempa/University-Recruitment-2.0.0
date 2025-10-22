package pl.edu.atar.recruitment.worker;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pl.edu.atar.recruitment.service.PayerService;

@Component
public class CreditDeductionWorker {

    Logger LOGGER = LoggerFactory.getLogger(CreditDeductionWorker.class);

    @JobWorker(type = "credit-deduction", autoComplete = false)
    public void handleCreditDeduction(final JobClient jobClient, final ActivatedJob job) {
        LOGGER.info("Task/Job definition type: {}", job.getType());
        final Map<String, Object> jobVariables = job.getVariablesAsMap();
        for (Map.Entry<String, Object> entry : jobVariables.entrySet()) {
            LOGGER.info("Process variables & task/job variables (e.g. data submitted by user): {} : {}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> variables = job.getVariablesAsMap();
        String paymentId = variables.get("paymentId").toString();
        Double recruitmentFee = Double.valueOf(variables.get("recruitmentFee").toString());

        PayerService creditService = new PayerService();
        double payerCredit = creditService.getPayerCredit();
        double openAmount = creditService.deductCredit(payerCredit, recruitmentFee);

        variables.put("paymentId", paymentId);
        variables.put("cardNumber", "4005 5500 0000 0019");
        variables.put("cvc", "111");
        variables.put("expiryDate", "04/2026");

        variables.put("payerCredit", payerCredit);
        variables.put("openAmount", openAmount);

        jobClient.newCompleteCommand(job).variables(variables).send().join();
    }
}