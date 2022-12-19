package iss.ma.ebankingbackend.services;

import iss.ma.ebankingbackend.dtos.*;
import iss.ma.ebankingbackend.entities.BankAccount;
import iss.ma.ebankingbackend.exceptions.BalanceNotSufficientException;
import iss.ma.ebankingbackend.exceptions.BankAccountNotFoundException;
import iss.ma.ebankingbackend.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
     CustomerDTO saveCustomer(CustomerDTO customer);
     CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException;
    SavingBankAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId) throws CustomerNotFoundException;
   List<CustomerDTO> listCustomers();
   BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException;

    CustomerDTO getCustomer(Long id) throws CustomerNotFoundException;

    void debit(String accountId, double amount, String descr) throws BankAccountNotFoundException, BalanceNotSufficientException;
  void credit(String accountId, double amount, String description) throws BankAccountNotFoundException;
  void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException;
    List<BankAccountDTO> bankAccountList();

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> accountHistory(String accountId);

    AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException;
}
