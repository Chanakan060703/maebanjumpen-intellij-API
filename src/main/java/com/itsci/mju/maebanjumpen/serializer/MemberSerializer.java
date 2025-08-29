package com.itsci.mju.maebanjumpen.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.itsci.mju.maebanjumpen.model.*;
import org.hibernate.Hibernate;
import java.io.IOException;

public class MemberSerializer extends StdSerializer<Member> {

    public MemberSerializer() {
        this(null);
    }

    public MemberSerializer(Class<Member> t) {
        super(t);
    }

    @Override
    public void serializeWithType(Member member, JsonGenerator generator,
                                  SerializerProvider provider, TypeSerializer typeSer) throws IOException {
        typeSer.writeTypePrefixForObject(member, generator);
        serialize(member, generator, provider);
        typeSer.writeTypeSuffixForObject(member, generator);
    }

    @Override
    public void serialize(Member member, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeNumberField("id", member.getId());

        if (member.getPerson() != null) {
            Person person = member.getPerson();
            generator.writeObjectFieldStart("person");
            generator.writeNumberField("personId", person.getPersonId());
            generator.writeStringField("email", person.getEmail());
            generator.writeStringField("firstName", person.getFirstName());
            generator.writeStringField("lastName", person.getLastName());
            generator.writeStringField("idCardNumber", person.getIdCardNumber());
            generator.writeStringField("phoneNumber", person.getPhoneNumber());
            generator.writeStringField("address", person.getAddress());
            generator.writeStringField("pictureUrl", person.getPictureUrl());
            generator.writeStringField("accountStatus", person.getAccountStatus());
            if (person.getLogin() != null) {
                generator.writeStringField("username", person.getLogin().getUsername());
            }
            generator.writeEndObject();
        } else {
            generator.writeNullField("person");
        }

        generator.writeNumberField("balance", member.getBalance());

        // --- Crucial Change for 'transactions' ---
        generator.writeFieldName("transactions"); // Always write the field name
        if (member.getTransactions() != null && Hibernate.isInitialized(member.getTransactions())) {
            // If initialized, serialize the actual collection
            generator.writeStartArray();
            for (Transaction transaction : member.getTransactions()) {
                // Let Jackson's default serializer or TransactionSerializer handle each transaction object
                generator.writeObject(transaction);
            }
            generator.writeEndArray();
        } else {
            // If not initialized, serialize as an empty array or null (empty array is generally better for collections)
            generator.writeStartArray();
            generator.writeEndArray();
            // Alternatively: generator.writeNull(); // if you prefer null when not loaded
        }


        // Handle specific fields for Hirer
        if (member instanceof Hirer) {
            Hirer hirer = (Hirer) member;
            generator.writeFieldName("hires"); // Always write the field name
            if (hirer.getHires() != null && Hibernate.isInitialized(hirer.getHires())) {
                generator.writeStartArray();
                for (Hire hire : hirer.getHires()) {
                    generator.writeObject(hire);
                }
                generator.writeEndArray();
            } else {
                generator.writeStartArray(); // Serialize as empty array if not loaded
                generator.writeEndArray();
            }
        }

        // Handle specific fields for Housekeeper
        if (member instanceof Housekeeper) {
            Housekeeper housekeeper = (Housekeeper) member;
            generator.writeStringField("photoVerifyUrl", housekeeper.getPhotoVerifyUrl());
            generator.writeStringField("statusVerify", housekeeper.getStatusVerify());
            generator.writeNumberField("rating", housekeeper.getRating());
            generator.writeNumberField("dailyRate", housekeeper.getDailyRate());

            generator.writeFieldName("hires"); // Always write the field name
            if (housekeeper.getHires() != null && Hibernate.isInitialized(housekeeper.getHires())) {
                generator.writeStartArray();
                for (Hire hire : housekeeper.getHires()) {
                    generator.writeObject(hire);
                }
                generator.writeEndArray();
            } else {
                generator.writeStartArray(); // Serialize as empty array if not loaded
                generator.writeEndArray();
            }

            generator.writeFieldName("housekeeperSkills"); // Always write the field name
            if (housekeeper.getHousekeeperSkills() != null && Hibernate.isInitialized(housekeeper.getHousekeeperSkills())) {
                generator.writeStartArray();
                for (HousekeeperSkill skill : housekeeper.getHousekeeperSkills()) {
                    generator.writeObject(skill);
                }
                generator.writeEndArray();
            } else {
                generator.writeStartArray(); // Serialize as empty array if not loaded
                generator.writeEndArray();
            }
        }
    }
}