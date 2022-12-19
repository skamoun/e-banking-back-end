package iss.ma.ebankingbackend.services;

import iss.ma.ebankingbackend.dtos.*;
import iss.ma.ebankingbackend.entities.*;
import iss.ma.ebankingbackend.enums.OperationType;
import iss.ma.ebankingbackend.exceptions.BalanceNotSufficientException;
import iss.ma.ebankingbackend.exceptions.BankAccountNotFoundException;
import iss.ma.ebankingbackend.exceptions.CustomerNotFoundException;
import iss.ma.ebankingbackend.mappers.BankAccountMapperImpl;
import iss.ma.ebankingbackend.repositories.AccountOperationRepository;
import iss.ma.ebankingbackend.repositories.BankAccountRepository;
import iss.ma.ebankingbackend.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;
    private final AccountOperationRepository accountOperationRepository;
    private BankAccountMapperImpl bankAccountMapper;
    @Override
    public CustomerDTO saveCustomer(CustomerDTO customer) {
        log.info(" saving new customer");
   Customer newCustomer= customerRepository.save(bankAccountMapper.fromCustomerDTO(customer));

        return bankAccountMapper.fromCustomer(newCustomer);
    }

    @Override
    public CurrentBankAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId) throws CustomerNotFoundException {
        return    customerRepository.findById(customerId)
                .map(customer -> {
                            CurrentAccount currentAccount=new CurrentAccount();
                    currentAccount.setId(UUID.randomUUID().toString());
                    currentAccount.setCreatedAt(new Date());
                    currentAccount.setBalance(initialBalance);
                    currentAccount.setOverDraft(overDraft);
                    currentAccount.setCustomer(customer);
                  CurrentBankAccountDTO currentBankAccountDTO=      bankAccountMapper.fromCurrentBankAccount(bankAccountRepository.save(currentAccount));
                  currentBankAccountDTO.setType(currentAccount.getClass().getSimpleName());
                  return currentBankAccountDTO;
                        }

                )
                .orElseThrow(()->new CustomerNotFoundException("Customer not found"));
    }

    @Override
    public SavingBankAccountDTO saveSavingBankAccount(double initialBalance,
                                                      double interestRate,
                                                      Long customerId) throws CustomerNotFoundException {
      return    customerRepository.findById(customerId)
              .map(customer -> {
                  SavingAccount savingAccount=new SavingAccount();
                  savingAccount.setId(UUID.randomUUID().toString());
                  savingAccount.setCreatedAt(new Date());
                  savingAccount.setBalance(initialBalance);
                  savingAccount.setInterestRate(interestRate);
                  savingAccount.setCustomer(customer);

            SavingBankAccountDTO savingBankAccountDTO=    bankAccountMapper.fromSavingBankAccount(bankAccountRepository.save(savingAccount));
             savingBankAccountDTO.setType(savingAccount.getClass().getSimpleName());
             return  savingBankAccountDTO;

                      }

              )
                .orElseThrow(()->new CustomerNotFoundException("Customer not found"));



    }


    @Override
    public List<CustomerDTO> listCustomers() {
        return customerRepository.findAll().stream()
                .map(customer -> bankAccountMapper.fromCustomer(customer))
                .collect(Collectors.toList());
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAccountNotFoundException {
      return    bankAccountRepository.findById(accountId)
                 .map(bankAccount -> {
                   if(bankAccount instanceof  SavingAccount){
                       SavingAccount savingAccount = (SavingAccount) bankAccount;
                       return bankAccountMapper.fromSavingBankAccount(savingAccount);
                   }
                   else{
                       CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                       return bankAccountMapper.fromCurrentBankAccount(currentAccount);
                   }
                 })
                .orElseThrow(()-> new BankAccountNotFoundException(" no bank account found"));
    }
    @Override
    public CustomerDTO getCustomer(Long id) throws CustomerNotFoundException {
        return customerRepository.findById(id)
                .map(customer ->
            bankAccountMapper.fromCustomer(customer)).orElseThrow(()-> new CustomerNotFoundException(" customer not found"));
    }

    @Override
    public void debit(String accountId, double amount, String descr) throws BankAccountNotFoundException, BalanceNotSufficientException {

        bankAccountRepository.findById(accountId).map(bankAccount -> {
            if(bankAccount.getBalance()<amount)
                try {
                    throw  new BalanceNotSufficientException(" balance not sufficient !");
                } catch (BalanceNotSufficientException e) {
                    e.printStackTrace();
                }

            AccountOperation accountOperation = AccountOperation.builder()
                    .bankAccount(bankAccount)
                    .amount(amount)
                    .description(descr)
                    .type(OperationType.DEBIT)
                    .operationDate(new Date())
                    .build();
            accountOperationRepository.save(accountOperation);
            bankAccount.setBalance(bankAccount.getBalance()-amount);
           return bankAccountRepository.save(bankAccount);
        }).orElseThrow(()-> new BankAccountNotFoundException(" no bank account found"));



    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAccountNotFoundException {

        bankAccountRepository.findById(accountId).map(bankAccount -> {
            AccountOperation accountOperation = AccountOperation.builder()
                    .bankAccount(bankAccount)
                    .amount(amount)
                    .description(description)
                    .type(OperationType.CREDIT)
                    .operationDate(new Date())
                    .build();
            accountOperationRepository.save(accountOperation);
            bankAccount.setBalance(bankAccount.getBalance()+ amount);
           return bankAccountRepository.save(bankAccount);
        }).orElseThrow(()-> new BankAccountNotFoundException(" no bank account found"));
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, BalanceNotSufficientException {
    debit(accountIdSource,amount," tranfer to %"+accountIdDestination);
        credit(accountIdDestination,amount," tranfer from % "+accountIdSource);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        return bankAccountRepository.findAll().stream().map(bankAccount -> {
            if(bankAccount instanceof SavingAccount)
                return bankAccountMapper.fromSavingBankAccount((SavingAccount) bankAccount);
            return  bankAccountMapper.fromCurrentBankAccount((CurrentAccount) bankAccount);
        }).collect(Collectors.toList());
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        Customer updatedCustomer = customerRepository.save(bankAccountMapper.fromCustomerDTO(customerDTO));
        return bankAccountMapper.fromCustomer(updatedCustomer);
    }
    @Override
    public void deleteCustomer(Long customerId){
        customerRepository.deleteById(customerId);
    }
   @Override
   public List<AccountOperationDTO> accountHistory(String accountId){
        return accountOperationRepository.findByBankAccountId(accountId)
                .map(accountOperation -> {
                    return bankAccountMapper.fromAccountOperation(accountOperation);
                }).toList();
   }


    @Override
    public AccountHistoryDTO getAccountHistory(String accountId, int page, int size) throws BankAccountNotFoundException {
        BankAccount bankAccount=bankAccountRepository.findById(accountId).orElse(null);
        if(bankAccount==null) throw new BankAccountNotFoundException("Account not Found");
        Page<AccountOperation> accountOperations = accountOperationRepository.findByBankAccountId(accountId, PageRequest.of(page, size));
        AccountHistoryDTO accountHistoryDTO=new AccountHistoryDTO();
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.getContent().stream().map(op -> bankAccountMapper.fromAccountOperation(op)).collect(Collectors.toList());
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        accountHistoryDTO.setAccountId(bankAccount.getId());
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        return accountHistoryDTO;
    }

}
