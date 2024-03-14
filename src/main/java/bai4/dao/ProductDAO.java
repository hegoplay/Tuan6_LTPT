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

import bai4.entities.Customer;
import bai4.entities.Product;
import utils.ConnectMongoDB;


public class ProductDAO {
	static MongoCollection<Document> docCollection;
	public static void getCollections() {
		MongoDatabase db = ConnectMongoDB.getDatabase();

		docCollection = db.getCollection(CollectionsList.products.getName());
	}

	public static Product convert2Product(Document doc) {
		Product s = new Product();
		s.setProductId(doc.getLong("_id"));
		s.setBrand(doc.getString("brand_name"));
		s.setCategory(doc.getString("category_name"));
		s.setColors(doc.getList("colors", String.class));
		s.setModelYear(doc.getInteger("model_year", 1999));
		s.setName(doc.getString("product_name"));
		s.setPrice(doc.getDouble("price"));
		return s;
	}
	public static Document convert2Doc(Product p) {
		Document doc = new Document();
		doc.append("_id", p.getProductId());
		doc.append("brand_name", p.getBrand());
		doc.append("category_name",p.getCategory());
		doc.append("colors",p.getColors());
		doc.append("model_year", p.getModelYear());
		doc.append("product_name", p.getName());
		doc.append("price", p.getPrice());
		return doc;
	}
	public static List<Product> getProductsWithCondition(Bson filter) {
		List<Product> res = new ArrayList<>();
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
				res.add(convert2Product(t));
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
	public static List<Document> getDocumentWithCondition(Bson filter) {
		List<Document> res = new ArrayList<>();
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
//		docCollection.find(filter).iterator().forEachRemaining(x -> res.add(convert2Cus(x)));
		return res;
	}
	public static boolean insertOne(Product p) {
		AtomicBoolean res = new AtomicBoolean(false);
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<InsertOneResult> pub = docCollection.insertOne(convert2Doc(p));
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
	public static boolean insertMany(List<Product> p) {
		List<Document> docList = new ArrayList<>();
		for(Product pro : p) {
			docList.add(convert2Doc(pro));
		}
		AtomicBoolean res = new AtomicBoolean(false);
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<InsertManyResult> pub = docCollection.insertMany(docList);
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
		return updateOne(filter, Arrays.asList(update));
	}
	public static UpdateResult updateOne(Bson filter, List<Bson> update) {
		AtomicReference<UpdateResult> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
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
		return updateMany(filter, Arrays.asList(update));
	}
	public static UpdateResult updateMany(Bson filter, List<Bson> update) {
		AtomicReference<UpdateResult> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
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
		AtomicReference<Document> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
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
		AtomicReference<DeleteResult> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
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
		AtomicReference<DeleteResult> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
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
		AtomicReference<Document> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
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
	public static Product getProuctById(long id) {
		AtomicReference<Document> res = new AtomicReference<>();
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<Document> pub = docCollection.find(Filters.eq("_id",id)).first();
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
		if(doc == null) return null;
		return convert2Product(doc);
	}
	public static List<Product> getProductsWithAggregate(List<Bson> bson){
		List<Product> list = new ArrayList<>();
		
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<Document> pub = docCollection.aggregate(bson);
		Subscriber<Document> sub = new Subscriber<Document>() {
			Subscription s;
			@Override
			public void onSubscribe(Subscription s) {
				this.s = s;
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(Document t) {
				// TODO Auto-generated method stub
				s.request(1);
				list.add(convert2Product(t));
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
	public static List<Document> getDocsWithAggregate(List<Bson> bson){
		List<Document> list = new ArrayList<>();
		
		CountDownLatch latch = new CountDownLatch(1);
		Publisher<Document> pub = docCollection.aggregate(bson);
		Subscriber<Document> sub = new Subscriber<Document>() {
			Subscription s;
			@Override
			public void onSubscribe(Subscription s) {
				this.s = s;
				// TODO Auto-generated method stub
				s.request(1);
			}
			
			@Override
			public void onNext(Document t) {
				// TODO Auto-generated method stub
				s.request(1);
				list.add(t);
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
