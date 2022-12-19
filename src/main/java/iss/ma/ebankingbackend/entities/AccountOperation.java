package iss.ma.ebankingbackend.entities;

import lombok.*;
import iss.ma.ebankingbackend.enums.OperationType;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AccountOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date operationDate;
    private double amount;
    @Enumerated(EnumType.STRING)
    private OperationType type;
    @ManyToOne
    private BankAccount bankAccount;
    private String description;
}
