package com.itsci.mju.maebanjumpen.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.itsci.mju.maebanjumpen.serializer.MemberSerializer;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@Entity
@JsonSerialize(using = MemberSerializer.class)
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Hirer.class, name = "hirer"),
        @JsonSubTypes.Type(value = Housekeeper.class, name = "housekeeper"),
        // *** แก้ไขตรงนี้: เปลี่ยน Member.class เป็น Hirer.class ***
        // ถ้า "type" เป็น "member" ใน JSON ให้สร้างเป็น Hirer class
        // เพราะ Member เป็น abstract คุณต้องระบุ class ที่เป็น concrete
        @JsonSubTypes.Type(value = Hirer.class, name = "member") // <<< เปลี่ยนตรงนี้!
})
public abstract class Member extends PartyRole {

    @Column(name = "balance")
    private Double balance = 0.0;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Transaction> transactions = new HashSet<>();

    public Member() {
        super();
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.add(transaction);
            transaction.setMember(this);
        }
    }

    public void removeTransaction(Transaction transaction) {
        if (transaction != null) {
            transactions.remove(transaction);
            transaction.setMember(null);
        }
    }
}