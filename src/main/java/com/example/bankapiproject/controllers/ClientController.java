package com.example.bankapiproject.controllers;

import com.example.bankapiproject.dto.ClientDto;
import com.example.bankapiproject.entity.ClientEntity;
import com.example.bankapiproject.mapper.ClientMapper;
import com.example.bankapiproject.repository.ClientRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RestController
@RequestMapping("/")
public class ClientController {

    private final ClientRepo clientRepo;

    @Autowired
    public ClientController(ClientRepo clientRepo) {
        this.clientRepo = clientRepo;
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
                ClientEntity updatedEntity = clientRepo.save(clientEntity);

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
}
