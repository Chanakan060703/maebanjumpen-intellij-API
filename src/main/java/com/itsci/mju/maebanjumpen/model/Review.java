package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import lombok.EqualsAndHashCode.Exclude;
import java.time.LocalDateTime;

@Entity
@Table(name = "review")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "reviewId")
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
    @ToString.Include
    private Integer reviewId;

    @Column(name = "review_message")
    private String reviewMessage;

    @Column(name = "score")
    private Double score;

    @Column(name = "review_date", nullable = false)
    private LocalDateTime reviewDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hire_id", nullable = false, unique = true)
    @ToString.Exclude
    @Exclude
    private Hire hire;
}
