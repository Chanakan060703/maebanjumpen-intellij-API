package com.itsci.mju.maebanjumpen.service;

import com.itsci.mju.maebanjumpen.model.*;
import com.itsci.mju.maebanjumpen.repository.LoginRepository;
import com.itsci.mju.maebanjumpen.repository.PartyRoleRepository;
import com.itsci.mju.maebanjumpen.repository.PersonRepository;
import org.hibernate.Hibernate; // Import Hibernate for explicit initialization
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PartyRoleRepository partyRoleRepository;

    @Override
    @Transactional(readOnly = true) // Changed to readOnly as authentication is a read operation
    public PartyRole authenticate(String username, String password) {
        return findPartyRoleByLogin(username, password);
    }

    @Override
    @Transactional
    public Login saveLogin(Login login) {
        if (login.getPassword() == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        login.setPassword(PasswordUtil.hashPassword(login.getPassword()));
        return loginRepository.save(login);
    }

    @Override
    @Transactional(readOnly = true)
    public Login getLoginByUsername(String username) {
        return loginRepository.findById(username).orElse(null);
    }

    @Override
    @Transactional
    public void deleteLogin(String username) {
        loginRepository.deleteById(username);
    }

    @Override
    @Transactional
    public Login updateLogin(String username, Login login) {
        Optional<Login> existingLogin = loginRepository.findById(username);
        if (existingLogin.isPresent()) {
            Login updatedLogin = existingLogin.get();
            // ควรอัปเดตแค่ password ถ้า username เปลี่ยนไม่ได้
            updatedLogin.setPassword(PasswordUtil.hashPassword(login.getPassword()));
            return loginRepository.save(updatedLogin);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = true) // Changed to readOnly as this is primarily a read operation
    public PartyRole findPartyRoleByLogin(String username, String password) {
        System.out.println("-> [LoginService] Attempting login for username: " + username);
        Optional<Login> loginOpt = loginRepository.findByUsername(username);

        if (loginOpt.isPresent()) {
            Login login = loginOpt.get();
            System.out.println("-> [LoginService] Found Login data for " + username + ".");

            if (PasswordUtil.verifyPassword(password, login.getPassword())) {
                System.out.println("-> [LoginService] Password verification successful!");
                Person person = personRepository.findByLoginUsername(username);

                if (person != null) {
                    // Ensure Person's Login is fully loaded if it's LAZY (though it's EAGER now)
                    if (!Hibernate.isInitialized(person.getLogin())) {
                        Hibernate.initialize(person.getLogin());
                    }

                    System.out.println("-> [LoginService] Found Person data: " + person.getFirstName() + " (ID: " + person.getPersonId() + ")");
                    List<PartyRole> roles = partyRoleRepository.findByPerson(person);
                    System.out.println("-> [LoginService] Found linked PartyRoles: " + roles.size() + " roles");

                    if (!roles.isEmpty()) {
                        PartyRole foundRole = roles.get(0); // Get the first found role

                        // Initialize common LAZY collections here if needed
                        // For example, if PartyRole had LAZY collections directly.

                        // Handle specific PartyRole subclasses and initialize their LAZY collections
                        if (foundRole instanceof Member) { // Covers Member, Hirer, Housekeeper
                            Member member = (Member) foundRole;

                            // Initialize Transactions for Member (and its subclasses)
                            if (!Hibernate.isInitialized(member.getTransactions())) {
                                Hibernate.initialize(member.getTransactions());
                                System.out.println("-> [LoginService] Initialized transactions collection for Member/subclass. Count: " + member.getTransactions().size());
                            } else {
                                System.out.println("-> [LoginService] Member/subclass transactions collection already initialized or null.");
                            }

                            if (member instanceof Hirer) {
                                Hirer hirer = (Hirer) member;
                                System.out.println("-> [LoginService] Identified as Hirer instance.");

                                // Initialize Hirer's Hires
                                if (!Hibernate.isInitialized(hirer.getHires())) {
                                    Hibernate.initialize(hirer.getHires());
                                    System.out.println("-> [LoginService] Initialized hires collection for Hirer. Count: " + hirer.getHires().size());

                                    // For each Hire, initialize its associated Housekeeper (and its Person/Login/Skills) and Review
                                    for (Hire hire : hirer.getHires()) {
                                        if (!Hibernate.isInitialized(hire.getHousekeeper())) {
                                            Hibernate.initialize(hire.getHousekeeper());
                                            if (hire.getHousekeeper() != null) {
                                                // Initialize Housekeeper's Person and Login
                                                if (!Hibernate.isInitialized(hire.getHousekeeper().getPerson())) {
                                                    Hibernate.initialize(hire.getHousekeeper().getPerson());
                                                    if (hire.getHousekeeper().getPerson() != null && !Hibernate.isInitialized(hire.getHousekeeper().getPerson().getLogin())) {
                                                        Hibernate.initialize(hire.getHousekeeper().getPerson().getLogin());
                                                    }
                                                }
                                                // Initialize Housekeeper's Skills
                                                if (!Hibernate.isInitialized(hire.getHousekeeper().getHousekeeperSkills())) {
                                                    Hibernate.initialize(hire.getHousekeeper().getHousekeeperSkills());
                                                    for (HousekeeperSkill skill : hire.getHousekeeper().getHousekeeperSkills()) {
                                                        if (!Hibernate.isInitialized(skill.getSkillType())) {
                                                            Hibernate.initialize(skill.getSkillType());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (!Hibernate.isInitialized(hire.getReview())) {
                                            Hibernate.initialize(hire.getReview());
                                        }
                                    }
                                } else {
                                    System.out.println("-> [LoginService] Hirer's hires collection already initialized or null.");
                                }
                                // No need to set username field, as it's removed from Hirer. PartyRole.getUsername() is sufficient.
                                return hirer;

                            } else if (member instanceof Housekeeper) {
                                Housekeeper housekeeper = (Housekeeper) member;
                                System.out.println("-> [LoginService] Identified as Housekeeper instance.");

                                // Initialize Housekeeper's Hires
                                if (!Hibernate.isInitialized(housekeeper.getHires())) {
                                    Hibernate.initialize(housekeeper.getHires());
                                    System.out.println("-> [LoginService] Initialized hires collection for Housekeeper. Count: " + housekeeper.getHires().size());

                                    // For each Hire, initialize its associated Hirer (and its Person/Login) and Review
                                    for (Hire hire : housekeeper.getHires()) {
                                        if (!Hibernate.isInitialized(hire.getHirer())) {
                                            Hibernate.initialize(hire.getHirer());
                                            if (hire.getHirer() != null) {
                                                if (!Hibernate.isInitialized(hire.getHirer().getPerson())) {
                                                    Hibernate.initialize(hire.getHirer().getPerson());
                                                    if (hire.getHirer().getPerson() != null && !Hibernate.isInitialized(hire.getHirer().getPerson().getLogin())) {
                                                        Hibernate.initialize(hire.getHirer().getPerson().getLogin());
                                                    }
                                                }
                                            }
                                        }
                                        if (!Hibernate.isInitialized(hire.getReview())) {
                                            Hibernate.initialize(hire.getReview());
                                        }
                                    }
                                } else {
                                    System.out.println("-> [LoginService] Housekeeper's hires collection already initialized or null.");
                                }

                                // Initialize Housekeeper's Skills
                                if (!Hibernate.isInitialized(housekeeper.getHousekeeperSkills())) {
                                    Hibernate.initialize(housekeeper.getHousekeeperSkills());
                                    System.out.println("-> [LoginService] Initialized housekeeperSkills collection for Housekeeper. Count: " + housekeeper.getHousekeeperSkills().size());
                                    for (HousekeeperSkill skill : housekeeper.getHousekeeperSkills()) {
                                        if (!Hibernate.isInitialized(skill.getSkillType())) {
                                            Hibernate.initialize(skill.getSkillType());
                                        }
                                    }
                                } else {
                                    System.out.println("-> [LoginService] Housekeeper's housekeeperSkills collection already initialized or null.");
                                }
                                // No need to set username field, as it's removed from Housekeeper. PartyRole.getUsername() is sufficient.
                                return housekeeper;

                            } else { // Generic Member (not Hirer or Housekeeper)
                                System.out.println("-> [LoginService] Identified as generic Member instance.");
                                return member;
                            }
                        } else if (foundRole instanceof Admin) {
                            System.out.println("-> [LoginService] Identified as Admin instance.");
                            return foundRole; // Admin might not have specific LAZY collections to initialize
                        } else if (foundRole instanceof AccountManager) {
                            System.out.println("-> [LoginService] Identified as AccountManager instance.");
                            return foundRole; // AccountManager might not have specific LAZY collections to initialize
                        } else {
                            System.err.println("-> [LoginService] ERROR: Found PartyRole is an unknown type: " + foundRole.getClass().getSimpleName());
                            return null;
                        }

                    } else {
                        System.out.println("-> [LoginService] ERROR: No PartyRole found for Person ID: " + person.getPersonId());
                    }
                } else {
                    System.out.println("-> [LoginService] ERROR: No Person data found for username: " + username);
                }
            } else {
                System.out.println("-> [LoginService] ERROR: Incorrect password for username: " + username);
            }
        } else {
            System.out.println("-> [LoginService] ERROR: No Login data found for username: " + username);
        }
        System.out.println("-> [LoginService] Authentication failed. Returning null.");
        return null;
    }
}