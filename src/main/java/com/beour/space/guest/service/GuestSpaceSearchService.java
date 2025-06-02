package com.beour.space.guest.service;

import com.beour.global.exception.exceptionType.InputInvalidFormatException;
import com.beour.global.exception.exceptionType.SpaceNotFoundException;
import com.beour.space.domain.entity.Space;
import com.beour.space.domain.repository.SpaceRepository;
import com.beour.space.guest.dto.FilteringSearchRequestDto;
import com.beour.space.guest.dto.SearchSpaceResponseDto;
import com.beour.space.host.enums.SpaceCategory;
import com.beour.space.host.enums.UseCategory;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GuestSpaceSearchService {

    private final SpaceRepository spaceRepository;

    public List<Space> search(String keyword){
        if(keyword.isEmpty()){
            throw new InputInvalidFormatException("키워드를 입력해주세요");
        }

        List<Space> result = spaceRepository.searchByKeyword("%" + keyword + "%");
        if(result.isEmpty()){
            throw new SpaceNotFoundException("키워드에 해당되는 공간이 없습니다.");
        }


        return result;
    }

    public List<SearchSpaceResponseDto> searchWithFiltering(FilteringSearchRequestDto requestDto){
        List<Space> spaceListWithKeyword = search(requestDto.getKeyword());
        List<Space> filtering = filterSpaces(spaceListWithKeyword, requestDto);

        if(filtering.isEmpty()){
            throw new SpaceNotFoundException("해당 조건에 일치하는 공간이 없습니다.");
        }

        return changeToSearchResponseDtoFrom(filtering);
    }

    private List<Space> filterSpaces(List<Space> spaceListWithKeyword, FilteringSearchRequestDto requestDto) {
        return spaceListWithKeyword.stream()
            .filter(space -> space.getDeletedAt() == null)

            .filter(space -> space.getPricePerHour() >= requestDto.getMinPrice())
            .filter(space -> requestDto.getMaxPrice() == 0 || space.getPricePerHour() <= requestDto.getMaxPrice())

            .filter(space -> requestDto.getAddress() == null ||
                space.getAddress() != null && space.getAddress().contains(requestDto.getAddress()))

            .filter(space -> requestDto.getMinCapacity() == 0 || space.getMaxCapacity() >= requestDto.getMinCapacity())

            .filter(space -> requestDto.getSpaceCategories() == null || requestDto.getSpaceCategories().isEmpty() ||
                requestDto.getSpaceCategories().contains(space.getSpaceCategory()))

            .filter(space -> requestDto.getUseCategories() == null || requestDto.getUseCategories().isEmpty() ||
                requestDto.getUseCategories().contains(space.getUseCategory()))

            .filter(space -> requestDto.getDate() == null ||
                space.getAvailableTimes().stream()
                    .anyMatch(at -> at.getDeletedAt() == null && requestDto.getDate().equals(at.getDate())))

            .collect(Collectors.toList());
    }

    public List<SearchSpaceResponseDto> searchSpaceWithSpaceCategory(SpaceCategory request){
        List<Space> space = spaceRepository.findBySpaceCategory(request);

        if(space.isEmpty()){
            throw new SpaceNotFoundException("해당 유형의 공간은 존재하지 않습니다.");
        }

        return changeToSearchResponseDtoFrom(space);
    }

    public List<SearchSpaceResponseDto> searchSpaceWithUseCategory(UseCategory request){
        List<Space> space = spaceRepository.findByUseCategory(request);

        if(space.isEmpty()){
            throw new SpaceNotFoundException("해당 유형의 공간은 존재하지 않습니다.");
        }

        return changeToSearchResponseDtoFrom(space);
    }

    private static List<SearchSpaceResponseDto> changeToSearchResponseDtoFrom(List<Space> space) {
        return space.stream()
            .map(SearchSpaceResponseDto::of)
            .toList();
    }

}
