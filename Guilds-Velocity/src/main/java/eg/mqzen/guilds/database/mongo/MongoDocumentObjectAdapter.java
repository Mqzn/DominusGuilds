package eg.mqzen.guilds.database.mongo;

import org.bson.Document;

/**
 * @author Mqzen
 * @date 18/5/2025
 */
public interface MongoDocumentObjectAdapter<T> {

    T fromDocument(Document document);

    Document toDocument(T object);

    default T fromDocument(String json) {
        return fromDocument(Document.parse(json));
    }

    default String toJson(T object) {
        return toDocument(object).toJson();
    }

    default String toJson(String json) {
        return toJson(fromDocument(json));
    }

}
