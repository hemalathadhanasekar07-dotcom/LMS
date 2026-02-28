package com.spinsoft.lms.Services;

import com.spinsoft.lms.DTO.UserDto;
import com.spinsoft.lms.Entities.OrganizationEntity;
import com.spinsoft.lms.Entities.UserEntity;
import com.spinsoft.lms.Repository.OrganizationRepository;
import com.spinsoft.lms.Repository.UserRepository;
import com.spinsoft.lms.enums.UserStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServices {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository orgRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    public UserEntity dtoToEntity(UserDto userDto) throws Exception {
       OrganizationEntity organizationEntity= organizationRepository.findById(userDto.getOrganizationId()).orElseThrow( () ->
               new Exception("Organization not found"));
        UserEntity userEntity = new UserEntity();
        userEntity.setId(userDto.getId());
        userEntity.setUserName(userDto.getUserName());
        userEntity.setPassword(userDto.getPassword());
        userEntity.setEmail(userDto.getEmail());
        userEntity.setOrganization(organizationEntity);
        userEntity.setRole(userDto.getRole());
        userEntity.setName(userDto.getName());
        return userEntity;

    }

    public UserDto entityToDto(UserEntity userEntity) {
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId());
        userDto.setUserName(userEntity.getUserName());
        userDto.setPassword(userEntity.getPassword());
        userDto.setEmail(userEntity.getEmail());
        userDto.setRole(userEntity.getRole());
        userDto.setName(userEntity.getName());
        userDto.setOrganizationId(userEntity.getOrganization().getId());
        userDto.setCreatedBy(userEntity.getCreatedBy());
        userDto.setModifiedBy(userEntity.getModifiedBy());
        userDto.setStatus(userEntity.getStatus().toString());
        userDto.setCreatedBy(userEntity.getCreatedBy());
        userDto.setModifiedBy(userEntity.getModifiedBy());
        return userDto;


    }

    public UserDto createUser(UserDto userdto) throws Exception {
    UserEntity user = dtoToEntity(userdto);
     userRepository.save(user);

     user.setCreatedBy(user.getId());
     user.setModifiedBy(user.getId());
     return entityToDto(user);

    }

    public UserDto updateUser(Long id,UserDto userdto) throws Exception {
        UserEntity user = userRepository.findById(id).orElseThrow(()->new Exception("User not found"));
        OrganizationEntity organizationEntity= organizationRepository.findById(userdto.getOrganizationId()).orElseThrow( () ->
                new Exception("Organization not found"));
        user.setUserName(userdto.getUserName());
        user.setPassword(userdto.getPassword());
        user.setEmail(userdto.getEmail());
        user.setRole(userdto.getRole());
        user.setName(userdto.getName());
        user.setOrganization(organizationEntity);


        userRepository.save(user);
        return entityToDto(user);}

    public UserDto deleteUser(long id,UserDto userdto){
        userRepository.deleteById(id);
        }


    }

}
