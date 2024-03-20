package com.mongodb.starter.repositories;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.ReplaceOneModel;
import com.mongodb.starter.models.ProduktEntity;
import jakarta.annotation.PostConstruct;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Accumulators.avg;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.ReturnDocument.AFTER;

@Repository
public class MongoDBProduktRepository implements ProduktRepository {

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
                                                                           .readPreference(ReadPreference.primary())
                                                                           .readConcern(ReadConcern.MAJORITY)
                                                                           .writeConcern(WriteConcern.MAJORITY)
                                                                           .build();
    private final MongoClient client;
    private MongoCollection<ProduktEntity> personCollection;

    public MongoDBProduktRepository(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        personCollection = client.getDatabase("test").getCollection("persons", ProduktEntity.class);
    }

    @Override
    public ProduktEntity save(ProduktEntity produktEntity) {
        produktEntity.setId(new ObjectId());
        personCollection.insertOne(produktEntity);
        return produktEntity;
    }

    @Override
    public List<ProduktEntity> saveAll(List<ProduktEntity> personEntities) {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(() -> {
                personEntities.forEach(p -> p.setId(new ObjectId()));
                personCollection.insertMany(clientSession, personEntities);
                return personEntities;
            }, txnOptions);
        }
    }

    @Override
    public List<ProduktEntity> findAll() {
        return personCollection.find().into(new ArrayList<>());
    }

    @Override
    public List<ProduktEntity> findAll(List<String> ids) {
        return personCollection.find(in("_id", mapToObjectIds(ids))).into(new ArrayList<>());
    }

    @Override
    public ProduktEntity findOne(String id) {
        return personCollection.find(eq("_id", new ObjectId(id))).first();
    }

    @Override
    public long count() {
        return personCollection.countDocuments();
    }

    @Override
    public long delete(String id) {
        return personCollection.deleteOne(eq("_id", new ObjectId(id))).getDeletedCount();
    }

    @Override
    public long delete(List<String> ids) {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> personCollection.deleteMany(clientSession, in("_id", mapToObjectIds(ids))).getDeletedCount(),
                    txnOptions);
        }
    }

    @Override
    public long deleteAll() {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> personCollection.deleteMany(clientSession, new BsonDocument()).getDeletedCount(), txnOptions);
        }
    }

    @Override
    public ProduktEntity update(ProduktEntity produktEntity) {
        FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().returnDocument(AFTER);
        return personCollection.findOneAndReplace(eq("_id", produktEntity.getId()), produktEntity, options);
    }

    @Override
    public long update(List<ProduktEntity> personEntities) {
        List<ReplaceOneModel<ProduktEntity>> writes = personEntities.stream()
                                                                   .map(p -> new ReplaceOneModel<>(eq("_id", p.getId()),
                                                                                                   p))
                                                                   .toList();
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> personCollection.bulkWrite(clientSession, writes).getModifiedCount(), txnOptions);
        }
    }

    private List<ObjectId> mapToObjectIds(List<String> ids) {
        return ids.stream().map(ObjectId::new).toList();
    }
}
