package bai2.dao;

import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;

import bai2.entities.Zip;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import utils.ConnectMongoDB;

public class ZipDAO {
	static MongoCollection<Zip> zipCollection;
	static MongoCollection<Document> docCollection;

	public static void getCollection() {
		CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(),
				fromProviders(PojoCodecProvider.builder().automatic(true).build()));
		zipCollection = ConnectMongoDB.getDatabase().getCollection("zips", Zip.class)
				.withCodecRegistry(pojoCodecRegistry);
		docCollection = ConnectMongoDB.getDatabase().getCollection("zips");
	}

	public static List<Zip> getAllZip() {
		List<Zip> res = new ArrayList<Zip>();
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<Zip> pub = zipCollection.find();
		Subscriber<Zip> sub = new Subscriber<Zip>() {

			private Subscription s;

			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				this.s = s;
				this.s.request(1);
			}

			@Override
			public void onNext(Zip t) {
				// TODO Auto-generated method stub
				res.add(t);
				this.s.request(1);
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				t.printStackTrace();
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				latch.countDown();
			}
		};
		pub.subscribe(sub);
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static boolean insertZip(Zip z) {

		AtomicBoolean result = new AtomicBoolean(false);

		CountDownLatch latch = new CountDownLatch(1);

		Publisher<InsertOneResult> pub = zipCollection.insertOne(z);

		Subscriber<InsertOneResult> sub = new Subscriber<InsertOneResult>() {

			@Override
			public void onSubscribe(Subscription s) {
				s.request(1);
			}

			@Override
			public void onNext(InsertOneResult t) {
				System.out.println("Product inserted: " + t.getInsertedId());

				result.set(t.getInsertedId() != null ? true : false);
			}

			@Override
			public void onError(Throwable t) {
				t.printStackTrace();
			}

			@Override
			public void onComplete() {
				System.out.println("Completed");
				latch.countDown();
			}

		};

		pub.subscribe(sub);

		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result.get();
	}

	public static void updateZip(Zip z) {
//		AtomicBoolean res = new AtomicBoolean(false);

		CountDownLatch latch = new CountDownLatch(1);

		Publisher<Zip> pub = zipCollection.findOneAndReplace(Filters.eq("_id", z.getId()), z);

		Subscriber<Zip> sub = new Subscriber<Zip>() {

			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}

			@Override
			public void onNext(Zip t) {
				// TODO Auto-generated method stub
				System.out.println(t.getId());
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				t.printStackTrace();
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				latch.countDown();
			}
		};
		pub.subscribe(sub);
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		return res.get();

	}

	public static List<Zip> getZipsFindConditions(Bson filter) {
		List<Zip> res = new ArrayList<Zip>();
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<Zip> pub = zipCollection.find(filter);
		Subscriber<Zip> sub = new Subscriber<Zip>() {

			private Subscription s;

			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				this.s = s;
				this.s.request(1);
			}

			@Override
			public void onNext(Zip t) {
				// TODO Auto-generated method stub
				res.add(t);
				this.s.request(1);
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				t.printStackTrace();
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				latch.countDown();
			}
		};
		pub.subscribe(sub);
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;

	}

	public static List<Zip> getZipsAggregate(List<Bson> filters) {
		List<Zip> res = new ArrayList<Zip>();
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<Zip> pub = zipCollection.aggregate(filters);
		Subscriber<Zip> sub = new Subscriber<Zip>() {

			Subscription s;

			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				this.s = s;
				this.s.request(1);
			}

			@Override
			public void onNext(Zip t) {
				// TODO Auto-generated method stub
				res.add(t);
				s.request(1);
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				t.printStackTrace();
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				latch.countDown();
			}
		};
		pub.subscribe(sub);
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static List<Document> getDocsAggregate(List<Bson> filters) {
		List<Document> res = new ArrayList<>();
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<Document> pub = docCollection.aggregate(filters);
		Subscriber<Document> sub = new Subscriber<Document>() {
			Subscription s;

			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				this.s = s;
				s.request(1);
			}

			@Override
			public void onNext(Document t) {
				// TODO Auto-generated method stub
				res.add(t);
				s.request(1);
			}

			@Override
			public void onError(Throwable t) {
				// TODO Auto-generated method stub
				t.printStackTrace();
			}

			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				latch.countDown();
			}
		};
		pub.subscribe(sub);
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static boolean removeDocument(List<Document> doc, String field, String by) {
		boolean res = true;
		for (Document d : doc) {
			CountDownLatch latch = new CountDownLatch(1);
			AtomicBoolean temp = new AtomicBoolean(true);
			Publisher<DeleteResult> pub = docCollection.deleteMany(Filters.eq(field, d.get(by)));
			Subscriber<DeleteResult> sub = new Subscriber<DeleteResult>() {

				Subscription s;

				@Override
				public void onSubscribe(Subscription s) {
					// TODO Auto-generated method stub
					this.s = s;
					s.request(1);
				}

				@Override
				public void onNext(DeleteResult t) {
					// TODO Auto-generated method stub
					temp.compareAndExchange(temp.get(), t.wasAcknowledged());
					
					s.request(1);
				}

				@Override
				public void onError(Throwable t) {
					// TODO Auto-generated method stub
					t.printStackTrace();
				}

				@Override
				public void onComplete() {
					// TODO Auto-generated method stub
					latch.countDown();
				}
			};
			
			try {
				latch.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			res&= temp.get();
		}
		return res;
	}
}
