package ru.vlsu.ispi.movieproject.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ru.vlsu.ispi.movieproject.dto.user.EditProfileRequest;
import ru.vlsu.ispi.movieproject.dto.user.UserDto;

import java.util.List;

public interface UserService {
    Page<UserDto> searchUsers(String query, String sortOrder, int page, int size);
    UserDto getCurrentUser();
    UserDto getUserById(Long id);
    UserDto getUserByUsername(String username);
    UserDto updateAvatar(MultipartFile file);
    UserDto updateAvatarByUserId(Long userId, MultipartFile file);
    UserDto updateProfile(EditProfileRequest request);
    UserDto updateProfileByUserId(Long userId, EditProfileRequest request);
    void deleteProfile();
    void deleteUserById(Long id);
    void follow(Long followedId);
    void unfollow(Long followedId);
    List<UserDto> getFollowers();
    List<UserDto> getFollowings();
    List<UserDto> getFollowersByUsername(String username);
    List<UserDto> getFollowingsByUsername(String username);
}
