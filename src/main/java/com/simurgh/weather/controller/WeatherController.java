package com.simurgh.weather.controller;

import com.simurgh.weather.dto.WeatherDto;
import com.simurgh.weather.service.WeatherService;
import com.simurgh.weather.validation.CityNameConstraint;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/weather")
@Validated
@Tag(name = "Open Weather API v1", description = "Open Weather API for filter by city for current temperature")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    // "", " ", 12321312
    @GetMapping("/{cityName}")
    @RateLimiter(name = "basic")
    @Operation(
            method = "GET",
            summary = "Open Weather API summary",
            description = "Open Weather API for filter by city",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Success",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = WeatherDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Bad Request",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<WeatherDto> getWeather(@PathVariable("cityName") @CityNameConstraint @NotBlank String cityName) {
        return ResponseEntity.ok(weatherService.getWeatherByCityName(cityName));
    }

}
