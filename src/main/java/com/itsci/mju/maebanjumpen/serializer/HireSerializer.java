package com.itsci.mju.maebanjumpen.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.itsci.mju.maebanjumpen.model.*;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import org.hibernate.Hibernate;

public class HireSerializer extends JsonSerializer<Hire> {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_TIME;

    @Override
    public void serialize(Hire hire, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("hireId", hire.getHireId());
        jsonGenerator.writeStringField("hireName", hire.getHireName());
        jsonGenerator.writeStringField("hireDetail", hire.getHireDetail());
        jsonGenerator.writeNumberField("paymentAmount", hire.getPaymentAmount());
        if (hire.getHireDate() != null) {
            jsonGenerator.writeStringField("hireDate", hire.getHireDate().format(DATETIME_FORMATTER));
        } else {
            jsonGenerator.writeNullField("hireDate");
        }
        if (hire.getStartDate() != null) {
            jsonGenerator.writeStringField("startDate", hire.getStartDate().format(DATE_FORMATTER));
        } else {
            jsonGenerator.writeNullField("startDate");
        }
        if (hire.getStartTime() != null) {
            jsonGenerator.writeStringField("startTime", hire.getStartTime().format(TIME_FORMATTER));
        } else {
            jsonGenerator.writeNullField("startTime");
        }
        if (hire.getEndTime() != null) {
            jsonGenerator.writeStringField("endTime", hire.getEndTime().format(TIME_FORMATTER));
        } else {
            jsonGenerator.writeNullField("endTime");
        }
        jsonGenerator.writeStringField("location", hire.getLocation());
        jsonGenerator.writeStringField("jobStatus", hire.getJobStatus());

        // ** จุดที่แก้ไข: เพิ่มการตรวจสอบ Hibernate.isInitialized() เพื่อป้องกัน LazyInitializationException **
        if (hire.getProgressionImageUrls() != null && Hibernate.isInitialized(hire.getProgressionImageUrls())) {
            jsonGenerator.writeFieldName("progressionImageUrls");
            jsonGenerator.writeStartArray();
            for (String url : hire.getProgressionImageUrls()) {
                jsonGenerator.writeString(url);
            }
            jsonGenerator.writeEndArray();
        } else {
            // กรณีไม่มีรูปภาพ หรือ List ว่างเปล่า
            jsonGenerator.writeFieldName("progressionImageUrls");
            jsonGenerator.writeStartArray();
            jsonGenerator.writeEndArray();
        }

        // --- Serialize Hirer details ---
        Hirer hirer = hire.getHirer();
        if (hirer != null) {
            jsonGenerator.writeObjectFieldStart("hirer");
            jsonGenerator.writeNumberField("id", hirer.getId());
            jsonGenerator.writeNumberField("balance", hirer.getBalance());
            jsonGenerator.writeStringField("username", hirer.getUsername()); // Should be fine as Person/Login are EAGER

            if (hirer.getPerson() != null) {
                Person hirerPerson = hirer.getPerson();
                jsonGenerator.writeObjectFieldStart("person");
                jsonGenerator.writeNumberField("personId", hirerPerson.getPersonId());
                jsonGenerator.writeStringField("email", hirerPerson.getEmail());
                jsonGenerator.writeStringField("firstName", hirerPerson.getFirstName());
                jsonGenerator.writeStringField("lastName", hirerPerson.getLastName());
                jsonGenerator.writeStringField("phoneNumber", hirerPerson.getPhoneNumber());
                jsonGenerator.writeStringField("address", hirerPerson.getAddress());
                if (hirerPerson.getPictureUrl() != null) {
                    jsonGenerator.writeStringField("pictureUrl", hirerPerson.getPictureUrl());
                } else {
                    jsonGenerator.writeNullField("pictureUrl");
                }
                jsonGenerator.writeStringField("accountStatus", hirerPerson.getAccountStatus());
                if (hirerPerson.getLogin() != null) {
                    jsonGenerator.writeStringField("loginUsername", hirerPerson.getLogin().getUsername());
                }
                jsonGenerator.writeEndObject(); // end person
            }
            jsonGenerator.writeEndObject(); // end hirer
        } else {
            jsonGenerator.writeNullField("hirer");
        }

        // --- Serialize Housekeeper details ---
        Housekeeper housekeeper = hire.getHousekeeper();
        if (housekeeper != null) {
            jsonGenerator.writeObjectFieldStart("housekeeper");
            jsonGenerator.writeNumberField("id", housekeeper.getId());
            jsonGenerator.writeNumberField("balance", housekeeper.getBalance());
            jsonGenerator.writeStringField("username", housekeeper.getUsername()); // Should be fine as Person/Login are EAGER

            if (housekeeper.getPhotoVerifyUrl() != null) {
                jsonGenerator.writeStringField("photoVerifyUrl", housekeeper.getPhotoVerifyUrl());
            } else {
                jsonGenerator.writeNullField("photoVerifyUrl");
            }
            if (housekeeper.getStatusVerify() != null) {
                jsonGenerator.writeStringField("statusVerify", housekeeper.getStatusVerify());
            } else {
                jsonGenerator.writeNullField("statusVerify");
            }
            if (housekeeper.getRating() != null) {
                jsonGenerator.writeNumberField("rating", housekeeper.getRating());
            } else {
                jsonGenerator.writeNullField("rating");
            }
            if (housekeeper.getDailyRate() != null) {
                jsonGenerator.writeNumberField("dailyRate", housekeeper.getDailyRate());
            } else {
                jsonGenerator.writeNullField("dailyRate");
            }

            if (housekeeper.getPerson() != null) {
                Person housekeeperPerson = housekeeper.getPerson();
                jsonGenerator.writeObjectFieldStart("person");
                jsonGenerator.writeNumberField("personId", housekeeperPerson.getPersonId());
                jsonGenerator.writeStringField("email", housekeeperPerson.getEmail());
                jsonGenerator.writeStringField("firstName", housekeeperPerson.getFirstName());
                jsonGenerator.writeStringField("lastName", housekeeperPerson.getLastName());
                jsonGenerator.writeStringField("phoneNumber", housekeeperPerson.getPhoneNumber());
                jsonGenerator.writeStringField("address", housekeeperPerson.getAddress());
                if (housekeeperPerson.getPictureUrl() != null) {
                    jsonGenerator.writeStringField("pictureUrl", housekeeperPerson.getPictureUrl());
                } else {
                    jsonGenerator.writeNullField("pictureUrl");
                }
                jsonGenerator.writeStringField("accountStatus", housekeeperPerson.getAccountStatus());
                if (housekeeperPerson.getLogin() != null) {
                    jsonGenerator.writeStringField("loginUsername", housekeeperPerson.getLogin().getUsername());
                }
                jsonGenerator.writeEndObject(); // end person
            }

            // HousekeeperSkills collection: Check if initialized
            if (housekeeper.getHousekeeperSkills() != null && Hibernate.isInitialized(housekeeper.getHousekeeperSkills())) {
                Set<HousekeeperSkill> housekeeperSkills = housekeeper.getHousekeeperSkills();
                jsonGenerator.writeFieldName("housekeeperSkills");
                jsonGenerator.writeStartArray();
                for (HousekeeperSkill skill : housekeeperSkills) {
                    jsonGenerator.writeObject(skill); // Let Jackson/HousekeeperSkillSerializer handle it
                }
                jsonGenerator.writeEndArray();
            } else {
                jsonGenerator.writeFieldName("housekeeperSkills");
                jsonGenerator.writeStartArray(); // Serialize as empty array if not loaded
                jsonGenerator.writeEndArray();
            }
            jsonGenerator.writeEndObject(); // end housekeeper
        } else {
            jsonGenerator.writeNullField("housekeeper");
        }

        // --- Serialize Review details ---
        // IMPORTANT: Review is LAZY fetched in Hire, so check for initialization
        if (hire.getReview() != null && Hibernate.isInitialized(hire.getReview())) { // <-- Add initialization check here
            jsonGenerator.writeFieldName("review");
            serializerProvider.defaultSerializeValue(hire.getReview(), jsonGenerator);
        } else {
            jsonGenerator.writeNullField("review");
        }

        jsonGenerator.writeEndObject();
    }
}
