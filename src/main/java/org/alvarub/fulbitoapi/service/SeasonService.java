package org.alvarub.fulbitoapi.service;

import org.alvarub.fulbitoapi.model.dto.SeasonDTO;
import org.alvarub.fulbitoapi.model.dto.SeasonResponseDTO;
import org.alvarub.fulbitoapi.model.dto.TeamDTO;
import org.alvarub.fulbitoapi.model.dto.TeamResponseDTO;
import org.alvarub.fulbitoapi.model.entity.Season;
import org.alvarub.fulbitoapi.model.entity.Team;
import org.alvarub.fulbitoapi.repository.SeasonRepository;
import org.alvarub.fulbitoapi.repository.TeamRepository;
import org.alvarub.fulbitoapi.utils.exception.NotFoundException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SeasonService {

    @Autowired
    private SeasonRepository seasonRepo;

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepo;

    public void saveSeason(SeasonDTO seasonDTO) {
        Season season = toEntity(seasonDTO);
        seasonRepo.save(season);
    }

    public List<SeasonResponseDTO> findAllSeasons() {
        List<Season> seasons = seasonRepo.findAll();
        List<SeasonResponseDTO> seasonsResponse = new ArrayList<>();
        for (Season season: seasons) {
            seasonsResponse.add(toDto(season));
        }
        return seasonsResponse;
    }

    public SeasonResponseDTO findSeasonById(Long id) {
        Season season = seasonRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Temporada con el id '" + id + "' no encontrada"));

        return toDto(season);
    }

    public SeasonResponseDTO findSeasonByCode(String code) {
        Season season = seasonRepo.findByCode(code)
                .orElseThrow(() -> new NotFoundException("Temporada con el código '" + code + "' no encontrada"));

        return toDto(season);
    }

    public void editSeason(Long id, SeasonDTO seasonDTO) {
        if (seasonRepo.existsById(id)) {
            Season season = toEntity(seasonDTO);
            season.setId(id);
            seasonRepo.save(season);
        } else {
            throw new NotFoundException("Temporada con el id '" + id + "' no encontrada");
        }
    }

    public TeamResponseDTO getRandomTeamBySeason(Long id) {
        Season season = seasonRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Temporada con el id '" + id + "' no encontrada"));
        List<Team> teams = season.getTeams();
        if (teams.isEmpty()) {
            throw new NotFoundException("No hay equipos en la temporada con el id '" + id + "'");
        }
        int randomIndex = (int) (Math.random() * teams.size());
        return teamService.toDto(teams.get(randomIndex));
    }

    // Mappers
    private Season toEntity(SeasonDTO seasonDTO) {
        List<Team> teams = new ArrayList<>();

        for (String team: seasonDTO.getTeams()) {
            teams.add(teamRepo.findByName(team)
                    .orElseThrow(() -> new NotFoundException("Equipo '" + team + "' no encontrado")));
        }

        return Season.builder()
                .code(seasonDTO.getCode())
                .year(seasonDTO.getYear())
                .teams(teams)
                .build();
    }

    public SeasonResponseDTO toDto(Season season) {
        List<TeamResponseDTO> teamsResponse = new ArrayList<>();
        for (Team team: season.getTeams()) {
            teamsResponse.add(teamService.toDto(team));
        }

        return SeasonResponseDTO.builder()
                .id(season.getId())
                .code(season.getCode())
                .year(season.getYear())
                .teams(teamsResponse)
                .build();
    }

    // Jsoup methods
    public void scrapeAndSaveSeason(String seasonPath, SeasonDTO seasonDTO) throws IOException {
        final String BASE_URL = "https://www.bdfutbol.com/";
        String url = BASE_URL + "es/t/" + seasonPath;

        Document doc = Jsoup.connect(url).get();
        Elements rows = doc.select("table#classific tbody tr");
        List<String> teams = new ArrayList<>();
        List<TeamDTO> teamDTOs = new ArrayList<>();

        // Recorro los equipos y extraigo el nombre y url del escudo
        for (Element row : rows) {
            String teamName = row.select("td.text-left a").text();
            String logoUrl = BASE_URL + row.select("td.fit.pr-0 img").attr("src");

            // Se guarda el nombre del equipo en la lista teams y si el equipo no está en la base de datos, se persiste en esta.
            if (!teamName.isEmpty()) {
                teams.add(teamName);

                if (!teamService.existsByName(teamName)) {
                    TeamDTO teamDTO = new TeamDTO(teamName, logoUrl);
                    teamDTOs.add(teamDTO);
                }
            }
        }

        // Guardo los equipos NUEVOS en la bd
        teamService.saveAllTeams(teamDTOs);

        // Creo el SeasonDTO y lo guardo en la bd
        seasonDTO.setTeams(teams);
        this.saveSeason(seasonDTO);
    }
}
