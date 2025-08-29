package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itsci.mju.maebanjumpen.serializer.ReportSerializer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "report")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonSerialize(using = ReportSerializer.class)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reportId")
    private Integer reportId;

    @Column(name = "reportTitle", nullable = false)
    private String reportTitle;

    @Column(name = "reportMessage", columnDefinition = "TEXT")
    private String reportMessage;

    @Column(name = "reportDate", nullable = false)
    private LocalDateTime reportDate;

    @Column(name = "reportStatus", nullable = false)
    private String reportStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id")
    private PartyRole reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hirer_id")
    private Member hirer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "housekeeper_id")
    private Member housekeeper;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "penalty_id")
    private Penalty penalty;
}