package com.codecademy.goldmedal.repository;

import com.codecademy.goldmedal.model.GoldMedal;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoldMedalRepository extends CrudRepository<GoldMedal, Integer> {

    Integer countGoldMedalByCountry(String countryName);
    Integer countGoldMedalBySeasonAndCountryOrderByYear(String season, String countryName);
    Integer countGoldMedalBySeason(String season);

    List<GoldMedal> findGoldMedalBySeasonAndCountryOrderByYear(String season,String countryName);
    Integer countGoldMedalByCountryAndGender(String countryName, String gender);
    List<GoldMedal> getByCountryOrderByYearAsc(String country);
    List<GoldMedal> getByCountryOrderByYearDesc(String country);
    List<GoldMedal> getByCountryOrderBySeasonAsc(String country);
    List<GoldMedal> getByCountryOrderBySeasonDesc(String country);
    List<GoldMedal> getByCountryOrderByCityAsc(String country);
    List<GoldMedal> getByCountryOrderByCityDesc(String country);
    List<GoldMedal> getByCountryOrderByNameAsc(String country);
    List<GoldMedal> getByCountryOrderByNameDesc(String country);
    List<GoldMedal> getByCountryOrderByEventAsc(String country);
    List<GoldMedal> getByCountryOrderByEventDesc(String country);

}
