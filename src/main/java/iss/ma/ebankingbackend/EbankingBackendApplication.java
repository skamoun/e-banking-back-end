package iss.ma.ebankingbackend;

import iss.ma.ebankingbackend.dtos.BankAccountDTO;
import iss.ma.ebankingbackend.dtos.CurrentBankAccountDTO;
import iss.ma.ebankingbackend.dtos.CustomerDTO;
import iss.ma.ebankingbackend.dtos.SavingBankAccountDTO;
import iss.ma.ebankingbackend.entities.*;
import iss.ma.ebankingbackend.exceptions.BalanceNotSufficientException;
import iss.ma.ebankingbackend.exceptions.BankAccountNotFoundException;
import iss.ma.ebankingbackend.exceptions.CustomerNotFoundException;
import iss.ma.ebankingbackend.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@AllArgsConstructor
public class EbankingBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(EbankingBackendApplication.class, args);
	}
    @Bean
	CommandLineRunner start(BankAccountService bankAccountService) {
		return args -> {
			Stream.of("Hassan","Ikrame","Mohamed").forEach(name->{
				CustomerDTO customer=new CustomerDTO();
				customer.setName(name);
				customer.setEmail(name+"@gmail.com");
				bankAccountService.saveCustomer(customer);
			});
			bankAccountService.listCustomers().forEach(customer->{
				try {
					bankAccountService.saveCurrentBankAccount(Math.random()*90000,9000,customer.getId());
					bankAccountService.saveSavingBankAccount(Math.random()*120000,5.5,customer.getId());


				} catch (CustomerNotFoundException e) {
					e.printStackTrace();
				}

			});
			List<BankAccountDTO> bankAccounts = bankAccountService.bankAccountList();
			for (BankAccountDTO bankAccount:bankAccounts){
				for (int i = 0; i <10 ; i++) {
					bankAccountService.credit(bankAccount.getId(),10000+Math.random()*120000,"Credit");
					bankAccountService.debit(bankAccount.getId(),1000+Math.random()*9000,"Debit");
				}
			}

		};
	}
		};




