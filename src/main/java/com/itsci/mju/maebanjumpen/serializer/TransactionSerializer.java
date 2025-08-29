package com.itsci.mju.maebanjumpen.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.itsci.mju.maebanjumpen.model.Transaction;
import com.itsci.mju.maebanjumpen.model.Member;
import com.itsci.mju.maebanjumpen.model.Person;
import org.hibernate.Hibernate;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class TransactionSerializer extends StdSerializer<Transaction> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public TransactionSerializer() {
        this(null);
    }

    public TransactionSerializer(Class<Transaction> t) {
        super(t);
    }

    @Override
    public void serialize(Transaction transaction, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();

        // Serialize basic transaction fields
        generator.writeNumberField("transactionId", transaction.getTransactionId());
        generator.writeStringField("transactionType", transaction.getTransactionType());
        generator.writeNumberField("transactionAmount", transaction.getTransactionAmount());
        generator.writeStringField("transactionDate",
                transaction.getTransactionDate() != null ?
                        transaction.getTransactionDate().format(formatter) : null);
        generator.writeStringField("transactionStatus", transaction.getTransactionStatus());

        // Serialize Member Information
        if (transaction.getMember() != null && Hibernate.isInitialized(transaction.getMember())) {
            Member member = transaction.getMember();
            generator.writeNumberField("memberId", member.getId());

            if (member.getPerson() != null && Hibernate.isInitialized(member.getPerson())) {
                Person person = member.getPerson();
                generator.writeStringField("memberFirstName", person.getFirstName());
                generator.writeStringField("memberLastName", person.getLastName());
                generator.writeStringField("memberPictureUrl", person.getPictureUrl());
            } else {
                // Handle uninitialized Person
                generator.writeStringField("memberFirstName", "N/A");
                generator.writeStringField("memberLastName", "");
                generator.writeNullField("memberPictureUrl");
            }
        } else {
            // Handle uninitialized Member
            generator.writeNullField("memberId");
            generator.writeStringField("memberFirstName", "N/A");
            generator.writeStringField("memberLastName", "");
            generator.writeNullField("memberPictureUrl");
        }

        // Serialize Payment Details
        generator.writeStringField("prompayNumber", transaction.getPrompayNumber());
        generator.writeStringField("bankAccountNumber", transaction.getBankAccountNumber());
        generator.writeStringField("bankAccountName", transaction.getBankAccountName());
        generator.writeStringField("transactionApprovalDate",
                transaction.getTransactionApprovalDate() != null ?
                        transaction.getTransactionApprovalDate().format(formatter) : null);

        generator.writeEndObject();
    }
}