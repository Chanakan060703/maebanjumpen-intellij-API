// File: src/main/java/com/itsci/mju/maebanjumpen/serializer/ReportSerializer.java
package com.itsci.mju.maebanjumpen.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.itsci.mju.maebanjumpen.model.*;
import org.hibernate.Hibernate;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

// ต้องลงทะเบียน Custom Serializer นี้ด้วย
// วิธีหนึ่งคือการเพิ่ม @JsonSerialize(using = ReportSerializer.class) บน Report class
// หรือลงทะเบียนใน ObjectMapper bean configuration
public class ReportSerializer extends StdSerializer<Report> {

    public ReportSerializer() {
        super(Report.class);
    }

    @Override
    public void serialize(Report report, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();

        gen.writeNumberField("reportId", report.getReportId());
        gen.writeStringField("reportTitle", report.getReportTitle());
        gen.writeStringField("reportMessage", report.getReportMessage());
        if (report.getReportDate() != null) {
            gen.writeStringField("reportDate", report.getReportDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        } else {
            gen.writeNullField("reportDate");
        }
        gen.writeStringField("reportStatus", report.getReportStatus());

        // --- Serialize Reporter ---
        if (report.getReporter() != null) {
            gen.writeFieldName("reporter");
            gen.writeStartObject();
            PartyRole reporter = report.getReporter();
            Hibernate.initialize(reporter);

            // ใช้ type จาก PartyRole object โดยตรงถ้ามี (และถ้ามันเก็บข้อมูลที่ถูกต้อง)
            // หรือใช้ instanceOf เหมือนเดิม
            gen.writeStringField("type", getTypeString(reporter)); // Helper method
            gen.writeNumberField("id", reporter.getId());

            if (reporter.getPerson() != null) {
                Hibernate.initialize(reporter.getPerson());
                gen.writeObjectFieldStart("person");
                gen.writeNumberField("personId", reporter.getPerson().getPersonId());
                gen.writeStringField("firstName", reporter.getPerson().getFirstName());
                gen.writeStringField("lastName", reporter.getPerson().getLastName());
                gen.writeStringField("email", reporter.getPerson().getEmail());
                gen.writeStringField("phoneNumber", reporter.getPerson().getPhoneNumber());
                gen.writeStringField("address", reporter.getPerson().getAddress());
                gen.writeStringField("idCardNumber", reporter.getPerson().getIdCardNumber());
                if (reporter.getPerson().getPictureUrl() != null) {
                    gen.writeStringField("pictureUrl", reporter.getPerson().getPictureUrl());
                }
                gen.writeStringField("accountStatus", reporter.getPerson().getAccountStatus());
                if (reporter.getPerson().getLogin() != null) {
                    gen.writeStringField("username", reporter.getPerson().getLogin().getUsername());
                } else {
                    gen.writeNullField("username"); // เพิ่ม null field ถ้าไม่มี username
                }
                gen.writeEndObject(); // end person
            } else {
                gen.writeNullField("person"); // เพิ่ม null field ถ้าไม่มี person
            }


            if (reporter instanceof Hirer) {
                Hirer hirerReporter = (Hirer) reporter;
                gen.writeNumberField("balance", hirerReporter.getBalance());
            } else if (reporter instanceof Housekeeper) {
                Housekeeper housekeeperReporter = (Housekeeper) reporter;
                gen.writeNumberField("balance", housekeeperReporter.getBalance());
                if (housekeeperReporter.getPhotoVerifyUrl() != null) {
                    gen.writeStringField("photoVerifyUrl", housekeeperReporter.getPhotoVerifyUrl());
                } else {
                    gen.writeNullField("photoVerifyUrl");
                }
                if (housekeeperReporter.getStatusVerify() != null) {
                    gen.writeStringField("statusVerify", housekeeperReporter.getStatusVerify());
                } else {
                    gen.writeNullField("statusVerify");
                }
                if (housekeeperReporter.getRating() != null) {
                    gen.writeNumberField("rating", housekeeperReporter.getRating());
                } else {
                    gen.writeNullField("rating");
                }
            }
            gen.writeEndObject(); // end reporter
        } else {
            gen.writeNullField("reporter"); // เพิ่ม null field ถ้าไม่มี reporter
        }


        // --- Serialize Hirer ---
        if (report.getHirer() != null) {
            gen.writeFieldName("hirer");
            gen.writeStartObject();
            PartyRole hirer = report.getHirer(); // ใช้ PartyRole แทน Member
            Hibernate.initialize(hirer);

            gen.writeStringField("type", getTypeString(hirer)); // Helper method
            gen.writeNumberField("id", hirer.getId());

            // Check if it's actually a Hirer (to access Hirer-specific fields like balance)
            if (hirer instanceof Hirer) {
                Hirer actualHirer = (Hirer) hirer;
                gen.writeNumberField("balance", actualHirer.getBalance());
            }


            if (hirer.getPerson() != null) {
                Hibernate.initialize(hirer.getPerson());
                gen.writeObjectFieldStart("person");
                gen.writeNumberField("personId", hirer.getPerson().getPersonId());
                gen.writeStringField("firstName", hirer.getPerson().getFirstName());
                gen.writeStringField("lastName", hirer.getPerson().getLastName());
                gen.writeStringField("email", hirer.getPerson().getEmail());
                gen.writeStringField("phoneNumber", hirer.getPerson().getPhoneNumber());
                gen.writeStringField("address", hirer.getPerson().getAddress());
                gen.writeStringField("idCardNumber", hirer.getPerson().getIdCardNumber());
                if (hirer.getPerson().getPictureUrl() != null) {
                    gen.writeStringField("pictureUrl", hirer.getPerson().getPictureUrl());
                } else {
                    gen.writeNullField("pictureUrl");
                }
                gen.writeStringField("accountStatus", hirer.getPerson().getAccountStatus());
                if (hirer.getPerson().getLogin() != null) {
                    gen.writeStringField("username", hirer.getPerson().getLogin().getUsername());
                } else {
                    gen.writeNullField("username");
                }
                gen.writeEndObject(); // end person
            } else {
                gen.writeNullField("person");
            }
            gen.writeEndObject(); // end hirer
        } else {
            gen.writeNullField("hirer");
        }

        // --- Serialize Housekeeper ---
        if (report.getHousekeeper() != null) {
            gen.writeFieldName("housekeeper");
            gen.writeStartObject();
            PartyRole housekeeper = report.getHousekeeper(); // ใช้ PartyRole แทน Member
            Hibernate.initialize(housekeeper);

            gen.writeStringField("type", getTypeString(housekeeper)); // Helper method
            gen.writeNumberField("id", housekeeper.getId());

            // Cast เพื่อเข้าถึง field เฉพาะของ Housekeeper
            if (housekeeper instanceof Housekeeper) {
                Housekeeper actualHousekeeper = (Housekeeper) housekeeper;
                gen.writeNumberField("balance", actualHousekeeper.getBalance()); // balance ใน Housekeeper
                if (actualHousekeeper.getPhotoVerifyUrl() != null) {
                    gen.writeStringField("photoVerifyUrl", actualHousekeeper.getPhotoVerifyUrl());
                } else {
                    gen.writeNullField("photoVerifyUrl");
                }
                if (actualHousekeeper.getStatusVerify() != null) {
                    gen.writeStringField("statusVerify", actualHousekeeper.getStatusVerify());
                } else {
                    gen.writeNullField("statusVerify");
                }
                if (actualHousekeeper.getRating() != null) {
                    gen.writeNumberField("rating", actualHousekeeper.getRating());
                } else {
                    gen.writeNullField("rating");
                }

                // Serialize HousekeeperSkills
                if (actualHousekeeper.getHousekeeperSkills() != null) {
                    Hibernate.initialize(actualHousekeeper.getHousekeeperSkills());
                    gen.writeFieldName("housekeeperSkills");
                    gen.writeStartArray();
                    for (HousekeeperSkill hks : actualHousekeeper.getHousekeeperSkills()) {
                        gen.writeStartObject();
                        gen.writeNumberField("skillId", hks.getSkillId());
                        gen.writeStringField("skillLevel", hks.getSkillLevel());
                        if (hks.getSkillType() != null) {
                            Hibernate.initialize(hks.getSkillType());
                            gen.writeObjectFieldStart("skillType");
                            gen.writeNumberField("skillTypeId", hks.getSkillType().getSkillTypeId());
                            gen.writeStringField("skillTypeName", hks.getSkillType().getSkillTypeName());
                            gen.writeStringField("skillTypeDetail", hks.getSkillType().getSkillTypeDetail());
                            gen.writeEndObject(); // end skillType
                        } else {
                            gen.writeNullField("skillType");
                        }
                        gen.writeEndObject(); // end housekeeperSkill
                    }
                    gen.writeEndArray(); // end housekeeperSkills
                } else {
                    gen.writeNullField("housekeeperSkills");
                }
            }
            if (housekeeper.getPerson() != null) {
                Hibernate.initialize(housekeeper.getPerson());
                gen.writeObjectFieldStart("person");
                gen.writeNumberField("personId", housekeeper.getPerson().getPersonId());
                gen.writeStringField("firstName", housekeeper.getPerson().getFirstName());
                gen.writeStringField("lastName", housekeeper.getPerson().getLastName());
                gen.writeStringField("email", housekeeper.getPerson().getEmail());
                gen.writeStringField("phoneNumber", housekeeper.getPerson().getPhoneNumber());
                gen.writeStringField("address", housekeeper.getPerson().getAddress());
                gen.writeStringField("idCardNumber", housekeeper.getPerson().getIdCardNumber());
                if (housekeeper.getPerson().getPictureUrl() != null) {
                    gen.writeStringField("pictureUrl", housekeeper.getPerson().getPictureUrl());
                } else {
                    gen.writeNullField("pictureUrl");
                }
                gen.writeStringField("accountStatus", housekeeper.getPerson().getAccountStatus());
                if (housekeeper.getPerson().getLogin() != null) {
                    gen.writeStringField("username", housekeeper.getPerson().getLogin().getUsername());
                } else {
                    gen.writeNullField("username");
                }
                gen.writeEndObject(); // end person
            } else {
                gen.writeNullField("person");
            }
            gen.writeEndObject(); // end housekeeper
        } else {
            gen.writeNullField("housekeeper");
        }

        // --- Serialize Penalty ---
        if (report.getPenalty() != null) {
            gen.writeFieldName("penalty");
            gen.writeStartObject();
            Penalty penalty = report.getPenalty();
            Hibernate.initialize(penalty);
            gen.writeNumberField("penaltyId", penalty.getPenaltyId());
            gen.writeStringField("penaltyType", penalty.getPenaltyType());
            gen.writeStringField("penaltyDetail", penalty.getPenaltyDetail());
            gen.writeStringField("penaltyStatus", penalty.getPenaltyStatus());
            if (penalty.getPenaltyDate() != null) {
                gen.writeStringField("penaltyDate", penalty.getPenaltyDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            } else {
                gen.writeNullField("penaltyDate");
            }
            gen.writeEndObject(); // end penalty
        } else {
            gen.writeNullField("penalty");
        }

        gen.writeEndObject(); // end report
    }

    // Helper method to get the type string
    private String getTypeString(PartyRole role) {
        if (role instanceof Hirer) {
            return "hirer";
        } else if (role instanceof Housekeeper) {
            return "housekeeper";
        } else if (role instanceof Admin) {
            return "admin";
        } else if (role instanceof AccountManager) {
            return "accountManager";
        } else {
            return "partyRole"; // Fallback type
        }
    }
}