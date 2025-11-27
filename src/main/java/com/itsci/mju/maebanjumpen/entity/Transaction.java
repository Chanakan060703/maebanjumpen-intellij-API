package com.itsci.mju.maebanjumpen.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    /** ประเภทของธุรกรรม เช่น Deposit, Withdraw */
    @Column(name = "transaction_type", nullable = false, length = 255)
    private String transactionType;

    /** จำนวนเงิน */
    @Column(nullable = false)
    private Double transactionAmount;

    /** วันที่ทำธุรกรรม */
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    /** สถานะธุรกรรม เช่น PENDING, APPROVED, REJECTED */
    @Column(name = "transaction_status", nullable = false, length = 255)
    private String transactionStatus;

    /** ผู้ทำธุรกรรม (Member: Hirer หรือ Housekeeper) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @ToString.Exclude
    private Member member;

    /** เบอร์พร้อมเพย์ (กรณีถอนเงิน) */
    @Column(name = "prompay_number", length = 50)
    private String prompayNumber;

    /** เลขบัญชีธนาคาร (กรณีถอนเงิน) */
    @Column(name = "bank_account_number", length = 50)
    private String bankAccountNumber;

    /** ชื่อบัญชีธนาคาร */
    @Column(name = "bank_account_name", length = 255)
    private String bankAccountName;

    /** วันที่อนุมัติธุรกรรม */
    @Column(name = "transaction_approval_date")
    private LocalDateTime transactionApprovalDate;
}
