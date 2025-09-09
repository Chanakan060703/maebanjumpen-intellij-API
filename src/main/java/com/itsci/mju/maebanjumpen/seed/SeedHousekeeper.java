package com.itsci.mju.maebanjumpen.seed;

import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Component
public class SeedHousekeeper implements CommandLineRunner {

  private final PersonRepository personRepository;
  private final HousekeeperRepository housekeeperRepository;
  private final SkillTypeRepository skillTypeRepository;
  private final HousekeeperSkillRepository housekeeperSkillRepository;

  public SeedHousekeeper(PersonRepository personRepository,
      HousekeeperRepository housekeeperRepository,
      SkillTypeRepository skillTypeRepository,
      HousekeeperSkillRepository housekeeperSkillRepository) {
    this.personRepository = personRepository;
    this.housekeeperRepository = housekeeperRepository;
    this.skillTypeRepository = skillTypeRepository;
    this.housekeeperSkillRepository = housekeeperSkillRepository;
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    // avoid seeding when data already exists
    if (housekeeperRepository.count() > 0) {
      return;
    }

    // Create some skill types
    List<SkillType> skillTypes = new ArrayList<>();
    skillTypes.add(new SkillType(null, "Cleaning", "General home cleaning and tidying"));
    skillTypes.add(new SkillType(null, "Cooking", "Basic to advanced cooking and meal prep"));
    skillTypes.add(new SkillType(null, "Babysitting", "Childcare and babysitting"));
    skillTypes.add(new SkillType(null, "Elderly Care", "Care and assistance for elderly people"));
    skillTypes.add(new SkillType(null, "Pet Care", "Taking care of pets and cleaning up after them"));
    skillTypeRepository.saveAll(skillTypes);

    // sample names and data
    String[][] sample = new String[][] {
        { "wanida.p", "Wanida", "Sukhum", "wanida.p@example.com" },
        { "napat.k", "Napat", "Kham", "napat.k@example.com" },
        { "siri.t", "Siri", "Thong", "siri.t@example.com" },
        { "anchan.a", "Anchan", "Aree", "anchan.a@example.com" },
        { "preecha.r", "Preecha", "Rung", "preecha.r@example.com" },
        { "kamon.p", "Kamon", "Phan", "kamon.p@example.com" },
        { "malee.c", "Malee", "Chai", "malee.c@example.com" },
        { "somchai.d", "Somchai", "Daeng", "somchai.d@example.com" },
        { "rupa.s", "Rupa", "Saeng", "rupa.s@example.com" },
        { "thida.b", "Thida", "Boon", "thida.b@example.com" }
    };

    Random rnd = new Random(12345);

    for (int i = 0; i < sample.length; i++) {
      String username = sample[i][0];
      String firstName = sample[i][1];
      String lastName = sample[i][2];
      String email = sample[i][3];

      // create Person with Login
      Person person = new Person();
      person.setEmail(email);
      person.setFirstName(firstName);
      person.setLastName(lastName);
      // simple unique idCardNumber and phone
      person.setIdCardNumber(String.format("%013d", 880000000000L + i));
      person.setPhoneNumber(String.format("0%09d", 800000000 + i));
      person.setAddress("123/" + (i + 1) + " Sukhumvit Rd, Bangkok");
      person.setPictureUrl("/uploads/profile_pictures/sample-" + (i + 1) + ".jpg");
      person.setAccountStatus("ACTIVE");

      // create login (saved via cascade when saving person)
      Login login = new Login(username, "password123");
      person.setLogin(login);

      personRepository.save(person);

      // create housekeeper
      Housekeeper hk = new Housekeeper();
      hk.setPerson(person);
      // alternate verified / not verified
      hk.setStatusVerify(i % 3 == 0 ? "verified" : (i % 3 == 1 ? "not verified" : null));
      hk.setPhotoVerifyUrl("/verify_photos/verify-" + (i + 1) + ".jpg");
      hk.setRating(Math.round((3.0 + rnd.nextDouble() * 2.0) * 10.0) / 10.0); // 3.0-5.0 one decimal
      hk.setDailyRate(250.0 + rnd.nextInt(751)); // 250 - 1000

      housekeeperRepository.save(hk);

      // assign 1-3 skills
      int skillCount = 1 + rnd.nextInt(3);
      Set<HousekeeperSkill> created = new HashSet<>();
      for (int s = 0; s < skillCount; s++) {
        SkillType st = skillTypes.get(rnd.nextInt(skillTypes.size()));
        HousekeeperSkill hks = new HousekeeperSkill();
        hks.setHousekeeper(hk);
        hks.setSkillType(st);
        // skill levels: Beginner, Intermediate, Expert
        String[] levels = new String[] { "Beginner", "Intermediate", "Expert" };
        hks.setSkillLevel(levels[rnd.nextInt(levels.length)]);
        housekeeperSkillRepository.save(hks);
        created.add(hks);
      }

      // attach skills to housekeeper and save
      hk.getHousekeeperSkills().addAll(created);
      housekeeperRepository.save(hk);
    }
  }
}
