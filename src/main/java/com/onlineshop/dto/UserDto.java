package com.onlineshop.dto;


import com.onlineshop.model.City;
import com.onlineshop.model.Country;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
	private Long user_id;
	
	@Size(min = 3, max = 10, message = "First name contains 3-10 characters")
    private String firstName;

    @Size(min = 3, max = 10, message = "Last name contains 3-10 characters")
    private String lastName;
    private String username;
    @Size(min = 3, max = 15, message = "Password contains 3-10 characters")
    private String password;

    @Size(min = 10, max = 15, message = "Phone number contains 10-15 characters")
    private String phoneNumber;

    private String confirmPassword;
    private String address;
    private City city;
    private String image;
    private Country country;

}
