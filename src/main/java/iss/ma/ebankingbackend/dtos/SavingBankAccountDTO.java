package iss.ma.ebankingbackend.dtos;

import iss.ma.ebankingbackend.enums.AccountStatus;
import lombok.Data;

import java.util.Date;


@Data
public  class SavingBankAccountDTO  extends BankAccountDTO{

    private double interestRate;
}
