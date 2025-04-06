package app.dietdash.service;

import app.dietdash.appuser.UserRole;
import app.dietdash.dto.ActivityDto;
import app.dietdash.dto.ProfileDto;
import app.dietdash.dto.ProfileNameDto;
import app.dietdash.dto.ProfileUpdateDto;
import app.dietdash.dto.SimpleProfileDto;
import app.dietdash.dto.UserDto;
import app.dietdash.enums.Activity;
import app.dietdash.model.Profile;
import app.dietdash.model.User;
import app.dietdash.repository.ProfileRepository;
import app.dietdash.service.mapper.UserMapper;
import exceptions.AppException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static app.dietdash.dto.ActivityDto.toActivityDto;

@Service
@AllArgsConstructor
public class ProfileService {
    private final ProfileRepository profileRepository;
    private final UtilService utilService;
    private final UserMapper mapper;

    public List<ActivityDto> getAllActivities(String lang) {
        return Arrays.stream(Activity.values()).map(x -> toActivityDto(x, lang)).toList();
    }

    public List<ProfileNameDto> getAllProfiles(final String term, final UserDto principal) {
        return utilService.getUserFromPrincipal(principal).getProfileList()
                .stream()
                .filter(x -> term == null || x.getName().toLowerCase().contains(term.toLowerCase()))
                .map(mapper::toProfileNameDto)
                .toList();
    }

    public Page<SimpleProfileDto> getProfileList(final String term, final UserDto principal, final Pageable pageable) {
        List<SimpleProfileDto> filteredProfiles = utilService.getUserFromPrincipal(principal).getProfileList()
                .stream()
                .filter(x -> term == null || x.getName().toLowerCase().contains(term.toLowerCase()))
                .map(mapper::toProfileDto)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredProfiles.size());

        List<SimpleProfileDto> pagedList = (start > filteredProfiles.size()) ? List.of() : filteredProfiles.subList(start, end);

        return new PageImpl<>(pagedList, pageable, filteredProfiles.size());
    }

    public Profile getProfileById(final UUID id) {
        return profileRepository.findById(id).orElseThrow(() -> new AppException("Unknown profile with id: " + id, HttpStatus.NOT_FOUND));
    }

    public ProfileDto getProfileDtoById(final UUID id, String lang) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException("Unknown profile with id: " + id, HttpStatus.NOT_FOUND));

        return ProfileDto.toProfileDto(profile, lang);
    }

    @Transactional
    public UUID addProfile(final Profile profile, final UserDto principal) {
        final User user = utilService.getUserFromPrincipal(principal);

        if (user.getUserRole() == UserRole.USER && !user.getProfileList().isEmpty()) {
            throw new AppException("Only PRO users can create more than 1 profile", HttpStatus.PAYMENT_REQUIRED);
        }

        profile.setId(UUID.randomUUID());
        profile.setUser(user);
        Profile entity = profileRepository.save(profile);
        user.addProfile(entity);

        return entity.getId();
    }

    @Transactional
    public void updateProfile(final UUID id, final Profile profile) {
        Profile oldProfile = profileRepository.findById(id)
                .orElseThrow(() -> new AppException("Unknown profile with id: " + profile.getId(), HttpStatus.NOT_FOUND));
        profile.setId(oldProfile.getId());
        profile.setUser(oldProfile.getUser());
        profileRepository.save(profile);
    }

    @Transactional
    public void deleteProfile(UUID id) {
        profileRepository.findById(id)
                .orElseThrow(() -> new AppException("Unknown profile with id: " + id, HttpStatus.NOT_FOUND));

        profileRepository.deleteById(id);
    }

    @Transactional
    public void updateProfilePartially(UUID id, ProfileUpdateDto dto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        if (dto.getName() != null) profile.setName(dto.getName());
        if (dto.getAge() != null) profile.setAge(dto.getAge());
        if (dto.getGender() != null) profile.setGender(dto.getGender());
        if (dto.getHeight() != null) profile.setHeight(dto.getHeight());
        if (dto.getWeight() != null) profile.setWeight(dto.getWeight());
        if (dto.getWaist() != null) profile.setWaist(dto.getWaist());
        if (dto.getHips() != null) profile.setHips(dto.getHips());
        if (dto.getGoalWeight() != null) profile.setGoalWeight(dto.getGoalWeight());
        if (dto.getActivity() != null) profile.setActivity(dto.getActivity());
        if (dto.getBodyMassIndex() != null) profile.setBodyMassIndex(dto.getBodyMassIndex());
        if (dto.getDailyCalorieNorm() != null) profile.setDailyCalorieNorm(dto.getDailyCalorieNorm());
        if (dto.getSelectedCalorieLimit() != null) profile.setSelectedCalorieLimit(dto.getSelectedCalorieLimit());
        if (dto.getDailyProteinLimit() != null) profile.setDailyProteinLimit(dto.getDailyProteinLimit());
        if (dto.getDailyFatLimit() != null) profile.setDailyFatLimit(dto.getDailyFatLimit());
        if (dto.getDailyCarbsLimit() != null) profile.setDailyCarbsLimit(dto.getDailyCarbsLimit());
        if (dto.getDailyProteinPercentage() != null) profile.setDailyProteinPercentage(dto.getDailyProteinPercentage());
        if (dto.getDailyFatPercentage() != null) profile.setDailyFatPercentage(dto.getDailyFatPercentage());
        if (dto.getDailyCarbsPercentage() != null) profile.setDailyCarbsPercentage(dto.getDailyCarbsPercentage());

        profileRepository.save(profile);
    }
}
