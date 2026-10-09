package org.example.social_network.csv;

import org.example.social_network.exception.ErrorCsv;
import org.example.social_network.exception.LoadCsvException;
import org.example.social_network.exception.LoadCsvResult;
import org.example.social_network.model.Community;
import org.example.social_network.model.DelProfile;
import org.example.social_network.model.FriendShip;
import org.example.social_network.model.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvLoader {
    public <T> LoadCsvResult<T> load(Path file) throws LoadCsvException {
        List<T> result = new ArrayList<>();
        List<LoadCsvException> errors = new ArrayList<>();

        try (var reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String header = reader.readLine();                      // строка-заголовок
            if (header == null) {
                throw new LoadCsvException(ErrorCsv.INVALID_DATA, 1, "Заголовок отсутствует");
            }

            header = header.trim();
            int fields;

            switch (header) {
                case "id;name;city;birthYear" -> fields = 4;
                case "id;name;city;birthYear;adminId" -> fields = 5;
                case "id;name;city;birthYear;dayDel;delReason" -> fields = 6;
                case "profile1;profile2;strength" -> fields = 3;
                default -> throw new LoadCsvException(ErrorCsv.INVALID_DATA, 1, "Неизвестный заголовок: " + header);
            }

            String line;
            int lineNumber = 1;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) continue;

                try {
                    String[] parts = line.split(";", -1);
                    if (parts.length != fields) {
                        throw new LoadCsvException(ErrorCsv.WRONG_FIELD_COUNT, lineNumber, "Ожидалось" + fields + " полей,получено " + parts.length);
                    }
                    Object item = parseLine(header,parts,lineNumber);
                    if(item!=null){
                        result.add((T) item);
                    }
                }catch (LoadCsvException e){
                    errors.add(e);
                }catch (Exception e){
                    errors.add(new LoadCsvException(ErrorCsv.INVALID_DATA,lineNumber,e.getMessage()));
                }
            }
        }catch (IOException e){
            throw new LoadCsvException(ErrorCsv.IO_ERROR,0,"ошибка чтения файла "+file,e);
        }
    return new LoadCsvResult<>(result,errors);
    }

    private Object parseLine(String header,String parts[],int lineNumber) throws LoadCsvException{
        return switch(header){
            case "id;name;city;birthYear" ->{
                int id = parseInt(parts[0],lineNumber,"ID");
                String name = parts[1];
                String city = parts[2];
                int birthYear = parseInt(parts[3],lineNumber,"Год рождения");

                Profile profile = new Profile(id,name,city,birthYear);
                List<String> validationErrors = profile.validate();
                if (!validationErrors.isEmpty()) {
                    throw new LoadCsvException(ErrorCsv.INVALID_DATA, lineNumber, String.join("; ", validationErrors));
                }
                yield profile;
            }
            case "id;name;city;birthYear;adminId" ->{
                int id = parseInt(parts[0],lineNumber,"ID");
                String name = parts[1];
                String city = parts[2];
                int birthYear = parseInt(parts[3],lineNumber,"Год рождения");
                int adminId = parseInt(parts[4],lineNumber,"ID администратора");

                Community community = new Community(id,name,city,birthYear,adminId);
                List<String> validationErrors = community.validate();
                if(!validationErrors.isEmpty()){
                    throw new LoadCsvException(ErrorCsv.INVALID_DATA,lineNumber,String.join(";", validationErrors));
                }
                yield community;
            }
            case "id;name;city;birthYear;dayDel;delReason" ->{
                int id = parseInt(parts[0],lineNumber,"ID");
                String name = parts[1];
                String city = parts[2];
                int birthYear = parseInt(parts[3],lineNumber,"Год рождения");
                int dayDel = parseInt(parts[4],lineNumber,"Дата удаления");
                String delReason = parts[5];


                if(name.isEmpty() || city.isEmpty() || delReason.isEmpty()||birthYear<=0||dayDel<=0){
                    throw new LoadCsvException(ErrorCsv.INVALID_DATA, lineNumber,"Имя,город,причина не могут быть пустыми,а год и дата должны быть > 0");
                }

                yield new DelProfile(id,name,city,birthYear,dayDel,delReason);
            }
            case "profile1;profile2;strength" ->{
                int id1 = parseInt(parts[0],lineNumber,"profile1");
                int id2 = parseInt(parts[1],lineNumber,"profile2");
                int strength = parseInt(parts[2],lineNumber,"сила связи");

                if(id1<=0 || id2<=0 || strength<0){
                    throw new LoadCsvException(ErrorCsv.INVALID_DATA,lineNumber,"ID пользователей должен быть неменьше 0, а сила связи больше 0");
                }
                yield new FriendShip(id1,id2,strength);
            }
            default -> null;
        };
    }

    private int parseInt(String value, int lineNumber, String fieldName) throws LoadCsvException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new LoadCsvException(ErrorCsv.BAD_NUMBER, lineNumber, "Поле '" + fieldName + "' должно быть числом");
        }
    }
}
