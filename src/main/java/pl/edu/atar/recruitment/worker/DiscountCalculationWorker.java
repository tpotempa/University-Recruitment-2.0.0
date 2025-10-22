package pl.edu.atar.recruitment.worker;

import io.camunda.client.CamundaClient;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.edu.atar.recruitment.service.DiscountService;

@Component
public class DiscountCalculationWorker {

    Logger LOGGER = LoggerFactory.getLogger(DiscountCalculationWorker.class);

    @Autowired
    private CamundaClient client;

    @JobWorker(type = "discount-application", autoComplete = false)
    public void handleDiscountCalculation(final JobClient jobClient, final ActivatedJob job) {
        LOGGER.info("Task/Job definition type: {}", job.getType());
        final Map<String, Object> jobVariables = job.getVariablesAsMap();
        for (Map.Entry<String, Object> entry : jobVariables.entrySet()) {
            LOGGER.info("Process variables & task/job variables (e.g. data submitted by user): {} : {}", entry.getKey(), entry.getValue());
        }

        Map<String, Object> variables = job.getVariablesAsMap();
        Double recruitmentFee = Double.valueOf(variables.get("recruitmentFee").toString());

        Double discount = 0.00;
        Boolean olympic = Boolean.valueOf(variables.get("olympic").toString());
        if (olympic == Boolean.TRUE) {
            discount = 0.5;
        }
        LOGGER.info("Discount: {}", discount);

        double discountedRecruitmentFee = new DiscountService().getDiscountedOrderTotal(recruitmentFee, discount);
        variables.put("discountedRecruitmentFee", discountedRecruitmentFee);

        jobClient.newCompleteCommand(job).variables(variables).send().join();
    }
}