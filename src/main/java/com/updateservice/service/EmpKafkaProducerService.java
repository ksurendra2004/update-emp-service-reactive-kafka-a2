package com.updateservice.service;

import com.updateservice.model.EmployeeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmpKafkaProducerService {

    @Value(value = "${EMP_UPDATES_TOPIC}")
    private String empTopic;
    @Value(value = "${DLQ_TOPIC}")
    private String dlqTopic;
    private final ReactiveKafkaProducerTemplate<String, EmployeeRequest> reactiveKafkaProducerTemplate;

    public void processAppUpdatesService(EmployeeRequest employeeRequest) {
        log.info("In processAppUpdatesService(-)");
        if(isFullOfNulls(employeeRequest)){
            log.info("Message sent to emp_DLQ Topic");
            sendMessages(employeeRequest, dlqTopic);
        } else {
            log.info("Message sent to emp_updates Topic");
            sendMessages(employeeRequest, empTopic);
        }
    }
    public void sendMessages(EmployeeRequest employeeRequest, String topic) {
        log.info("send to topic={}, {}={},", topic, EmployeeRequest.class.getSimpleName(), employeeRequest);
        reactiveKafkaProducerTemplate.send(topic, String.valueOf(employeeRequest.getEmployeeId()), employeeRequest)
                .doOnSuccess(senderResult -> log.info("sent {} offset : {}", employeeRequest,
                        senderResult.recordMetadata().offset()))
                .subscribe();
    }

    private boolean isFullOfNulls(EmployeeRequest request) {
        boolean empName = request.getEmployeeName() == null || request.getEmployeeName().isEmpty();
        boolean empCity = request.getEmployeeCity() == null || request.getEmployeeCity().isEmpty();
        boolean empPhone = request.getEmployeePhone() == null || request.getEmployeePhone().isEmpty();
        log.info("isFullOfNulls: {}", (empName || empCity || empPhone));

        return empName || empCity || empPhone;
    }

}
