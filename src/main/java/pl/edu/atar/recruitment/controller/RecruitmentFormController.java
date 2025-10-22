package pl.edu.atar.recruitment.controller;

import io.camunda.client.CamundaClient;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.atar.recruitment.ProcessConstants;
import pl.edu.atar.recruitment.service.TaskListService;

@RestController
@RequestMapping("/")
public class RecruitmentFormController {

    private static final Logger LOG = LoggerFactory.getLogger(RecruitmentFormController.class);

    @Autowired
    private TaskListService taskListService;
    @Autowired
    private CamundaClient client;

    @PostMapping("/start")
    public void startProcessInstance(@RequestBody Map<String, Object> variables) {

        LOG.info("Starting process " + ProcessConstants.BPMN_PROCESS_RECRUITMENT_PROCESS_ID + " with variables: {}", variables);

        variables.put("applicationReceived", true);
        client
                .newCreateInstanceCommand()
                .bpmnProcessId(ProcessConstants.BPMN_PROCESS_RECRUITMENT_PROCESS_ID)
                .latestVersion()
                .variables(variables)
                .send();

        LOG.info("Process " + ProcessConstants.BPMN_PROCESS_RECRUITMENT_PROCESS_ID + " started with variables: {}", variables);
    }
}