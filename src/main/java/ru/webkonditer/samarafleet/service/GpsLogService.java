package ru.webkonditer.samarafleet.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Сервис для обработки GPS логов и вычисления пройденного пути.
 */
@Service
public class GpsLogService {

    private static final Logger logger = LoggerFactory.getLogger(GpsLogService.class);

    /**
     * Обработка GPS лога для вычисления пройденного пути.
     *
     * @param file MultipartFile, содержащий GPS лог.
     * @return Строка с результатами обработки лога.
     * @throws IOException В случае ошибок ввода/вывода.
     */
    public String processGpsLog(MultipartFile file) throws IOException {
        List<String> logLineList = parseGpsLog(file);
        double totalDistance = calculateTotalDistance(logLineList);

        return "Общая дистанция: " + String.format("%.3f", totalDistance) + " километров.";
    }

    /**
     * Создает список строк из GPS лога, исключая строки, начинающиеся с $GNZDA.
     *
     * @param file MultipartFile, содержащий GPS лог.
     * @return Список строк лога без $GNZDA.
     * @throws IOException В случае ошибок ввода/вывода.
     */
    private List<String> parseGpsLog(MultipartFile file) throws IOException {
        List<String> logLinesList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Пропустить пустые строки
                if (line.trim().isEmpty()) {
                    continue;
                }
                // Исключаем $GNZDA
                if (!line.startsWith("$GNZDA")) {
                    logLinesList.add(line);
                }
            }
        }
        return logLinesList;
    }

    /**
     * Вычисляет общую дистанцию, используя строки с координатами и скоростью из лога.
     *
     * @param logLinesList Список строк GPS лога.
     * @return Общая дистанция в метрах.
     */
    private double calculateTotalDistance(List<String> logLinesList) {
        double totalDistance = 0;
        for (int i = 0; i < logLinesList.size(); i++) {
            int previousIndex = i - 1;
            int nextIndex = i + 1;

            String line = logLinesList.get(i);
            if (line.startsWith("$GNVTG") && extractSpeedFromGNVTG(line) > 0) {
                if (previousIndex >= 0 && nextIndex < logLinesList.size()) {
                    GPGGAPoint startingCoordinate = parseGPGGA(logLinesList.get(previousIndex));
                    GPGGAPoint finalCoordinate = parseGPGGA(logLinesList.get(nextIndex));

                    if (startingCoordinate != null && finalCoordinate != null) {

                        // Получаем длину отрезка и прибавляем к общему расстоянию
                        totalDistance += calculateDistance(startingCoordinate, finalCoordinate);
                    }
                }
            }
        }
        return totalDistance;
    }

    /**
     * Класс с координатами точки.
     */
    @AllArgsConstructor
    @Data
    private static class GPGGAPoint {
        private String latitude;
        private String longitude;
    }

    /**
     * Парсит строку $GPGGA и возвращает объект GPGGAPoint.
     *
     * @param nmeaString Строка $GPGGA.
     * @return Объект GPGGAPoint или null, если парсинг не удался.
     */
    public static GPGGAPoint parseGPGGA(String nmeaString) {
        try {
            // Проверяем, что строка начинается с $GPGGA
            if (nmeaString.startsWith("$GPGGA")) {

                // Разбиваем строку по разделителю
                String[] tokens = nmeaString.split(",");

                // Извлекаем нужные параметры из строки
                String latitude = tokens[2];
                String longitude = tokens[4];

                // Проверяем, что координаты не пусты
                if (latitude.isEmpty() || longitude.isEmpty()) {
                    throw new IllegalArgumentException("Latitude or longitude is empty");
                }

                // Создаем и возвращаем объект GPGGAPoint
                return new GPGGAPoint(latitude, longitude);
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            // Обработка ошибок при отсутствии значения или пустых координатах
            // Логируем сообщение об ошибке
            logger.error("Ошибка (Невалидная GPGGA строка): " + nmeaString);
        }

        // Возвращаем null в случае неудачи
        return null;
    }

    /**
     * Извлекает скорость из строки $GNVTG.
     *
     * @param nmeaString Строка $GNVTG.
     * @return Скорость в км/ч.
     */
    private int extractSpeedFromGNVTG(String nmeaString) {
        try {
            // Разбиваем строку по разделителю
            String[] tokens = nmeaString.split(",");

            // Индекс, соответствующий параметру скорости в км/ч
            int speedIndex = 7;

            // Извлекаем значение скорости в км/ч, округленное до целого
            return (int) Math.round(Double.parseDouble(tokens[speedIndex]));
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Логируем сообщение об ошибке
            logger.error("Ошибка (Невалидная GNVTG строка): " + nmeaString);
            // Возвращаем 0 в случае ошибки
            return 0;
        }
    }

    /**
     * Вычисляет расстояние между двумя точками на Земле с использованием формулы гаверсинуса.
     *
     * @param point1 Первая точка.
     * @param point2 Вторая точка.
     * @return Расстояние между точками в км.
     */
    private double calculateDistance(GPGGAPoint point1, GPGGAPoint point2) {
        // Радиус Земли в километрах
        final double EARTH_RADIUS = 6371.0;

        // Конвертируем широту и долготу в радианы
        double lat1 = Math.toRadians(Double.parseDouble(point1.getLatitude()));
        double lon1 = Math.toRadians(Double.parseDouble(point1.getLongitude()));
        double lat2 = Math.toRadians(Double.parseDouble(point2.getLatitude()));
        double lon2 = Math.toRadians(Double.parseDouble(point2.getLongitude()));

        // Разница между широтами и долготами
        double dLat = lat2 - lat1;
        double dLon = lon2 - lon1;

        // Формула гаверсинуса для вычисления расстояния
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Вычисление расстояния в километрах
        return EARTH_RADIUS * c;
    }
}
