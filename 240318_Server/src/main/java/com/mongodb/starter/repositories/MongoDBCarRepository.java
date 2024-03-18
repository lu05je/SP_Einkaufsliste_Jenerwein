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
import com.mongodb.starter.dtos.AverageAgeDTO;
import com.mongodb.starter.models.CarEntity;
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
public class MongoDBCarRepository implements CarRepository {

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .readConcern(ReadConcern.MAJORITY)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    private final MongoClient client;
    private MongoCollection<CarEntity> carCollection;

    public MongoDBCarRepository(MongoClient mongoClient) { // Änderung hier
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        carCollection = client.getDatabase("test").getCollection("cars", CarEntity.class);
    }

    @Override
    public CarEntity save(CarEntity carEntity) {
        carEntity.setId(new ObjectId());
        carCollection.insertOne(carEntity);
        return carEntity;
    }

    @Override
    public List<CarEntity> saveAll(List<CarEntity> carEntities) {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(() -> {
                carEntities.forEach(c -> c.setId(new ObjectId()));
                carCollection.insertMany(clientSession, carEntities);
                return carEntities;
            }, txnOptions);
        }
    }

    @Override
    public List<CarEntity> findAll() { // Änderung hier
        return carCollection.find().into(new ArrayList<>());
    }

    @Override
    public List<CarEntity> findAll(List<String> ids) {
        return carCollection.find(in("_id", mapToObjectIds(ids))).into(new ArrayList<>());
    }

    @Override
    public CarEntity findOne(String id) { // Änderung hier
        return carCollection.find(eq("_id", new ObjectId(id))).first();
    }

    @Override
    public long count() {
        return carCollection.countDocuments();
    }

    @Override
    public long delete(String id) { // Änderung hier
        return carCollection.deleteOne(eq("_id", new ObjectId(id))).getDeletedCount();
    }

    @Override
    public long delete(List<String> ids) {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> carCollection.deleteMany(clientSession, in("_id", mapToObjectIds(ids))).getDeletedCount(),
                    txnOptions);
        }
    }

    @Override
    public long deleteAll() {
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> carCollection.deleteMany(clientSession, new BsonDocument()).getDeletedCount(), txnOptions);
        }
    }

    @Override
    public CarEntity update(CarEntity carEntity) {
        FindOneAndReplaceOptions options = new FindOneAndReplaceOptions().returnDocument(AFTER);
        return carCollection.findOneAndReplace(eq("_id", carEntity.getId()), carEntity, options);
    }

    @Override
    public long update(List<CarEntity> carEntities) {
        List<ReplaceOneModel<CarEntity>> writes = carEntities.stream()
                .map(c -> new ReplaceOneModel<>(eq("_id", c.getId()), c))
                .toList();
        try (ClientSession clientSession = client.startSession()) {
            return clientSession.withTransaction(
                    () -> carCollection.bulkWrite(clientSession, writes).getModifiedCount(), txnOptions);
        }
    }

    private List<ObjectId> mapToObjectIds(List<String> ids) {
        return ids.stream().map(ObjectId::new).toList();
    }
}
