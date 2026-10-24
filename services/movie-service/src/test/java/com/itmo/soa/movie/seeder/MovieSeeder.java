package com.itmo.soa.movie.seeder;

import com.itmo.soa.movie.domain.movie.model.Color;
import com.itmo.soa.movie.domain.movie.model.Country;
import com.itmo.soa.movie.domain.movie.model.MovieGenre;
import com.itmo.soa.movie.domain.movie.model.MpaaRating;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("seeder")
class MovieSeeder {

    private static final int MOVIES_COUNT = 250;

    private static final String MOVIE_SERVICE_URL = "https://localhost:8181";
    private static final String MOVIES_CSV = "/movies.csv";

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/movies";
    private static final String DB_USER = "movies";
    private static final String DB_PASSWORD = "movies";

    private static final Pattern CREATED_ID = Pattern.compile("\"id\":(\\d+)");

    private static final int MIN_COORDINATE = -1000;
    private static final int MAX_COORDINATE = 1000;
    private static final int MAX_OSCARS = 15;
    private static final double NO_PASSPORT_SHARE = 0.3;
    private static final double NO_HAIR_COLOR_SHARE = 0.25;

    private final Random random = new Random();

    @Test
    void seedMovies() throws Exception {
        List<CsvMovie> csv = readCsv();
        Collections.shuffle(csv, random);
        HttpClient client = HttpClient.newBuilder().sslContext(trustAll()).build();

        List<String> failures = new ArrayList<>();
        try (Connection db = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement setReleaseDate = db.prepareStatement("UPDATE movies SET creation_date = ? WHERE id = ?")) {
            for (int i = 0; i < MOVIES_COUNT; i++) {
                CsvMovie movie = csv.get(i % csv.size());
                HttpRequest request = HttpRequest.newBuilder(URI.create(MOVIE_SERVICE_URL + "/movies"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(toJson(movie), StandardCharsets.UTF_8))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 201) {
                    failures.add(movie.title() + " → " + response.statusCode() + " " + response.body());
                    continue;
                }
                Matcher id = CREATED_ID.matcher(response.body());
                if (!id.find()) {
                    failures.add(movie.title() + " → в ответе нет id: " + response.body());
                    continue;
                }
                setReleaseDate.setDate(1, Date.valueOf(movie.releaseDate()));
                setReleaseDate.setInt(2, Integer.parseInt(id.group(1)));
                setReleaseDate.executeUpdate();
            }
        }
        System.out.printf("Создано фильмов: %d из %d%n", MOVIES_COUNT - failures.size(), MOVIES_COUNT);
        assertEquals(List.of(), failures, "Не все фильмы созданы");
    }

    private String toJson(CsvMovie movie) {
        String passportID = random.nextDouble() < NO_PASSPORT_SHARE ? null : passportID();
        Color hairColor = random.nextDouble() < NO_HAIR_COLOR_SHARE ? null : any(Color.values());
        return """
                {"name":%s,"coordinates":{"x":%d,"y":%d},"oscarsCount":%d,"genre":%s,"mpaaRating":%s,\
                "screenwriter":{"name":%s,"passportID":%s,"hairColor":%s,"nationality":%s}}"""
                .formatted(
                        string(movie.title()),
                        random.nextInt(MIN_COORDINATE, MAX_COORDINATE + 1),
                        random.nextLong(MIN_COORDINATE, MAX_COORDINATE + 1),
                        random.nextInt(1, MAX_OSCARS + 1),
                        string(movie.genre().name()),
                        string(any(MpaaRating.values()).name()),
                        string(movie.screenwriter()),
                        string(passportID),
                        string(hairColor == null ? null : hairColor.name()),
                        string(any(Country.values()).name()));
    }

    private String passportID() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder id = new StringBuilder();
        int length = random.nextInt(5, 11);
        for (int i = 0; i < length; i++) {
            id.append(alphabet.charAt(random.nextInt(alphabet.length())));
        }
        return id.toString();
    }

    private <T> T any(T[] values) {
        return values[random.nextInt(values.length)];
    }

    private static String string(String value) {
        if (value == null) {
            return "null";
        }
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static List<CsvMovie> readCsv() throws Exception {
        try (InputStream in = MovieSeeder.class.getResourceAsStream(MOVIES_CSV);
             BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .skip(1)
                    .filter(line -> !line.isBlank())
                    .map(MovieSeeder::parseLine)
                    .map(f -> new CsvMovie(f.get(0), Integer.parseInt(f.get(1)), MovieGenre.valueOf(f.get(2)), f.get(3),
                            LocalDate.parse(f.get(4))))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        }
    }

    private static List<String> parseLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;
        for (char c : line.toCharArray()) {
            if (c == '"') {
                quoted = !quoted;
            } else if (c == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        fields.add(field.toString());
        return fields;
    }

    private static SSLContext trustAll() throws Exception {
        TrustManager trustAll = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        };
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[]{trustAll}, null);
        return context;
    }

    private record CsvMovie(String title, int year, MovieGenre genre, String screenwriter, LocalDate releaseDate) {
    }
}
