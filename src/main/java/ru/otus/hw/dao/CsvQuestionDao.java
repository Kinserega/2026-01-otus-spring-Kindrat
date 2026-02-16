package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private static final String RESOURCES_PATH = "/";

    private final TestFileNameProvider fileNameProvider;


    @Override
    public List<Question> findAll() {
        String fileName = fileNameProvider.getTestFileName();
        try (InputStream inputStream = getClass().getResourceAsStream(RESOURCES_PATH + fileName);
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            List<QuestionDto> questionDtos = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withType(QuestionDto.class)
                    .withSeparator(';')
                    .withSkipLines(1)
                    .build()
                    .parse();

            return questionDtos.stream()
                    .map(QuestionDto::toDomainObject)
                    .collect(Collectors.toList());

        } catch (IllegalArgumentException e) {
            throw new QuestionReadException(String.format("Invalid CSV format. Check file structure: %s", fileName), e);
        } catch (Exception e) {
            throw new QuestionReadException("Failed to load questions due to unexpected error.");
        }
    }
}
