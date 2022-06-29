package com.codecademy.goldmedal.controller;

import com.codecademy.goldmedal.model.*;
import com.codecademy.goldmedal.repository.CountryRepository;
import com.codecademy.goldmedal.repository.GoldMedalRepository;
import org.apache.commons.text.WordUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/countries")
public class GoldMedalController {

    // TODO: declare references to your repositories
    CountryRepository countryRepository;
    GoldMedalRepository goldMedalRepository;
    // TODO: update your constructor to include your repositories

    public GoldMedalController(CountryRepository countryRepository, GoldMedalRepository goldMedalRepository) {
        this.countryRepository = countryRepository;
        this.goldMedalRepository = goldMedalRepository;
    }

    @GetMapping
    public CountriesResponse getCountries(@RequestParam String sort_by, @RequestParam String ascending) {
        var ascendingOrder = ascending.toLowerCase().equals("y");
        return new CountriesResponse(getCountrySummaries(sort_by.toLowerCase(), ascendingOrder));
    }

    @GetMapping("/{country}")
    public CountryDetailsResponse getCountryDetails(@PathVariable String country) {
        String countryName = WordUtils.capitalizeFully(country);
        return getCountryDetailsResponse(countryName);
    }

    @GetMapping("/{country}/medals")
    public CountryMedalsListResponse getCountryMedalsList(@PathVariable String country, @RequestParam String sort_by, @RequestParam String ascending) {
        String countryName = WordUtils.capitalizeFully(country);
        var ascendingOrder = ascending.toLowerCase().equals("y");
        return getCountryMedalsListResponse(countryName, sort_by.toLowerCase(), ascendingOrder);
    }

    private CountryMedalsListResponse getCountryMedalsListResponse(String countryName, String sortBy, boolean ascendingOrder) {
        List<GoldMedal> medalsList;
        switch (sortBy) {
            case "year":
                if(ascendingOrder)
                    medalsList = this.goldMedalRepository.getByCountryOrderByYearAsc(countryName);
                else
                    medalsList = this.goldMedalRepository.getByCountryOrderByYearDesc(countryName);   // TODO: list of medals sorted by year in the given order
                break;
            case "season":
                if(ascendingOrder)
                    medalsList = this.goldMedalRepository.getByCountryOrderBySeasonAsc(countryName);
                else
                    medalsList = this.goldMedalRepository.getByCountryOrderBySeasonDesc(countryName);// TODO: list of medals sorted by season in the given order
                break;
            case "city":
                if(ascendingOrder)
                    medalsList = this.goldMedalRepository.getByCountryOrderByCityAsc(countryName);
                else
                    medalsList = this.goldMedalRepository.getByCountryOrderByCityDesc(countryName);// TODO: list of medals sorted by city in the given order
                break;
            case "name":
                if(ascendingOrder)
                    medalsList = this.goldMedalRepository.getByCountryOrderByNameAsc(countryName);   // TODO: list of medals sorted by athlete's name in the given order
                else
                    medalsList = this.goldMedalRepository.getByCountryOrderByNameDesc(countryName);
                break;
            case "event":
                if(ascendingOrder)
                    medalsList = this.goldMedalRepository.getByCountryOrderByEventAsc(countryName);   // TODO: list of medals sorted by event in the given order
                else
                    medalsList = this.goldMedalRepository.getByCountryOrderByEventDesc(countryName);
                break;
            default:
                medalsList = new ArrayList<>();
                break;
        }

        return new CountryMedalsListResponse(medalsList);
    }

    private CountryDetailsResponse getCountryDetailsResponse(String countryName) {
        Optional<Country> countryOptional = this.countryRepository.findByName(countryName);   // TODO: get the country; this repository method should return a java.util.Optional

        if (!countryOptional.isPresent()) {
            return new CountryDetailsResponse(countryName);
        }

        Country country = countryOptional.get();
        int goldMedalCount = this.goldMedalRepository.countGoldMedalByCountry(country.getName());   // TODO: get the medal count

        List<GoldMedal> summerWins = this.goldMedalRepository.findGoldMedalBySeasonAndCountryOrderByYear("Summer", country.getName());   // TODO: get the collection of wins at the Summer Olympics, sorted by year in ascending order
        int numberSummerWins = summerWins.size() > 0 ? summerWins.size() : null;
        int totalSummerEvents = this.goldMedalRepository.countGoldMedalBySeason("Summer");   // TODO: get the total number of events at the Summer Olympics
        float percentageTotalSummerWins = totalSummerEvents != 0 && numberSummerWins != 0 ? (float) summerWins.size() / totalSummerEvents : null;
        int yearFirstSummerWin = summerWins.size() > 0 ? summerWins.get(0).getYear() : null;

        List<GoldMedal> winterWins = this.goldMedalRepository.findGoldMedalBySeasonAndCountryOrderByYear("Winter", country.getName());   // TODO: get the collection of wins at the Winter Olympics
        int numberWinterWins = 0;
        if(winterWins.size() != 0)
            numberWinterWins = winterWins.size();
//        int numberWinterWins = winterWins.size() > 0 ? winterWins.size() : 0;
        List<GoldMedal> totalWinterEvents = this.goldMedalRepository.findGoldMedalBySeasonAndCountryOrderByYear("Winter", country.getName());   // TODO: get the total number of events at the Winter Olympics, sorted by year in ascending order
        float percentageTotalWinterWins = totalWinterEvents.size() != 0 && numberWinterWins != 0 ? (float) winterWins.size() / totalWinterEvents.size() : null;
        int yearFirstWinterWin = winterWins.size() > 0 ? winterWins.get(0).getYear() : null;

        int numberEventsWonByFemaleAthletes = this.goldMedalRepository.countGoldMedalByCountryAndGender(country.getName(), "Women");   // TODO: get the number of wins by female athletes
        int numberEventsWonByMaleAthletes = this.goldMedalRepository.countGoldMedalByCountryAndGender(country.getName(), "Men");   // TODO: get the number of wins by male athletes

        return new CountryDetailsResponse(
                countryName,
                country.getGdp(),
                country.getPopulation(),
                goldMedalCount,
                numberSummerWins,
                percentageTotalSummerWins,
                yearFirstSummerWin,
                numberWinterWins,
                percentageTotalWinterWins,
                yearFirstWinterWin,
                numberEventsWonByFemaleAthletes,
                numberEventsWonByMaleAthletes);
    }

    private List<CountrySummary> getCountrySummaries(String sortBy, boolean ascendingOrder) {
        List<Country> countries;
        switch (sortBy) {
            case "name":
                if(ascendingOrder)
                    countries = this.countryRepository.getAllByOrderByNameAsc();
                else
                    countries = this.countryRepository.getAllByOrderByNameDesc();   // TODO: list of countries sorted by name in the given order
                break;
            case "gdp":
                if(ascendingOrder)
                    countries = this.countryRepository.getAllByOrderByGdpAsc();
                else
                    countries = this.countryRepository.getAllByOrderByGdpDesc();   // TODO: list of countries sorted by gdp in the given order
                break;
            case "population":
                if(ascendingOrder)
                    countries = this.countryRepository.getAllByOrderByPopulationAsc();
                else
                    countries = this.countryRepository.getAllByOrderByPopulationDesc();   // TODO: list of countries sorted by population in the given order
                break;
            case "medals":
            default:
                countries = this.countryRepository.findAll();   // TODO: list of countries in any order you choose; for sorting by medal count, additional logic below will handle that
                break;
        }

        var countrySummaries = getCountrySummariesWithMedalCount(countries);

        if (sortBy.equalsIgnoreCase("medals")) {
            countrySummaries = sortByMedalCount(countrySummaries, ascendingOrder);
        }

        return countrySummaries;
    }

    private List<CountrySummary> sortByMedalCount(List<CountrySummary> countrySummaries, boolean ascendingOrder) {
        return countrySummaries.stream()
                .sorted((t1, t2) -> ascendingOrder ?
                        t1.getMedals() - t2.getMedals() :
                        t2.getMedals() - t1.getMedals())
                .collect(Collectors.toList());
    }

    private List<CountrySummary> getCountrySummariesWithMedalCount(List<Country> countries) {
        List<CountrySummary> countrySummaries = new ArrayList<>();
        for (Country country : countries) {
            int goldMedalCount = this.goldMedalRepository.countGoldMedalByCountry(country.getName());   // TODO: get count of medals for the given country
            countrySummaries.add(new CountrySummary(country, goldMedalCount));
        }
        return countrySummaries;
    }
}
