package com.itmo.soa.movie.api.http.movie;

import com.itmo.soa.contract.request.movie.CoordinatesDto;
import com.itmo.soa.contract.request.movie.CreateMovieRequest;
import com.itmo.soa.contract.request.movie.PersonDto;
import com.itmo.soa.contract.request.movie.UpdateMovieRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MovieContractValidationTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void invalidMovieFieldsExampleReturnsAllFourErrors() {
        CreateMovieRequest input = new CreateMovieRequest(
                "",
                new CoordinatesDto(1, 2L),
                0,
                null,
                "PG13",
                new PersonDto("Author", "AB12", null, "FRANCE")
        );

        assertEquals(Map.of(
                "name", "Название не может быть пустым",
                "oscarsCount", "Значение должно быть больше 0",
                "screenwriter.passportID", "Длина должна быть не меньше 5 символов",
                "mpaaRating", "Допустимые значения: G, PG, PG_13, R, NC_17"
        ), errors(validator.validate(input)));
    }

    @Test
    void movieInputAcceptsRequiredFieldsAndNullableOptionals() {
        CreateMovieRequest input = new CreateMovieRequest(
                "Film name 1",
                new CoordinatesDto(51, -120L),
                4,
                null,
                "R",
                null
        );

        assertTrue(validator.validate(input).isEmpty());
    }

    @Test
    void movieInputRequiresExactlyTheOpenApiRequiredFields() {
        assertEquals(Map.of(
                "name", "Поле обязательно",
                "coordinates", "Поле обязательно",
                "oscarsCount", "Поле обязательно",
                "mpaaRating", "Поле обязательно"
        ), errors(validator.validate(new CreateMovieRequest())));
    }

    @Test
    void allEnumViolationsAreCollectedAcrossNestedFields() {
        CreateMovieRequest input = new CreateMovieRequest(
                "Film name 1",
                new CoordinatesDto(51, -120L),
                4,
                "THRILLER",
                "PG13",
                new PersonDto("Author", "ABC123456", "GREEN", "USA")
        );

        assertEquals(Map.of(
                "genre", "Допустимые значения: ACTION, DRAMA, COMEDY, MUSICAL, HORROR",
                "mpaaRating", "Допустимые значения: G, PG, PG_13, R, NC_17",
                "screenwriter.hairColor", "Допустимые значения: RED, BLUE, ORANGE, WHITE",
                "screenwriter.nationality", "Допустимые значения: RUSSIA, UNITED_KINGDOM, FRANCE, SPAIN, NORTH_KOREA"
        ), errors(validator.validate(input)));
    }

    @Test
    void readOnlyFieldsExampleReturnsBothErrors() {
        UpdateMovieRequest patch = new UpdateMovieRequest();
        patch.setId(5);
        patch.setCreationDate("2026-01-01");

        assertEquals(Map.of(
                "id", "Поле id нельзя изменить",
                "creationDate", "Поле creationDate нельзя изменить"
        ), errors(validator.validate(patch)));
    }

    @Test
    void emptyMoviePatchIsRejected() {
        assertEquals(
                Map.of("notEmpty", "Нужно передать хотя бы одно поле"),
                errors(validator.validate(new UpdateMovieRequest()))
        );
    }

    @Test
    void moviePatchValidatesGenreInsideOptional() {
        UpdateMovieRequest patch = new UpdateMovieRequest();
        patch.setGenre(Optional.of("THRILLER"));

        assertEquals(
                Map.of("genre", "Допустимые значения: ACTION, DRAMA, COMEDY, MUSICAL, HORROR"),
                errors(validator.validate(patch))
        );
    }

    @Test
    void moviePatchAcceptsNullableFields() {
        UpdateMovieRequest patch = new UpdateMovieRequest();
        patch.setOscarsCount(5);
        patch.setGenre(Optional.empty());
        patch.setScreenwriter(Optional.empty());

        assertTrue(validator.validate(patch).isEmpty());
    }

    private static Map<String, String> errors(Set<? extends ConstraintViolation<?>> violations) {
        return violations.stream().collect(Collectors.toMap(
                violation -> violation.getPropertyPath().toString(),
                ConstraintViolation::getMessage
        ));
    }
}
