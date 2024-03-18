package com.mongodb.starter;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.starter.dtos.EinkaufszettelDTO;
import com.mongodb.starter.models.EinkaufszettelEntity;
import com.mongodb.starter.repositories.PersonRepository;
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
public class PersonControllerIT {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate rest;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private TestHelper testHelper;
    private String URL;

    @Autowired
    PersonControllerIT(MongoClient mongoClient) {
        createPersonCollectionIfNotPresent(mongoClient);
    }

    @PostConstruct
    void setUp() {
        URL = "http://localhost:" + port + "/api";
    }

    @AfterEach
    void tearDown() {
        personRepository.deleteAll();
    }

    @DisplayName("POST /person with 1 person")
    @Test
    void postPerson() {
        // GIVEN
        // WHEN
        ResponseEntity<EinkaufszettelDTO> result = rest.postForEntity(URL + "/person", testHelper.getMaxDTO(), EinkaufszettelDTO.class);
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EinkaufszettelDTO einkaufszettelDTOResult = result.getBody();
        assertThat(einkaufszettelDTOResult).isNotNull();
        assertThat(einkaufszettelDTOResult.id()).isNotNull();
        assertThat(einkaufszettelDTOResult).usingRecursiveComparison()
                                   .ignoringFields("id", "createdAt")
                                   .isEqualTo(testHelper.getMaxDTO());
    }

    @DisplayName("POST /persons with 2 person")
    @Test
    void postPersons() {
        // GIVEN
        // WHEN
        HttpEntity<List<EinkaufszettelDTO>> body = new HttpEntity<>(testHelper.getListMaxAlexDTO());
        ResponseEntity<List<EinkaufszettelDTO>> response = rest.exchange(URL + "/persons", HttpMethod.POST, body,
                                                                 new ParameterizedTypeReference<>() {
                                                                 });
        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).usingElementComparatorIgnoringFields("id", "createdAt")
                                      .containsExactlyInAnyOrderElementsOf(testHelper.getListMaxAlexDTO());
    }

    @DisplayName("GET /persons with 2 persons")
    @Test
    void getPersons() {
        // GIVEN
        List<EinkaufszettelEntity> personEntities = personRepository.saveAll(testHelper.getListMaxAlexEntity());
        // WHEN
        ResponseEntity<List<EinkaufszettelDTO>> result = rest.exchange(URL + "/persons", HttpMethod.GET, null,
                                                               new ParameterizedTypeReference<>() {
                                                               });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<EinkaufszettelDTO> expected = List.of(testHelper.getMaxDTOWithId(personEntities.get(0).getId()),
                                           testHelper.getAlexDTOWithId(personEntities.get(1).getId()));
        assertThat(result.getBody()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt")
                                    .containsExactlyInAnyOrderElementsOf(expected);
    }

    @DisplayName("GET /person/{id}")
    @Test
    void getPersonById() {
        // GIVEN
        EinkaufszettelEntity personInserted = personRepository.save(testHelper.getAlexEntity());
        ObjectId idInserted = personInserted.getId();
        // WHEN
        ResponseEntity<EinkaufszettelDTO> result = rest.getForEntity(URL + "/person/" + idInserted, EinkaufszettelDTO.class);
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).usingRecursiveComparison()
                                    .ignoringFields("createdAt")
                                    .isEqualTo(testHelper.getAlexDTOWithId(idInserted));
    }

    @DisplayName("GET /persons/{ids}")
    @Test
    void getPersonsByIds() {
        // GIVEN
        List<EinkaufszettelEntity> personsInserted = personRepository.saveAll(testHelper.getListMaxAlexEntity());
        List<String> idsInserted = personsInserted.stream().map(EinkaufszettelEntity::getId).map(ObjectId::toString).toList();
        // WHEN
        String url = URL + "/persons/" + String.join(",", idsInserted);
        ResponseEntity<List<EinkaufszettelDTO>> result = rest.exchange(url, HttpMethod.GET, null,
                                                               new ParameterizedTypeReference<>() {
                                                               });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt")
                                    .containsExactlyInAnyOrderElementsOf(testHelper.getListMaxAlexDTO());
    }

    @DisplayName("GET /persons/count")
    @Test
    void getCount() {
        // GIVEN
        personRepository.saveAll(testHelper.getListMaxAlexEntity());
        // WHEN
        ResponseEntity<Long> result = rest.getForEntity(URL + "/persons/count", Long.class);
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(2L);
    }

    @DisplayName("DELETE /person/{id}")
    @Test
    void deletePersonById() {
        // GIVEN
        EinkaufszettelEntity personInserted = personRepository.save(testHelper.getMaxEntity());
        String idInserted = personInserted.getId().toHexString();
        // WHEN
        ResponseEntity<Long> result = rest.exchange(URL + "/person/" + idInserted, HttpMethod.DELETE, null,
                                                    new ParameterizedTypeReference<>() {
                                                    });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(1L);
        assertThat(personRepository.count()).isEqualTo(0L);
    }

    @DisplayName("DELETE /persons/{ids}")
    @Test
    void deletePersonsByIds() {
        // GIVEN
        List<EinkaufszettelEntity> personsInserted = personRepository.saveAll(testHelper.getListMaxAlexEntity());
        List<String> idsInserted = personsInserted.stream().map(EinkaufszettelEntity::getId).map(ObjectId::toString).toList();
        // WHEN
        ResponseEntity<Long> result = rest.exchange(URL + "/persons/" + String.join(",", idsInserted),
                                                    HttpMethod.DELETE, null, new ParameterizedTypeReference<>() {
                });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(2L);
        assertThat(personRepository.count()).isEqualTo(0L);
    }

    @DisplayName("DELETE /persons")
    @Test
    void deletePersons() {
        // GIVEN
        personRepository.saveAll(testHelper.getListMaxAlexEntity());
        // WHEN
        ResponseEntity<Long> result = rest.exchange(URL + "/persons", HttpMethod.DELETE, null,
                                                    new ParameterizedTypeReference<>() {
                                                    });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(2L);
        assertThat(personRepository.count()).isEqualTo(0L);
    }

    @DisplayName("PUT /person")
    @Test
    void putPerson() {
        // GIVEN
        EinkaufszettelEntity personInserted = personRepository.save(testHelper.getMaxEntity());
        // WHEN
        personInserted.setAge(32);
        personInserted.setInsurance(false);
        HttpEntity<EinkaufszettelDTO> body = new HttpEntity<>(new EinkaufszettelDTO(personInserted));
        ResponseEntity<EinkaufszettelDTO> result = rest.exchange(URL + "/person", HttpMethod.PUT, body,
                                                         new ParameterizedTypeReference<>() {
                                                         });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(
                new EinkaufszettelDTO(personRepository.findOne(personInserted.getId().toString())));
        assertThat(result.getBody().age()).isEqualTo(32);
        assertThat(result.getBody().insurance()).isFalse();
        assertThat(personRepository.count()).isEqualTo(1L);
    }

    @DisplayName("PUT /persons with 2 persons")
    @Test
    void putPersons() {
        // GIVEN
        List<EinkaufszettelEntity> personsInserted = personRepository.saveAll(testHelper.getListMaxAlexEntity());
        // WHEN
        personsInserted.get(0).setAge(32);
        personsInserted.get(0).setInsurance(false);
        personsInserted.get(1).setAge(28);
        personsInserted.get(1).setInsurance(true);
        HttpEntity<List<EinkaufszettelDTO>> body = new HttpEntity<>(personsInserted.stream().map(EinkaufszettelDTO::new).toList());
        ResponseEntity<Long> result = rest.exchange(URL + "/persons", HttpMethod.PUT, body,
                                                    new ParameterizedTypeReference<>() {
                                                    });
        // THEN
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEqualTo(2L);
        EinkaufszettelEntity max = personRepository.findOne(personsInserted.get(0).getId().toString());
        EinkaufszettelEntity alex = personRepository.findOne(personsInserted.get(1).getId().toString());
        assertThat(max.getAge()).isEqualTo(32);
        assertThat(max.getInsurance()).isFalse();
        assertThat(alex.getAge()).isEqualTo(28);
        assertThat(alex.getInsurance()).isTrue();
        assertThat(personRepository.count()).isEqualTo(2L);
    }

    @DisplayName("GET /persons/averageAge")
    @Test
    void getAverageAge() {
        // GIVEN
        personRepository.saveAll(testHelper.getListMaxAlexEntity());
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
