package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonBackReference; // This import might not be needed
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itsci.mju.maebanjumpen.serializer.ReviewSerializer; // Make sure this serializer exists
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import lombok.EqualsAndHashCode.Include;

import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "reviewId")
@JsonSerialize(using = ReviewSerializer.class)
@NamedEntityGraph(
        name = "review-with-hire-details",
        attributeNodes = {
                @NamedAttributeNode(value = "hire", subgraph = "hireSubgraph")
        },
        subgraphs = {
                @NamedSubgraph(
                        name = "hireSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "hirer", subgraph = "hirerSubgraph"),
                                @NamedAttributeNode(value = "housekeeper", subgraph = "housekeeperSubgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "hirerSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "person", subgraph = "personSubgraph")
                        }
                ),
                @NamedSubgraph(
                        name = "housekeeperSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "person", subgraph = "personSubgraph"),
                                @NamedAttributeNode(value = "housekeeperSkills")
                        }
                ),
                @NamedSubgraph(
                        name = "personSubgraph",
                        attributeNodes = {
                                @NamedAttributeNode(value = "login")
                        }
                )
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include
    private Integer reviewId;

    @Column(name = "review_message")
    private String reviewMessage;

    @Column(name = "score")
    private Double score;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hire_id")
    @ToString.Exclude
    @Exclude
    // If you use @JsonIdentityInfo, you generally don't need @JsonBackReference
    // @JsonBackReference("hire-review")
    private Hire hire;
}