package bai4.dao;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;

import bai4.entities.Address;
import bai4.entities.Customer;
import utils.ConnectMongoDB;

public class CustomerDAO {
	private static MongoCollection<Customer> customerCollection;
	private static MongoCollection<Document> docCollection;
	private static CodecProvider pojoCodecProvider;
	private static CodecRegistry pojoCodecRegistry;

	public static void getCollections() {
		pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
		pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
//		connect

		MongoDatabase db = ConnectMongoDB.getDatabase();

//		customerCollection = db.getCollection(CollectionsList.customers.getName(),Customer.class);
		docCollection = db.getCollection(CollectionsList.customers.getName());
	}


	public static Customer convert2Cus(Document doc) {
		Customer cus = new Customer();
		cus.setCustomerId(doc.getString("_id"));

		Document docAdd = doc.get("address", Document.class);
		Address add = new Address();
		add.setCity(docAdd.getString("city"));
		add.setState(docAdd.getString("state"));
		add.setStreet(docAdd.getString("street"));
		add.setZipCode(docAdd.getInteger("zip_code"));

		cus.setEmail(doc.getString("email"));
		cus.setFirstName(doc.getString("first_name"));
		cus.setLastName(doc.getString("last_name"));
		cus.setRegistrationDate(doc.getDate("registration_date"));
		cus.setAddress(add);
		return cus;
	}

	public static Document convert2Doc(Customer c) {
		Document doc = new Document();
		doc.append("_id", c.getCustomerId());

		Document addressDoc = new Document();
		addressDoc.append("city", c.getAddress().getCity());
		addressDoc.append("state", c.getAddress().getState());
		addressDoc.append("street", c.getAddress().getStreet());
		addressDoc.append("zip_code", c.getAddress().getZipCode());

		doc.append("address", addressDoc);
		doc.append("email", c.getEmail());
		doc.append("first_name", c.getFirstName());
		doc.append("last_name", c.getLastName());
		doc.append("registration_date", c.getRegistrationDate());
		return doc;
	}

	public static List<Customer> getCustomerWithCondition(Bson filter) {
		List<Customer> res = new ArrayList<>();
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<Document> pub = docCollection.find(filter);
		Subscriber<Document> sub = new Subscriber<Document>() {
			Subscription s ;
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				this.s = s;
				this.s.request(1);
			}
			
			@Override
			public void onNext(Document t) {
				// TODO Auto-generated method stub
				res.add(convert2Cus(t));
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res;
	}

	public static boolean insertOne(Customer c) {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicBoolean res = new AtomicBoolean(false);
		Publisher<InsertOneResult> pub = docCollection.insertOne(convert2Doc(c));
		Subscriber<InsertOneResult> sub = new Subscriber<InsertOneResult>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(InsertOneResult t) {
				// TODO Auto-generated method stub
				res.set(t.wasAcknowledged());
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get();
	}

	public static boolean insertMany(List<Customer> c) {
		List<Document> listDoc = new ArrayList<>();
		for (Customer cus : c) {
			listDoc.add(convert2Doc(cus));
		}
		CountDownLatch latch = new CountDownLatch(1);
		AtomicBoolean res = new AtomicBoolean(false);
		Publisher<InsertManyResult> pub = docCollection.insertMany(listDoc);
		Subscriber<InsertManyResult> sub = new Subscriber<InsertManyResult>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(InsertManyResult t) {
				// TODO Auto-generated method stub
				res.set(t.wasAcknowledged());
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get();
	}

	public static UpdateResult updateOne(Bson filter, Bson update) {
		return updateOne(filter,Arrays.asList(update));
	}

	public static UpdateResult updateOne(Bson filter, List<Bson> update) {
//		return docCollection.updateOne(filter, update);
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<UpdateResult> res = new AtomicReference<>(); 
		
		Publisher<UpdateResult> pub = docCollection.updateOne(filter, update);
		Subscriber<UpdateResult> sub = new Subscriber<UpdateResult>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(UpdateResult t) {
				// TODO Auto-generated method stub
				res.set(t);
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get();
	}

	public static UpdateResult updateMany(Bson filter, Bson update) {
		return updateMany(filter, Arrays.asList( update));
	}

	public static UpdateResult updateMany(Bson filter, List<Bson> update) {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<UpdateResult> res = new AtomicReference<>(); 
		
		Publisher<UpdateResult> pub = docCollection.updateMany(filter, update);
		Subscriber<UpdateResult> sub = new Subscriber<UpdateResult>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(UpdateResult t) {
				// TODO Auto-generated method stub
				res.set(t);
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get();
	}

	public static Document findOneAndUpdate(Bson filter, Bson update) {
		return findOneAndUpdate(filter, Arrays.asList(update));
	}

	public static Document findOneAndUpdate(Bson filter, List<Bson> update) {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<Document> res = new AtomicReference<>(); 
		
		Publisher<Document> pub = docCollection.findOneAndUpdate(filter, update);
		Subscriber<Document> sub = new Subscriber<Document>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(Document t) {
				// TODO Auto-generated method stub
				res.set(t);
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get();
	}

	public static DeleteResult deleteOne(Bson filter) {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<DeleteResult> res = new AtomicReference<>(); 
		
		Publisher<DeleteResult> pub = docCollection.deleteOne(filter);
		Subscriber<DeleteResult> sub = new Subscriber<DeleteResult>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(DeleteResult t) {
				// TODO Auto-generated method stub
				res.set(t);
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get();
	}

	public static DeleteResult deleteMany(Bson filter) {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<DeleteResult> res = new AtomicReference<>(); 
		
		Publisher<DeleteResult> pub = docCollection.deleteMany(filter);
		Subscriber<DeleteResult> sub = new Subscriber<DeleteResult>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(DeleteResult t) {
				// TODO Auto-generated method stub
				res.set(t);
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get();
	}

	public static Document findOneAndDelete(Bson filter) {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<Document> res = new AtomicReference<>(); 
		
		Publisher<Document> pub = docCollection.findOneAndDelete(filter);
		Subscriber<Document> sub = new Subscriber<Document>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(Document t) {
				// TODO Auto-generated method stub
				res.set(t);
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get();
	}

	public static Customer getCustomerById(String id) {
		CountDownLatch latch = new CountDownLatch(1);
		AtomicReference<Document> res = new AtomicReference<>(); 
		
		Publisher<Document> pub = docCollection.find(Filters.eq("_id", id)).first();
		Subscriber<Document> sub = new Subscriber<Document>() {
		
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(Document t) {
				// TODO Auto-generated method stub
				res.set(t);
			
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		Document doc = res.get();
		if (doc == null)
			return null;
		return convert2Cus(doc);
		
	}
	public static List<Document> getDocsWithAggregate(List<Bson> bson){
		List<Document> list = new ArrayList<>();
		CountDownLatch latch = new CountDownLatch(1); 
		
		Publisher<Document> pub = docCollection.aggregate(bson);
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
				list.add(t);
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
		return list;
	}
}
