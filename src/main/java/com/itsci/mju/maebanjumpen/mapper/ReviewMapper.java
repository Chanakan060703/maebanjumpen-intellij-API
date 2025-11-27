package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.review.dto.ReviewDTO;
import com.itsci.mju.maebanjumpen.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {HirerMapper.class, PersonMapper.class /* และ Mapper อื่นๆ ที่ Review อาจต้องใช้ */})
public interface ReviewMapper {


    @Mapping(target = "hirerPictureUrl", source = "hire.hirer.person.pictureUrl")
    @Mapping(target = "hirerFirstName", source = "hire.hirer.person.firstName")
    @Mapping(target = "hirerLastName", source = "hire.hirer.person.lastName")
    @Mapping(target = "hireId", source = "hire.hireId")
    ReviewDTO toDto(Review review);

    Review toEntity(ReviewDTO reviewDto);

    List<ReviewDTO> toDtoList(List<Review> reviews);
    List<Review> toEntityList(List<ReviewDTO> reviewDtos);
}