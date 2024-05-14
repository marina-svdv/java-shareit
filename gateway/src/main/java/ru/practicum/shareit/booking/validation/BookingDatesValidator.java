package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class BookingDatesValidator implements ConstraintValidator<ValidBookingDates, BookingDto> {

    @Override
    public void initialize(ValidBookingDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
            return true;
        }
        return bookingDto.getStart().isBefore(bookingDto.getEnd());
    }
}
