package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.Person;
import com.itsci.mju.maebanjumpen.model.Login; // เพิ่ม import สำหรับ Login
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import com.itsci.mju.maebanjumpen.repository.LoginRepository; // เพิ่ม import สำหรับ LoginRepository
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // เพิ่ม import สำหรับ @Transactional

import java.util.List;
import java.util.Optional;

@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private LoginRepository loginRepository; // ยังคงต้องการ LoginRepository สำหรับการค้นหา Login ที่มีอยู่

    @Override
    public List<Person> getAllPersons() {
        return personRepository.findAll();
    }

    @Override
    public Person getPersonById(int id) {
        // เมธอดนี้ควรอยู่ใน Transactional context (โดยปกติ JpaRepository จะจัดการให้)
        // และ FetchType.EAGER ควรทำให้ Login ถูกโหลดมาด้วย
        return personRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional // สำคัญมากสำหรับเมธอดที่เกี่ยวข้องกับการเขียนข้อมูล
    public Person savePerson(Person person) {
        if (person.getLogin() != null && person.getLogin().getUsername() != null) {
            // ตรวจสอบว่า Login นี้มีอยู่แล้วหรือไม่
            Optional<Login> existingLogin = loginRepository.findById(person.getLogin().getUsername());

            if (existingLogin.isPresent()) {
                // ถ้ามีอยู่แล้ว ให้ใช้ Login object ที่ดึงมาจาก DB
                // เพื่อหลีกเลี่ยงการสร้าง Login ซ้ำซ้อน และเชื่อมโยง Person กับ Login ที่มีอยู่
                person.setLogin(existingLogin.get());

                // **** สำคัญ: หากมีการอัปเดต password ใน Login ที่มีอยู่
                // ต้องทำการอัปเดต password บน existingLogin.get() ด้วย
                // และทำการ hash password ใหม่ที่นี่
                // existingLogin.get().setPassword(passwordEncoder.encode(person.getLogin().getPassword()));
                // loginRepository.save(existingLogin.get()); // บันทึกการเปลี่ยนแปลงใน Login ที่มีอยู่
            } else {
                // ถ้า Login ไม่มีอยู่,
                // **ก่อนบันทึก Login object ใหม่ ควรมีการเข้ารหัส password ที่นี่**
                // Login newLogin = person.getLogin();
                // newLogin.setPassword(passwordEncoder.encode(newLogin.getPassword())); // ตัวอย่างการเข้ารหัส password
                // ไม่ต้องเรียก loginRepository.save(newLogin); ตรงนี้ หากใช้ CascadeType.ALL
                // เพราะ personRepository.save(person) จะจัดการให้เอง
            }
        }
        // เมื่อถึงตรงนี้ Hibernate จะบันทึก Login (ถ้าเป็น newLogin)
        // หรือเชื่อมโยง Person กับ Login ที่มีอยู่ (ถ้าเป็น existingLogin)
        return personRepository.save(person);
    }

    @Override
    @Transactional
    public void deletePerson(int id) {
        // หากต้องการลบ Login ด้วยเมื่อ Person ถูกลบ และใช้ CascadeType.ALL บน Login field ใน Person
        // Hibernate จะจัดการให้เองเมื่อ personRepository.deleteById(id) ถูกเรียก
        personRepository.deleteById(id);
    }

    @Override
    public Person getPersonByUsername(String username) {
        // เมธอดนี้ควรอยู่ใน Transactional context (โดยปกติ JpaRepository จะจัดการให้)
        // และ FetchType.EAGER ควรทำให้ Login ถูกโหลดมาด้วย
        return personRepository.findByLoginUsername(username);
    }

    @Override
    @Transactional
    public Person updatePerson(int id, Person person) {
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Person with ID " + id + " not found"));

        existingPerson.setEmail(person.getEmail());
        existingPerson.setFirstName(person.getFirstName());
        existingPerson.setLastName(person.getLastName());
        existingPerson.setIdCardNumber(person.getIdCardNumber());
        existingPerson.setPhoneNumber(person.getPhoneNumber());
        existingPerson.setAddress(person.getAddress());
        existingPerson.setPictureUrl(person.getPictureUrl());
        existingPerson.setAccountStatus(person.getAccountStatus());

        if (person.getLogin() != null) {
            Login updatedLogin = person.getLogin();
            Optional<Login> currentLoginOptional = loginRepository.findById(updatedLogin.getUsername());

            if (currentLoginOptional.isPresent()) {
                Login currentLogin = currentLoginOptional.get();
                // อัปเดต password ของ Login ที่มีอยู่ (ควรมีการเข้ารหัส password ที่นี่)
                currentLogin.setPassword(updatedLogin.getPassword()); // ตัวอย่าง: ควรเข้ารหัส
                loginRepository.save(currentLogin); // บันทึกการเปลี่ยนแปลงใน Login
                existingPerson.setLogin(currentLogin); // เชื่อมโยง Person กับ Login ที่อัปเดตแล้ว
            } else {
                // กรณีมีการเปลี่ยน username ใหม่ หรือส่ง Login object ใหม่ที่ไม่ตรงกับที่มี
                // ควรมีการเข้ารหัส password ที่นี่ก่อนบันทึก
                // updatedLogin.setPassword(passwordEncoder.encode(updatedLogin.getPassword()));
                loginRepository.save(updatedLogin); // บันทึก Login ใหม่
                existingPerson.setLogin(updatedLogin);
            }
        }

        return personRepository.save(existingPerson);
    }
}