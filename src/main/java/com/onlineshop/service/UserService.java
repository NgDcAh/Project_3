package com.onlineshop.service;

import java.util.List;
import java.util.Optional;

import com.onlineshop.dto.UserDto;
import com.onlineshop.model.User;

public interface UserService {
	User saveCustomer(UserDto customerDto);
	
	User saveStaff(UserDto staffDto);
	
	User saveAdmin(UserDto adminDto);

	User findByUsername(String username);

	User update(UserDto userDto);

	User changePass(UserDto userDto);

	UserDto getUserDto(String username);

	List<UserDto> findAllbyRole(String role);
	
	UserDto getById(Long id);
	
	Optional<User> findById(Long id);

	void remove(Long id);
}
