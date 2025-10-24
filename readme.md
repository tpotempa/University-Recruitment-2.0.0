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
* Create a Camunda 8 SaaS cluster.
* Set API client and DB connection details in the file `application.properties`.
* Set process id and jobs type in the class ProcessConstants.
* Log in Camunda account.
* With Camunda Modeler change id of the processes and messages name according to names in class ProcessConstants:
  * In the file recruitment.bpmn (proces[CandidateApplicationWorker.java](src/main/java/pl/edu/atar/recruitment/worker/CandidateApplicationWorker.java)s id);
  * In the file calculate-pay-recruitment-fee (process id) & in message intermediate event "Payment completed" (message name);
  * In the file payment (process id) & in message start event "Paymen required" (message name).
* Run the application:

```
mvn package exec:java
```

* Open http://localhost:8080/