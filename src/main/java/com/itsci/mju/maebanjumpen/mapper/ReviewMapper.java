package com.itsci.mju.maebanjumpen.mapper;

import com.itsci.mju.maebanjumpen.dto.ReviewDTO;
import com.itsci.mju.maebanjumpen.model.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(source = "hire.hireId", target = "hireId")
    ReviewDTO toDto(Review review);

    @Mapping(source = "hireId", target = "hire.hireId")
    Review toEntity(ReviewDTO reviewDto);

    List<ReviewDTO> toDtoList(List<Review> reviews);
    List<Review> toEntityList(List<ReviewDTO> reviewDtos);
}
