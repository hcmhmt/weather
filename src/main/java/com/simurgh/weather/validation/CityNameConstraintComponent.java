package com.simurgh.weather.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class CityNameConstraintComponent implements ConstraintValidator<CityNameConstraint, String> {
    @Override
    public void initialize(CityNameConstraint constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        //"city1-city2-city3 -> city1city2city3"
        //"-  -  -  " -> "      "
        value = value.replaceAll("[^a-zA-Z0-9]", "");
        return !StringUtils.isNumeric(value) && !StringUtils.isBlank(value);
    }
}
