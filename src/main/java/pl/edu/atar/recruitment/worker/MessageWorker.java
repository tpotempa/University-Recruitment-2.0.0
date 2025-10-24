package pl.edu.atar.recruitment.worker;

import io.camunda.client.CamundaClient;
import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import pl.edu.atar.recruitment.ProcessConstants;
import pl.edu.atar.recruitment.service.RandomService;

@Component
public class MessageWorker {

    Logger LOGGER = LoggerFactory.getLogger(MessageWorker.class);

    @Autowired
    private CamundaClient client;

    //@JobWorker(type = ProcessConstants.BPMN_PROCESS_PAYMENT_JOB_TYPE_INVOCATION, autoComplete = false)
    @JobWorker(type = "payment-invocation", autoComplete = false)
    public void handlePaymentInvocation(final JobClient jobClient, final ActivatedJob job) {
        LOGGER.info("Task/Job definition type: {}", job.getType());
        final Map<String, Object> variables = job.getVariablesAsMap();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            LOGGER.info("Process variables & task/job variables (e.g. data submitted by user): {} : {}", entry.getKey(), entry.getValue());
        }

        RandomService rs = new RandomService();
        String paymentId = rs.generateRandom(40);
        variables.put("paymentId", paymentId);
        variables.put("recruitmentFee", variables.get("recruitmentFee").toString());

        String messageName = ProcessConstants.BPMN_PROCESS_PAYMENT_MESSAGE_INVOCATION;
        client.newPublishMessageCommand().messageName(messageName).correlationKey(paymentId).variables(variables).send().join();

        LOGGER.info("Message name (sent): {}", messageName);
        LOGGER.info("Correlation key (created and sent): {}", paymentId);
        LOGGER.info("Then process instance goes into subscription mode with use of catching message event.");

        jobClient.newCompleteCommand(job).variables(variables).send().join();
    }

    //@JobWorker(type = ProcessConstants.BPMN_PROCESS_PAYMENT_JOB_TYPE_COMPLETION, autoComplete = false)
    @JobWorker(type = "payment-completion", autoComplete = false)
    public void handlePaymentCompletion(final JobClient jobClient, final ActivatedJob job) {
        LOGGER.info("Task/Job definition type: {}", job.getType());
        final Map<String, Object> variables = job.getVariablesAsMap();
        LOGGER.info("Correlation key (sent): {}", variables.get("paymentId").toString());
        LOGGER.info("Broker seeks form process instance in subscription mode with the correlation key.");
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            LOGGER.info("Process variables & task/job variables (e.g. data submitted by user): {} : {}", entry.getKey(), entry.getValue());
        }

        String messageName = ProcessConstants.BPMN_PROCESS_PAYMENT_MESSAGE_COMPLETION;
        client.newPublishMessageCommand().messageName(messageName).correlationKey(variables.get("paymentId").toString()).send().join();

        jobClient.newCompleteCommand(job).send().join();
    }
}