package iss.ma.ebankingbackend.dtos;

import iss.ma.ebankingbackend.entities.BankAccount;
import iss.ma.ebankingbackend.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;


@Data

public class AccountOperationDTO {

    private Long id;
    private Date operationDate;
    private double amount;
    private OperationType type;
    private String description;
}
