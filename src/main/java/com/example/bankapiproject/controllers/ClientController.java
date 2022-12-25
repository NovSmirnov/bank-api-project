package com.example.bankapiproject.controllers;

import com.example.bankapiproject.dto.ClientDto;
import com.example.bankapiproject.dto.TransactionDto;
import com.example.bankapiproject.entity.ClientEntity;
import com.example.bankapiproject.entity.TransactionEntity;
import com.example.bankapiproject.entity.TwoClientsOperationEntity;
import com.example.bankapiproject.mapper.CalendarStringMapper;
import com.example.bankapiproject.mapper.ClientMapper;
import com.example.bankapiproject.mapper.TransactionMapper;
import com.example.bankapiproject.repository.ClientRepo;
import com.example.bankapiproject.repository.TransactionRepo;
import com.example.bankapiproject.repository.TwoClientsOperationRepo;
import com.example.bankapiproject.structures.OperationListRequest;
import com.example.bankapiproject.structures.TransferMoneyRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
@RequestMapping("/")
public class ClientController {

    private final ClientRepo clientRepo;
    private final TransactionRepo transactionRepo;
    private final TwoClientsOperationRepo twoClientsOperationRepo;

    @Autowired
    public ClientController(ClientRepo clientRepo, TransactionRepo transactionRepo, TwoClientsOperationRepo twoClientsOperationRepo) {
        this.clientRepo = clientRepo;
        this.transactionRepo = transactionRepo;
        this.twoClientsOperationRepo = twoClientsOperationRepo;
    }

    @PostMapping("/balance")
    public String getBalance(@RequestBody String id) {
        Long longId;
        try {
            longId = Long.parseLong(id.trim());
        } catch (NumberFormatException e) {
            return "-1\nНеверный формат ввода данных";
        }

        boolean isClientPresent;
        try {
            isClientPresent = clientRepo.findById(longId).isPresent();
        } catch (Exception e){
            return "-1\nПроблемы с подключением к базе данных";
        }
        if (isClientPresent) {
            String result = String.valueOf(ClientMapper
                            .toDto(clientRepo.findById(longId).orElse(new ClientEntity())).getMoney());
            //Эта конструкция округляет выводимый результат до 2 знаков после запятой
            return String.valueOf(new BigDecimal(result).setScale(2, RoundingMode.HALF_UP).doubleValue());
        } else {
            return "-1\nКлиент с данным Id не существует!";
        }
    }

    @PutMapping("/income")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String putMoney(@RequestBody String data) {
        return moneyOperation(data, true);
    }

    @PutMapping("/outcome")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String takeMoney(@RequestBody String data) {
        return moneyOperation(data, false);
    }

    private String moneyOperation(String data, boolean isAddMoney) {
        String[] request;
        Long id;
        Double moneyToAddOrSubtract;
        try {
            request = data.split("\n");
            id = Long.parseLong(request[0].trim());
            moneyToAddOrSubtract = new BigDecimal(request[1].trim()).setScale(2, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception e) {
            return "-1\nПроверьте правильность ввода, нужно 2 строки: 1 - Id клиента, 2 - сумма вносимых средств";
        }
        boolean isClientPresent;
        try {
            isClientPresent = clientRepo.findById(id).isPresent();
        } catch (Exception e){
            return "-1\nПроблемы с подключением к базе данных";
        }
        if (isClientPresent) {
            ClientDto clientDto = ClientMapper.toDto(clientRepo.findById(id).orElse(new ClientEntity()));
            double startMoney = clientDto.getMoney();
            if (isAddMoney) {
                clientDto.setMoney(startMoney + moneyToAddOrSubtract);
                ClientEntity clientEntity = ClientMapper.toEntity(clientDto);
                ClientEntity updatedEntity = clientRepo.saveAndFlush(clientEntity);
                TransactionEntity transactionEntity = TransactionEntity.builder()
                        .clientEntity(updatedEntity)
                        .operationType("Income")
                        .operationSum(moneyToAddOrSubtract)
                        .transactionDateTime(new GregorianCalendar())
                        .build();
                transactionRepo.save(transactionEntity);

                //Проверка то, что указанная сумма добавлена на счёт
                double newMoney = ClientMapper.toDto(clientRepo.findById(id).orElse(new ClientEntity())).getMoney();
                if (newMoney != startMoney) {
                    return String.valueOf(0);
                } else {
                    return "-1\nУказанная сумма не добавлена на счёт";
                }
            } else {
                if (startMoney - moneyToAddOrSubtract >= 0) {
                    clientDto.setMoney(startMoney - moneyToAddOrSubtract);
                    ClientEntity clientEntity = ClientMapper.toEntity(clientDto);
                    ClientEntity updatedEntity = clientRepo.save(clientEntity);
                    TransactionEntity transactionEntity = TransactionEntity.builder()
                            .clientEntity(updatedEntity)
                            .operationType("Outcome")
                            .operationSum(moneyToAddOrSubtract)
                            .transactionDateTime(new GregorianCalendar())
                            .build();
                    transactionRepo.save(transactionEntity);

                    //Проверка то, что указанная сумма добавлена на счёт
                    double newMoney = ClientMapper.toDto(clientRepo.findById(id).orElse(new ClientEntity())).getMoney();
                    if (newMoney != startMoney) {
                        return String.valueOf(0);
                    } else {
                        return "-1\nУказанная сумма не списана со счёта";
                    }

                } else {
                    return "-1\nУ вас не достаточно средств на счёте";
                }
            }
        } else {
            return "-1\nКлиент с данным Id не существует!";
        }
    }

    @PostMapping("/operation_list")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<TransactionDto> getOperationList(@RequestBody OperationListRequest data) {
        if (data.getClientId() != null) {
            List<TransactionEntity> transactionEntities;
            ClientEntity clientEntity = clientRepo.findById(data.getClientId()).orElse(new ClientEntity());
            if (data.getFirstDate() != null && data.getLastDate() != null) {
                transactionEntities = transactionRepo
                        .findAllByClientEntityAndTransactionDateTimeBetween(clientEntity,
                                CalendarStringMapper.stringToCalendar(data.getFirstDate()),
                                CalendarStringMapper.stringToCalendar(data.getLastDate()));
            } else if (data.getFirstDate() != null && data.getLastDate() == null) {
                transactionEntities = transactionRepo
                        .findAllByClientEntityAndTransactionDateTimeAfter(clientEntity,
                                CalendarStringMapper.stringToCalendar(data.getFirstDate()));
            } else if (data.getFirstDate() == null && data.getLastDate() != null) {
                transactionEntities = transactionRepo
                        .findAllByClientEntityAndTransactionDateTimeBefore(clientEntity,
                                CalendarStringMapper.stringToCalendar(data.getLastDate()));
            } else {
                transactionEntities = transactionRepo.findAllByClientEntity(clientEntity);
            }
            return TransactionMapper.toDtoList(transactionEntities);
        }
        return new ArrayList<>();
    }

    @PostMapping("/transfer_money")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public String transferMoney(@RequestBody TransferMoneyRequest transferMoneyRequest) {
        if (!transferMoneyRequest.getFirstClientId().equals(transferMoneyRequest.getSecondClientId())) {
            ClientEntity firstClientEntity = clientRepo.findById(transferMoneyRequest.getFirstClientId()).orElse(new ClientEntity());
            if (!clientRepo.existsById(firstClientEntity.getId())) {
                return "-1\nНет первого клиента клиента с таким Id";
            }
            if (firstClientEntity.getMoney() > transferMoneyRequest.getMoney()) {
                ClientEntity secondClientEntity = clientRepo.findById(transferMoneyRequest.getSecondClientId()).orElse(new ClientEntity());
                if (!clientRepo.existsById(secondClientEntity.getId())) {
                    return "-1\nНет второго клиента с таким Id";
                }
                double moneyMinus = firstClientEntity.getMoney() - transferMoneyRequest.getMoney();
                firstClientEntity.setMoney(moneyMinus);
                double moneyPlus = secondClientEntity.getMoney() + transferMoneyRequest.getMoney();
                secondClientEntity.setMoney(moneyPlus);
                TransactionEntity firstTransaction = TransactionEntity.builder()
                        .clientEntity(firstClientEntity)
                        .operationType("Outcome")
                        .operationSum(transferMoneyRequest.getMoney())
                        .transactionDateTime(new GregorianCalendar())
                        .build();
                TransactionEntity secondTransaction = TransactionEntity.builder()
                        .clientEntity(secondClientEntity)
                        .operationType("Income")
                        .operationSum(transferMoneyRequest.getMoney())
                        .transactionDateTime(new GregorianCalendar())
                        .build();
                List<ClientEntity> clientEntities = new ArrayList<>();
                List<TransactionEntity> transactionEntities = new ArrayList<>();
                clientEntities.add(firstClientEntity);
                clientEntities.add(secondClientEntity);
                transactionEntities.add(firstTransaction);
                transactionEntities.add(secondTransaction);
                clientRepo.saveAll(clientEntities);
                List<TransactionEntity> updatedTransactions = transactionRepo.saveAll(transactionEntities);
                TwoClientsOperationEntity twoClientsOperationEntity = TwoClientsOperationEntity.builder()
                        .clientOperations(updatedTransactions)
                        .build();
                twoClientsOperationRepo.save(twoClientsOperationEntity);
                return "0";
            } else {
                return "-1\nНедостаточно средств на счёте";
            }
        } else {
            return "-1\nНедопустимая операция";
        }
    }
}
