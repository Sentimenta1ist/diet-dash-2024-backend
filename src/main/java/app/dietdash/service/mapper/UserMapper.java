package app.dietdash.service.mapper;

import app.dietdash.dto.ActivityDto;
import app.dietdash.dto.ProfileNameDto;
import app.dietdash.dto.SignUpDto;
import app.dietdash.dto.UserDto;
import app.dietdash.enums.Activity;
import app.dietdash.model.Profile;
import app.dietdash.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);

    ProfileNameDto toProfileDto(Profile profile);

    ActivityDto toActivityDto(Activity activity);
}