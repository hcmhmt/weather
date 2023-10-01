package com.simurgh.weather.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.simurgh.weather.constants.Constants;
import com.simurgh.weather.dto.WeatherDto;
import com.simurgh.weather.dto.WeatherResponse;
import com.simurgh.weather.entity.WeatherEntity;
import com.simurgh.weather.repository.WeatherRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static com.simurgh.weather.constants.Constants.WEATHERS;

@Service
@CacheConfig(cacheNames = {WEATHERS})
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final WeatherRepository weatherRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherService(WeatherRepository weatherRepository, RestTemplate restTemplate) {
        this.weatherRepository = weatherRepository;
        this.restTemplate = restTemplate;
    }

    @Cacheable(key = "#cityName")
    public WeatherDto getWeatherByCityName(String cityName) {

        logger.info("Requested cityName: " + cityName);
        Optional<WeatherEntity> weatherEntityOptional = weatherRepository.findFirstByRequestedCityNameOrderByUpdatedTimeDesc(cityName);

        return weatherEntityOptional.map(weatherEntity -> {
            if (weatherEntity.getUpdatedTime().isBefore(LocalDateTime.now().minusSeconds(30))) {
                return WeatherDto.convert(getWeatherEntityFromWeatherStack(cityName));
            }
            return WeatherDto.convert(weatherEntity);
        }).orElseGet(() -> WeatherDto.convert(getWeatherEntityFromWeatherStack(cityName)));

    }

    private WeatherEntity getWeatherEntityFromWeatherStack(String cityName) {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(getWeatherStackUrl(cityName), String.class);

        try {
            WeatherResponse weatherResponse = objectMapper.readValue(responseEntity.getBody(), WeatherResponse.class);
            return saveWeatherEntity(cityName, weatherResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @CacheEvict(allEntries = true)
    @PostConstruct
    @Scheduled(fixedRateString = "10000")
    public void clearCache() {
        logger.info("Cache cleared!");
    }

    private WeatherEntity saveWeatherEntity(String cityName, WeatherResponse weatherResponse) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        WeatherEntity weatherEntity = new WeatherEntity(
                cityName,
                weatherResponse.location().name(),
                weatherResponse.location().country(),
                weatherResponse.current().temperature(),
                LocalDateTime.now(),
                LocalDateTime.parse(weatherResponse.location().localtime(), dateTimeFormatter));

        return weatherRepository.save(weatherEntity);

    }

    private String getWeatherStackUrl(String cityName) {
        return Constants.API_URL + Constants.ACCESS_KEY_PARAM + Constants.API_KEY + Constants.QUERY_KEY_PARAM + cityName;
    }

}
