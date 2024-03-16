package tuan5.dao;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

import com.mongodb.reactivestreams.client.MongoCollection;

import component.ListSub;
import tuan5.entities.Movie;
import utils.ConnectMongoDB;

public class MovieDAO {
	static MongoCollection<Document> docCollection;
	static MongoCollection<Movie> movieCollection;
	public static void getCollections() {
		CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		docCollection = ConnectMongoDB.getDatabase().getCollection("movies");
		movieCollection = ConnectMongoDB.getDatabase().getCollection("movies",Movie.class).withCodecRegistry(pojoCodecRegistry);
	}
	public static List<Movie> getMoviesFindCond(Bson filter){
		List<Movie> list = new ArrayList<>();
		Publisher<Movie> pub = movieCollection.find(filter);
		ListSub<Movie> sub = new ListSub<>(list);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	public static List<Document> getDocsWithAggregate(List<Bson> filter){
		List<Document> list = new ArrayList<>();
		Publisher<Document> pub = docCollection.aggregate(filter);
		ListSub<Document> sub = new ListSub<>(list);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
}
