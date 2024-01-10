package com.onlineshop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlineshop.model.Country;
import com.onlineshop.repository.CountryRepository;
import com.onlineshop.service.CountryService;

@Service
public class CountryServiceImpl implements CountryService{
	@Autowired
	private CountryRepository countryRepository;

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }
}
