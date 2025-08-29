package com.itsci.mju.maebanjumpen.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.itsci.mju.maebanjumpen.model.Review;
import com.itsci.mju.maebanjumpen.model.Hire;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class ReviewSerializer extends JsonSerializer<Review> {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void serialize(Review review, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("reviewId", review.getReviewId());
        jsonGenerator.writeStringField("reviewMessage", review.getReviewMessage());
        if (review.getScore() != null) {
            jsonGenerator.writeNumberField("score", review.getScore());
        } else {
            jsonGenerator.writeNullField("score");
        }
        jsonGenerator.writeStringField("reviewDate", review.getReviewDate().format(DATETIME_FORMATTER));

        // *** การเปลี่ยนแปลงที่สำคัญที่สุด: สำหรับ Hire field ใน ReviewSerializer ***
        // แทนที่จะเรียก HireSerializer โดยตรง เราจะเขียนแค่ ID ของ Hire
        // เพราะนี่คือฝั่ง @JsonBackReference และเราต้องการหลีกเลี่ยง Circular Reference
        Hire hire = review.getHire();
        if (hire != null) {
            jsonGenerator.writeObjectFieldStart("hire"); // เริ่มต้น Object สำหรับ Hire
            jsonGenerator.writeNumberField("hireId", hire.getHireId()); // เขียนแค่ ID ของ Hire
            // อาจจะเพิ่มข้อมูลอื่นๆ ของ Hire ที่จำเป็นจริงๆ เช่น hireName, jobStatus
            // แต่ควรระวังไม่ให้เกิดการเรียก HireSerializer ซ้ำ
            // ถ้าไม่จำเป็นจริงๆ ควรมีแค่ ID เท่านั้น
            jsonGenerator.writeStringField("hireName", hire.getHireName()); // เพิ่ม hireName
            jsonGenerator.writeStringField("jobStatus", hire.getJobStatus()); // เพิ่ม jobStatus
            jsonGenerator.writeEndObject(); // ปิด Object สำหรับ Hire
        } else {
            jsonGenerator.writeNullField("hire");
        }


        jsonGenerator.writeEndObject();
    }
}