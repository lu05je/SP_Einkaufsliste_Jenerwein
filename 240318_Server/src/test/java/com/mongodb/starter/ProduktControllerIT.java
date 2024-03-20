package com.mongodb.starter;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.starter.dtos.ProduktDTO;
import com.mongodb.starter.models.ProduktEntity;
import com.mongodb.starter.repositories.ProduktRepository;
import jakarta.annotation.PostConstruct;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProduktControllerIT {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private ProduktRepository produktRepository;
    @Autowired
    private TestHelper testHelper;
    private String URL;

    @Autowired
    ProduktControllerIT(MongoClient mongoClient) {
        createPersonCollectionIfNotPresent(mongoClient);
    }

    @PostConstruct
    void setUp() {
        URL = "http://localhost:" + port + "/api";
    }

    @AfterEach
    void tearDown() {
        produktRepository.deleteAll();
    }

    @DisplayName("POST /produkt with 1 produkt")
    @Test
    void postProdukt() {
        // GIVEN
        // WHEN
        ResponseEntity<ProduktDTO> result = rest.postForEntity(URL + "/produkt", testHelper.getJogurtDTO(), ProduktDTO.class);
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ProduktDTO produktDTOResult = result.getBody();
        assertThat(produktDTOResult).isNotNull();
        assertThat(produktDTOResult.id()).isNotNull();
        assertThat(produktDTOResult).usingRecursiveComparison()
                                   .ignoringFields("id", "createdAt")
                                   .isEqualTo(testHelper.getJogurtDTO());
    }

    @DisplayName("POST /produkte with 2 produkte")
    @Test
    void postProdukte() {
        // GIVEN
        // WHEN
        HttpEntity<List<ProduktDTO>> body = new HttpEntity<>(testHelper.getListJogurtTomateDTO());
        ResponseEntity<List<ProduktDTO>> response = rest.exchange(URL + "/produkte", HttpMethod.POST, body,
                                                                 new ParameterizedTypeReference<>() {
                                                                 });
        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).usingElementComparatorIgnoringFields("id", "createdAt")
                                      .containsExactlyInAnyOrderElementsOf(testHelper.getListJogurtTomateDTO());
    }

    @DisplayName("GET /produkte with 2 produkte")
    @Test
    void getProdukte() {
        // GIVEN
        List<ProduktEntity> personEntities = produktRepository.saveAll(testHelper.getListJogurtTomateEntity());
        // WHEN
        ResponseEntity<List<ProduktDTO>> result = rest.exchange(URL + "/produkte", HttpMethod.GET, null,
                                                               new ParameterizedTypeReference<>() {
                                                               });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<ProduktDTO> expected = List.of(testHelper.getJogurtDTOWithId(personEntities.get(0).getId()),
                                           testHelper.getTomateDTOWithId(personEntities.get(1).getId()));
        assertThat(result.getBody()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt")
                                    .containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("GET /produkt/{id}")
    @Test
    void getProduktById() {
        // GIVEN
        ProduktEntity personInserted = produktRepository.save(testHelper.getTomateEntity());
        ObjectId idInserted = personInserted.getId();
        // WHEN
        ResponseEntity<ProduktDTO> result = rest.getForEntity(URL + "/produkt/" + idInserted, ProduktDTO.class);
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).usingRecursiveComparison()
                                    .ignoringFields("createdAt")
                                    .isEqualTo(testHelper.getTomateDTOWithId(idInserted));
    }

    @DisplayName("GET /produkte/{ids}")
    @Test
    void getProdukteByIds() {
        // GIVEN
        List<ProduktEntity> personsInserted = produktRepository.saveAll(testHelper.getListJogurtTomateEntity());
        List<String> idsInserted = personsInserted.stream().map(ProduktEntity::getId).map(ObjectId::toString).toList();
        // WHEN
        String url = URL + "/produkte/" + String.join(",", idsInserted);
        ResponseEntity<List<ProduktDTO>> result = rest.exchange(url, HttpMethod.GET, null,
                                                               new ParameterizedTypeReference<>() {
                                                               });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt")
                                    .containsExactlyInAnyOrderElementsOf(testHelper.getListJogurtTomateDTO());
    }

    @DisplayName("GET /produkte/count")
    @Test
    void getCount() {
        // GIVEN
        produktRepository.saveAll(testHelper.getListJogurtTomateEntity());
        // WHEN
        ResponseEntity<Long> result = rest.getForEntity(URL + "/produkte/count", Long.class);
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(2L);
    }

    @DisplayName("DELETE /produkt/{id}")
    @Test
    void deleteProduktById() {
        // GIVEN
        ProduktEntity personInserted = produktRepository.save(testHelper.getJogurtEntity());
        String idInserted = personInserted.getId().toHexString();
        // WHEN
        ResponseEntity<Long> result = rest.exchange(URL + "/produkt/" + idInserted, HttpMethod.DELETE, null,
                                                    new ParameterizedTypeReference<>() {
                                                    });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(1L);
        assertThat(produktRepository.count()).isEqualTo(0L);
    }

    @DisplayName("DELETE /produkte/{ids}")
    @Test
    void deleteProdukteByIds() {
        // GIVEN
        List<ProduktEntity> personsInserted = produktRepository.saveAll(testHelper.getListJogurtTomateEntity());
        List<String> idsInserted = personsInserted.stream().map(ProduktEntity::getId).map(ObjectId::toString).toList();
        // WHEN
        ResponseEntity<Long> result = rest.exchange(URL + "/produkte/" + String.join(",", idsInserted),
                                                    HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
                });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(2L);
        assertThat(produktRepository.count()).isEqualTo(0L);
    }

    @DisplayName("DELETE /produkte")
    @Test
    void deleteProdukte() {
        // GIVEN
        produktRepository.saveAll(testHelper.getListJogurtTomateEntity());
        // WHEN
        ResponseEntity<Long> result = rest.exchange(URL + "/produkte", HttpMethod.DELETE, null,
                                                    new ParameterizedTypeReference<>() {
                                                    });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(2L);
        assertThat(produktRepository.count()).isEqualTo(0L);
    }

    @DisplayName("PUT /produkt")
    @Test
    void putProdukt() {
        // GIVEN
        ProduktEntity produktInserted = produktRepository.save(testHelper.getJogurtEntity());
        // WHEN
        produktInserted.setAnzahl(5);
        produktInserted.setProdukt("Apfel");
        HttpEntity<ProduktDTO> body = new HttpEntity<>(new ProduktDTO(produktInserted));
        ResponseEntity<ProduktDTO> result = rest.exchange(URL + "/person", HttpMethod.PUT, body,
                                                         new ParameterizedTypeReference<>() {
                                                         });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(
                new ProduktDTO(produktRepository.findOne(produktInserted.getId().toString())));
        assertThat(result.getBody().anzahl()).isEqualTo(5);
        assertThat(result.getBody().produkt()).isEqualTo("Apfel");
        assertThat(produktRepository.count()).isEqualTo(1L);
    }

    @DisplayName("PUT /produkte with 2 produkte")
    @Test
    void putProdukte() {
        // GIVEN
        List<ProduktEntity> produkteInserted = produktRepository.saveAll(testHelper.getListJogurtTomateEntity());
        // WHEN
        produkteInserted.get(0).setAnzahl(5);
        produkteInserted.get(0).setProdukt("Apfel");
        produkteInserted.get(1).setAnzahl(3);
        produkteInserted.get(1).setProdukt("Milch");
        HttpEntity<List<ProduktDTO>> body = new HttpEntity<>(produkteInserted.stream().map(ProduktDTO::new).toList());
        ResponseEntity<Long> result = rest.exchange(URL + "/produkte", HttpMethod.PUT, body,
                                                    new ParameterizedTypeReference<>() {
                                                    });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(2L);
        ProduktEntity max = produktRepository.findOne(produkteInserted.get(0).getId().toString());
        ProduktEntity alex = produktRepository.findOne(produkteInserted.get(1).getId().toString());
        assertThat(max.getAnzahl()).isEqualTo(5);
        assertThat(max.getProdukt()).isEqualTo("Apfel");
        assertThat(alex.getAnzahl()).isEqualTo(3);
        assertThat(alex.getProdukt()).isEqualTo("Milch");
        assertThat(produktRepository.count()).isEqualTo(2L);
    }

    @DisplayName("GET /persons/averageAge")
    @Test
    void getAverageAge() {
        // GIVEN
        produktRepository.saveAll(testHelper.getListJogurtTomateEntity());
        // WHEN
        ResponseEntity<Long> result = rest.getForEntity(URL + "/persons/averageAge", Long.class);
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(29L);
    }

    private void createPersonCollectionIfNotPresent(MongoClient mongoClient) {
        // This is required because it is not possible to create a new collection within a multi-documents transaction.
        // Some tests start by inserting 2 documents with a transaction.
        MongoDatabase db = mongoClient.getDatabase("test");
        if (!db.listCollectionNames().into(new ArrayList<>()).contains("persons")) {
            db.createCollection("persons");
        }
    }
}
