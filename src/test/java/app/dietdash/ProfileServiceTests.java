package app.dietdash;

import app.dietdash.dto.UserDto;
import app.dietdash.model.Profile;
import app.dietdash.model.User;
import app.dietdash.repository.ProfileRepository;
import app.dietdash.service.ProfileService;
import app.dietdash.service.UtilService;
import app.dietdash.service.mapper.UserMapper;
import exceptions.AppException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProfileServiceTests {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UtilService utilService;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private ProfileService profileService;

    private UserDto userDto;
    private User user;
    private Profile profile;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userDto = new UserDto();
        userDto.setId(1L);

        user = new User();
        user.setId(1L);

        profile = new Profile();
        profile.setId(UUID.randomUUID());
        profile.setName("Test Profile");
        profile.setUser(user);

        pageable = mock(Pageable.class);
    }


    @Test
    void testGetProfileById_shouldReturnProfile() {
        UUID profileId = profile.getId();

        // Мокируем поведение profileRepository
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        Profile result = profileService.getProfileById(profileId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(profileId);
    }

    @Test
    void testGetProfileById_shouldThrowException_whenProfileNotFound() {
        UUID profileId = UUID.randomUUID();

        // Мокируем, что профиль не найден
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        AppException thrown = catchThrowableOfType(() -> profileService.getProfileById(profileId), AppException.class);

        assertThat(thrown).isNotNull();
        assertThat(thrown.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(thrown.getMessage()).contains("Unknown profile with id");
    }

    @Test
    void testUpdateProfile_shouldUpdateProfile() {
        UUID profileId = profile.getId();
        Profile updatedProfile = new Profile();
        updatedProfile.setId(profileId);
        updatedProfile.setName("Updated Profile");

        // Мокируем, что профиль найден
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        // Мокируем сохранение обновленного профиля
        //when(profileRepository.save(any(Profile.class))).thenReturn(updatedProfile);

        profileService.updateProfile(profileId, updatedProfile);

        // Проверяем, что профиль был обновлен
        verify(profileRepository, times(1)).save(updatedProfile);
    }

    @Test
    void testDeleteProfile_shouldDeleteProfile() {
        UUID profileId = profile.getId();

        // Мокируем, что профиль найден
        when(profileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        profileService.deleteProfile(profileId);

        // Проверяем, что профиль был удален
        verify(profileRepository, times(1)).deleteById(profileId);
    }

    @Test
    void testAddProfile_shouldSaveProfile() {
        when(utilService.getUserFromPrincipal(userDto)).thenReturn(user);
        when(profileRepository.save(profile)).thenReturn(profile);

        UUID profileId = profileService.addProfile(profile, userDto);

        assertThat(profileId).isNotNull();
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void testUpdateProfile_shouldThrowException_whenProfileNotFound() {
        UUID profileId = UUID.randomUUID();
        Profile updatedProfile = new Profile();
        updatedProfile.setId(profileId);
        updatedProfile.setName("Updated Profile");

        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        AppException thrown = catchThrowableOfType(() -> profileService.updateProfile(profileId, updatedProfile), AppException.class);

        assertThat(thrown).isNotNull();
        assertThat(thrown.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(thrown.getMessage()).contains("Unknown profile with id");
    }

    @Test
    void testDeleteProfile_shouldThrowException_whenProfileNotFound() {
        UUID profileId = UUID.randomUUID();
        when(profileRepository.findById(profileId)).thenReturn(Optional.empty());

        AppException thrown = catchThrowableOfType(() -> profileService.deleteProfile(profileId), AppException.class);

        assertThat(thrown).isNotNull();
        assertThat(thrown.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(thrown.getMessage()).contains("Unknown profile with id");
    }
}