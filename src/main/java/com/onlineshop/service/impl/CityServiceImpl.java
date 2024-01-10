package com.onlineshop.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.onlineshop.model.City;
import com.onlineshop.repository.CityRepository;
import com.onlineshop.service.CityService;

@Service
public class CityServiceImpl implements CityService{
	@Autowired
	private CityRepository cityRepository;

    @Override
    public List<City> findAll() {
        return cityRepository.findAll();
    }
}
