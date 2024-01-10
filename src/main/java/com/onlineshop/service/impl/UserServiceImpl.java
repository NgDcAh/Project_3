package com.onlineshop.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlineshop.dto.UserDto;
import com.onlineshop.model.Product;
import com.onlineshop.model.Role;
import com.onlineshop.model.User;
import com.onlineshop.repository.UserRepository;
import com.onlineshop.repository.RoleRepository;
import com.onlineshop.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	@Override
    public User saveCustomer(UserDto customerDto) {
        User customer = new User();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setPassword(customerDto.getPassword());
        customer.setUsername(customerDto.getUsername());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        return userRepository.save(customer);
    }
	@Override
	public User saveStaff(UserDto staffDto) {
		User staff = new User();
        staff.setFirstName(staffDto.getFirstName());
        staff.setLastName(staffDto.getLastName());
        staff.setPhoneNumber(staffDto.getPhoneNumber());
        staff.setPassword(staffDto.getPassword());
        staff.setUsername(staffDto.getUsername());
        staff.setCountry(staffDto.getCountry());
        staff.setCity(staffDto.getCity());
        staff.setAddress(staffDto.getAddress());
        staff.setRoles(Arrays.asList(roleRepository.findByName("STAFF")));
        return userRepository.save(staff);
	}
	
//	@Override
//	public User saveAdmin(UserDto staffDto) {
//		User staff = new User();
//        staff.setFirstName(staffDto.getFirstName());
//        staff.setLastName(staffDto.getLastName());
//        staff.setPassword(staffDto.getPassword());
//        staff.setUsername(staffDto.getUsername());
//        staff.setRoles(Arrays.asList(roleRepository.findByName("ADMIN")));
//        return userRepository.save(staff);
//	}

	@Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserDto getUserDto(String username) {
        UserDto userDto = new UserDto();
        User user = userRepository.findByUsername(username);
        userDto.setUser_id(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setAddress(user.getAddress());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setCity(user.getCity());
        userDto.setCountry(user.getCountry());
        return userDto;
    }

    @Override
    public User changePass(UserDto customerDto) {
        User customer = userRepository.findByUsername(customerDto.getUsername());
        customer.setPassword(customerDto.getPassword());
        return userRepository.save(customer);
    }

    @Override
    public User update(UserDto dto) {
        User user = userRepository.findByUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setCountry(dto.getCountry());
        user.setPhoneNumber(dto.getPhoneNumber());
        return userRepository.save(user);
    }
	@Override
	public List<UserDto> findAllbyRole(String role) {
		List<UserDto> userDtos = new ArrayList<>();
		List<User> users = userRepository.findByRoles(role);
		for (User user : users) {
			UserDto userDto = new UserDto();
			userDto.setUser_id(user.getId());
			userDto.setFirstName(user.getFirstName());
			userDto.setLastName(user.getLastName());
			userDto.setPhoneNumber(user.getPhoneNumber());
			userDto.setAddress(user.getAddress());
			userDto.setCity(user.getCity());
			userDto.setCountry(user.getCountry());
			userDto.setUsername(user.getUsername());
			userDto.setPassword(user.getPassword());
			userDtos.add(userDto);
		}
		return userDtos;
	}
	@Override
	public UserDto getById(Long id) {
		UserDto userDto = new UserDto();
		User user = userRepository.findById(id).orElse(null);
		userDto.setUser_id(user.getId());
		userDto.setFirstName(user.getFirstName());
		userDto.setLastName(user.getLastName());
		userDto.setPhoneNumber(user.getPhoneNumber());
		userDto.setAddress(user.getAddress());
		userDto.setCity(user.getCity());
		userDto.setCountry(user.getCountry());
		userDto.setUsername(user.getUsername());
		userDto.setPassword(user.getPassword());
		return userDto;
	}
	
	@Override
	public Optional<User> findById(Long id) {
		return userRepository.findById(id);
	}
	
	@Override
	@Transactional
	public void remove(Long id) {
		userRepository.deleteUserRolesByUserId(id);
        userRepository.deleteUserById(id);
	}
	@Override
	public User saveAdmin(UserDto adminDto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<UserDto> getUsersByCityCountryAndRole(String city, String country, String role , Double minPrice, Double maxPrice, Integer minQuantity, Integer maxQuantity){
		List<UserDto> userDtos = new ArrayList<>();
		List<User> users = userRepository.findUsersByCityCountryAndRoleAndOrderDetails(city, country, role, minPrice, maxPrice, minQuantity, maxQuantity);
		for (User user : users) {
			UserDto userDto = new UserDto();
			userDto.setUser_id(user.getId());
			userDto.setFirstName(user.getFirstName());
			userDto.setLastName(user.getLastName());
			userDto.setPhoneNumber(user.getPhoneNumber());
			userDto.setAddress(user.getAddress());
			userDto.setCity(user.getCity());
			userDto.setCountry(user.getCountry());
			userDto.setUsername(user.getUsername());
			userDto.setPassword(user.getPassword());
			userDtos.add(userDto);
		}
		return userDtos;
	}
	
	public Object getTotalAmountAndProductsByUserId(Long id) {
		return userRepository.getTotalAmountAndProductsByUserId(id);
	}
	

	
}
