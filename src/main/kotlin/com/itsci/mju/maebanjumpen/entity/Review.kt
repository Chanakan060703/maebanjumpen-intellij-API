package com.itsci.mju.maebanjumpen.entity

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.itsci.mju.maebanjumpen.review.dto.ReviewDTO
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "review")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "reviewId")
@NamedEntityGraph(
    name = "review-with-hire-details",
    attributeNodes = [
        NamedAttributeNode(value = "hire", subgraph = "hireSubgraph")
    ],
    subgraphs = [
        NamedSubgraph(
            name = "hireSubgraph",
            attributeNodes = [
                NamedAttributeNode(value = "hirer", subgraph = "hirerSubgraph"),
                NamedAttributeNode(value = "housekeeper", subgraph = "housekeeperSubgraph"),
                NamedAttributeNode(value = "progressionImageUrls")
            ]
        ),
        NamedSubgraph(
            name = "hirerSubgraph",
            attributeNodes = [
                NamedAttributeNode(value = "person", subgraph = "personSubgraph")
            ]
        ),
        NamedSubgraph(
            name = "housekeeperSubgraph",
            attributeNodes = [
                NamedAttributeNode(value = "person", subgraph = "personSubgraph"),
                NamedAttributeNode(value = "housekeeperSkills")
            ]
        ),
        NamedSubgraph(
            name = "personSubgraph",
            attributeNodes = [
                NamedAttributeNode(value = "login")
            ]
        )
    ]
)
data class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = 0,

    @Column(name = "review_message")
    var reviewMessage: String? = null,

    @Column(name = "score")
    var score: Double? = null,

    @Column(name = "review_date", nullable = false)
    var reviewDate: LocalDateTime? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hire_id", nullable = false, unique = true)
    var hires: Hire? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Review) return false
        return id != null && id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 0

    override fun toString(): String = "Review(reviewId=$id, score=$score)"
  fun toReviewDTO(): ReviewDTO {
        return ReviewDTO(
            id = id,
            reviewMessage = reviewMessage,
            score = score,
            reviewDate = reviewDate
        )
    }
}

