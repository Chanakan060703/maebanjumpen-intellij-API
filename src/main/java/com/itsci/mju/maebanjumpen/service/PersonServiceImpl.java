package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.dto.PersonDTO; // ⬅️ ต้องใช้ DTO
import com.itsci.mju.maebanjumpen.mapper.PersonMapper; // ⬅️ ต้องใช้ Mapper
import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.model.Login;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import com.itsci.mju.maebanjumpen.repository.LoginRepository;
import lombok.RequiredArgsConstructor; // ⬅️ แนะนำให้ใช้ RequiredArgsConstructor
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // ⬅️ สำหรับ Constructor Injection (แทน @Autowired)
@Transactional(readOnly = true)
public class PersonServiceImpl implements PersonService {

    // ⬅️ Fields สำหรับ Constructor Injection
    private final PersonRepository personRepository;
    private final LoginRepository loginRepository;
    private final PersonMapper personMapper; // ⬅️ Mapper

    @Override
    public List<PersonDTO> getAllPersons() { // ⬅️ รับ/คืน DTO
        List<Person> persons = personRepository.findAll();
        return personMapper.toDtoList(persons); // ⬅️ ใช้ Mapper
    }

    @Override
    public PersonDTO getPersonById(int id) { // ⬅️ รับ/คืน DTO
        return personRepository.findById(id)
                .map(personMapper::toDto) // ⬅️ ใช้ Mapper
                .orElse(null);
    }

    @Override
    public PersonDTO getPersonByUsername(String username) { // ⬅️ รับ/คืน DTO
        return personRepository.findByLoginUsername(username)
                .map(personMapper::toDto) // ⬅️ ใช้ Mapper
                .orElse(null);
    }

    @Override
    @Transactional
    public PersonDTO savePerson(PersonDTO personDto) { // ⬅️ รับ/คืน DTO
        Person person = personMapper.toEntity(personDto); // 1. แปลง DTO เป็น Entity

        if (person.getLogin() != null && person.getLogin().getUsername() != null) {
            Optional<Login> existingLogin = loginRepository.findById(person.getLogin().getUsername());

            if (existingLogin.isPresent()) {
                // ถ้า Login มีอยู่แล้ว ให้ใช้ Login object ที่ดึงมาจาก DB
                // เพื่อเชื่อมโยง Person กับ Login ที่มีอยู่
                person.setLogin(existingLogin.get());
                // *** ถ้ามีการส่ง password มาใน DTO คุณต้อง hash และอัปเดต Login ที่นี่ ***
            }
            // หาก Login ไม่มีอยู่, person.getLogin() จะถูกบันทึกโดย CascadeType.ALL
            // *** ควรมีการเข้ารหัส password ก่อนบันทึกที่นี่ ***
        }

        Person savedPerson = personRepository.save(person);
        return personMapper.toDto(savedPerson); // 2. แปลง Entity กลับเป็น DTO
    }

    @Override
    @Transactional
    public PersonDTO updatePerson(int id, PersonDTO personDto) { // ⬅️ รับ/คืน DTO
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person with ID " + id + " not found"));

        // 1. อัปเดต Entity ด้วยค่าจาก DTO
        // MapStruct สามารถช่วยได้ (ถ้าคุณใช้ @Mapping), แต่ทำด้วยมือแบบนี้ก็ใช้ได้:
        if (personDto.getEmail() != null) existingPerson.setEmail(personDto.getEmail());
        if (personDto.getFirstName() != null) existingPerson.setFirstName(personDto.getFirstName());
        if (personDto.getLastName() != null) existingPerson.setLastName(personDto.getLastName());
        if (personDto.getIdCardNumber() != null) existingPerson.setIdCardNumber(personDto.getIdCardNumber());
        if (personDto.getPhoneNumber() != null) existingPerson.setPhoneNumber(personDto.getPhoneNumber());
        if (personDto.getAddress() != null) existingPerson.setAddress(personDto.getAddress());
        if (personDto.getPictureUrl() != null) existingPerson.setPictureUrl(personDto.getPictureUrl());
        if (personDto.getAccountStatus() != null) existingPerson.setAccountStatus(personDto.getAccountStatus());

        // 2. จัดการ Login (ถ้ามีการส่งข้อมูล Login มา)
        if (personDto.getLogin() != null && personDto.getLogin().getUsername() != null) {
            Optional<Login> currentLoginOptional = loginRepository.findById(personDto.getLogin().getUsername());

            if (currentLoginOptional.isPresent()) {
                Login currentLogin = currentLoginOptional.get();
                // *** อัปเดต password ของ Login ที่มีอยู่ (ควรมีการเข้ารหัส password ที่นี่) ***
                // currentLogin.setPassword(passwordEncoder.encode(personDto.getLogin().getPassword()));
                // loginRepository.save(currentLogin); // บันทึกการเปลี่ยนแปลงใน Login
                existingPerson.setLogin(currentLogin); // เชื่อมโยง Person กับ Login ที่อัปเดตแล้ว
            } else {
                // กรณีเปลี่ยน username หรือส่ง Login object ใหม่ที่ไม่ตรงกับที่มี
                // *** ควรมีการเข้ารหัส password ที่นี่ก่อนบันทึก ***
                Login newLogin = personMapper.toEntity(personDto).getLogin(); // แปลง LoginDTO เป็น Entity
                loginRepository.save(newLogin); // บันทึก Login ใหม่
                existingPerson.setLogin(newLogin);
            }
        }

        Person updatedPerson = personRepository.save(existingPerson);
        return personMapper.toDto(updatedPerson); // 3. แปลง Entity กลับเป็น DTO
    }

    @Override
    @Transactional
    public PersonDTO updatePersonPictureUrl(int id, String newBaseUrl) { // ⬅️ รับ/คืน DTO
        return personRepository.findById(id).map(person -> {
            String oldPictureUrl = person.getPictureUrl();
            if (oldPictureUrl != null && !oldPictureUrl.isEmpty()) {
                int lastSlashIndex = oldPictureUrl.indexOf("/maeban/files");
                if (lastSlashIndex != -1) {
                    String pathAndFile = oldPictureUrl.substring(lastSlashIndex);
                    String newPictureUrl = newBaseUrl + pathAndFile;
                    person.setPictureUrl(newPictureUrl);
                    return personMapper.toDto(personRepository.save(person)); // ⬅️ บันทึกและแปลงเป็น DTO
                }
            }
            return personMapper.toDto(person); // ⬅️ คืน DTO เดิมหากไม่มีการอัปเดต
        }).orElse(null);
    }

    @Override
    @Transactional
    public void deletePerson(int id) {
        personRepository.deleteById(id);
    }

    // -----------------------------------------------------
    // ⬅️ เมธอดที่ถูกเพิ่มเพื่อรองรับตรรกะใน PenaltyService
    // -----------------------------------------------------
    @Override
    @Transactional
    public void updateAccountStatus(int personId, String newStatus) {
        Person existingPerson = personRepository.findById(personId)
                .orElseThrow(() -> new RuntimeException("Person not found with ID: " + personId));

        if (newStatus != null && !newStatus.trim().isEmpty()) {
            existingPerson.setAccountStatus(newStatus);
            personRepository.save(existingPerson);
        }
    }

    // -----------------------------------------------------
    // ⬅️ เมธอดสำหรับอัปเดต URL ทั้งหมด
    // -----------------------------------------------------
    @Override
    @Transactional
    public void updateAllPersonPictureUrls(String newBaseUrl) {
        List<Person> allPersons = personRepository.findAll();
        for (Person person : allPersons) {
            String oldPictureUrl = person.getPictureUrl();
            if (oldPictureUrl != null && !oldPictureUrl.isEmpty()) {
                int lastSlashIndex = oldPictureUrl.indexOf("/maeban/files");
                if (lastSlashIndex != -1) {
                    String pathAndFile = oldPictureUrl.substring(lastSlashIndex);
                    String newPictureUrl = newBaseUrl + pathAndFile;
                    person.setPictureUrl(newPictureUrl);
                }
            }
        }
        personRepository.saveAll(allPersons);
    }
}