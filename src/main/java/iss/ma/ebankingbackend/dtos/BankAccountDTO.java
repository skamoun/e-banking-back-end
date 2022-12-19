package iss.ma.ebankingbackend.dtos;

import iss.ma.ebankingbackend.entities.AccountOperation;
import iss.ma.ebankingbackend.entities.Customer;
import iss.ma.ebankingbackend.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Data
public abstract class BankAccountDTO {

    private String id;
    private double balance;
    private Date createdAt;

    private AccountStatus status;

    private CustomerDTO customerDTO;
    private String type;

}
