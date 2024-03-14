package bai4.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.bson.Document;
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

import bai4.entities.Phone;
import bai4.entities.Product;
import bai4.entities.Staff;
import utils.ConnectMongoDB;

public class StaffDAO {
	static MongoCollection<Document> docCollection;

	public static void getCollections() {
		MongoDatabase db = ConnectMongoDB.getDatabase();

		docCollection = db.getCollection(CollectionsList.staffs.getName());
	}

	public static Staff convert2Staff(Document doc) {
		Staff s = new Staff();
		s.setStaffId(doc.getLong("_id"));
		s.setEmail(doc.getString("email"));
		s.setFirstName(doc.getString("first_name"));
		s.setLastName(doc.getString("last_name"));

		Phone e = new Phone();
		Document doc1 = (Document) doc.get("phone");
		if (doc1 != null) {
			e.setNumber(doc1.getString("number"));
			e.setType(doc1.getString("type"));
		}
		s.setPhone(e);
		if (doc.getLong("manager_id") != null) {
			s.setManager(new Staff(doc.getLong("manager_id")));
		}
		return s;
	}
	public static Document convert2Doc(Staff s) {
		Document doc = new Document();
		doc.append("_id", s.getStaffId());
		doc.append("first_name", s.getFirstName());
		doc.append("last_name", s.getLastName());

		Document phoneDoc = new Document();
		phoneDoc.append("type", s.getPhone().getType());
		phoneDoc.append("number", s.getPhone().getNumber());
		
		doc.append("phone", phoneDoc);
		doc.append("email", s.getEmail());
		doc.append("manager_id", s.getManager().getStaffId());
		
		return doc;
	}
	public static List<Staff> findStaffWithCondition(Bson filter) {
		List<Staff> res = new ArrayList<>();
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
				res.add(convert2Staff(t));
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
	public static boolean insertOne(Staff s) {
		AtomicReference<InsertOneResult> res = new AtomicReference<>();
		Publisher<InsertOneResult> pub = docCollection.insertOne(convert2Doc(s));
		SingleSub<InsertOneResult> sub = new SingleSub<InsertOneResult>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res.get().wasAcknowledged();
	}
	public static boolean insertMany(List<Staff> list) {
		List<Document> listDoc = new ArrayList<>();
		for(Staff s : list) {
			listDoc.add(convert2Doc(s));
		}
		
		AtomicBoolean res = new AtomicBoolean(false);
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<InsertManyResult> pub = docCollection.insertMany(listDoc);
		Subscriber<InsertManyResult> sub = new Subscriber<InsertManyResult>() {
			Subscription s ;
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				this.s = s;
				this.s.request(1);
			}
			
			@Override
			public void onNext(InsertManyResult t) {
				// TODO Auto-generated method stub
				res.set(t.wasAcknowledged());
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
		return res.get();
	}
	public static UpdateResult updateOne(Bson filter, Bson update) {
		return updateOne(filter, Arrays.asList(update));
	}
	public static UpdateResult updateOne(Bson filter, List<Bson> update) {
		AtomicReference<UpdateResult> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<UpdateResult> pub = docCollection.updateOne(filter, update);
		Subscriber<UpdateResult> sub = new Subscriber<UpdateResult>() {
			Subscription s ;
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				this.s = s;
				this.s.request(1);
			}
			
			@Override
			public void onNext(UpdateResult t) {
				// TODO Auto-generated method stub
				res.set(t);
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
		return res.get();
	}
	public static UpdateResult updateMany(Bson filter, Bson update) {
		return updateMany(filter, Arrays.asList(update));
	}
	public static UpdateResult updateMany(Bson filter, List<Bson> update) {
		AtomicReference<UpdateResult> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<UpdateResult> pub = docCollection.updateMany(filter, update);
		Subscriber<UpdateResult> sub = new Subscriber<UpdateResult>() {
			Subscription s ;
			@Override
			public void onSubscribe(Subscription s) {
				// TODO Auto-generated method stub
				this.s = s;
				this.s.request(1);
			}
			
			@Override
			public void onNext(UpdateResult t) {
				// TODO Auto-generated method stub
				res.set(t);
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
		return res.get();
	}
	public static Document findOneAndUpdate(Bson filter, Bson update) {
		AtomicReference<Document> res = new AtomicReference<>();
		Publisher<Document> pub = docCollection.findOneAndUpdate(filter, update);
		SingleSub<Document> sub = new SingleSub<Document>(res);
		
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sub.res.get();
	}
	public static Document findOneAndUpdate(Bson filter, List<Bson> update) {
		AtomicReference<Document> res = new AtomicReference<>();
		Publisher<Document> pub = docCollection.findOneAndUpdate(filter, update);
		SingleSub<Document> sub = new SingleSub<Document>(res);
		
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sub.res.get();
	}
	public static DeleteResult deleteOne(Bson filter) {
		AtomicReference<DeleteResult> res = new AtomicReference<>();
		Publisher<DeleteResult> pub = docCollection.deleteOne(filter);
		SingleSub<DeleteResult> sub = new SingleSub<DeleteResult>(res);
		
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sub.res.get();
	}
	public static DeleteResult deleteMany(Bson filter) {
		AtomicReference<DeleteResult> res = new AtomicReference<>();
		Publisher<DeleteResult> pub = docCollection.deleteMany(filter);
		SingleSub<DeleteResult> sub = new SingleSub<DeleteResult>(res);
		
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sub.res.get();
	}
	public static Document findOneAndDelete(Bson filter) {
		AtomicReference<Document> res = new AtomicReference<>();
		Publisher<Document> pub = docCollection.findOneAndDelete(filter);
		SingleSub<Document> sub = new SingleSub<Document>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sub.res.get();
	}
	public static Staff getStaffById(long id) {
		AtomicReference<Document> res = new AtomicReference<>();
		Publisher<Document> pub = docCollection.find(Filters.eq("_id", id)).first();
		SingleSub<Document> sub = new SingleSub<Document>(res);
		pub.subscribe(sub);
		try {
			sub.latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Document doc = sub.res.get();
		
		if (doc==null)return null;
		return convert2Staff(doc);
	}
	
	
}
