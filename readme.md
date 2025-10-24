Components:
* [Camunda form](https://docs.camunda.io/docs/components/modeler/forms/camunda-forms-reference/)
* HTML website with using [form-js](https://bpmn.io/toolkit/form-js/) to render the form and submit on request
* REST endpoint to take the data from the form and start a process instance

Requirements:
* Camunda Platform 8.8+
* Java >= 17
* Maven

Run:
* Download/clone the code.
* Log in Camunda account.
* Create a Camunda 8 SaaS cluster (if the cluster doesn't exist).
* Create new client API.
* Set API client credentials and DB connection details in the file `application.properties`.
* Set process id and message names in the class `ProcessConstants`.
* Using Camunda Modeler:
  * Create new project/folder in the Camunda workspace;
  * Upload BPMN files/models from local project `resources/model` into project/folder created in the Camunda workspace.
* With Camunda Modeler change id of the processes and names of the messages according to names in class ProcessConstants:
  * In the file recruitment.bpmn (process id);
  * In the file calculate-pay-recruitment-fee (process id) & in message intermediate event "Payment completed" (message name);
  * In the file payment (process id) & in message start event "Payment required" (message name).
* Download changed BPMN files/models into local project `resources/model`.
* Run the application:

```
mvn package exec:java
```

* Open http://localhost:8080/