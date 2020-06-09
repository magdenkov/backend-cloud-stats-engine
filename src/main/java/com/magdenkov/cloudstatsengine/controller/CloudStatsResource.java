package com.magdenkov.cloudstatsengine.controller;

import com.magdenkov.cloudstatsengine.domain.ErrorResponse;
import com.magdenkov.cloudstatsengine.domain.GeometricModel;
import com.magdenkov.cloudstatsengine.domain.ProcessedResponse;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.apache.commons.math3.stat.StatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import static com.google.common.math.Quantiles.percentiles;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/files")
@CrossOrigin("*")
public class CloudStatsResource {

    private static final Logger logger = LoggerFactory.getLogger(CloudStatsResource.class);

    private static final String IGNORE_VALUE = "-999.25";
    private static final String FORMAT_DOUBLE = "%.4f";

    @GetMapping("/default")
    public ResponseEntity getDefault(@RequestParam(value = "percentile", defaultValue = "75") Integer percentile) {
        final String inputCsv = "DEPTH,GR,RHOB\n" +
                "1000.0,80,-999.25\n" +
                "1000.5,80,-999.25\n" +
                "1001.0,85,-999.25\n" +
                "1001.5,85,2.5\n" +
                "1002.0,75,2.6\n" +
                "1002.5,75,2.7";
        final MockMultipartFile csvdata = new MockMultipartFile("file", "testdata.csv", "text/plain", inputCsv.getBytes());

        return uploadToLocalFileSystem(csvdata, percentile);
    }

    @PostMapping("/upload")
    public ResponseEntity uploadToLocalFileSystem(@RequestParam(value = "file") MultipartFile file,
                                                  @RequestParam(value = "percentile") Integer percentile
    ) {

        final ProcessedResponse response = new ProcessedResponse();
        if (percentile < 0 || percentile > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (file.isEmpty()) {
            // todo add more validations, like check is it correct CSV file
             return ResponseEntity.badRequest().build();
        }

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            final List<GeometricModel> geometricModelListInput = parseCsv(reader);
            response.setOriginalInput(geometricModelListInput);

            calcDephs(percentile, response, geometricModelListInput);

            calcGr(percentile, response, geometricModelListInput);

            calcRhob(percentile, response, geometricModelListInput);

            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(getErrorResponse(ex));
        }
    }

    private ErrorResponse getErrorResponse(Exception ex) {
        final ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setExceptionType(ex.getClass().toString());
        errorResponse.setMessage(ex.getMessage());
        return errorResponse;
    }

    private void calcDephs(final Integer percentile, final ProcessedResponse response, final List<GeometricModel> geometricModelListInput) {
        final List<Double> depthsList = geometricModelListInput.stream()
                .map(GeometricModel::getDepth)
                .filter(it -> !it.equals(IGNORE_VALUE))
                .map(Double::parseDouble)
                .collect(toList());
        // check if it is not empty IllegalArgumentException
        final double guavaDepthPercentile = percentiles().index(percentile).compute(depthsList);
        response.getGuavaCalculatedPercentile().setDepth(formatResp(guavaDepthPercentile));

        final double apacheDepthPercentile = StatUtils.percentile(depthsList.stream()
                .mapToDouble(Double::doubleValue)
                .toArray(), percentile);
        response.getApacheCalculatedPercentile().setDepth(formatResp(apacheDepthPercentile));
    }

    private String formatResp(double apacheDepthPercentile) {
        return String.format(FORMAT_DOUBLE, apacheDepthPercentile);
    }

    private void calcGr(final Integer percentile, final ProcessedResponse response, final List<GeometricModel> geometricModelListInput) {
        final List<Double> gammaRaysList = geometricModelListInput.stream()
                .map(GeometricModel::getGammaRay)
                .filter(it -> !it.equals(IGNORE_VALUE))
                .map(Double::valueOf)
                .collect(toList());
        // todo check if it is not empty for IllegalArgumentException
        final double gammaRayPercentile = percentiles().index(percentile).compute(gammaRaysList);
        response.getGuavaCalculatedPercentile().setGammaRay(formatResp(gammaRayPercentile));

        final double apacheGammaRayPercentile = StatUtils.percentile(gammaRaysList.stream()
                .mapToDouble(Double::doubleValue)
                .toArray(), percentile);
        response.getApacheCalculatedPercentile().setGammaRay(formatResp(apacheGammaRayPercentile));
    }

    private void calcRhob(final Integer percentile, final ProcessedResponse response, final List<GeometricModel> geometricModelListInput) {
        final List<Double> phobsList = geometricModelListInput.stream()
                .map(GeometricModel::getRhob)
                .filter(it -> !it.equals(IGNORE_VALUE))
                .map(Double::valueOf)
                .collect(toList());
        // todo check if it is not empty for IllegalArgumentException

        final double guavaRhobsPercentile = percentiles().index(percentile).compute(phobsList);
        response.getGuavaCalculatedPercentile().setRhob(formatResp(guavaRhobsPercentile));

        final double apachRhobPercentile = StatUtils.percentile(phobsList.stream()
                .mapToDouble(Double::doubleValue)
                .toArray(), percentile);
        response.getApacheCalculatedPercentile().setRhob(formatResp(apachRhobPercentile));
    }

    private List<GeometricModel> parseCsv(final Reader reader) {
        final CsvToBean<GeometricModel> csvToBean = new CsvToBeanBuilder(reader)
                .withType(GeometricModel.class)
                .withIgnoreLeadingWhiteSpace(true)
                .build();
        return csvToBean.parse();
    }
}
