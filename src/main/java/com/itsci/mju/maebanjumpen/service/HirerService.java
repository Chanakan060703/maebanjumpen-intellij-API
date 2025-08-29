package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.exception.HirerNotFoundException;
import com.itsci.mju.maebanjumpen.exception.InsufficientBalanceException;
import com.itsci.mju.maebanjumpen.model.Hirer;

import java.util.List;


public interface HirerService {
    Hirer saveHirer(Hirer hirer);
    Hirer getHirerById(int id);
    List<Hirer> getAllHirers();
    Hirer updateHirer(int id, Hirer hirer);
    void deleteHirer(int id);

    // เพิ่มเมธอดสำหรับหักเงิน
    void deductBalance(Integer hirerId, Double amount) throws InsufficientBalanceException, HirerNotFoundException;
    // เพิ่มเมธอดสำหรับเพิ่มเงิน (เผื่อสำหรับการเติมเงินหรือคืนเงิน)
    void addBalance(Integer hirerId, Double amount) throws HirerNotFoundException;

}
