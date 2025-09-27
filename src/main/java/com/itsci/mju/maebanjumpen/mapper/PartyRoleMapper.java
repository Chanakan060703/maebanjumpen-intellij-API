// File: src/main/java/com/itsci/mju/maebanjumpen/mapper/PartyRoleMapper.java
package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.*;
import com.itsci.mju.maebanjumpen.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {PersonMapper.class}) // ⬅️ ต้อง uses PersonMapper ด้วย
public abstract class PartyRoleMapper {

    /**
     * Factory Method: แปลง PartyRole Entity (Abstract) ไปเป็น PartyRoleDTO (Abstract)
     * Method นี้จะตรวจสอบชนิดของ Entity และเรียกใช้เมธอด Subclass ที่เหมาะสม
     */
    public PartyRoleDTO toDto(PartyRole entity) {
        if (entity == null) return null;

        // ตรวจสอบชนิดของ Entity และเรียกใช้เมธอดเฉพาะของ Subclass
        if (entity instanceof Admin admin) return toAdminDto(admin);
        if (entity instanceof AccountManager accountManager) return toAccountManagerDto(accountManager);
        if (entity instanceof Housekeeper housekeeper) return toHousekeeperDto(housekeeper);
        if (entity instanceof Hirer hirer) return toHirerDto(hirer);
        if (entity instanceof Member member) return toMemberDto(member);

        throw new IllegalArgumentException("Cannot map unknown PartyRole type: " + entity.getClass().getSimpleName());
    }

    /**
     * Factory Method: แปลง PartyRoleDTO (Abstract) ไปเป็น PartyRole Entity (Abstract)
     * Method นี้จะตรวจสอบชนิดของ DTO และเรียกใช้เมธอด Subclass ที่เหมาะสม
     */
    public PartyRole toEntity(PartyRoleDTO dto) {
        if (dto == null) return null;

        // ตรวจสอบชนิดของ DTO (ใช้ instanceof เพราะ dto.type ไม่ได้มาจาก Getter/Field จริงๆ)
        if (dto instanceof AdminDTO adminDto) return toAdminEntity(adminDto);
        if (dto instanceof AccountManagerDTO accountManagerDto) return toAccountManagerEntity(accountManagerDto);
        if (dto instanceof HousekeeperDTO housekeeperDto) return toHousekeeperEntity(housekeeperDto);
        if (dto instanceof HirerDTO hirerDto) return toHirerEntity(hirerDto);
        if (dto instanceof MemberDTO memberDto) return toMemberEntity(memberDto);

        throw new IllegalArgumentException("Cannot map unknown PartyRoleDTO type: " + dto.getClass().getSimpleName());
    }

    // List mapping ใช้ Factory Method ด้านบน
    public List<PartyRoleDTO> toDtoList(List<PartyRole> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDto).collect(Collectors.toList());
    }

    // -----------------------------------------------------
    // Protected Abstract Methods for MapStruct to Implement
    // -----------------------------------------------------

    // Entity to DTO Mappings
    @Mapping(source = "person.login.username", target = "username")
    protected abstract MemberDTO toMemberDto(Member member);

    @Mapping(source = "person.login.username", target = "username")
    protected abstract HirerDTO toHirerDto(Hirer hirer);

    @Mapping(source = "person.login.username", target = "username")
    protected abstract HousekeeperDTO toHousekeeperDto(Housekeeper housekeeper);

    @Mapping(source = "person.login.username", target = "username")
    protected abstract AdminDTO toAdminDto(Admin admin);

    @Mapping(source = "person.login.username", target = "username")
    protected abstract AccountManagerDTO toAccountManagerDto(AccountManager accountManager);

    // DTO to Entity Mappings (ต้องมีการประกาศ Mappers ของ Subclass DTO/Entity ทั้งหมด)
    // ⚠️ MapStruct จะใช้ PersonMapper ในการจัดการ Person/PersonDTO

    protected abstract Member toMemberEntity(MemberDTO memberDto);
    protected abstract Hirer toHirerEntity(HirerDTO hirerDto);
    protected abstract Housekeeper toHousekeeperEntity(HousekeeperDTO housekeeperDto);
    protected abstract Admin toAdminEntity(AdminDTO adminDto);
    protected abstract AccountManager toAccountManagerEntity(AccountManagerDTO accountManagerDto);

    // -----------------------------------------------------
    // Optional: Shared Mappings (ถ้ามี)
    // -----------------------------------------------------
    // @InheritConfiguration(name = "baseToDto")
    // ...
}